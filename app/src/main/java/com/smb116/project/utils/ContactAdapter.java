package com.smb116.project.utils;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smb116.project.R;
import com.smb116.project.model.SelfPosition;
import com.smb116.project.model.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private static final int EARTH_RADIUS = 6371;

    private List<Contact> contactList;
    private SelfPosition userPos = null;
    private OnClickListener onClickListener;
    public ContactAdapter(List<Contact> contactList) {
        this.contactList = contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
    }
    private static String calcAngle(double lat1, double lon1, double lat2, double lon2) {
        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff) * Math.cos(latitude2);
        double x=Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);
        double resultDegree = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
        String coordNames[] = {"N","NNE", "NE","ENE","E", "ESE","SE","SSE", "S","SSW", "SW","WSW", "W","WNW", "NW","NNW", "N"};
        double directionid = Math.round(resultDegree / 22.5);
        // no of array contain 360 / 16 = 22.5
        if (directionid < 0) {
            directionid = directionid + 16;
            //no. of contains in array
        }
        String compasLoc=coordNames[(int) directionid];

        return String.format("%s %s", String.format("%.3f", resultDegree), compasLoc);
    }

    private static double calcDist(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double lon1Rad = Math.toRadians(lon1);
        double lon2Rad = Math.toRadians(lon2);
        Log.d("log d calcDist", String.valueOf(lat1) + " " + String.valueOf(lat2) + " " + String.valueOf(lon1) + " " + String.valueOf(lon2));
        double x = (lon2Rad - lon1Rad) * Math.cos((lat1Rad + lat2Rad) / 2);
        double y = (lat2Rad - lat1Rad);

        double distance = Math.sqrt(x * x + y * y) * EARTH_RADIUS;
        Log.d("log d calcDist", x + " " + y + " " + distance);

        return distance;
    }

    private static String calcTime(long timeContact) {
        Log.d("log d time contact", String.valueOf(timeContact));
        timeContact = System.currentTimeMillis() - timeContact;
        timeContact = timeContact / 1000;
        Log.d("log d time contact2", String.valueOf(timeContact));
        Log.d("log d time contact2", String.valueOf(timeContact));
        Log.d("log d tc", String.valueOf(System.currentTimeMillis()));
        int day = (int)(timeContact / 86400);
        timeContact = timeContact % 86400;
        int hours = (int)(timeContact / 3600);
        timeContact = timeContact % 3600;
        int minutes = (int)(timeContact / 60);
        timeContact = timeContact % 60;

        return String.valueOf(day) + " days " + String.valueOf(hours) + "h " + String.valueOf(minutes) + "m " + String.valueOf(timeContact) + "s" ;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_card, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        if(userPos == null) {
            userPos = SelfPosition.getInstance();
        }
        holder.contactCard.setText(contact.getName());
        holder.latCard.setText(String.format("Latitude: %s", String.valueOf(contact.getLat())));
        holder.lonCard.setText(String.format("Longitude: %s", String.valueOf(contact.getLon())));
        holder.timeCard.setText(String.format("Dernière mesure: %s", calcTime(contact.getLastCurrentTimeSeconds())));
        if(userPos.isSetPosition()) {
            holder.distanceCard.setText(String.format("Distance: %s Km", String.format("%.3f", calcDist(userPos.getLat(),
                    userPos.getLon(), contact.getLat(), contact.getLon()))));
            holder.direCard.setText(String.format("Direction:%s", calcAngle(userPos.getLat(),
                    userPos.getLon(), contact.getLat(), contact.getLon())));
        }
        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, contact);
            }
        });

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Contact contact);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    // ViewHolder class
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView contactCard, distanceCard, latCard, lonCard, direCard, timeCard;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            contactCard = itemView.findViewById(R.id.contactCard);
            distanceCard = itemView.findViewById(R.id.distanceCard);
            latCard = itemView.findViewById(R.id.latCard);
            lonCard = itemView.findViewById(R.id.lonCard);
            direCard = itemView.findViewById(R.id.direCard);
            timeCard = itemView.findViewById(R.id.timeCard);
        }
    }
}