package sn.rangmoygaaw.transport.infrastructure.messaging.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.event.BusPositionMiseAJour;
import sn.rangmoygaaw.transport.domain.port.out.GpsCacheRepository;
import sn.rangmoygaaw.transport.domain.valueobject.PositionGPS;

@Component
public class GpsEventConsumer {

    private static final Logger log =
        LoggerFactory.getLogger(GpsEventConsumer.class);

    private final GpsCacheRepository gpsCacheRepository;

    public GpsEventConsumer(GpsCacheRepository gpsCacheRepository) {
        this.gpsCacheRepository = gpsCacheRepository;
    }

    @KafkaListener(topics = "bus-positions",
                   groupId = "transport-service",
                   containerFactory = "kafkaListenerContainerFactory")
    public void recevoirPosition(BusPositionMiseAJour event) {
        log.info("GPS recu — bus:{} lat:{} lon:{}",
            event.getBusId(), event.getLatitude(), event.getLongitude());

        PositionGPS pos = new PositionGPS(
            event.getLatitude(),
            event.getLongitude(),
            System.currentTimeMillis()
        );
        gpsCacheRepository.savePosition(event.getBusId(), pos);
    }
}