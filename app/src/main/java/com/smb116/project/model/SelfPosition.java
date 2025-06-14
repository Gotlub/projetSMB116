package com.smb116.project.model;

public class SelfPosition {
    private static SelfPosition instance = null;
    private double lat;
    private double lon;

    private boolean setPosition = false;


    public static SelfPosition getInstance() {
        if(SelfPosition.instance == null) {
            synchronized(SelfPosition.class) {
                if (SelfPosition.instance == null) {
                    SelfPosition.instance = new SelfPosition();
                }
            }
        }
         return SelfPosition.instance;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLatLong(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        setPosition = true;
    }

    public boolean isSetPosition() {
        return setPosition;
    }

}
