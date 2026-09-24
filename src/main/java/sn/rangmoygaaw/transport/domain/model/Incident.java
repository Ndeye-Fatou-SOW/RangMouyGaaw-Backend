package sn.rangmoygaaw.transport.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import sn.rangmoygaaw.transport.domain.valueobject.PositionGPS;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Incident {
    private UUID id;
    private UUID busId;
    private UUID ligneId;
    private TypeIncident type;
    private String description;
    private PositionGPS position;
    private IncidentStatut statut;
    private LocalDateTime dateSignalement;
    private LocalDateTime dateResolution;
    private String signalePar; // username de la personne qui a signalé
}