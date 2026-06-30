package sn.rangmoygaaw.transport.domain.port.out;

import sn.rangmoygaaw.transport.domain.model.Ligne;
import sn.rangmoygaaw.transport.domain.model.LigneStatut;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LigneRepository {
    Ligne save(Ligne ligne);
    Optional<Ligne> findById(UUID id);
    List<Ligne> findAll();
    List<Ligne> findByStatut(LigneStatut statut);
    void deleteById(UUID id);
}