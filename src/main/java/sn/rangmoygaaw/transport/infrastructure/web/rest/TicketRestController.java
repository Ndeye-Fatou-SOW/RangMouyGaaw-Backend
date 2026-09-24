package sn.rangmoygaaw.transport.infrastructure.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.rangmoygaaw.transport.domain.model.ModePaiement;
import sn.rangmoygaaw.transport.domain.model.Ticket;
import sn.rangmoygaaw.transport.domain.model.TicketStatut;
import sn.rangmoygaaw.transport.domain.port.in.AcheterTicketUseCase;
import sn.rangmoygaaw.transport.domain.port.out.LigneRepository;
import sn.rangmoygaaw.transport.domain.port.out.TicketRepository;
import sn.rangmoygaaw.transport.domain.port.out.UtilisateurRepository;
import sn.rangmoygaaw.transport.infrastructure.payment.PayTechAdapter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transport/tickets")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class TicketRestController {

    private static final Logger log =
        LoggerFactory.getLogger(TicketRestController.class);

    private final AcheterTicketUseCase acheterTicket;
    private final PayTechAdapter payTechAdapter;
    private final LigneRepository ligneRepository;
    private final TicketRepository ticketRepository;
    private final UtilisateurRepository utilisateurRepository;

    public TicketRestController(
            AcheterTicketUseCase acheterTicket,
            PayTechAdapter payTechAdapter,
            LigneRepository ligneRepository,
            TicketRepository ticketRepository,
            UtilisateurRepository utilisateurRepository) {
        this.acheterTicket         = acheterTicket;
        this.payTechAdapter        = payTechAdapter;
        this.ligneRepository       = ligneRepository;
        this.ticketRepository      = ticketRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Achat d'un ticket pour le citoyen authentifié.
     *
     * L'identifiant du citoyen est déduit du jeton, jamais transmis par le
     * client : accepter un paramètre citoyenId permettrait à un utilisateur
     * d'acheter — ou de consulter — au nom de quelqu'un d'autre.
     */
    @PostMapping("/acheter")
    public ResponseEntity<Map<String, Object>> acheter(
            @RequestParam UUID ligneId,
            @RequestParam ModePaiement modePaiement,
            @RequestParam(required = false) String telephone,
            Authentication authentication) {

        UUID citoyenId = citoyenConnecte(authentication);
        if (citoyenId == null) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Utilisateur non authentifié"));
        }

        Ticket ticket = acheterTicket.acheter(citoyenId, ligneId, modePaiement);

        // Paiement en espèces : le ticket est immédiatement valable.
        if (modePaiement == ModePaiement.CASH) {
            return ResponseEntity.ok(Map.of(
                "ticket",  ticket,
                "success", true
            ));
        }

        // Paiement mobile : le ticket reste EN_ATTENTE_PAIEMENT (voir
        // AcheterTicketService) jusqu'à confirmation par le rappel PayTech.
        String ligneNom = ligneRepository.findById(ligneId)
            .map(l -> l.getNumero() + " " + l.getDepart() + " → " + l.getArrivee())
            .orElse("Ligne inconnue");

        Map<String, Object> paytech = payTechAdapter.initierPaiement(
            ticket.getId(),
            ticket.getMontant(),
            telephone != null ? telephone : "",
            ligneNom
        );

        return ResponseEntity.ok(Map.of(
            "ticket",     ticket,
            "paymentUrl", paytech.getOrDefault("redirect_url", ""),
            "success",    true
        ));
    }

    /** Historique des tickets du citoyen authentifié. */
    @GetMapping("/mes-tickets")
    public ResponseEntity<?> mesTickets(Authentication authentication) {
        UUID citoyenId = citoyenConnecte(authentication);
        if (citoyenId == null) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Utilisateur non authentifié"));
        }
        List<Ticket> tickets = ticketRepository.findByCitoyenId(citoyenId);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Détail d'un ticket. Un citoyen ne peut consulter que ses propres
     * tickets ; un administrateur peut consulter n'importe lequel.
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<?> detail(
            @PathVariable UUID ticketId,
            Authentication authentication) {

        UUID citoyenId = citoyenConnecte(authentication);
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);

        if (ticket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        boolean estAdmin = authentication != null
            && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!estAdmin && !ticket.get().getCitoyenId().equals(citoyenId)) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "Ce ticket ne vous appartient pas"));
        }

        return ResponseEntity.ok(ticket.get());
    }

    /**
     * Rappel appelé par les serveurs PayTech après une tentative de paiement.
     *
     * C'est ici, et seulement ici, qu'un ticket payé par mobile devient
     * valable. Le payload est authentifié par sa signature avant tout
     * traitement : cet endpoint est public, donc n'importe qui peut l'appeler.
     */
    @PostMapping("/ipn")
    public ResponseEntity<String> ipnCallback(
            @RequestBody Map<String, Object> payload) {

        if (!payTechAdapter.signatureValide(payload)) {
            log.warn("Rappel PayTech rejeté : signature invalide — payload={}", payload);
            return ResponseEntity.status(403).body("Signature invalide");
        }

        String typeEvent  = (String) payload.get("type_event");
        String refCommand = (String) payload.get("ref_command");

        if (!"sale_complete".equals(typeEvent)) {
            log.info("Rappel PayTech ignoré (type_event={})", typeEvent);
            return ResponseEntity.ok("OK");
        }

        UUID ticketId = extraireTicketId(refCommand);
        if (ticketId == null) {
            log.warn("Rappel PayTech : ref_command illisible « {} »", refCommand);
            return ResponseEntity.badRequest().body("ref_command invalide");
        }

        Optional<Ticket> trouve = ticketRepository.findById(ticketId);
        if (trouve.isEmpty()) {
            log.warn("Rappel PayTech : ticket {} introuvable", ticketId);
            return ResponseEntity.notFound().build();
        }

        Ticket ticket = trouve.get();

        // Le rappel peut être émis plusieurs fois pour un même paiement :
        // on n'active que les tickets encore en attente.
        if (ticket.getStatut() != TicketStatut.EN_ATTENTE_PAIEMENT) {
            log.info("Rappel PayTech : ticket {} déjà traité (statut {})",
                ticketId, ticket.getStatut());
            return ResponseEntity.ok("OK");
        }

        ticket.setStatut(TicketStatut.ACTIF);
        Object idTransaction = payload.get("transaction_id");
        ticket.setTransactionId(
            idTransaction != null ? idTransaction.toString() : refCommand);
        ticketRepository.save(ticket);

        log.info("Paiement confirmé — ticket {} activé", ticketId);
        return ResponseEntity.ok("OK");
    }

    /**
     * Résout l'identifiant du citoyen à partir du nom d'utilisateur porté
     * par le jeton. Retourne null si la requête n'est pas authentifiée.
     */
    private UUID citoyenConnecte(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return utilisateurRepository.findByUsername(authentication.getName())
            .map(u -> u.getId())
            .orElse(null);
    }

    /** ref_command a la forme « {uuid}-{horodatage} » (voir PayTechAdapter). */
    private UUID extraireTicketId(String refCommand) {
        if (refCommand == null || refCommand.length() < 36) {
            return null;
        }
        try {
            return UUID.fromString(refCommand.substring(0, 36));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
