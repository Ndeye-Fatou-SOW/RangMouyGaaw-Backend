//package sn.rangmoygaaw.transport.infrastructure.payment;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import sn.rangmoygaaw.transport.domain.port.out.PaiementPort;
//import java.math.BigDecimal;
//import java.util.UUID;
//
//@Component("orangeMoneyPaiement")
//public class OrangeMoneyAdapter implements PaiementPort {
//
//    private static final Logger log =
//        LoggerFactory.getLogger(OrangeMoneyAdapter.class);
//
//    @Value("${orangemoney.api.key:om-test-key}")
//    private String apiKey;
//
//    @Override
//    public String initierPaiement(UUID citoyenId,
//            BigDecimal montant, String telephone) {
//        log.info("OrangeMoney — paiement {} FCFA pour {}",
//            montant, telephone);
//        // TODO : appel réel API Orange Money
//        String txId = "OM-" +
//            UUID.randomUUID().toString().substring(0,8).toUpperCase();
//        log.info("OrangeMoney — transaction: {}", txId);
//        return txId;
//    }
//
//    @Override
//    public boolean verifierPaiement(String transactionId) {
//        // TODO : vérification réelle
//        return transactionId.startsWith("OM-");
//    }
//}