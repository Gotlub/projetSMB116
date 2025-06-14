package com.smb116.project.model;
import com.google.gson.annotations.SerializedName;

public class Contact {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lon")
    private double lon;
    @SerializedName("time_")
    private long lastCurrentTimeSeconds;

    public Contact(String name, double lat, double lon, long lastCurrentTimeSeconds) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.lastCurrentTimeSeconds = lastCurrentTimeSeconds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getLastCurrentTimeSeconds() {
        return lastCurrentTimeSeconds;
    }

    public void setLastCurrentTimeSeconds(long lastCurrentTimeSeconds) {
        this.lastCurrentTimeSeconds = lastCurrentTimeSeconds;
    }
}
