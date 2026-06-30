package sn.rangmoygaaw.transport.infrastructure.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.port.out.NotificationPort;

@Component("orangeSmsNotification")
public class OrangeSmsAdapter implements NotificationPort {

    private static final Logger log =
        LoggerFactory.getLogger(OrangeSmsAdapter.class);

    @Value("${orange.sms.api.key:orange-sms-test-key}")
    private String apiKey;

    @Override
    public void envoyerPushNotification(String deviceToken,
            String titre, String message) {
        log.info("Push non supporté — utilise Firebase");
    }

    @Override
    public void envoyerSms(String telephone, String message) {
        log.info("Orange SMS → {}: {}...", telephone,
            message.substring(0, Math.min(30, message.length())));
        // TODO : POST https://api.orange.com/smsmessaging/v1/...
        // Header: Authorization: Bearer {apiKey}
    }
}