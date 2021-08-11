package com.example.messenger.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.messenger.R;
import com.example.messenger.models.User;
import com.example.messenger.models.UserLoggedIn;
import com.example.messenger.rest.ClientAPI;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.messenger.models.Constants.*;

public class ProfileFragment extends Fragment {

    private Retrofit retrofit;
    private ClientAPI clientAPI;
    private ImageView avatar;
    private TextView nickname, login;
    private User user;
    private String url;
    private Handler handler;
    private Runnable runnable;
    private MaterialButton uploadPhoto, logout;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build();
        clientAPI = retrofit.create(ClientAPI.class);

        avatar = view.findViewById(R.id.avatar);
        nickname = view.findViewById(R.id.nickname);
        login = view.findViewById(R.id.login);
        uploadPhoto = view.findViewById(R.id.upload_photo);
        logout = view.findViewById(R.id.logout);

        user = UserLoggedIn.getUser(getContext());

        nickname.setText(user.getNickname());
        login.setText("@" + user.getLogin());

        url = user.getAvatarUrl();

        handler = new Handler();

        runnable = () -> {
            clientAPI.getUser(TOKEN_VALUE, user.getId())
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            User mUser = response.body();
                            if (mUser != null) {
                                user = mUser;
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {

                        }
                    });
            if (!url.equals(user.getAvatarUrl())) {
                updateAvatar();
            }
            handler.postDelayed(runnable, 1000);
        };

        updateAvatar();

        uploadPhoto.setOnClickListener(v -> uploadFile());
        logout.setOnClickListener(v -> logout());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(runnable);
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    private void uploadFile() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("image/*");
        getActivity().startActivityForResult(
                Intent.createChooser(chooseFile, "Choose an image"),
                1
        );
    }

    public void updateAvatar() {
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().equals("null")) {
            Picasso.with(getContext())
                    .load(BASE_URL + "/getAvatar?url=" + user.getAvatarUrl())
                    .placeholder(R.drawable.user_round)
                    .into(avatar);
            url = user.getAvatarUrl();
        }
    }

    public void logout() {
        SharedPreferences sp = getActivity().getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
        clientAPI.goOffline(TOKEN_VALUE, user.getId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }
}
