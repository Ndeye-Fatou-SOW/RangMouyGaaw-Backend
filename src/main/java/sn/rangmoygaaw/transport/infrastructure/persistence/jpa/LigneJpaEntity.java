package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;
import sn.rangmoygaaw.transport.domain.model.LigneStatut;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "lignes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String depart;

    @Column(nullable = false, length = 100)
    private String arrivee;

    @Column(precision = 10, scale = 2)
    private BigDecimal tarif;

    private Integer dureeMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LigneStatut statut = LigneStatut.ACTIVE;
}