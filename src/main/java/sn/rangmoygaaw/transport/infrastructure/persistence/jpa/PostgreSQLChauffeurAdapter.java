package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.model.Chauffeur;
import sn.rangmoygaaw.transport.domain.model.ChauffeurStatut;
import sn.rangmoygaaw.transport.domain.port.out.ChauffeurRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PostgreSQLChauffeurAdapter implements ChauffeurRepository {

    private final ChauffeurJpaRepository jpaRepository;

    public PostgreSQLChauffeurAdapter(ChauffeurJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Chauffeur save(Chauffeur chauffeur) {
        return toDomain(jpaRepository.save(toEntity(chauffeur)));
    }

    @Override
    public Optional<Chauffeur> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Chauffeur> findAll() {
        return jpaRepository.findAll()
            .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Chauffeur> findByStatut(ChauffeurStatut statut) {
        return jpaRepository.findByStatut(statut)
            .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ChauffeurJpaEntity toEntity(Chauffeur c) {
        ChauffeurJpaEntity e = new ChauffeurJpaEntity();
        e.setId(c.getId());
        e.setPrenom(c.getPrenom());
        e.setNom(c.getNom());
        e.setTelephone(c.getTelephone());
        e.setNumeroPermis(c.getNumeroPermis());
        e.setStatut(c.getStatut());
        e.setScoreConduite(c.getScoreConduite());
        return e;
    }

    private Chauffeur toDomain(ChauffeurJpaEntity e) {
        Chauffeur c = new Chauffeur();
        c.setId(e.getId());
        c.setPrenom(e.getPrenom());
        c.setNom(e.getNom());
        c.setTelephone(e.getTelephone());
        c.setNumeroPermis(e.getNumeroPermis());
        c.setStatut(e.getStatut());
        c.setScoreConduite(e.getScoreConduite());
        return c;
    }
}