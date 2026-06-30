package sn.rangmoygaaw.transport.domain.service;

import sn.rangmoygaaw.transport.domain.model.Arret;
import sn.rangmoygaaw.transport.domain.model.Ligne;
import sn.rangmoygaaw.transport.domain.valueobject.PositionGPS;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CalculMoteurItineraire {

    // Trouve les lignes proches d'une position GPS (rayon en km)
    public List<Ligne> trouverLignesProches(
            PositionGPS positionDepart,
            List<Ligne> toutesLesLignes,
            List<Arret> tousLesArrets,
            double rayonKm) {

        List<Ligne> lignesProches = new ArrayList<>();

        for (Ligne ligne : toutesLesLignes) {
            if (ligne.getArretIds() == null) continue;

            for (UUID arretId : ligne.getArretIds()) {
                Arret arret = tousLesArrets.stream()
                    .filter(a -> a.getId().equals(arretId))
                    .findFirst().orElse(null);

                if (arret != null && arret.getPosition() != null) {
                    double dist = positionDepart
                        .distanceKm(arret.getPosition());
                    if (dist <= rayonKm) {
                        lignesProches.add(ligne);
                        break;
                    }
                }
            }
        }
        return lignesProches;
    }

    // Estime la durée de trajet en minutes
    public int estimerDureeMinutes(PositionGPS depart,
            PositionGPS arrivee, double vitesseMoyenneKmh) {
        double distKm = depart.distanceKm(arrivee);
        return (int) Math.ceil((distKm / vitesseMoyenneKmh) * 60);
    }
}