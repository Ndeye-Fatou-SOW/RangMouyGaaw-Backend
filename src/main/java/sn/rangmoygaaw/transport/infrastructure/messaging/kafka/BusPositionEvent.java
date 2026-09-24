package sn.rangmoygaaw.transport.infrastructure.messaging.kafka;

public class BusPositionEvent {
    private String busId;
    private double latitude;
    private double longitude;
    private long timestamp;

    public String getBusId() { return busId; }
    public void setBusId(String busId) { this.busId = busId; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "BusPositionEvent{busId='" + busId + "', latitude=" + latitude +
                ", longitude=" + longitude + ", timestamp=" + timestamp + "}";
    }
}