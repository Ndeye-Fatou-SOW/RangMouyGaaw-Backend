package sn.rangmoygaaw.transport.domain.port.out;

import sn.rangmoygaaw.transport.domain.model.Incident;
import sn.rangmoygaaw.transport.domain.model.IncidentStatut;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncidentRepository {
    Incident save(Incident incident);
    Optional<Incident> findById(UUID id);
    List<Incident> findAll();
    List<Incident> findByStatut(IncidentStatut statut);
    List<Incident> findByBusId(UUID busId);
    List<Incident> findByLigneId(UUID ligneId);
    void deleteById(UUID id);
}