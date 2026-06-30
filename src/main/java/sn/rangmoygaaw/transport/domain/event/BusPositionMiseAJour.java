package sn.rangmoygaaw.transport.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class BusPositionMiseAJour {
    private UUID busId;
    private double latitude;
    private double longitude;
    private double vitesseKmh;
    private LocalDateTime timestamp;

    public BusPositionMiseAJour() {}

    public BusPositionMiseAJour(UUID busId, double latitude,
            double longitude, double vitesseKmh) {
        this.busId      = busId;
        this.latitude   = latitude;
        this.longitude  = longitude;
        this.vitesseKmh = vitesseKmh;
        this.timestamp  = LocalDateTime.now();
    }

    public UUID getBusId()             { return busId; }
    public void setBusId(UUID v)       { this.busId = v; }
    public double getLatitude()        { return latitude; }
    public void setLatitude(double v)  { this.latitude = v; }
    public double getLongitude()       { return longitude; }
    public void setLongitude(double v) { this.longitude = v; }
    public double getVitesseKmh()      { return vitesseKmh; }
    public void setVitesseKmh(double v){ this.vitesseKmh = v; }
    public LocalDateTime getTimestamp(){ return timestamp; }
    public void setTimestamp(LocalDateTime v){ this.timestamp = v; }
}