package sn.rangmoygaaw.transport.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component("payTechPaiement")
public class PayTechAdapter {

    private static final Logger log =
        LoggerFactory.getLogger(PayTechAdapter.class);

    private static final String PAYTECH_URL =
        "https://paytech.sn/api/payment/request-payment";

    @Value("${paytech.api.key}")
    private String apiKey;

    @Value("${paytech.api.secret}")
    private String apiSecret;

    @Value("${paytech.ipn.url}")
    private String ipnUrl;

    @Value("${paytech.success.url}")
    private String successUrl;

    @Value("${paytech.cancel.url}")
    private String cancelUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PayTechAdapter() {
        RestTemplate rt = new RestTemplate();
        // Accepte application/octet-stream (réponse PayTech)
        for (HttpMessageConverter<?> converter : rt.getMessageConverters()) {
            if (converter instanceof MappingJackson2HttpMessageConverter c) {
                List<MediaType> types = new ArrayList<>(
                    c.getSupportedMediaTypes());
                types.add(MediaType.APPLICATION_OCTET_STREAM);
                c.setSupportedMediaTypes(types);
            }
        }
        this.restTemplate = rt;
    }

    /**
     * Vérifie qu'un rappel provient bien de PayTech.
     *
     * PayTech joint au payload les empreintes SHA-256 de la clé et du secret
     * du marchand ; seul quelqu'un connaissant ces valeurs peut les produire.
     * Sans ce contrôle, l'endpoint IPN étant public, n'importe qui pourrait
     * activer un ticket en envoyant une simple requête.
     *
     * @see <a href="https://docs.intech.sn/doc_paytech.php">Documentation PayTech</a>
     */
    public boolean signatureValide(Map<String, Object> payload) {
        if (payload == null) {
            return false;
        }

        Object cleRecue    = payload.get("api_key_sha256");
        Object secretRecu  = payload.get("api_secret_sha256");

        if (cleRecue == null || secretRecu == null) {
            log.warn("Rappel PayTech sans empreintes api_key_sha256/api_secret_sha256");
            return false;
        }

        // Comparaison à temps constant : une comparaison naïve permettrait de
        // deviner l'empreinte caractère par caractère en mesurant le temps.
        boolean cleOk = MessageDigest.isEqual(
            sha256(apiKey).getBytes(StandardCharsets.UTF_8),
            cleRecue.toString().trim().getBytes(StandardCharsets.UTF_8));

        boolean secretOk = MessageDigest.isEqual(
            sha256(apiSecret).getBytes(StandardCharsets.UTF_8),
            secretRecu.toString().trim().getBytes(StandardCharsets.UTF_8));

        return cleOk && secretOk;
    }

    private String sha256(String valeur) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] empreinte = digest.digest(valeur.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexa = new StringBuilder(empreinte.length * 2);
            for (byte b : empreinte) {
                hexa.append(Character.forDigit((b >> 4) & 0xF, 16));
                hexa.append(Character.forDigit(b & 0xF, 16));
            }
            return hexa.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 fait partie des algorithmes obligatoires de la JVM
            throw new IllegalStateException("SHA-256 indisponible", e);
        }
    }

    public Map<String, Object> initierPaiement(
            UUID ticketId,
            BigDecimal montant,
            String telephone,
            String ligneNom) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("API_KEY", apiKey);
        headers.set("API_SECRET", apiSecret);

        String customField = "{}";
        try {
            customField = objectMapper.writeValueAsString(Map.of(
                "ticket_id", ticketId.toString(),
                "telephone", telephone
            ));
        } catch (Exception e) {
            log.warn("Erreur custom_field: {}", e.getMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("item_name", "Ticket Bus " + ligneNom);
        body.put("item_price", montant.intValue());
        body.put("currency", "XOF");
        body.put("ref_command", ticketId.toString() + "-"
            + System.currentTimeMillis());
        body.put("command_name", "Achat Ticket Rang Moy Gaaw");
        body.put("env", "test");
        body.put("ipn_url", ipnUrl);
        body.put("success_url", successUrl);
        body.put("cancel_url", cancelUrl);
        body.put("custom_field", customField);

        HttpEntity<Map<String, Object>> request =
            new HttpEntity<>(body, headers);

        try {
            // ← Lis la réponse comme String d'abord
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                PAYTECH_URL,
                HttpMethod.POST,
                request,
                String.class
            );

            String responseBody = responseEntity.getBody();
            log.info("PayTech réponse brute: {}", responseBody);

            // ← Parse le JSON string manuellement
            Map<String, Object> response = objectMapper.readValue(
                responseBody, Map.class);
            log.info("PayTech réponse parsée: {}", response);
            return response;

        } catch (Exception e) {
            log.error("Erreur PayTech: {}", e.getMessage());
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("error", e.getMessage());
            fallback.put("redirect_url", "");
            return fallback;
        }
    }
}