package sn.rangmoygaaw.transport.domain.port.out;

import sn.rangmoygaaw.transport.domain.model.Utilisateur;

import java.util.Optional;
import java.util.UUID;

public interface UtilisateurRepository {
    Utilisateur save(Utilisateur utilisateur);
    Optional<Utilisateur> findByUsername(String username);
    Optional<Utilisateur> findById(UUID id);
    boolean existsByUsername(String username);
}