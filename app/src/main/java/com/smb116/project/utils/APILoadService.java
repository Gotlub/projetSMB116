package com.smb116.project.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;




import com.smb116.project.model.Contact;
import com.smb116.project.model.SelfPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APILoadService extends Service {

    private final String TAG = APILoadService.class.getSimpleName();
    private List<Contact> contactList = new ArrayList<>();
    private InfoLoader infoLoader = null;
    private int refreshRate = 10;
    private  Messenger messager;

    public APILoadService() {
    }

    public List<Contact> getContactList() {
        return contactList;
    }

    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }
    private class InfoLoader extends Thread {
        private Boolean running = true;

        public void stopLoading() {
            running = false;
            interrupt(); //if thread sleeping()
        }

        private InfoLoader() {
        }

        public void run() {
           while(running) {
                getContact();
               setPosition();
               try {
                    sleep(refreshRate * 1000);
                } catch (InterruptedException e) {
                    Log.d("log d APIrunner", "Erreur: " + e);
                }
           }
        }

        private void getContact(){
            SelfPosition position = SelfPosition.getInstance();
            if(position != null) {
                String jsonEncoded = String.format("{\"id\":%d,\"name\":\"%s\", \"password\":\"%s\"}",
                        position.getId(), position.getName(), position.getMpd());
                Log.d("log d getContacte", jsonEncoded);
                RetrofitInstance.getApiInterface().getContactById(jsonEncoded).enqueue(new Callback<List<Contact>>() {
                    @Override
                    public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                        Log.d("log d getContact1", response.body().toString());
                        if (response.isSuccessful()) {
                            SelfPosition.getInstance().setContactList(response.body());
                        } else {
                            Log.d("log d API", "Erreur: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Contact>> call, Throwable t) {
                        Log.d("log d getContact fail", t.getLocalizedMessage());
                        Log.d("log d getContact fail", call.toString());
                    }
                });
            }
        }
    }

    private void setPosition() {
        SelfPosition position = SelfPosition.getInstance();
        long timeNow = System.currentTimeMillis();
        if(position != null) {
            String jsonEncoded = String.format(Locale.US,"{\"id\":%d,\"lat\":%.8f, \"lon\":%.8f, \"password\":\"%s\", \"time_\":%d}",
                    position.getId(), position.getLat(), position.getLon(), position.getMpd(), timeNow);
            Log.d("log d getPosition", jsonEncoded);
            RetrofitInstance.getApiInterface().setActuPos(jsonEncoded).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("log d setPosition", "isSuccessful");
                    } else {
                        Log.d("log d API", "Erreur: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d("log d setPosition fail", "setPosition fail");
                }
            });
        }
    }

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public APILoadService getService() {
            return APILoadService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void startInfoLoader(int refreshRate) {
        this.refreshRate = refreshRate;
        if(infoLoader == null) {

            infoLoader = new InfoLoader();
            infoLoader.start();
        }
    }

    public void stop() {
        infoLoader.stopLoading();
        infoLoader = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        if (infoLoader != null) {
            infoLoader.interrupt();
            infoLoader = null;
        }
    }

}