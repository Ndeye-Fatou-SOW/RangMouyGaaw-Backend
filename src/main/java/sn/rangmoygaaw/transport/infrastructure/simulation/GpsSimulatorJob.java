package sn.rangmoygaaw.transport.infrastructure.simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.event.BusPositionMiseAJour;
import sn.rangmoygaaw.transport.domain.model.Bus;
import sn.rangmoygaaw.transport.domain.port.in.ListerBusActifsUseCase;
import sn.rangmoygaaw.transport.infrastructure.messaging.publisher.KafkaTransportPublisher;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GpsSimulatorJob {

    private static final Logger log = LoggerFactory.getLogger(GpsSimulatorJob.class);

    private static final double DAKAR_LAT = 14.6928;
    private static final double DAKAR_LON = -17.4467;

    private final ListerBusActifsUseCase listerBusActifsUseCase;
    private final KafkaTransportPublisher kafkaTransportPublisher;
    private final boolean simulationEnabled;

    private final Map<UUID, double[]> positionsCourantes = new ConcurrentHashMap<>();

    public GpsSimulatorJob(
            ListerBusActifsUseCase listerBusActifsUseCase,
            KafkaTransportPublisher kafkaTransportPublisher,
            @Value("${transport.gps.simulation.enabled:true}") boolean simulationEnabled) {
        this.listerBusActifsUseCase = listerBusActifsUseCase;
        this.kafkaTransportPublisher = kafkaTransportPublisher;
        this.simulationEnabled = simulationEnabled;
    }

    @Scheduled(fixedDelay = 5000, initialDelay = 3000)
    public void publierPositionsSimulees() {
        if (!simulationEnabled) {
            return;
        }

        List<Bus> busActifs = listerBusActifsUseCase.listerBusActifs();

        if (busActifs.isEmpty()) {
            log.debug("Simulation GPS : aucun bus EN_SERVICE trouvé, rien à publier.");
            return;
        }

        for (Bus bus : busActifs) {
            double[] position = deplacerBus(bus.getId());
            double vitesseSimuleeKmh = 15 + Math.random() * 25;

            BusPositionMiseAJour event = new BusPositionMiseAJour(
                bus.getId(),
                position[0],
                position[1],
                vitesseSimuleeKmh
            );

            kafkaTransportPublisher.publierPositionBus(event);
        }

        log.debug("Simulation GPS : {} position(s) publiée(s) sur Kafka.", busActifs.size());
    }

    private double[] deplacerBus(UUID busId) {
        double[] courante = positionsCourantes.computeIfAbsent(busId, id -> new double[]{
            DAKAR_LAT + (Math.random() - 0.5) * 0.08,
            DAKAR_LON + (Math.random() - 0.5) * 0.08
        });

        double nouvelleLat = courante[0] + (Math.random() - 0.5) * 0.0015;
        double nouvelleLon = courante[1] + (Math.random() - 0.5) * 0.0015;

        double[] nouvellePosition = {nouvelleLat, nouvelleLon};
        positionsCourantes.put(busId, nouvellePosition);
        return nouvellePosition;
    }
}