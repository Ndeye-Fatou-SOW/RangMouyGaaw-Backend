package sn.rangmoygaaw.transport.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {
    private UUID id;
    private String username;
    private String password; // stocké haché (BCrypt), jamais en clair
    private RoleUtilisateur role;
}