package com.smb116.project.utils;

import com.smb116.project.model.Contact;
import com.smb116.project.model.NContact;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("contact/{json}")
    Call<List<Contact>> getContactById(@Path(value = "json", encoded = true) String jsonEncoded);

    @GET("demande/{json}")
    Call<List<NContact>> getDemandeAtt(@Path(value = "json", encoded = true) String jsonEncoded);

    @GET("attente/{json}")
    Call<List<NContact>> getMesDemande(@Path(value = "json", encoded = true) String jsonEncoded);

    @GET("all/{json}")
    Call<List<NContact>> getAllUser(@Path(value = "json", encoded = true) String jsonEncoded);

    @POST("demande/{json}")
    Call<Void> postDemande(@Path(value = "json", encoded = true) String jsonEncoded);


    @PUT("accept/{json}")
    Call<Void> setDemande(@Path(value = "json", encoded = true) String jsonEncoded);

    @DELETE("demand/{json}")
    Call<Void> delDemande(@Path(value = "json", encoded = true) String jsonEncoded);
}