package com.example.messenger.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentController;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.example.messenger.crypt.Crypt;
import com.example.messenger.crypt.RestCrypt;
import com.example.messenger.models.User;
import com.example.messenger.rest.ClientAPI;
import com.example.messenger.rest.DataLoader;
import com.example.messenger.rest.ServerInteraction;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.messenger.models.Constants.BUNDLE_ARGUMENT;
import static com.example.messenger.models.Constants.BUNDLE_ERROR;
import static com.example.messenger.models.Constants.BUNDLE_TYPE;
import static com.example.messenger.models.Constants.BUNDLE_TYPE_LOGIN;
import static com.example.messenger.models.Constants.TOKEN_VALUE;


public class LoginFragment extends Fragment {

    private Button toRegisterFragment, toLogin;
    private TextInputLayout login, password;
    private TextInputEditText login_input, password_input;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        toRegisterFragment = view.findViewById(R.id.toRegisterFragment);
        toLogin = view.findViewById(R.id.toLogin);
        login = view.findViewById(R.id.login);
        password = view.findViewById(R.id.password);
        login_input = view.findViewById(R.id.login_input);
        password_input = view.findViewById(R.id.password_input);

        Bundle extra = getArguments();
        if (extra != null && extra.getBoolean(BUNDLE_ERROR)) {
            password.setError(getString(R.string.wrong_data));
        }

        password_input.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                password.setError(null);
            }
        });

        toRegisterFragment.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.fragment, new RegisterFragment())
                    .commit();
        });


        toLogin.setOnClickListener(v -> {
            String mLogin = login_input.getText().toString();
            String mPassword = password_input.getText().toString();
            Crypt crypt = new Crypt(RestCrypt.KEY);
            User user = new User(mLogin, crypt.encode(mPassword));

            LoaderFragment fragment = new LoaderFragment();
            Bundle arguments = new Bundle();
            arguments.putSerializable(BUNDLE_ARGUMENT, user);
            arguments.putString(BUNDLE_TYPE, BUNDLE_TYPE_LOGIN);
            fragment.setArguments(arguments);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.fragment, fragment)
                    .commit();
        });

        return view;
    }


    public static class LoginLoader extends AsyncTaskLoader<User> {

        private User user, result;
        private boolean f = false;

        public LoginLoader(Context context, User user) {
            super(context);
            this.user = user;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Override
        public User loadInBackground() {
            result = null;
            f = false;
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://192.168.0.113:8080")
                    .build();
            ClientAPI clientAPI = retrofit.create(ClientAPI.class);
            clientAPI.login(TOKEN_VALUE, user)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            result = response.body();
                            f = true;
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            result = new User();
                            f = true;
                        }
                    });
            while (!f) {
            }
            return result;
        }
    }

    private class LoginLoaderCallbacks implements LoaderManager.LoaderCallbacks<User> {

        private User user;

        public LoginLoaderCallbacks(User user) {
            this.user = user;
        }

        @Override
        public Loader<User> onCreateLoader(int id, Bundle args) {
            return new LoginLoader(getActivity(), user);
        }

        @Override
        public void onLoadFinished(Loader<User> loader, User data) {
            Toast.makeText(getContext(), data.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLoaderReset(Loader<User> loader) {
        }
    }
}