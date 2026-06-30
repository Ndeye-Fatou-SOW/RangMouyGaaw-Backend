package sn.rangmoygaaw.transport.domain.event;

import sn.rangmoygaaw.transport.domain.model.ModePaiement;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TicketAchete {
    private UUID ticketId;
    private UUID citoyenId;
    private UUID ligneId;
    private BigDecimal montant;
    private ModePaiement modePaiement;
    private String codeQR;
    private LocalDateTime timestamp;

    public TicketAchete() {}

    public TicketAchete(UUID ticketId, UUID citoyenId, UUID ligneId,
            BigDecimal montant, ModePaiement modePaiement, String codeQR) {
        this.ticketId     = ticketId;
        this.citoyenId    = citoyenId;
        this.ligneId      = ligneId;
        this.montant      = montant;
        this.modePaiement = modePaiement;
        this.codeQR       = codeQR;
        this.timestamp    = LocalDateTime.now();
    }

    public UUID getTicketId()              { return ticketId; }
    public void setTicketId(UUID v)        { this.ticketId = v; }
    public UUID getCitoyenId()             { return citoyenId; }
    public void setCitoyenId(UUID v)       { this.citoyenId = v; }
    public UUID getLigneId()               { return ligneId; }
    public void setLigneId(UUID v)         { this.ligneId = v; }
    public BigDecimal getMontant()         { return montant; }
    public void setMontant(BigDecimal v)   { this.montant = v; }
    public ModePaiement getModePaiement()  { return modePaiement; }
    public void setModePaiement(ModePaiement v){ this.modePaiement = v; }
    public String getCodeQR()              { return codeQR; }
    public void setCodeQR(String v)        { this.codeQR = v; }
    public LocalDateTime getTimestamp()    { return timestamp; }
    public void setTimestamp(LocalDateTime v){ this.timestamp = v; }
}