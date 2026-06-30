package sn.rangmoygaaw.transport.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
@Getter 
@Setter
public class Ticket {
    private UUID id;
    private String codeQR;
    private UUID citoyenId;
    private UUID ligneId;
    private ModePaiement modePaiement;
    private BigDecimal montant;
    private TicketStatut statut;
    private LocalDateTime dateAchat;
    private LocalDateTime dateExpiration;
    private String transactionId;
    @Version
    private Long version;

    public Ticket() {}

    public boolean estValide() {
        return statut == TicketStatut.ACTIF
            && dateExpiration.isAfter(LocalDateTime.now());
    }
 
}