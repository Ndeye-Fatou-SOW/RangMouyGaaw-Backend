package sn.rangmoygaaw.transport.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chauffeur {
    private UUID id;
    private String prenom;
    private String nom;
    private String telephone;
    private String numeroPermis;
    private ChauffeurStatut statut;
    private Double scoreConduite;
}