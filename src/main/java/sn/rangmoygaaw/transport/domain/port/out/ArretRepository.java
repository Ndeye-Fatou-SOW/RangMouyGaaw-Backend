package sn.rangmoygaaw.transport.domain.port.out;

import sn.rangmoygaaw.transport.domain.model.Arret;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArretRepository {
    Arret save(Arret arret);
    Optional<Arret> findById(UUID id);
    List<Arret> findAll();
    List<Arret> findByCommune(String commune);
    void deleteById(UUID id);
}