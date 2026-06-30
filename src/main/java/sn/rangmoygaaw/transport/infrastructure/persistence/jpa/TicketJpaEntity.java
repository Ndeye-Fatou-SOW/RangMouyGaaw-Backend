package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import sn.rangmoygaaw.transport.domain.model.ModePaiement;
import sn.rangmoygaaw.transport.domain.model.TicketStatut;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket")
@Getter @Setter @NoArgsConstructor
public class TicketJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code_qr", nullable = false, unique = true, length = 64)
    private String codeQR;

    @Column(name = "citoyen_id", nullable = false)
    private UUID citoyenId;

    @Column(name = "ligne_id", nullable = false)
    private UUID ligneId;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false, length = 20)
    private ModePaiement modePaiement;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatut statut;

    @Column(name = "date_achat", nullable = false)
    private LocalDateTime dateAchat;

    @Column(name = "date_expiration", nullable = false)
    private LocalDateTime dateExpiration;

    @Column(name = "date_utilisation")
    private LocalDateTime dateUtilisation;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;
}