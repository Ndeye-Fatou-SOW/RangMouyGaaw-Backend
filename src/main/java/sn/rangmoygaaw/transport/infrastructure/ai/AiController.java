 package sn.rangmoygaaw.transport.infrastructure.ai;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}