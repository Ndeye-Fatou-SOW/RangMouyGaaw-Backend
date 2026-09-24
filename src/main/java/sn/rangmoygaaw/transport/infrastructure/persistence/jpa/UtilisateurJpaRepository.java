package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UtilisateurJpaRepository extends JpaRepository<UtilisateurJpaEntity, UUID> {
    Optional<UtilisateurJpaEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}