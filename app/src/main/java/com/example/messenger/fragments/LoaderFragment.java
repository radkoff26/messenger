package com.example.messenger.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.example.messenger.crypt.Crypt;
import com.example.messenger.crypt.DeviceCrypt;
import com.example.messenger.crypt.RestCrypt;
import com.example.messenger.models.User;
import com.example.messenger.models.UserLoggedIn;
import com.example.messenger.rest.ClientAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.messenger.models.Constants.*;

public class LoaderFragment extends Fragment {

    public static final String FALSE = "false";
    public static final String TRUE = "true";

    private ImageView loader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loader, container, false);

        Bundle bundle = getArguments();

        loader = view.findViewById(R.id.loader);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.loader_rotating);

        loader.startAnimation(animation);

        User user = (User) bundle.getSerializable(BUNDLE_ARGUMENT);
        String type = bundle.getString(BUNDLE_TYPE);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        ClientAPI clientAPI = retrofit.create(ClientAPI.class);

        switch (type) {
            case BUNDLE_TYPE_LOGIN:
                clientAPI.login(TOKEN_VALUE, user)
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                User result = response.body();
                                SharedPreferences sp = getActivity().getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                Crypt restCrypt = new Crypt(RestCrypt.KEY);
                                Crypt internalCrypt = new Crypt(DeviceCrypt.KEY);
                                editor.putInt(ID, result.getId());
                                editor.putString(NICKNAME, result.getNickname());
                                editor.putString(LOGIN, result.getLogin());
                                editor.putString(PASSWORD, internalCrypt.encode(restCrypt.decode(result.getPassword())));
                                Log.d("DEBUG", restCrypt.decode(result.getPassword()));
                                editor.putString(CHAT_CONTAINER_NAME, result.getChatContainerName());
                                editor.putString(AVATAR_URL, result.getAvatarUrl() == null ? "" : result.getAvatarUrl());
                                editor.apply();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                                        .add(R.id.fragment, new MainFragment())
                                        .commit();
                                ((MainActivity) getActivity()).removeButLastFragment();
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                ((MainActivity) getActivity()).removeButLastFragment();
                                LoginFragment fragment = new LoginFragment();
                                Bundle args = new Bundle();
                                args.putBoolean(BUNDLE_ERROR, true);
                                fragment.setArguments(args);
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                                        .add(R.id.fragment, fragment)
                                        .commit();
                                ((MainActivity) getActivity()).removeButLastFragment();
                            }
                        });
                break;
            case BUNDLE_TYPE_REGISTER:
                clientAPI.register(TOKEN_VALUE, user)
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                String result = response.body();
                                ((MainActivity) getActivity()).removeButLastFragment();
                                if (result.equals(FALSE)) {
                                    RegisterFragment fragment = new RegisterFragment();
                                    Bundle args = new Bundle();
                                    args.putBoolean(BUNDLE_ERROR, true);
                                    fragment.setArguments(args);
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                                            .add(R.id.fragment, fragment)
                                            .commit();
                                } else if (result.equals(TRUE)) {
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                            .add(R.id.fragment, new LoginFragment())
                                            .commit();
                                } else {
                                    getActivity().finish();
                                }
                                ((MainActivity) getActivity()).removeButLastFragment();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                getActivity().finish();
                            }
                        });
                break;
            case BUNDLE_TYPE_IS_LOGIN_CHECK:
                User mUser = UserLoggedIn.getUser(getContext());
                if (mUser != null) {
                    mUser.setPassword(new Crypt(RestCrypt.KEY).encode(mUser.getPassword()));
                }
                clientAPI.login(TOKEN_VALUE, mUser)
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                User user = response.body();
                                if (user != null) {
                                    Crypt crypt = new Crypt(RestCrypt.KEY);
                                    user.setPassword(crypt.encode(user.getPassword()));
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .add(R.id.fragment, new MainFragment())
                                            .commit();
                                    ((MainActivity) getActivity()).removeButLastFragment();
                                    return;
                                }
                                onFailure(call, new Exception());
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .add(R.id.fragment, new LoginFragment())
                                        .commit();
                            }
                        });
                break;
        }


        return view;
    }
}