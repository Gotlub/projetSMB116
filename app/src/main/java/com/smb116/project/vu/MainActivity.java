package com.smb116.project.vu;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smb116.project.R;
import com.smb116.project.model.Contact;
import com.smb116.project.model.SelfPosition;
import com.smb116.project.utils.APILoadService;
import com.smb116.project.utils.ContactAdapter;

import java.io.IOException;
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
    private APILoadService loadService;
    private boolean bound = false;
    private TextView demandeContact, txtLat, txtLon, txtmcontact, txtAddr, optionsenvoidistance, optionsenvoitemp, optionsenvoicontact;
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
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                position.setLatLong(lat, lon);
                txtLat.setText(String.format("Latitude :%s", String.format("%.5f",lat)));
                txtLon.setText(String.format("Longitude :%s", String.format("%.5f",lon)));
                try {
                    List<Address> adresses = geocoder.getFromLocation(lat, lon, 1);
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

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("log d connection", "onServiceConnected");
            APILoadService.LocalBinder binder = (APILoadService.LocalBinder) iBinder;
            loadService = binder.getService();
            loadService.startInfoLoader(contactActualisationTime);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
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
        demandeContact = findViewById(R.id.demandeContact);
        txtLat = findViewById(R.id.lat);
        txtLon = findViewById(R.id.lon);
        txtAddr = findViewById(R.id.addr);
        txtmcontact = findViewById(R.id.txtmcontact);
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
        demandeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Demandes.class);
                intent.putExtra("id", position.getId());
                intent.putExtra("name", position.getName());
                intent.putExtra("password", position.getMpd());
                startActivity(intent);
            }
        });
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
                if(bound) {
                    loadService.setRefreshRate(contactActualisationTime);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        locationLautcher();
        Intent intent = new Intent(this, APILoadService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        Log.d("log d onResume", "bindService?");
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (bound) {
            loadService.stop();
            //TODO loadService
            unbindService(connection);
            bound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Intent intent = getIntent();
        position = new SelfPosition(this,
                intent.getIntExtra("userId", 0),
                intent.getStringExtra("name"),
                intent.getStringExtra("password"));
        setContentView(R.layout.activity_main);
        //TODO RECUP l'utilisateur de l'intant et le set dans selfPosition
        init();
        contactList = position.getContactList();
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

    public void updateAdapter() {
        Log.d("log d updateAdapter", "plop");
        adapter.setContactList(position.getContactList());
        adapter.notifyDataSetChanged();
        String textDebut =  String.format("Mes %s contacts \n Le plus proche : %s , %.3f km",
                adapter.getItemCount(), adapter.getPlusProcheLogin(),
                adapter.getDistanceMin() ==  Double.POSITIVE_INFINITY ? 0 : adapter.getDistanceMin());
        txtmcontact.setText(textDebut);
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


}