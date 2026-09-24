package sn.rangmoygaaw.transport.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import sn.rangmoygaaw.transport.domain.model.Ligne;
import sn.rangmoygaaw.transport.domain.port.out.LigneRepository;

import java.time.Duration;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TransportAiService {

    private static final Logger log = LoggerFactory.getLogger(TransportAiService.class);

    // Endpoint Gemini 2.5 Flash — meilleure compréhension du wolof que les
    // modèles concurrents, cf. cahier des charges section 3.1 (FR/Wolof)
    private static final String GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String modele;

    @Value("${gemini.tts.model:gemini-2.5-flash-preview-tts}")
    private String modeleTts;

    @Value("${gemini.tts.voice:Kore}")
    private String voixTts;

    private final LigneRepository ligneRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TransportAiService(LigneRepository ligneRepository) {
        this.ligneRepository = ligneRepository;

        // Timeouts explicites : sans eux, un reseau degrade bloque un thread
        // Tomcat pendant des dizaines de secondes et l'utilisateur voit le
        // bouton tourner sans fin. Ici l'echec est net et rapide : le frontend
        // bascule alors sur la voix du navigateur.
        SimpleClientHttpRequestFactory fabrique = new SimpleClientHttpRequestFactory();
        fabrique.setConnectTimeout(Duration.ofSeconds(5));
        fabrique.setReadTimeout(Duration.ofSeconds(20));
        this.restTemplate = new RestTemplate(fabrique);
    }

    /** L'assistant n'est utilisable que si une clé Gemini est configurée. */
    private boolean cleAbsente() {
        return apiKey == null || apiKey.isBlank();
    }

    // L'utilisateur pose une question sur le transport (français ou wolof)
    public String repondre(String questionUtilisateur) {

        if (cleAbsente()) {
            log.warn("GEMINI_API_KEY absente : l'assistant Dama dem est désactivé.");
            return "L'assistant n'est pas disponible pour le moment "
                 + "(clé d'API non configurée).";
        }

        // 1. RAG : on récupère le contexte depuis la base
        String contexte = construireContexte();

        // 2. On construit le prompt avec le contexte
        String prompt = """
            Tu es Dama dem, l'assistant transport intelligent de Dakar.
            Tu aides les citoyens à trouver les meilleures lignes de bus,
            horaires et itinéraires à Dakar, Sénégal.
            Tu comprends et réponds aussi bien en français qu'en wolof,
            selon la langue utilisée par le citoyen dans sa question.
            Réponds toujours de façon claire et concise.

            Voici les informations disponibles sur les lignes actuelles :
            %s

            Question du citoyen : %s
            """.formatted(contexte, questionUtilisateur);

        // 3. Appel à l'API Gemini
        try {
            return appelerGemini(prompt);
        } catch (Exception e) {
            log.error("Erreur lors de l'appel à Gemini", e);
            return "Désolé, je rencontre une difficulté technique. Réessayez dans un instant.";
        }
    }

    private String appelerGemini(String prompt) {
        String url = GEMINI_URL.formatted(modele, apiKey);

        Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", prompt)
                ))
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requete = new HttpEntity<>(body, headers);

        String reponseBrute = postAvecRetry(url, requete);

        try {
            JsonNode racine = objectMapper.readTree(reponseBrute);
            return racine
                .path("candidates").path(0)
                .path("content").path("parts").path(0)
                .path("text").asText("Je n'ai pas pu générer de réponse.");
        } catch (Exception e) {
            log.error("Erreur de parsing de la réponse Gemini : {}", reponseBrute, e);
            return "Désolé, je n'ai pas pu traiter la réponse. Réessayez.";
        }
    }

    // Question posée à l'oral (audio brut envoyé à Gemini, qui transcrit
    // ET répond en une seule requête — gère nativement le wolof, contrairement
    // à la reconnaissance vocale du navigateur qui ne le supporte pas)
    public Map<String, String> repondreAudio(byte[] audioBytes, String mimeType) {
        if (cleAbsente()) {
            log.warn("GEMINI_API_KEY absente : question vocale ignorée.");
            return Map.of("transcription", "",
                "reponse", "L'assistant n'est pas disponible pour le moment "
                         + "(clé d'API non configurée).");
        }

        String contexte = construireContexte();
        String audioBase64 = Base64.getEncoder().encodeToString(audioBytes);

        String instructions = """
            Tu es Dama dem, l'assistant transport intelligent de Dakar.
            L'audio ci-joint contient une question posée par un citoyen,
            en français ou en wolof.

            Voici les informations disponibles sur les lignes actuelles :
            %s

            Fais deux choses :
            1. Transcris fidèlement la question posée dans l'audio (dans la
               langue d'origine, français ou wolof).
            2. Réponds à cette question de façon claire et concise, dans la
               même langue que la question.

            Réponds UNIQUEMENT avec un objet JSON valide, sans texte autour,
            sans balises markdown, au format exact suivant :
            {"transcription": "...", "reponse": "..."}
            """.formatted(contexte);

        try {
            String reponseBrute = appelerGeminiAvecAudio(instructions, audioBase64, mimeType);
            return parserReponseAudio(reponseBrute);
        } catch (Exception e) {
            log.error("Erreur lors de l'appel audio à Gemini", e);
            return Map.of(
                "transcription", "",
                "reponse", "Désolé, je n'ai pas pu traiter votre message vocal."
            );
        }
    }

    private String appelerGeminiAvecAudio(String instructions, String audioBase64, String mimeType) {
        String url = GEMINI_URL.formatted(modele, apiKey);

        String typeAudio = (mimeType != null && !mimeType.isBlank())
            ? mimeType : "audio/webm";

        Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", instructions),
                    Map.of("inline_data", Map.of(
                        "mime_type", typeAudio,
                        "data", audioBase64
                    ))
                ))
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requete = new HttpEntity<>(body, headers);

        return postAvecRetry(url, requete);
    }

    // Réessaie automatiquement en cas de surcharge temporaire de Gemini
    // (503 Service Unavailable, 429 Too Many Requests) — 2 tentatives max,
    // avec un petit délai entre chaque essai.
    private String postAvecRetry(String url, HttpEntity<Map<String, Object>> requete) {
        int maxTentatives = 3;
        long delaiMs = 2000;

        for (int tentative = 1; tentative <= maxTentatives; tentative++) {
            try {
                return restTemplate.postForObject(url, requete, String.class);
            } catch (HttpServerErrorException.ServiceUnavailable | HttpClientErrorException.TooManyRequests e) {
                boolean derniereTentative = tentative == maxTentatives;
                log.warn("Gemini surchargé (tentative {}/{}) : {}",
                    tentative, maxTentatives, e.getMessage());

                if (derniereTentative) {
                    throw e;
                }
                try {
                    Thread.sleep(delaiMs * tentative); // 2s, puis 4s
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
        throw new IllegalStateException("Ne devrait jamais arriver");
    }

    private Map<String, String> parserReponseAudio(String reponseBrute) {
        try {
            JsonNode racine = objectMapper.readTree(reponseBrute);
            String texte = racine
                .path("candidates").path(0)
                .path("content").path("parts").path(0)
                .path("text").asText("");

            // Nettoyage au cas où Gemini entoure quand même de ```json ... ```
            String texteNettoye = texte
                .replaceAll("(?s)```json", "")
                .replaceAll("(?s)```", "")
                .trim();

            JsonNode json = objectMapper.readTree(texteNettoye);
            return Map.of(
                "transcription", json.path("transcription").asText(""),
                "reponse", json.path("reponse").asText("Je n'ai pas pu générer de réponse.")
            );
        } catch (Exception e) {
            log.error("Erreur de parsing JSON de la réponse audio Gemini : {}", reponseBrute, e);
            return Map.of(
                "transcription", "",
                "reponse", "Désolé, je n'ai pas pu traiter la réponse. Réessayez."
            );
        }
    }

    // Génère une vraie voix (Gemini TTS natif) pour un texte donné — gère
    // le wolof, contrairement à la synthèse vocale du navigateur. Retourne
    // directement une data URL audio/wav prête à être jouée côté frontend.
    public String genererAudio(String texte) {
        if (cleAbsente()) {
            return "";
        }
        try {
            String url = GEMINI_URL.formatted(modeleTts, apiKey);

            String prompt = "Lis ce texte à voix haute, dans sa langue d'origine "
                + "(français ou wolof), avec un ton naturel et clair : " + texte;

            Map<String, Object> body = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(
                        Map.of("text", prompt)
                    ))
                ),
                "generationConfig", Map.of(
                    "responseModalities", List.of("AUDIO"),
                    "speechConfig", Map.of(
                        "voiceConfig", Map.of(
                            "prebuiltVoiceConfig", Map.of("voiceName", voixTts)
                        )
                    )
                )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requete = new HttpEntity<>(body, headers);

            String reponseBrute = postAvecRetry(url, requete);

            JsonNode racine = objectMapper.readTree(reponseBrute);
            JsonNode partieAudio = racine
                .path("candidates").path(0)
                .path("content").path("parts").path(0)
                .path("inlineData");

            String audioBase64 = partieAudio.path("data").asText("");
            String mimeType = partieAudio.path("mimeType").asText("audio/L16;rate=24000");

            if (audioBase64.isBlank()) {
                log.error("Gemini TTS n'a renvoyé aucun audio pour : {}", texte);
                return "";
            }

            byte[] pcm = Base64.getDecoder().decode(audioBase64);
            int sampleRate = extraireFrequence(mimeType);
            byte[] wav = construireWav(pcm, sampleRate, 16, 1);

            return "data:audio/wav;base64," + Base64.getEncoder().encodeToString(wav);

        } catch (Exception e) {
            log.error("Erreur lors de la génération audio TTS Gemini", e);
            return "";
        }
    }

    private int extraireFrequence(String mimeType) {
        // Format attendu : "audio/L16;rate=24000"
        Matcher m = Pattern.compile("rate=(\\d+)").matcher(mimeType);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 24000; // valeur par défaut de Gemini TTS
    }

    // Construit un fichier WAV valide (en-tête 44 octets) à partir de données
    // PCM brutes renvoyées par Gemini, pour que le navigateur puisse le jouer
    // directement avec <audio> ou new Audio(...).
    private byte[] construireWav(byte[] pcm, int sampleRate, int bitsPerSample, int channels) {
        int byteRate = sampleRate * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;
        int dataLength = pcm.length;

        ByteBuffer buffer = ByteBuffer.allocate(44 + dataLength);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.put("RIFF".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(36 + dataLength);
        buffer.put("WAVE".getBytes(StandardCharsets.US_ASCII));
        buffer.put("fmt ".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(16);
        buffer.putShort((short) 1); // PCM
        buffer.putShort((short) channels);
        buffer.putInt(sampleRate);
        buffer.putInt(byteRate);
        buffer.putShort((short) blockAlign);
        buffer.putShort((short) bitsPerSample);
        buffer.put("data".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(dataLength);
        buffer.put(pcm);

        return buffer.array();
    }

    // Construit le contexte RAG à partir de la base de données
    private String construireContexte() {
        List<Ligne> lignes = ligneRepository.findAll();

        return lignes.stream()
            .map(l -> "Ligne " + l.getNumero()
                    + " : " + l.getDepart()
                    + " → " + l.getArrivee()
                    + ", tarif : " + l.getTarif() + " FCFA")
            .collect(Collectors.joining("\n"));
    }
}