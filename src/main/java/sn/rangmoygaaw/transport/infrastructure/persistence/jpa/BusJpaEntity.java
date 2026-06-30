package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import sn.rangmoygaaw.transport.domain.model.BusStatut;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bus")
@Getter @Setter @NoArgsConstructor
public class BusJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    
    @Version  // ← essentiel pour le verrouillage optimiste
    private Long version;

    @Column(nullable = false, unique = true, length = 20)
    private String immatriculation;

    @Column(nullable = false, length = 100)
    private String modele;

    @Column(nullable = false)
    private Integer capacite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BusStatut statut = BusStatut.EN_DEPOT;

    @Column(name = "score_fiabilite")
    private Double scoreFilabilite;

    @Column(nullable = false)
    private Long kilometrage = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }
}