package sn.rangmoygaaw.transport.domain.model;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
@Getter 
@Setter
public class Bus {
    private UUID id;
    private String immatriculation;
    private String modele;
    private Integer capacite;
    private BusStatut statut;
    private Double scoreFilabilite;
    private Long kilometrage;

    public Bus() {}

    public Bus(UUID id, String immatriculation,
               String modele, Integer capacite) {
        this.id = id;
        this.immatriculation = immatriculation;
        this.modele = modele;
        this.capacite = capacite;
        this.statut = BusStatut.EN_DEPOT;
    }

    
}
