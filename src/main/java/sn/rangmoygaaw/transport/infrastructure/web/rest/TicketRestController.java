package sn.rangmoygaaw.transport.infrastructure.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.rangmoygaaw.transport.domain.model.ModePaiement;
import sn.rangmoygaaw.transport.domain.model.Ticket;
import sn.rangmoygaaw.transport.domain.port.in.AcheterTicketUseCase;
import sn.rangmoygaaw.transport.domain.port.out.LigneRepository;
import sn.rangmoygaaw.transport.infrastructure.payment.PayTechAdapter;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transport/tickets")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class TicketRestController {

    private final AcheterTicketUseCase acheterTicket;
    private final PayTechAdapter payTechAdapter;
    private final LigneRepository ligneRepository;

    public TicketRestController(
            AcheterTicketUseCase acheterTicket,
            PayTechAdapter payTechAdapter,
            LigneRepository ligneRepository) {
        this.acheterTicket   = acheterTicket;
        this.payTechAdapter  = payTechAdapter;
        this.ligneRepository = ligneRepository;
    }

    @PostMapping("/acheter")
    public ResponseEntity<Map<String, Object>> acheter(
            @RequestParam UUID citoyenId,
            @RequestParam UUID ligneId,
            @RequestParam ModePaiement modePaiement,
            @RequestParam(required = false) String telephone) {

        // 1. Crée le ticket
        Ticket ticket = acheterTicket.acheter(
            citoyenId, ligneId, modePaiement);

        // 2. Si paiement mobile → initie PayTech
        if (modePaiement != ModePaiement.CASH) {
            String ligneNom = ligneRepository.findById(ligneId)
                .map(l -> l.getNumero() + " " + l.getDepart()
                    + " → " + l.getArrivee())
                .orElse("Ligne inconnue");

            Map<String, Object> paytech = payTechAdapter
                .initierPaiement(
                    ticket.getId(),
                    ticket.getMontant(),
                    telephone != null ? telephone : "",
                    ligneNom
                );

            // Retourne le lien de paiement PayTech
            return ResponseEntity.ok(Map.of(
                "ticket",       ticket,
                "paymentUrl",   paytech.getOrDefault(
                    "redirect_url", ""),
                "success",      true
            ));
        }

        // Cash → retourne juste le ticket
        return ResponseEntity.ok(Map.of(
            "ticket",   ticket,
            "success",  true
        ));
    }

    @PostMapping("/ipn")
    public ResponseEntity<String> ipnCallback(
            @RequestBody Map<String, Object> payload) {
        // PayTech appelle cette URL après paiement réussi
        String refCommand = (String) payload.get("ref_command");
        String typeEvent  = (String) payload.get("type_event");

        if ("sale_complete".equals(typeEvent)) {
            // TODO : activer le ticket en base
        }

        return ResponseEntity.ok("OK");
    }
}