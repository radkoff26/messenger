package com.example.messenger.rest;

import com.example.messenger.models.Chat;
import com.example.messenger.models.Message;
import com.example.messenger.models.MessageSending;
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

    @POST("/getMessages")
    Call<List<Message>> getMessages(
            @Header("token") String token,
            @Body String chatId
    );

    @POST("/getPersonalData")
    Call<User> getUser(
            @Header("token") String token,
            @Body Integer userId
    );

    @POST("/sendMessage")
    Call<ResponseBody> sendMessage(
            @Header("token") String token,
            @Body MessageSending message
    );

    @POST("/watchMessage")
    Call<ResponseBody> watchMessage(
            @Header("token") String token,
            @Body String body
    );

    @POST("/removeMessage")
    Call<ResponseBody> removeMessage(
            @Header("token") String token,
            @Body String body
    );

    @POST("/goOnline")
    Call<ResponseBody> goOnline(
            @Header("token") String token,
            @Body Integer user_id
    );

    @POST("/goOffline")
    Call<ResponseBody> goOffline(
            @Header("token") String token,
            @Body Integer user_id
    );

    @POST("/isUserOnline")
    Call<Boolean> isUserOnline(
            @Header("token") String token,
            @Body Integer user_id
    );

    @POST("/getUsers")
    Call<List<User>> getUsers(
            @Header("token") String token,
            @Body String body
    );
}
