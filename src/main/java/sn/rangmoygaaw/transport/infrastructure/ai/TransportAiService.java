package sn.rangmoygaaw.transport.infrastructure.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import sn.rangmoygaaw.transport.domain.model.Ligne;
import sn.rangmoygaaw.transport.domain.port.out.LigneRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransportAiService {

    private final ChatClient chatClient;
    private final LigneRepository ligneRepository;

    public TransportAiService(ChatClient.Builder builder,
                               LigneRepository ligneRepository) {
        this.chatClient = builder.build();
        this.ligneRepository = ligneRepository;
    }

    // L'utilisateur pose une question sur le transport
    public String repondre(String questionUtilisateur) {

        // 1. RAG : on récupère le contexte depuis la base
        String contexte = construireContexte();

        // 2. On construit le prompt avec le contexte
        String prompt = """
            Tu es Dama dem, l'assistant transport intelligent de Dakar.
            Tu aides les citoyens à trouver les meilleures lignes de bus,
            horaires et itinéraires à Dakar, Sénégal.
            Réponds toujours en français, de façon claire et concise.

            Voici les informations disponibles sur les lignes actuelles :
            %s

            Question du citoyen : %s
            """.formatted(contexte, questionUtilisateur);

        // 3. Appel à l'IA
        return chatClient.prompt()
            .user(prompt)
            .call()
            .content();
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