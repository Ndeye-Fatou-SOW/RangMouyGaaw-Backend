package sn.rangmoygaaw.transport.domain.port.in;

import sn.rangmoygaaw.transport.domain.model.ModePaiement;
import sn.rangmoygaaw.transport.domain.model.Ticket;
import java.util.UUID;

public interface AcheterTicketUseCase {

    Ticket acheter(UUID citoyenId, UUID ligneId, ModePaiement modePaiement);
}