package sn.rangmoygaaw.transport.domain.port.out;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaiementPort {
    String initierPaiement(UUID citoyenId,
                            BigDecimal montant,
                            String telephone);
    boolean verifierPaiement(String transactionId);
}
 