package sn.rangmoygaaw.transport.infrastructure.messaging.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.event.*;

@Component
public class KafkaTransportPublisher {

    private static final Logger log =
        LoggerFactory.getLogger(KafkaTransportPublisher.class);

    private static final String TOPIC_GPS       = "bus-positions";
    private static final String TOPIC_INCIDENTS = "incidents";
    private static final String TOPIC_TICKETS   = "tickets-achetes";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaTransportPublisher(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publierPositionBus(BusPositionMiseAJour event) {
        log.info("Pub GPS bus:{}", event.getBusId());
        kafkaTemplate.send(TOPIC_GPS,
            event.getBusId().toString(), event);
    }

    public void publierIncident(IncidentSignale event) {
        log.warn("Pub Incident type:{}", event.getType());
        kafkaTemplate.send(TOPIC_INCIDENTS,
            event.getIncidentId().toString(), event);
    }

    public void publierTicketAchete(TicketAchete event) {
        log.info("Pub Ticket:{}", event.getTicketId());
        kafkaTemplate.send(TOPIC_TICKETS,
            event.getTicketId().toString(), event);
    }
}