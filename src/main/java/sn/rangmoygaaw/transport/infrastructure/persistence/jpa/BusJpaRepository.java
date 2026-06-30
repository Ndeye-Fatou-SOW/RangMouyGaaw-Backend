package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.rangmoygaaw.transport.domain.model.BusStatut;

import java.util.List;
import java.util.UUID;

@Repository
public interface BusJpaRepository
        extends JpaRepository<BusJpaEntity, UUID> {

    // Spring génère le SQL automatiquement
    List<BusJpaEntity> findByStatut(BusStatut statut);

    List<BusJpaEntity> findByStatutOrderByImmatriculationAsc(BusStatut statut);

    @Query("SELECT b FROM BusJpaEntity b WHERE b.scoreFilabilite < :seuil")
    List<BusJpaEntity> findBusAFaibleScoreFilabilite(double seuil);

    long countByStatut(BusStatut statut);
}