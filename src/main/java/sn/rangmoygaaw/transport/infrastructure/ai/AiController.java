package sn.rangmoygaaw.transport.infrastructure.ai;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transport/assistant")
public class AiController {

    private final TransportAiService aiService;

    public AiController(TransportAiService aiService) {
        this.aiService = aiService;
    }

    // POST /api/v1/transport/assistant/demander
    // Body : { "question": "Comment aller de Medina à Plateau ?" }
    @PostMapping("/demander")
    public ResponseEntity<Map<String, String>> demander(
            @RequestBody Map<String, String> body) {

        String question = body.get("question");
        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "La question est vide"));
        }

        String reponse = aiService.repondre(question);
        return ResponseEntity.ok(Map.of("reponse", reponse));
    }

    // POST /api/v1/transport/assistant/demander-vocal
    // multipart/form-data, champ "audio" (webm/ogg/mp3...)
    // Gemini transcrit ET répond en une seule requête (gère le wolof)
    @PostMapping(value = "/demander-vocal", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> demanderVocal(
            @RequestParam("audio") MultipartFile audio) {

        if (audio.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Aucun fichier audio reçu"));
        }

        try {
            Map<String, String> resultat = aiService.repondreAudio(
                audio.getBytes(), audio.getContentType());
            return ResponseEntity.ok(resultat);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Impossible de lire le fichier audio"));
        }
    }

    // POST /api/v1/transport/assistant/tts
    // Body : { "texte": "..." }
    // Génère une vraie voix (Gemini TTS), y compris en wolof — appelé à la
    // demande (clic sur ▶️), pas automatiquement, pour économiser le quota
    @PostMapping("/tts")
    public ResponseEntity<Map<String, String>> tts(
            @RequestBody Map<String, String> body) {

        String texte = body.get("texte");
        if (texte == null || texte.isBlank()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Le texte est vide"));
        }

        String audioUrl = aiService.genererAudio(texte);
        return ResponseEntity.ok(Map.of("audioUrl", audioUrl));
    }
}