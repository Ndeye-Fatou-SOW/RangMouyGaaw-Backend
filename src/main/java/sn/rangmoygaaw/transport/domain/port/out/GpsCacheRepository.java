package sn.rangmoygaaw.transport.domain.port.out;

import sn.rangmoygaaw.transport.domain.valueobject.PositionGPS;
import java.util.Optional;
import java.util.UUID;

// Port pour Redis — positions GPS temps réel
public interface GpsCacheRepository {

    void savePosition(UUID busId, PositionGPS position);

    Optional<PositionGPS> getPosition(UUID busId);

    void removePosition(UUID busId);
}
