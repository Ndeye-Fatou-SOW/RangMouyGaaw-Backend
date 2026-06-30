package sn.rangmoygaaw.transport.domain.event;

import sn.rangmoygaaw.transport.domain.model.TypeIncident;
import java.time.LocalDateTime;
import java.util.UUID;

public class IncidentSignale {
    private UUID incidentId;
    private UUID busId;
    private UUID ligneId;
    private TypeIncident type;
    private String description;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;

    public IncidentSignale() {}

    public IncidentSignale(UUID incidentId, UUID busId, UUID ligneId,
            TypeIncident type, String description,
            double latitude, double longitude) {
        this.incidentId  = incidentId;
        this.busId       = busId;
        this.ligneId     = ligneId;
        this.type        = type;
        this.description = description;
        this.latitude    = latitude;
        this.longitude   = longitude;
        this.timestamp   = LocalDateTime.now();
    }

    public UUID getIncidentId()          { return incidentId; }
    public void setIncidentId(UUID v)    { this.incidentId = v; }
    public UUID getBusId()               { return busId; }
    public void setBusId(UUID v)         { this.busId = v; }
    public UUID getLigneId()             { return ligneId; }
    public void setLigneId(UUID v)       { this.ligneId = v; }
    public TypeIncident getType()        { return type; }
    public void setType(TypeIncident v)  { this.type = v; }
    public String getDescription()       { return description; }
    public void setDescription(String v) { this.description = v; }
    public double getLatitude()          { return latitude; }
    public void setLatitude(double v)    { this.latitude = v; }
    public double getLongitude()         { return longitude; }
    public void setLongitude(double v)   { this.longitude = v; }
    public LocalDateTime getTimestamp()  { return timestamp; }
    public void setTimestamp(LocalDateTime v){ this.timestamp = v; }
}
