package com.smb116.project.model;

import android.util.Log;

import com.smb116.project.vu.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class SelfPosition {
    private static SelfPosition instance = null;
    private double lat;
    private double lon;
    private int id ;
    private String name ;
    private String mpd ;

    private MainActivity activity = null;


    private List<Contact> contactList =  new ArrayList<>();

    public SelfPosition(MainActivity activityCompat,int id, String name, String mdp) {
        this.instance = this;
        this.activity = activityCompat;
        this.id = id;
        this.name = name;
        this.mpd = mdp;
    }

    private boolean setPosition = false;

    public static SelfPosition getInstance() {
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

    public List<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
        Log.d("log d setContactList", contactList.toString());
        Log.d("log d setContactList adapter", contactList.toString());
        activity.updateAdapter();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMpd() {
        return mpd;
    }
}
