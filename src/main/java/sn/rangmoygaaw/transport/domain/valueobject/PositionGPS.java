package sn.rangmoygaaw.transport.domain.valueobject;

import java.time.Instant;
import java.util.Objects;
//import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
//import sn.rangmoygaaw.transport.domain.model.BusStatut;
@Getter 
@Setter
public final class PositionGPS {

    private final double latitude;
    private final double longitude;
    private final Long timestamp;

    public PositionGPS(double latitude, double longitude, Long timestamp) {
        if (latitude < -90 || latitude > 90)
            throw new IllegalArgumentException("Latitude invalide : " + latitude);
        if (longitude < -180 || longitude > 180)
            throw new IllegalArgumentException("Longitude invalide : " + longitude);
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

   

    // Calcul de distance en km (formule Haversine)
    public double distanceKm(PositionGPS autre) {
        final double R = 6371;
        double dLat = Math.toRadians(autre.latitude - this.latitude);
        double dLon = Math.toRadians(autre.longitude - this.longitude);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                 + Math.cos(Math.toRadians(this.latitude))
                 * Math.cos(Math.toRadians(autre.latitude))
                 * Math.sin(dLon/2) * Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionGPS)) return false;
        PositionGPS p = (PositionGPS) o;
        return Double.compare(latitude, p.latitude) == 0
            && Double.compare(longitude, p.longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "GPS(" + latitude + ", " + longitude + ")";
    }
}