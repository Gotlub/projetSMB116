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

    private double distanceMin = Double.POSITIVE_INFINITY;
    private String plusProcheLogin = "";
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

    private static String calcTime(long pastTimestampMillis) {
        long elapsedSeconds = (System.currentTimeMillis() - pastTimestampMillis) / 1000;

        int days = (int) (elapsedSeconds / 86400);
        elapsedSeconds %= 86400;

        int hours = (int) (elapsedSeconds / 3600);
        elapsedSeconds %= 3600;

        int minutes = (int) (elapsedSeconds / 60);
        int seconds = (int) (elapsedSeconds % 60);

        return String.format("%d days %02dh %02dm %02ds", days, hours, minutes, seconds);
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
            double distance = calcDist(userPos.getLat(),
                    userPos.getLon(), contact.getLat(), contact.getLon());
            holder.distanceCard.setText(String.format("Distance: %s Km", String.format("%.3f", distance)));
            holder.direCard.setText(String.format("Direction:%s", calcAngle(userPos.getLat(),
                    userPos.getLon(), contact.getLat(), contact.getLon())));
            if(distance <= distanceMin) {
                distanceMin = distance;
                plusProcheLogin = contact.getName();
            }
        }
        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, contact);
            }
        });

    }
    public double getDistanceMin() {
        return distanceMin;
    }

    public String getPlusProcheLogin() {
        return plusProcheLogin;
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