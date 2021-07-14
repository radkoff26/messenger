package com.example.messenger.rest;

import com.example.messenger.models.Chat;
import com.example.messenger.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ClientAPI {

    @POST("/createAccount")
    Call<String> register(
            @Header("token") String token,
            @Body User user
    );

    @POST("/login")
    Call<User> login(
            @Header("token") String token,
            @Body User user
    );

    @POST("/getChats")
    Call<List<Chat>> getChats(
            @Header("token") String token,
            @Body Integer userId
    );

    @POST("/getNotCheckedNumber")
    Call<Integer> getNotCheckedNumber(
            @Header("token") String token,
            @Body Integer userId
    );
}
