package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArretJpaRepository
        extends JpaRepository<ArretJpaEntity, UUID> {
    List<ArretJpaEntity> findByCommune(String commune);
}