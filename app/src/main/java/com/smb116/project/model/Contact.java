package com.smb116.project.model;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Contact implements Parcelable {

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

    public Contact(int id,String name, double lat, double lon, long lastCurrentTimeSeconds) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.lastCurrentTimeSeconds = lastCurrentTimeSeconds;
    }

    protected Contact(Parcel in) {
        id = in.readInt();
        name = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        lastCurrentTimeSeconds = in.readLong();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeDouble(lat);
        parcel.writeDouble(lon);
        parcel.writeLong(lastCurrentTimeSeconds);
    }
}
