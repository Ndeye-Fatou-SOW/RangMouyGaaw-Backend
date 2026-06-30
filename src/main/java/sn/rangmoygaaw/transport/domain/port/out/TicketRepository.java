package sn.rangmoygaaw.transport.domain.port.out;

import sn.rangmoygaaw.transport.domain.model.Ticket;
import sn.rangmoygaaw.transport.domain.model.TicketStatut;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {

    Ticket save(Ticket ticket);

    Optional<Ticket> findById(UUID id);

    Optional<Ticket> findByCodeQR(String codeQR);

    List<Ticket> findByCitoyenId(UUID citoyenId);

    List<Ticket> findByStatut(TicketStatut statut);
}
