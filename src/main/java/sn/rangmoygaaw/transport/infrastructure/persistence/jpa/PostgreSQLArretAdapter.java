package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.model.Arret;
import sn.rangmoygaaw.transport.domain.port.out.ArretRepository;
import sn.rangmoygaaw.transport.domain.valueobject.PositionGPS;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PostgreSQLArretAdapter implements ArretRepository {

    private final ArretJpaRepository jpaRepository;

    public PostgreSQLArretAdapter(ArretJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Arret save(Arret arret) {
        return toDomain(jpaRepository.save(toEntity(arret)));
    }

    @Override
    public Optional<Arret> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Arret> findAll() {
        return jpaRepository.findAll()
            .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Arret> findByCommune(String commune) {
        return jpaRepository.findByCommune(commune)
            .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ArretJpaEntity toEntity(Arret a) {
        ArretJpaEntity e = new ArretJpaEntity();
        e.setId(a.getId());
        e.setNom(a.getNom());
        e.setCommune(a.getCommune());
        if (a.getPosition() != null) {
            e.setLatitude(a.getPosition().getLatitude());
            e.setLongitude(a.getPosition().getLongitude());
        }
        e.setAccessible(a.getAccessible());
        return e;
    }

    private Arret toDomain(ArretJpaEntity e) {
        Arret a = new Arret();
        a.setId(e.getId());
        a.setNom(e.getNom());
        a.setCommune(e.getCommune());
        if (e.getLatitude() != null && e.getLongitude() != null) {
            a.setPosition(new PositionGPS(
                e.getLatitude(), e.getLongitude(),
                System.currentTimeMillis()));
        }
        a.setAccessible(e.getAccessible());
        return a;
    }
}
