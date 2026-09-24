package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;
import sn.rangmoygaaw.transport.domain.model.IncidentStatut;
import sn.rangmoygaaw.transport.domain.model.TypeIncident;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "incidents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentJpaEntity {

    @Id
    private UUID id;

    private UUID busId;

    private UUID ligneId;

    @Enumerated(EnumType.STRING)
    private TypeIncident type;

    @Column(length = 2000)
    private String description;
    @Column(name = "signale_par")
    private String signalePar;

    private Double latitude;

    private Double longitude;
    private Long positionTimestamp;

    @Enumerated(EnumType.STRING)
    private IncidentStatut statut;

    private LocalDateTime dateSignalement;

    private LocalDateTime dateResolution;
}