package sn.rangmoygaaw.transport.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    /**
     * Clé de signature des jetons. Volontairement SANS valeur par défaut :
     * une clé de repli écrite dans le code permettrait à quiconque lit le
     * dépôt de forger un jeton administrateur. Elle doit être fournie par
     * la variable d'environnement JWT_SECRET (voir .env.example).
     */
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24h en millisecondes
    private long expiration;

    /**
     * HMAC-SHA256 exige au moins 256 bits de clé. On échoue au démarrage
     * plutôt que d'accepter silencieusement un secret trop court.
     */
    @PostConstruct
    void verifierSecret() {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                "jwt.secret est absent ou trop court : au moins 32 caractères "
                + "sont requis. Définissez la variable d'environnement JWT_SECRET.");
        }
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Générer un token pour un utilisateur
    public String generateToken(String username, String role) {
        return Jwts.builder()
            .subject(username)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getKey())
            .compact();
    }

    // Extraire le nom d'utilisateur du token
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Extraire le rôle du token ("CLIENT" ou "ADMIN")
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // Vérifier si le token est valide
    public boolean isTokenValid(String token) {
        try {
            return getClaims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}