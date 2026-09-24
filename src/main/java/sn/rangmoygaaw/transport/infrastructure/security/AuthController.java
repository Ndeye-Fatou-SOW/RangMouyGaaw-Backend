package sn.rangmoygaaw.transport.infrastructure.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sn.rangmoygaaw.transport.domain.model.RoleUtilisateur;
import sn.rangmoygaaw.transport.domain.model.Utilisateur;
import sn.rangmoygaaw.transport.domain.port.out.UtilisateurRepository;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
@RequestMapping("/api/v1/auth")
public class AuthController {

    /** Longueur minimale acceptée pour un mot de passe à l'inscription. */
    private static final int MIN_LONGUEUR_MOT_DE_PASSE = 8;

    private final JwtUtil jwtUtil;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil, UtilisateurRepository utilisateurRepository,
                           PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @RequestBody Map<String, String> body) {

        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Nom d'utilisateur et mot de passe sont obligatoires"));
        }

        if (password.length() < MIN_LONGUEUR_MOT_DE_PASSE) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Le mot de passe doit contenir au moins "
                    + MIN_LONGUEUR_MOT_DE_PASSE + " caractères"));
        }

        if (utilisateurRepository.existsByUsername(username)) {
            return ResponseEntity.status(409)
                .body(Map.of("error", "Ce nom d'utilisateur est déjà pris"));
        }

        // Le rôle n'est JAMAIS lu depuis la requête : accepter un champ "role"
        // envoyé par le client permettrait à n'importe qui de se créer un
        // compte ADMIN. Toute inscription publique donne un compte CLIENT ;
        // les comptes administrateurs sont créés via /register-admin, qui
        // exige déjà un jeton ADMIN valide.
        RoleUtilisateur role = RoleUtilisateur.CLIENT;

        Utilisateur nouvelUtilisateur = new Utilisateur(
            null, username, passwordEncoder.encode(password), role
        );
        utilisateurRepository.save(nouvelUtilisateur);

        String token = jwtUtil.generateToken(username, role.name());
        return ResponseEntity.ok(Map.of("token", token, "role", role.name()));
    }

    /**
     * Création d'un compte administrateur. Réservée aux administrateurs
     * déjà authentifiés (voir SecurityConfig) : c'est le seul chemin par
     * lequel un rôle ADMIN peut être attribué.
     */
    @PostMapping("/register-admin")
    public ResponseEntity<Map<String, String>> registerAdmin(
            @RequestBody Map<String, String> body) {

        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Nom d'utilisateur et mot de passe sont obligatoires"));
        }

        if (password.length() < MIN_LONGUEUR_MOT_DE_PASSE) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Le mot de passe doit contenir au moins "
                    + MIN_LONGUEUR_MOT_DE_PASSE + " caractères"));
        }

        if (utilisateurRepository.existsByUsername(username)) {
            return ResponseEntity.status(409)
                .body(Map.of("error", "Ce nom d'utilisateur est déjà pris"));
        }

        utilisateurRepository.save(new Utilisateur(
            null, username, passwordEncoder.encode(password), RoleUtilisateur.ADMIN
        ));

        return ResponseEntity.ok(Map.of(
            "message", "Compte administrateur créé",
            "role", RoleUtilisateur.ADMIN.name()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody Map<String, String> body) {

        String username = body.get("username");
        String password = body.get("password");

        return utilisateurRepository.findByUsername(username)
            .filter(u -> passwordEncoder.matches(password, u.getPassword()))
            .map(u -> {
                String token = jwtUtil.generateToken(u.getUsername(), u.getRole().name());
                return ResponseEntity.ok(Map.of("token", token, "role", u.getRole().name()));
            })
            .orElseGet(() -> ResponseEntity.status(401)
                .body(Map.of("error", "Identifiants incorrects")));
    }
}