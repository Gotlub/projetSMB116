package com.smb116.project.utils;

import com.smb116.project.model.Contact;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("contact/{json}")
    Call<List<Contact>> getContactById(@Path(value = "json", encoded = true) String jsonEncoded);
}