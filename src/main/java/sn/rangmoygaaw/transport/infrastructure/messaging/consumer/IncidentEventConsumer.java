package sn.rangmoygaaw.transport.infrastructure.messaging.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.event.IncidentSignale;
import sn.rangmoygaaw.transport.domain.model.*;
import sn.rangmoygaaw.transport.domain.port.out.IncidentRepository;
import sn.rangmoygaaw.transport.domain.valueobject.PositionGPS;
import java.time.LocalDateTime;

@Component
public class IncidentEventConsumer {

    private static final Logger log =
        LoggerFactory.getLogger(IncidentEventConsumer.class);

    private final IncidentRepository incidentRepository;

    public IncidentEventConsumer(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @KafkaListener(topics = "incidents", groupId = "transport-service")
    public void recevoirIncident(IncidentSignale event) {
        log.warn("Incident recu — type:{} bus:{}",
            event.getType(), event.getBusId());

        Incident incident = new Incident();
        incident.setId(event.getIncidentId());
        incident.setBusId(event.getBusId());
        incident.setLigneId(event.getLigneId());
        incident.setType(event.getType());
        incident.setDescription(event.getDescription());
        incident.setPosition(new PositionGPS(
            event.getLatitude(), event.getLongitude(),
            System.currentTimeMillis()));
        incident.setStatut(IncidentStatut.SIGNALE);
        incident.setDateSignalement(LocalDateTime.now());

        incidentRepository.save(incident);
    }
}