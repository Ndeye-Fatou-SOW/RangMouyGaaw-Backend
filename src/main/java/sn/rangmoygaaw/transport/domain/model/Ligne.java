package sn.rangmoygaaw.transport.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ligne {
    private UUID id;
    private String numero;
    private String nom;
    private String depart;
    private String arrivee;
    private BigDecimal tarif;
    private Integer dureeMinutes;
    private LigneStatut statut;
    private List<UUID> arretIds;
}