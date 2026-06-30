package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.model.Ligne;
import sn.rangmoygaaw.transport.domain.model.LigneStatut;
import sn.rangmoygaaw.transport.domain.port.out.LigneRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PostgreSQLLigneAdapter implements LigneRepository {

    private final LigneJpaRepository jpaRepository;

    public PostgreSQLLigneAdapter(LigneJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Ligne save(Ligne ligne) {
        return toDomain(jpaRepository.save(toEntity(ligne)));
    }

    @Override
    public Optional<Ligne> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Ligne> findAll() {
        return jpaRepository.findAll()
            .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Ligne> findByStatut(LigneStatut statut) {
        return jpaRepository.findByStatut(statut)
            .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private LigneJpaEntity toEntity(Ligne l) {
        LigneJpaEntity e = new LigneJpaEntity();
        e.setId(l.getId());
        e.setNumero(l.getNumero());
        e.setNom(l.getNom());
        e.setDepart(l.getDepart());
        e.setArrivee(l.getArrivee());
        e.setTarif(l.getTarif());
        e.setDureeMinutes(l.getDureeMinutes());
        e.setStatut(l.getStatut());
        return e;
    }

    private Ligne toDomain(LigneJpaEntity e) {
        Ligne l = new Ligne();
        l.setId(e.getId());
        l.setNumero(e.getNumero());
        l.setNom(e.getNom());
        l.setDepart(e.getDepart());
        l.setArrivee(e.getArrivee());
        l.setTarif(e.getTarif());
        l.setDureeMinutes(e.getDureeMinutes());
        l.setStatut(e.getStatut());
        return l;
    }
}
