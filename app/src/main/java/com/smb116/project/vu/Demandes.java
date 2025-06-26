package com.smb116.project.vu;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smb116.project.R;
import com.smb116.project.model.NContact;
import com.smb116.project.utils.APILoadService;
import com.smb116.project.utils.NContactAdapter;
import com.smb116.project.utils.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Demandes extends AppCompatActivity {

    private int userId;
    private String name;
    private String password;
    private RecyclerView recyclerView, recyclerViewAtt;
    private TextView nvlDemande;
    private List<NContact> nContacts = new ArrayList<>();
    private List<NContact> nContacts2 = new ArrayList<>();

    private NContactAdapter adapter, adapter2;


    private void init() {
        nvlDemande = findViewById(R.id.faireDemande);
        nvlDemande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListUser.class);
                intent.putExtra("userId", userId);
                intent.putExtra("name", name);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });
        getNContact();
        getMesDemAttente();
    }

    private void udapteList(List<NContact> nContactsFresh) {
        Log.d("thread", "UI thread ? " + (Looper.myLooper() == Looper.getMainLooper()));
        nContacts = nContactsFresh;
        adapter.setNContactList(nContacts);
        adapter.notifyDataSetChanged();
        Log.d("log d udapteList", String.valueOf(adapter.getItemCount()));
        for(NContact nContact: nContacts){
            Log.d("log d updateList", nContact.getName());
        }
    }

    private void updateTexte(List<NContact> nContactsAtt) {
        nContacts2 = nContactsAtt;
        adapter2.setNContactList(nContacts2);
        adapter2.notifyDataSetChanged();
        Log.d("log d updateTexte", String.valueOf(adapter2.getItemCount()));
        for(NContact nContact: nContactsAtt) {
            Log.d("log d updateList", nContact.getName());
        }
    }

    private void getNContact() {
        String jsonEncoded = String.format("{\"id\":%d,\"name\":\"%s\", \"password\":\"%s\"}",
                userId, name, password);
        RetrofitInstance.getApiInterface().getDemandeAtt(jsonEncoded).enqueue(new Callback<List<NContact>>() {
            @Override
            public void onResponse(Call<List<NContact>> call, Response<List<NContact>> response) {
                Log.d("log d getContact demande", response.body().toString());
                if (response.isSuccessful()) {
                    udapteList(response.body());
                } else {
                    Log.d("log d API demande", "Erreur: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<NContact>> call, Throwable t) {
                Log.d("log d getContact fail demande", t.getLocalizedMessage());
                Log.d("log d getContact fail demande", call.toString());
            }
        });
    }

    private void getMesDemAttente() {
        String jsonEncoded = String.format("{\"id\":%d,\"name\":\"%s\", \"password\":\"%s\"}",
                userId, name, password);
        RetrofitInstance.getApiInterface().getMesDemande(jsonEncoded).enqueue(new Callback<List<NContact>>() {
            @Override
            public void onResponse(Call<List<NContact>> call, Response<List<NContact>> response) {
                Log.d("log d getContact demande", response.body().toString());
                if (response.isSuccessful()) {
                    updateTexte(response.body());
                } else {
                    Log.d("log d API demande", "Erreur: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<NContact>> call, Throwable t) {
                Log.d("log d getContact fail demande", t.getLocalizedMessage());
                Log.d("log d getContact fail demande", call.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNContact();
        getMesDemAttente();
    }

    private void reset() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_demandes);
        Intent intent = getIntent();
        userId = intent.getIntExtra("id", 0);
        name = intent.getStringExtra("name");
        password = intent.getStringExtra("password");
        //vue du haut
        recyclerView = findViewById(R.id.recyclerViewD);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NContactAdapter(nContacts);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        // du bas (propres demandes)
        recyclerViewAtt = findViewById(R.id.mesDemAtt);
        recyclerViewAtt.setLayoutManager(new LinearLayoutManager(this));
        adapter2 = new NContactAdapter(nContacts2);
        recyclerViewAtt.setAdapter(adapter2);
        recyclerViewAtt.setNestedScrollingEnabled(false);

        adapter.setOnClickListener(new NContactAdapter.OnClickListener() {
            @Override
            public void onClick(int position, NContact ncontact) {
                NContact nContact = nContacts.get(position);

                new AlertDialog.Builder(Demandes.this)
                        .setTitle("Valider la demande")
                        .setMessage("Êtes-vous sûr de vouloir valider la demande de " + nContact.getName() + "?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Action à effectuer si l'utilisateur clique sur "Oui"
                                Toast.makeText(getApplicationContext(), "Action confirmée", Toast.LENGTH_SHORT).show();
                                String jsonEncoded = String.format("{\"id\":%d, \"id_1\":%d, \"password\":\"%s\"}",
                                        userId, ncontact.getId(), password);
                                Log.d("log d accept", jsonEncoded);
                                RetrofitInstance.getApiInterface().setDemande(jsonEncoded).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Log.d("log d put", "réussite");
                                        reset();
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.d("log d put", "fail");
                                        reset();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Ferme simplement la boîte de dialogue
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });
        adapter2.setOnClickListener(new NContactAdapter.OnClickListener() {
            @Override
            public void onClick(int position, NContact ncontact) {
                NContact nContact = nContacts2.get(position);

                new AlertDialog.Builder(Demandes.this)
                        .setTitle("Supprimer la demande")
                        .setMessage("Êtes-vous sûr de vouloir Supprimer la demande de " + nContact.getName() + "?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Action à effectuer si l'utilisateur clique sur "Oui"
                                Toast.makeText(getApplicationContext(), "Action confirmée", Toast.LENGTH_SHORT).show();
                                String jsonEncoded = String.format("{\"id\":%d, \"id_1\":%d, \"password\":\"%s\"}",
                                        userId, ncontact.getId(), password);
                                Log.d("log d accept", jsonEncoded);
                                RetrofitInstance.getApiInterface().delDemande(jsonEncoded).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Log.d("log d put", "réussite");
                                        reset();
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.d("log d put", "fail");
                                        reset();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Ferme simplement la boîte de dialogue
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        init();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}