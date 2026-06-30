package sn.rangmoygaaw.transport.domain.port.out;

public interface NotificationPort {
    void envoyerPushNotification(String deviceToken,
                                  String titre,
                                  String message);
    void envoyerSms(String telephone, String message);
}
 