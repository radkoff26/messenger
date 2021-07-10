package com.example.messenger.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentController;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import static com.example.messenger.models.Constants.TOKEN_VALUE;


public class LoginFragment extends Fragment {

    private Button toRegisterFragment, toLogin;
    private TextInputLayout login, password;
    private TextInputEditText login_input, password_input;

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
            LoaderManager.getInstance(this).initLoader(1, Bundle.EMPTY, new StubLoaderCallbacks(user));
        });

        return view;
    }

    public boolean validateInputs() {
        String mLogin = login_input.getText().toString();
        String mPassword = password_input.getText().toString();
        if (mPassword.trim().contains(" ")) {
            password.setError(getString(R.string.password_helper_text));
            return false;
        }
        return false;
    }

    public static class StubLoader extends AsyncTaskLoader<User> {

        private User user;

        public StubLoader(Context context, User user) {
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
            // emulate long-running operation
            final User[] result = {null};
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://192.168.0.120:8080")
                    .build();
            ClientAPI clientAPI = retrofit.create(ClientAPI.class);
            clientAPI.login(TOKEN_VALUE, user)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            result[0] = response.body();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            result[0] = new User();
                        }
                    });
            while (result[0] == null) {
            }
            return result[0];
        }
    }

    private class StubLoaderCallbacks implements LoaderManager.LoaderCallbacks<User> {

        private User user;

        public StubLoaderCallbacks(User user) {
            this.user = user;
        }

        @Override
        public Loader<User> onCreateLoader(int id, Bundle args) {
            return new StubLoader(getActivity(), user);
        }

        @Override
        public void onLoadFinished(Loader<User> loader, User data) {
            Toast.makeText(getContext(), data.getNickname(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLoaderReset(Loader<User> loader) {
            // Do nothing
        }
    }
}