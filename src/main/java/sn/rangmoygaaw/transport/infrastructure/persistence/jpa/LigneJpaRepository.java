package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.rangmoygaaw.transport.domain.model.LigneStatut;

import java.util.List;
import java.util.UUID;

@Repository
public interface LigneJpaRepository
        extends JpaRepository<LigneJpaEntity, UUID> {
    List<LigneJpaEntity> findByStatut(LigneStatut statut);
}