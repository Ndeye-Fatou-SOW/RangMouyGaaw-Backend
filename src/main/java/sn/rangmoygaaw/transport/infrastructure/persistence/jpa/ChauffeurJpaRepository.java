package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.rangmoygaaw.transport.domain.model.ChauffeurStatut;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChauffeurJpaRepository
        extends JpaRepository<ChauffeurJpaEntity, UUID> {
    List<ChauffeurJpaEntity> findByStatut(ChauffeurStatut statut);
}