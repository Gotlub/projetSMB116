package com.smb116.project;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smb116.project.model.Contact;
import com.smb116.project.model.SelfPosition;
import com.smb116.project.utils.ContactAdapter;
import com.smb116.project.utils.RetrofitInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private LocationManager locationManager = null;
    private Geocoder geocoder;
    private int gpsActuationDist = 20;
    private int gpsActuationTime = 3;
    private int contactActualisationTime = 10;
    private static final int GPS_CODE = 100;
    private SelfPosition position;
    private TextView txtLat, txtLon, txtAddr, optionsenvoidistance, optionsenvoitemp, optionsenvoicontact;
    private SeekBar seekBarDist, seekBarSec, seekBarContact;
    private RecyclerView recyclerView;
    private ImageView bearing;
    private ContactAdapter adapter;
    private List<Contact> contactList;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] gravity;
    private float[] geomagnetic;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("log d locationListener", "Location update: " + location);
            if (location.getAccuracy() <= 100) {
                position.setLatLong(location.getLatitude(), location.getLongitude());
                txtLat.setText(String.format("Latitude :%s", String.format("%.5f",location.getLatitude())));
                txtLon.setText(String.format("Longitude :%s", String.format("%.5f",location.getLongitude())));
                try {
                    List<Address> adresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    StringBuilder addr = new StringBuilder();
                    if(adresses != null && !adresses.isEmpty()) {
                        addr.append(adresses.get(0).getAddressLine(0)).append("\n");
                        txtAddr.setText(String.format("Adresse :%s", addr));
                        Log.d("log d addr", String.valueOf(addr));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(@NonNull String provider) { }

        @Override
        public void onProviderDisabled(@NonNull String provider) { }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GPS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationLautcher();
            }
        }
    }

    private void locationLautcher() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("log d" , "prout");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},GPS_CODE);
            return;
        }
        if(locationManager != null) {
            locationManager.removeUpdates(mLocationListener);
        }else {
            locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, gpsActuationTime * 1000L,
                gpsActuationDist, mLocationListener);
    }

    private void init() {
        geocoder = new Geocoder(getApplicationContext(), Locale.FRANCE);
        txtLat = findViewById(R.id.lat);
        txtLon = findViewById(R.id.lon);
        txtAddr = findViewById(R.id.addr);
        optionsenvoidistance = findViewById(R.id.optionsenvoidistance);
        optionsenvoitemp = findViewById(R.id.optionsenvoitemp);
        optionsenvoicontact = findViewById(R.id.optionsenvoicontact);
        seekBarDist = findViewById(R.id.seekBarDist);
        seekBarSec = findViewById(R.id.seekBarSec);
        seekBarContact = findViewById(R.id.seekBarContact);
        recyclerView = findViewById(R.id.recyclerView);
        bearing = findViewById(R.id.bearing);
        seekBarDist.setProgress(gpsActuationDist);
        seekBarSec.setProgress(gpsActuationTime);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        seekBarDist.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Appelé à chaque changement de valeur
                Log.d("SeekBar", "Progress: " + progress + " (fromUser=" + fromUser + ")");
                gpsActuationDist = progress;
                optionsenvoidistance.setText(String.format("Tous les %s mètres", String.valueOf(gpsActuationDist)));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                locationLautcher();
            }
        });
        seekBarSec.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Appelé à chaque changement de valeur
                Log.d("SeekBar", "Progress: " + progress + " (fromUser=" + fromUser + ")");
                gpsActuationTime = progress;
                optionsenvoitemp.setText(String.format("Toutes les %s secondes", String.valueOf(gpsActuationTime)));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                locationLautcher();
            }
        });
        seekBarContact.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Appelé à chaque changement de valeur
                Log.d("SeekBar", "Progress: " + progress + " (fromUser=" + fromUser + ")");
                contactActualisationTime = progress;
                optionsenvoicontact.setText(String.format("Actualisation des contacts, toutes les %s secondes", String.valueOf(contactActualisationTime)));

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //todo
            }
        });
        locationLautcher();
        getContact();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        position = SelfPosition.getInstance();
        setContentView(R.layout.activity_main);
        init();
        contactList = new ArrayList<>();
        contactList.add(new Contact("pouet" , 46.882271, 0.080815, System.currentTimeMillis()));
        contactList.add(new Contact("paspouet" , 46.842422, 0.090881, System.currentTimeMillis()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter(contactList);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new ContactAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Contact contact) {
                //Intent intent = new Intent(MainActivity.this, BorneDetails.class);
                //intent.putExtra("borne", borne);
                //startActivity(intent);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = event.values;

        if (gravity != null && geomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimuth = (float) Math.toDegrees(orientation[0]);
                azimuth = (azimuth + 360) % 360;

                bearing.setRotation(-azimuth);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void getContact() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        RetrofitInstance.getApiInterface().getContactById("{\"id\":1}").enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                Log.d("log d getContact1", response.body().toString());
                if (response.isSuccessful()) {
                    List<Contact> contacts = response.body();
                    adapter.setContactList(contacts);
                    adapter.notifyDataSetChanged();
                    Log.d("log d getContact", response.body().toString());
                } else {
                    // Gérer l'erreur (ex: code 401 ou 404)
                    Log.d("log d API", "Erreur: " + response.code());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.d("log d getContact fail", t.getLocalizedMessage());
                Log.d("log d getContact fail", call.toString());
                progressDialog.dismiss();
            }
        });
    }
}