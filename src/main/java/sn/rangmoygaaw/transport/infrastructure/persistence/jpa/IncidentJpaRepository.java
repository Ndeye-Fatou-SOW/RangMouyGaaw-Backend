package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.rangmoygaaw.transport.domain.model.IncidentStatut;

import java.util.List;
import java.util.UUID;

@Repository
public interface IncidentJpaRepository
        extends JpaRepository<IncidentJpaEntity, UUID> {
    List<IncidentJpaEntity> findByStatut(IncidentStatut statut);
    List<IncidentJpaEntity> findByBusId(UUID busId);
    List<IncidentJpaEntity> findByLigneId(UUID ligneId);
}