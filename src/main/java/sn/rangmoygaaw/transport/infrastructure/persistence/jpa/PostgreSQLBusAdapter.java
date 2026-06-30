package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.model.Bus;
import sn.rangmoygaaw.transport.domain.model.BusStatut;
import sn.rangmoygaaw.transport.domain.port.out.BusRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component  // ← Spring voit cet adaptateur comme implémentation de BusRepository
public class PostgreSQLBusAdapter implements BusRepository {

    private final BusJpaRepository jpaRepository;

    public PostgreSQLBusAdapter(BusJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    
    

//    @Override
//    public Bus save(Bus bus) {
//        BusJpaEntity entity = toEntity(bus);
//        return toDomain(jpaRepository.save(entity));
//    }
    
    
    
    @Override
    public Bus save(Bus bus) {
        BusJpaEntity entity;
        
        if (bus.getId() != null && jpaRepository.existsById(bus.getId())) {
            // Mise à jour : on part de l'entité managée en base
            entity = jpaRepository.findById(bus.getId())
                    .orElseThrow(() -> new RuntimeException("Bus introuvable : " + bus.getId()));
            // On applique les modifications sur l'entité déjà managée
            entity.setImmatriculation(bus.getImmatriculation());
            entity.setModele(bus.getModele());
            entity.setCapacite(bus.getCapacite());
            entity.setStatut(bus.getStatut());
            entity.setScoreFilabilite(bus.getScoreFilabilite());
            entity.setKilometrage(bus.getKilometrage() != null ? bus.getKilometrage() : 0L);
        } else {
            // Création : nouvelle entité
            entity = toEntity(bus);
        }
        
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Bus> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Bus> findAll() {
        return jpaRepository.findAll()
                .stream().map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Bus> findByStatut(BusStatut statut) {
        return jpaRepository.findByStatut(statut)
                .stream().map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    // ── Mapping domaine → JPA ──────────────────────────────────
    private BusJpaEntity toEntity(Bus bus) {
        BusJpaEntity e = new BusJpaEntity();
        // ⚠️ Ne pas setter l'ID ici — Hibernate le génère via @GeneratedValue
        e.setImmatriculation(bus.getImmatriculation());
        e.setModele(bus.getModele());
        e.setCapacite(bus.getCapacite());
        e.setStatut(bus.getStatut() != null ? bus.getStatut() : BusStatut.EN_DEPOT);
        e.setScoreFilabilite(bus.getScoreFilabilite());
        e.setKilometrage(bus.getKilometrage() != null ? bus.getKilometrage() : 0L);
        return e;
    }

    // ── Mapping JPA → domaine ──────────────────────────────────
    private Bus toDomain(BusJpaEntity e) {
        Bus bus = new Bus();
        bus.setId(e.getId());          // ← L'ID vient de la base, pas du domaine
        bus.setImmatriculation(e.getImmatriculation());
        bus.setModele(e.getModele());
        bus.setCapacite(e.getCapacite());
        bus.setStatut(e.getStatut());
        bus.setScoreFilabilite(e.getScoreFilabilite());
        bus.setKilometrage(e.getKilometrage());
        return bus;
    }
}