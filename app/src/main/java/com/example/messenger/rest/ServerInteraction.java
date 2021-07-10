package com.example.messenger.rest;

import android.content.Context;
import android.widget.Toast;

import com.example.messenger.crypt.Crypt;
import com.example.messenger.crypt.DeviceCrypt;
import com.example.messenger.crypt.RestCrypt;
import com.example.messenger.models.User;
import com.example.messenger.models.UserLoggedIn;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.messenger.models.Constants.TOKEN_VALUE;

public class ServerInteraction {

    private static Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.0.120:8080")
            .build();

    private static ClientAPI clientAPI = retrofit.create(ClientAPI.class);

    public static boolean isLoggedIn(Context ctx) {
        User user = UserLoggedIn.getUser(ctx);
        if (user == null) {
            return false;
        }
        Crypt restCrypt = new Crypt(RestCrypt.KEY);
        Crypt deviceCrypt = new Crypt(DeviceCrypt.KEY);
        String passwordEncoded = restCrypt.encode(deviceCrypt.decode(user.getPassword()));
        user.setPassword(passwordEncoded);
        clientAPI.login(TOKEN_VALUE, user)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User mUser = response.body();
                        Toast.makeText(ctx, (mUser == null) + "", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(ctx, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        return true;
    }

    public static void registerUser(User user, Context ctx, String result) {
        final String[] finalResult = new String[1];
        clientAPI.register(TOKEN_VALUE, user)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        finalResult[0] = response.body();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        finalResult[0] = null;
                    }
                });
    }
}
