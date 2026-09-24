package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.model.Utilisateur;
import sn.rangmoygaaw.transport.domain.port.out.UtilisateurRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class PostgreSQLUtilisateurAdapter implements UtilisateurRepository {

    private final UtilisateurJpaRepository jpaRepository;

    public PostgreSQLUtilisateurAdapter(UtilisateurJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        UtilisateurJpaEntity entity = new UtilisateurJpaEntity(
            utilisateur.getId(),
            utilisateur.getUsername(),
            utilisateur.getPassword(),
            utilisateur.getRole()
        );
        UtilisateurJpaEntity sauvegarde = jpaRepository.save(entity);
        return versDomaine(sauvegarde);
    }

    @Override
    public Optional<Utilisateur> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::versDomaine);
    }

    @Override
    public Optional<Utilisateur> findById(UUID id) {
        return jpaRepository.findById(id).map(this::versDomaine);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    private Utilisateur versDomaine(UtilisateurJpaEntity e) {
        return new Utilisateur(e.getId(), e.getUsername(), e.getPassword(), e.getRole());
    }
}