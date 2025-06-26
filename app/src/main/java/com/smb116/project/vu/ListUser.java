package com.smb116.project.vu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.smb116.project.R;
import com.smb116.project.model.NContact;
import com.smb116.project.utils.AllUserAdapter;
import com.smb116.project.utils.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListUser extends AppCompatActivity {

    private EditText searchBox;
    private ListView listView;
    private AllUserAdapter adapter;
    private List<NContact> utilisateurs;
    private int userId;
    private String name;
    private String password;

    private void loadAllUser() {
        String jsonEncoded = String.format("{\"id\":%d}",
                userId);
        RetrofitInstance.getApiInterface().getAllUser(jsonEncoded).enqueue(new Callback<List<NContact>>() {
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

    public void udapteList(List<NContact> lesContactes) {
        utilisateurs.clear();
        utilisateurs.addAll(lesContactes);
        //adapter.setOriginalList(utilisateurs);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_user);
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        Log.d("log d listuser", String.valueOf(userId));
        name = intent.getStringExtra("name");
        password = intent.getStringExtra("password");
        searchBox = findViewById(R.id.searchBox);
        listView = findViewById(R.id.listView);
        utilisateurs = new ArrayList<>();
        adapter = new AllUserAdapter(this, utilisateurs);
        listView.setAdapter(adapter);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            NContact nContact = adapter.getItem(position);
            if (nContact != null) {
                Toast.makeText(ListUser.this, "Sélectionné : " + nContact.getName(), Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(ListUser.this)
                        .setTitle("Effectuer la demande")
                        .setMessage("Êtes-vous sûr de vouloir effectuer la demande de " + nContact.getName() + "?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Action à effectuer si l'utilisateur clique sur "Oui"
                                Toast.makeText(getApplicationContext(), "Demande confirmée", Toast.LENGTH_SHORT).show();
                                String jsonEncoded = String.format("{\"id\":%d, \"id_1\":%d, \"password\":\"%s\"}",
                                        userId, nContact.getId(), password);
                                Log.d("log d accept", jsonEncoded);
                                RetrofitInstance.getApiInterface().postDemande(jsonEncoded).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Log.d("log d put", "réussite");
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.d("log d put", "fail");
                                        finish();
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
        loadAllUser();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}