package sn.rangmoygaaw.transport.application.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.rangmoygaaw.transport.domain.model.ModePaiement;
import sn.rangmoygaaw.transport.domain.model.Ticket;
import sn.rangmoygaaw.transport.domain.model.TicketStatut;
import sn.rangmoygaaw.transport.domain.port.in.AcheterTicketUseCase;
import sn.rangmoygaaw.transport.domain.port.out.TicketRepository;
import sn.rangmoygaaw.transport.domain.port.out.LigneRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Override
    public Ticket acheter(UUID citoyenId, UUID ligneId,
                          ModePaiement modePaiement) {

        // Récupère le tarif depuis la ligne
        BigDecimal montant = BigDecimal.valueOf(200); // valeur par défaut

        java.util.Optional<sn.rangmoygaaw.transport.domain.model.Ligne> ligneOpt = 
            ligneRepository.findById(ligneId);
        
        if (ligneOpt.isPresent() && ligneOpt.get().getTarif() != null) {
            montant = ligneOpt.get().getTarif();
        }

        Ticket ticket = new Ticket();
       // ticket.setId(UUID.randomUUID());
        ticket.setCitoyenId(citoyenId);
        ticket.setLigneId(ligneId);
        ticket.setModePaiement(modePaiement);
        ticket.setMontant(montant);
        ticket.setStatut(TicketStatut.ACTIF);
        ticket.setDateAchat(LocalDateTime.now());
        ticket.setDateExpiration(LocalDateTime.now().plusHours(2));
        ticket.setCodeQR(UUID.randomUUID().toString()
            .replace("-", "").toUpperCase().substring(0, 12));

        return ticketRepository.save(ticket);
    }
}