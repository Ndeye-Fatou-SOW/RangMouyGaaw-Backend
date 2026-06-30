package sn.rangmoygaaw.transport.infrastructure.web.websocket;

import java.time.Instant;

public class  BusPositionMessage {

    private String busId;
    private String immatriculation;
    private Double latitude;
    private Double longitude;
    private Long timestamp;

    public BusPositionMessage() {
    }

    public BusPositionMessage(String busId,
                              String immatriculation,
                              Double latitude,
                              Double longitude,
                              Long timestamp) {
        this.busId = busId;
        this.immatriculation = immatriculation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

	public Long getTimestamp() {
        return timestamp;
        }

		public void setTimestamp(Long timestamp) {
			this.timestamp = timestamp;
		}

    @Override
    public String toString() {
        return "BusPositionMessage{" +
                "busId='" + busId + '\'' +
                ", immatriculation='" + immatriculation + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                '}';
    }
}