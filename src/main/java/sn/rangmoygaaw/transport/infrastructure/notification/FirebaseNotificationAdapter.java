package sn.rangmoygaaw.transport.infrastructure.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.port.out.NotificationPort;

@Component("firebaseNotification")
public class FirebaseNotificationAdapter implements NotificationPort {

    private static final Logger log =
        LoggerFactory.getLogger(FirebaseNotificationAdapter.class);

    @Override
    public void envoyerPushNotification(String deviceToken,
            String titre, String message) {
        log.info("Firebase Push → {}...: {}",
            deviceToken.substring(0, Math.min(10,
                deviceToken.length())), titre);

        // TODO : ajouter dans pom.xml :
        // com.google.firebase:firebase-admin:9.2.0
        //
        // Message msg = Message.builder()
        //   .setNotification(Notification.builder()
        //     .setTitle(titre).setBody(message).build())
        //   .setToken(deviceToken).build();
        // FirebaseMessaging.getInstance().send(msg);
    }

    @Override
    public void envoyerSms(String telephone, String message) {
        log.info("SMS — utilise OrangeSmsAdapter");
    }
}