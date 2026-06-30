package sn.rangmoygaaw.transport.domain.port.in;

import sn.rangmoygaaw.transport.domain.valueobject.PositionGPS;
import java.util.Optional;
import java.util.UUID;

public interface ObtenirPositionBusUseCase {

    Optional<PositionGPS> obtenirPosition(UUID busId);
}