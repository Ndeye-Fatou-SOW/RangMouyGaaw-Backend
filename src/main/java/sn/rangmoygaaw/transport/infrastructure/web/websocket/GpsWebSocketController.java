package sn.rangmoygaaw.transport.infrastructure.web.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.model.Bus;
import sn.rangmoygaaw.transport.domain.port.in.ListerBusActifsUseCase;
import sn.rangmoygaaw.transport.domain.port.out.GpsCacheRepository;
import sn.rangmoygaaw.transport.domain.valueobject.PositionGPS;

import java.util.List;
import java.util.Optional;

@Component
public class GpsWebSocketController {

    private final SimpMessagingTemplate messaging;
    private final ListerBusActifsUseCase listerBusActifs;
    private final GpsCacheRepository gpsCacheRepository;

    public GpsWebSocketController(
            SimpMessagingTemplate messaging,
            ListerBusActifsUseCase listerBusActifs,
            GpsCacheRepository gpsCacheRepository
    ) {
        this.messaging = messaging;
        this.listerBusActifs = listerBusActifs;
        this.gpsCacheRepository = gpsCacheRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void envoyerPositions() {
        List<Bus> busActifs = listerBusActifs.listerBusActifs();

        System.out.println("=== ENVOI POSITIONS GPS ===");
        System.out.println("Nombre de bus actifs trouvés : " + busActifs.size());

        for (Bus bus : busActifs) {
            System.out.println("Bus actif : " + bus.getId() + " / " + bus.getImmatriculation());

            Optional<PositionGPS> posOpt = gpsCacheRepository.getPosition(bus.getId());

            if (posOpt.isPresent()) {
                PositionGPS pos = posOpt.get();

                System.out.println("Position trouvée pour bus " + bus.getId()
                        + " -> lat=" + pos.getLatitude()
                        + ", lon=" + pos.getLongitude()
                        + ", time=" + pos.getTimestamp());

                BusPositionMessage payload = new BusPositionMessage(
                        bus.getId().toString(),
                        bus.getImmatriculation(),
                        pos.getLatitude(),
                        pos.getLongitude(),
                        pos.getTimestamp()
                );

                System.out.println("Envoi STOMP vers /topic/bus-positions : " + payload);

                messaging.convertAndSend("/topic/bus-positions", payload);

            } else {
                System.out.println("AUCUNE position GPS trouvée pour bus " + bus.getId());
            }
        }

        System.out.println("=== FIN ENVOI POSITIONS GPS ===");
    }
}