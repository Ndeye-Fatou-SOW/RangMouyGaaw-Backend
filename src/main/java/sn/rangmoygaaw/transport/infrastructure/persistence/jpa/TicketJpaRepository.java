package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.rangmoygaaw.transport.domain.model.TicketStatut;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketJpaRepository
        extends JpaRepository<TicketJpaEntity, UUID> {

    Optional<TicketJpaEntity> findByCodeQR(String codeQR);

    List<TicketJpaEntity> findByCitoyenId(UUID citoyenId);

    List<TicketJpaEntity> findByStatut(TicketStatut statut);

    List<TicketJpaEntity> findByCitoyenIdAndStatut(UUID citoyenId,
                                                    TicketStatut statut);
}