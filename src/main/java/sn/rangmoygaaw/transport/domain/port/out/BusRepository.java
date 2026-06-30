package sn.rangmoygaaw.transport.domain.port.out;

import sn.rangmoygaaw.transport.domain.model.Bus;
import sn.rangmoygaaw.transport.domain.model.BusStatut;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusRepository {

    Bus save(Bus bus);

    Optional<Bus> findById(UUID id);

    List<Bus> findAll();

    List<Bus> findByStatut(BusStatut statut);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}