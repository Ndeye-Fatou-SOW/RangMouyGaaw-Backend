package sn.rangmoygaaw.transport.application.query;

import org.springframework.stereotype.Service;
import sn.rangmoygaaw.transport.domain.port.in.ObtenirPositionBusUseCase;
import sn.rangmoygaaw.transport.domain.port.out.GpsCacheRepository;
import sn.rangmoygaaw.transport.domain.valueobject.PositionGPS;

import java.util.Optional;
import java.util.UUID;

@Service
public class ObtenirPositionBusService implements ObtenirPositionBusUseCase {

    private final GpsCacheRepository gpsCacheRepository;

    // Injection par constructeur (recommandé)
    public ObtenirPositionBusService(GpsCacheRepository gpsCacheRepository) {
        this.gpsCacheRepository = gpsCacheRepository;
    }

    @Override
    public Optional<PositionGPS> obtenirPosition(UUID busId) {
        return gpsCacheRepository.getPosition(busId);
    }
}