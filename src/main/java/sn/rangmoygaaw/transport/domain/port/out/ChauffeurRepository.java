package sn.rangmoygaaw.transport.domain.port.out;

import sn.rangmoygaaw.transport.domain.model.Chauffeur;
import sn.rangmoygaaw.transport.domain.model.ChauffeurStatut;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChauffeurRepository {
    Chauffeur save(Chauffeur chauffeur);
    Optional<Chauffeur> findById(UUID id);
    List<Chauffeur> findAll();
    List<Chauffeur> findByStatut(ChauffeurStatut statut);
    void deleteById(UUID id);
}