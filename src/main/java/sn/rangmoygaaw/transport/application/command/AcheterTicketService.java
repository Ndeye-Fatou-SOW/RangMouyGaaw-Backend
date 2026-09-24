package sn.rangmoygaaw.transport.application.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.rangmoygaaw.transport.domain.model.Ligne;
import sn.rangmoygaaw.transport.domain.model.ModePaiement;
import sn.rangmoygaaw.transport.domain.model.Ticket;
import sn.rangmoygaaw.transport.domain.model.TicketStatut;
import sn.rangmoygaaw.transport.domain.port.in.AcheterTicketUseCase;
import sn.rangmoygaaw.transport.domain.port.out.TicketRepository;
import sn.rangmoygaaw.transport.domain.port.out.LigneRepository;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class AcheterTicketService implements AcheterTicketUseCase {

    private final TicketRepository ticketRepository;

    private final LigneRepository ligneRepository; 

    public AcheterTicketService(
            TicketRepository ticketRepository,
            LigneRepository ligneRepository) {
        this.ticketRepository = ticketRepository;
        this.ligneRepository  = ligneRepository;
    }

    /** Tarif appliqué lorsque la ligne n'en définit aucun. */
    private static final BigDecimal TARIF_PAR_DEFAUT = BigDecimal.valueOf(200);

    /** Durée de validité d'un ticket à compter de son achat. */
    private static final int VALIDITE_HEURES = 2;

    @Override
    public Ticket acheter(UUID citoyenId, UUID ligneId,
                          ModePaiement modePaiement) {

        BigDecimal montant = ligneRepository.findById(ligneId)
            .map(Ligne::getTarif)
            .filter(Objects::nonNull)
            .orElse(TARIF_PAR_DEFAUT);

        Ticket ticket = new Ticket();
        ticket.setCitoyenId(citoyenId);
        ticket.setLigneId(ligneId);
        ticket.setModePaiement(modePaiement);
        ticket.setMontant(montant);
        ticket.setStatut(statutInitial(modePaiement));
        ticket.setDateAchat(LocalDateTime.now());
        ticket.setDateExpiration(LocalDateTime.now().plusHours(VALIDITE_HEURES));
        ticket.setCodeQR(genererCode());

        return ticketRepository.save(ticket);
    }

    /**
     * Un ticket payé par mobile n'est PAS valable tant que l'opérateur n'a
     * pas confirmé le paiement : il naît EN_ATTENTE_PAIEMENT et n'est activé
     * que par le rappel PayTech. Sans cela, annuler le paiement laisserait
     * un titre de transport utilisable — donc gratuit.
     */
    private TicketStatut statutInitial(ModePaiement modePaiement) {
        return modePaiement == ModePaiement.CASH
            ? TicketStatut.ACTIF
            : TicketStatut.EN_ATTENTE_PAIEMENT;
    }

    /**
     * Code de validation présenté au valideur. Tiré d'un générateur
     * cryptographique plutôt que d'un UUID tronqué, pour qu'il ne soit pas
     * devinable à partir d'un autre code.
     */
    private String genererCode() {
        byte[] octets = new byte[8];
        ALEATOIRE.nextBytes(octets);

        StringBuilder code = new StringBuilder(12);
        for (byte b : octets) {
            code.append(ALPHABET.charAt((b & 0xFF) % ALPHABET.length()));
        }
        // Longueur alignée sur la contrainte du value object CodeTicket (≥ 8)
        while (code.length() < 12) {
            code.append(ALPHABET.charAt(ALEATOIRE.nextInt(ALPHABET.length())));
        }
        return code.toString();
    }

    // Alphabet sans caractères ambigus (0/O, 1/I) : le code est lu à voix
    // haute ou recopié à la main par les valideurs.
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom ALEATOIRE = new SecureRandom();
}