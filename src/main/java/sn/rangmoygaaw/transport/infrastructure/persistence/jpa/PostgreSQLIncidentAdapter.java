package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.model.Incident;
import sn.rangmoygaaw.transport.domain.model.IncidentStatut;
import sn.rangmoygaaw.transport.domain.port.out.IncidentRepository;
import sn.rangmoygaaw.transport.domain.valueobject.PositionGPS;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PostgreSQLIncidentAdapter implements IncidentRepository {

    private final IncidentJpaRepository jpaRepository;

    public PostgreSQLIncidentAdapter(IncidentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Incident save(Incident incident) {
        return toDomain(jpaRepository.save(toEntity(incident)));
    }

    @Override
    public Optional<Incident> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Incident> findAll() {
        return jpaRepository.findAll()
            .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Incident> findByStatut(IncidentStatut statut) {
        return jpaRepository.findByStatut(statut)
            .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Incident> findByBusId(UUID busId) {
        return jpaRepository.findByBusId(busId)
            .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Incident> findByLigneId(UUID ligneId) {
        return jpaRepository.findByLigneId(ligneId)
            .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private IncidentJpaEntity toEntity(Incident i) {
        IncidentJpaEntity e = new IncidentJpaEntity();
        e.setId(i.getId());
        e.setBusId(i.getBusId());
        e.setLigneId(i.getLigneId());
        e.setType(i.getType());
        e.setDescription(i.getDescription());
        if (i.getPosition() != null) {
            e.setLatitude(i.getPosition().getLatitude());
            e.setLongitude(i.getPosition().getLongitude());
        }
        e.setStatut(i.getStatut());
        e.setDateSignalement(i.getDateSignalement());
        e.setDateResolution(i.getDateResolution());
        return e;
    }

    private Incident toDomain(IncidentJpaEntity e) {
        Incident i = new Incident();
        i.setId(e.getId());
        i.setBusId(e.getBusId());
        i.setLigneId(e.getLigneId());
        i.setType(e.getType());
        i.setDescription(e.getDescription());
        if (e.getLatitude() != null && e.getLongitude() != null) {
            i.setPosition(new PositionGPS(
                e.getLatitude(), e.getLongitude(),System.currentTimeMillis()));
        }
        i.setStatut(e.getStatut());
        i.setDateSignalement(e.getDateSignalement());
        i.setDateResolution(e.getDateResolution());
        return i;
    }
}