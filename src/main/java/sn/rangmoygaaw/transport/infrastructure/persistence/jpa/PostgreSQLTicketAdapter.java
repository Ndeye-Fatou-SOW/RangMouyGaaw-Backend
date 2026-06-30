package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import org.springframework.stereotype.Component;
import sn.rangmoygaaw.transport.domain.model.Ticket;
import sn.rangmoygaaw.transport.domain.model.TicketStatut;
import sn.rangmoygaaw.transport.domain.port.out.TicketRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PostgreSQLTicketAdapter implements TicketRepository {

    private final TicketJpaRepository jpaRepository;

    public PostgreSQLTicketAdapter(TicketJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        return toDomain(jpaRepository.save(toEntity(ticket)));
    }

    @Override
    public Optional<Ticket> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Ticket> findByCodeQR(String codeQR) {
        return jpaRepository.findByCodeQR(codeQR).map(this::toDomain);
    }

    @Override
    public List<Ticket> findByCitoyenId(UUID citoyenId) {
        return jpaRepository.findByCitoyenId(citoyenId)
            .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Ticket> findByStatut(TicketStatut statut) {
        return jpaRepository.findByStatut(statut)
            .stream().map(this::toDomain).collect(Collectors.toList());
    }

    private TicketJpaEntity toEntity(Ticket t) {
        TicketJpaEntity e = new TicketJpaEntity();
       // e.setId(t.getId());
        if (t.getId() != null) {
            e.setId(t.getId());
        }
        e.setCodeQR(t.getCodeQR());
        e.setCitoyenId(t.getCitoyenId());
        e.setLigneId(t.getLigneId());
        e.setModePaiement(t.getModePaiement());
        e.setMontant(t.getMontant());
        e.setStatut(t.getStatut());
        e.setDateAchat(t.getDateAchat());
        e.setDateExpiration(t.getDateExpiration());
        e.setTransactionId(t.getTransactionId());
        return e;
    }

    private Ticket toDomain(TicketJpaEntity e) {
        Ticket t = new Ticket();
        t.setId(e.getId());
        t.setCodeQR(e.getCodeQR());
        t.setCitoyenId(e.getCitoyenId());
        t.setLigneId(e.getLigneId());
        t.setModePaiement(e.getModePaiement());
        t.setMontant(e.getMontant());
        t.setStatut(e.getStatut());
        t.setDateAchat(e.getDateAchat());
        t.setDateExpiration(e.getDateExpiration());
        t.setTransactionId(e.getTransactionId());
        return t;
    }
}