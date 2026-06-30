package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;
import sn.rangmoygaaw.transport.domain.model.ChauffeurStatut;
import java.util.UUID;

@Entity
@Table(name = "chauffeurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChauffeurJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String prenom;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(length = 20)
    private String telephone;

    @Column(name = "numero_permis", length = 50)
    private String numeroPermis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChauffeurStatut statut = ChauffeurStatut.EN_REPOS;

    @Column(name = "score_conduite")
    private Double scoreConduite;
}