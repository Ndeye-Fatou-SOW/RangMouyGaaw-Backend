package sn.rangmoygaaw.transport.infrastructure.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.rangmoygaaw.transport.domain.model.Bus;
import sn.rangmoygaaw.transport.domain.model.BusStatut;
import sn.rangmoygaaw.transport.domain.model.Ligne;
import sn.rangmoygaaw.transport.domain.port.in.ListerBusActifsUseCase;
import sn.rangmoygaaw.transport.domain.port.in.ObtenirPositionBusUseCase;
import sn.rangmoygaaw.transport.domain.port.out.BusRepository;
import sn.rangmoygaaw.transport.domain.port.out.LigneRepository;
import sn.rangmoygaaw.transport.domain.valueobject.PositionGPS;

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

    public TransportRestController(
            ObtenirPositionBusUseCase obtenirPositionBus,
            ListerBusActifsUseCase listerBusActifs,
            LigneRepository ligneRepository ,BusRepository busRepository) {
        this.obtenirPositionBus = obtenirPositionBus;
        this.listerBusActifs    = listerBusActifs;
        this.ligneRepository    = ligneRepository;
        this.busRepository=busRepository;
    }

//    // Injection par constructeur des Port.In uniquement
//    public TransportRestController(
//            ObtenirPositionBusUseCase obtenirPositionBus,
//            ListerBusActifsUseCase listerBusActifs) {
//        this.obtenirPositionBus = obtenirPositionBus;
//        this.listerBusActifs = listerBusActifs;
//    }
    
    
 // GET tous les bus (pas seulement actifs)
    @GetMapping("/bus/tous")
    public ResponseEntity<List<Bus>> getTousBus() {
        return ResponseEntity.ok(busRepository.findAll());
    }

    // POST ajouter un bus
    @PostMapping("/bus")
    public ResponseEntity<Bus> ajouterBus(@RequestBody Bus bus) {
        bus.setId(UUID.randomUUID());
        return ResponseEntity.ok(busRepository.save(bus));
    }

    // PATCH changer statut
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

    // GET /api/v1/transport/bus
    @GetMapping("/bus")
    public ResponseEntity<List<Bus>> getBusActifs() {
        return ResponseEntity.ok(listerBusActifs.listerBusActifs());
    }

    // GET /api/v1/transport/bus/{busId}/position
    @GetMapping("/bus/{busId}/position")
    public ResponseEntity<PositionGPS> getPosition(
            @PathVariable UUID busId) {
        return obtenirPositionBus.obtenirPosition(busId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/lignes")
    public ResponseEntity<List<Ligne>> getLignes() {
        return ResponseEntity.ok(ligneRepository.findAll());
    }
}