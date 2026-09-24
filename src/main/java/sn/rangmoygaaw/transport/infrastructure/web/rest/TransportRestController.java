package sn.rangmoygaaw.transport.infrastructure.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.rangmoygaaw.transport.domain.model.Bus;
import sn.rangmoygaaw.transport.domain.model.BusStatut;
import sn.rangmoygaaw.transport.domain.model.Ligne;
import sn.rangmoygaaw.transport.domain.port.in.ListerBusActifsUseCase;
import sn.rangmoygaaw.transport.domain.port.in.ObtenirPositionBusUseCase;
import sn.rangmoygaaw.transport.domain.port.out.BusRepository;
import sn.rangmoygaaw.transport.domain.port.out.ChauffeurRepository;
import sn.rangmoygaaw.transport.domain.port.out.IncidentRepository;
import sn.rangmoygaaw.transport.domain.port.out.LigneRepository;
import sn.rangmoygaaw.transport.domain.valueobject.PositionGPS;
import sn.rangmoygaaw.transport.domain.model.Chauffeur;
import sn.rangmoygaaw.transport.domain.model.ChauffeurStatut;
import sn.rangmoygaaw.transport.domain.model.Incident;
import sn.rangmoygaaw.transport.domain.model.IncidentStatut;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transport")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class TransportRestController {

    private final ObtenirPositionBusUseCase obtenirPositionBus;
    private final ListerBusActifsUseCase listerBusActifs;
    private final LigneRepository ligneRepository;
    private final BusRepository busRepository;
    private final ChauffeurRepository chauffeurRepository;
    private final IncidentRepository incidentRepository;

    public TransportRestController(
            ObtenirPositionBusUseCase obtenirPositionBus,
            ListerBusActifsUseCase listerBusActifs,
            LigneRepository ligneRepository, BusRepository busRepository,
            ChauffeurRepository chauffeurRepository, IncidentRepository incidentRepository) {
        this.obtenirPositionBus = obtenirPositionBus;
        this.listerBusActifs    = listerBusActifs;
        this.ligneRepository    = ligneRepository;
        this.busRepository = busRepository;
        this.chauffeurRepository = chauffeurRepository;
        this.incidentRepository = incidentRepository;
    }

    // =========================================================
    // BUS
    // =========================================================

    @GetMapping("/bus/tous")
    public ResponseEntity<List<Bus>> getTousBus() {
        return ResponseEntity.ok(busRepository.findAll());
    }

    @PostMapping("/bus")
    public ResponseEntity<Bus> ajouterBus(@RequestBody Bus bus) {
        bus.setId(null);
        return ResponseEntity.ok(busRepository.save(bus));
    }

    @PutMapping("/bus/{id}")
    public ResponseEntity<Bus> modifierBus(
            @PathVariable UUID id,
            @RequestBody Bus busModifie) {
        return busRepository.findById(id)
            .map(bus -> {
                busModifie.setId(id);
                return ResponseEntity.ok(busRepository.save(busModifie));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/bus/{id}")
    public ResponseEntity<Void> supprimerBus(@PathVariable UUID id) {
        if (!busRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        busRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/bus/{busId}/statut")
    public ResponseEntity<Bus> changerStatut(
            @PathVariable UUID busId,
            @RequestBody Map<String, String> body) {
        return busRepository.findById(busId)
            .map(bus -> {
                bus.setStatut(BusStatut.valueOf(body.get("statut")));
                return ResponseEntity.ok(busRepository.save(bus));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bus")
    public ResponseEntity<List<Bus>> getBusActifs() {
        return ResponseEntity.ok(listerBusActifs.listerBusActifs());
    }

    @GetMapping("/bus/{busId}/position")
    public ResponseEntity<PositionGPS> getPosition(
            @PathVariable UUID busId) {
        return obtenirPositionBus.obtenirPosition(busId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================================================
    // LIGNES
    // =========================================================

    @GetMapping("/lignes")
    public ResponseEntity<List<Ligne>> getLignes() {
        return ResponseEntity.ok(ligneRepository.findAll());
    }

    @PostMapping("/lignes")
    public ResponseEntity<Ligne> ajouterLigne(@RequestBody Ligne ligne) {
        ligne.setId(null);
        return ResponseEntity.ok(ligneRepository.save(ligne));
    }

    @PutMapping("/lignes/{id}")
    public ResponseEntity<Ligne> modifierLigne(
            @PathVariable UUID id,
            @RequestBody Ligne ligneModifiee) {
        return ligneRepository.findById(id)
            .map(l -> {
                ligneModifiee.setId(id);
                return ResponseEntity.ok(ligneRepository.save(ligneModifiee));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/lignes/{id}")
    public ResponseEntity<Void> supprimerLigne(@PathVariable UUID id) {
        if (ligneRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ligneRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // CHAUFFEURS
    // =========================================================

    @GetMapping("/chauffeurs")
    public ResponseEntity<List<Chauffeur>> getChauffeurs() {
        return ResponseEntity.ok(chauffeurRepository.findAll());
    }

    @PostMapping("/chauffeurs")
    public ResponseEntity<Chauffeur> ajouterChauffeur(
            @RequestBody Chauffeur chauffeur) {
        chauffeur.setId(null);
        return ResponseEntity.ok(chauffeurRepository.save(chauffeur));
    }

    @PutMapping("/chauffeurs/{id}")
    public ResponseEntity<Chauffeur> modifierChauffeur(
            @PathVariable UUID id,
            @RequestBody Chauffeur chauffeurModifie) {
        return chauffeurRepository.findById(id)
            .map(c -> {
                chauffeurModifie.setId(id);
                return ResponseEntity.ok(chauffeurRepository.save(chauffeurModifie));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/chauffeurs/{id}")
    public ResponseEntity<Void> supprimerChauffeur(@PathVariable UUID id) {
        if (chauffeurRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        chauffeurRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/chauffeurs/{id}/statut")
    public ResponseEntity<Chauffeur> changerStatutChauffeur(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        return chauffeurRepository.findById(id)
            .map(c -> {
                c.setStatut(ChauffeurStatut.valueOf(body.get("statut")));
                return ResponseEntity.ok(chauffeurRepository.save(c));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // =========================================================
    // INCIDENTS
    // =========================================================

    @GetMapping("/incidents")
    public ResponseEntity<List<Incident>> getIncidents() {
        return ResponseEntity.ok(incidentRepository.findAll());
    }

     
    
    @PostMapping("/incidents")
    public ResponseEntity<Incident> signalerIncident(
            @RequestBody Incident incident,
            org.springframework.security.core.Authentication authentication) {
        incident.setId(java.util.UUID.randomUUID());
        incident.setDateSignalement(java.time.LocalDateTime.now());
        incident.setSignalePar(authentication.getName());
        return ResponseEntity.ok(incidentRepository.save(incident));
    }

    @PutMapping("/incidents/{id}")
    public ResponseEntity<?> modifierIncident(
            @PathVariable UUID id,
            @RequestBody Incident incidentModifie,
            org.springframework.security.core.Authentication authentication) {
        return incidentRepository.findById(id)
            .map(i -> {
                boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                boolean estAuteur = authentication.getName().equals(i.getSignalePar());

                if (!estAdmin && !estAuteur) {
                    return ResponseEntity.status(403)
                        .body(Map.of("error", "Vous ne pouvez modifier que vos propres signalements"));
                }

                incidentModifie.setId(id);
                incidentModifie.setDateSignalement(i.getDateSignalement());
                incidentModifie.setSignalePar(i.getSignalePar());
                return ResponseEntity.ok(incidentRepository.save(incidentModifie));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/incidents/{id}")
    public ResponseEntity<?> supprimerIncident(
            @PathVariable UUID id,
            org.springframework.security.core.Authentication authentication) {
        return incidentRepository.findById(id)
            .map(i -> {
                boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                boolean estAuteur = authentication.getName().equals(i.getSignalePar());

                if (!estAdmin && !estAuteur) {
                    return ResponseEntity.status(403)
                        .body(Map.of("error", "Vous ne pouvez supprimer que vos propres signalements"));
                }

                incidentRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/incidents/statistiques")
    public ResponseEntity<Map<String, Object>> statistiquesIncidents() {
        List<Incident> tous = incidentRepository.findAll();

        Map<String, Long> parType = tous.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                i -> i.getType().name(), java.util.stream.Collectors.counting()));

        Map<String, Long> parStatut = tous.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                i -> i.getStatut().name(), java.util.stream.Collectors.counting()));

        return ResponseEntity.ok(Map.of(
            "total", tous.size(),
            "parType", parType,
            "parStatut", parStatut
        ));
    }

    @PatchMapping("/incidents/{id}/statut")
    public ResponseEntity<Incident> changerStatutIncident(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        return incidentRepository.findById(id)
            .map(i -> {
                i.setStatut(IncidentStatut.valueOf(body.get("statut")));
                return ResponseEntity.ok(incidentRepository.save(i));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}