package sn.rangmoygaaw.transport.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import sn.rangmoygaaw.transport.domain.valueobject.PositionGPS;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Arret {
    private UUID id;
    private String nom;
    private String commune;
    private PositionGPS position;
    private Boolean accessible;
}