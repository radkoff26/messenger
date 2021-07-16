package com.example.messenger.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.messenger.R;
import com.example.messenger.crypt.Crypt;
import com.example.messenger.crypt.RestCrypt;
import com.example.messenger.models.User;
import com.example.messenger.rest.ClientAPI;
import com.google.android.material.snackbar.Snackbar;
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
import static com.example.messenger.models.Constants.BUNDLE_TYPE_REGISTER;
import static com.example.messenger.models.Constants.TOKEN_VALUE;


public class RegisterFragment extends Fragment {

    private TextInputLayout login, password, nickname, password_repeat;
    private TextInputEditText login_input, password_input, nickname_input, password_repeat_input;
    private static final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    );

    static {
        params.setMargins(0, 0, 0, 36);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        Button toRegister = view.findViewById(R.id.toRegister);
        login = view.findViewById(R.id.login);
        password = view.findViewById(R.id.password);
        nickname = view.findViewById(R.id.nickname);
        password_repeat = view.findViewById(R.id.password_repeat);
        login_input = view.findViewById(R.id.login_input);
        password_input = view.findViewById(R.id.password_input);
        nickname_input = view.findViewById(R.id.nickname_input);
        password_repeat_input = view.findViewById(R.id.password_repeat_input);

        Bundle extra = getArguments();
        if (extra != null && extra.getBoolean(BUNDLE_ERROR)) {
            password_repeat.setError(getString(R.string.user_exists));
        }

        toRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                String mNickname = nickname_input.getText().toString();
                String mLogin = login_input.getText().toString();
                String mPassword = password_input.getText().toString();
                Crypt crypt = new Crypt(RestCrypt.KEY);
                User user = new User(mNickname, mLogin, crypt.encode(mPassword));
                LoaderFragment fragment = new LoaderFragment();
                Bundle arguments = new Bundle();
                arguments.putSerializable(BUNDLE_ARGUMENT, user);
                arguments.putString(BUNDLE_TYPE, BUNDLE_TYPE_REGISTER);
                fragment.setArguments(arguments);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(R.id.fragment, fragment)
                        .commit();
            }
        });

        return view;
    }

    public boolean validateInputs() {
        String mNickname = nickname_input.getText().toString();
        String mLogin = login_input.getText().toString();
        String mPassword = password_input.getText().toString();
        String mPasswordRepeat = password_repeat_input.getText().toString();
        boolean isPasswordCorrect = false, isLoginCorrect = false;
        char n;
        for (int i = 0; i < mPassword.length(); i++) {
            n = mPassword.charAt(i);
            if (
                    (n >= 'a' && n <= 'z') || (n >= '0' && n <= '9')
            ) {
                isPasswordCorrect = true;
            } else {
                isPasswordCorrect = false;
                break;
            }
        }
        for (int i = 0; i < mLogin.length(); i++) {
            n = mLogin.charAt(i);
            if (
                    (n >= 'a' && n <= 'z') || (n >= '0' && n <= '9')
            ) {
                isLoginCorrect = true;
            } else {
                isLoginCorrect = false;
                break;
            }
        }
        if (mNickname.trim().isEmpty()) {
            Snackbar.make(getView(), getString(R.string.empty_nickname_error), Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();
            return false;
        }
        if (!isLoginCorrect) {
            Snackbar.make(getView(), getString(R.string.login_error_text), Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();
            return false;
        }
        if (mPassword.contains(" ") || mPassword.isEmpty() || !isPasswordCorrect) {
            Snackbar.make(getView(), getString(R.string.password_error_text), Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();
            return false;
        }
        if (!mPasswordRepeat.equals(mPassword)) {
            Snackbar.make(getView(), getString(R.string.password_mismatch_error), Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();
            return false;
        }
        if (mPassword.length() < 6) {
            Snackbar.make(getView(), getString(R.string.password_length_error), Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();
            return false;
        }
        return true;
    }

    public static class RegistrationLoader extends AsyncTaskLoader<String> {

        private User user;
        private boolean f = false;
        private String result;

        public RegistrationLoader(Context context, User user) {
            super(context);
            this.user = user;
        }

        @Override
        protected void onStartLoading() {
            Log.d("DEBUG", "Started Loading");
            super.onStartLoading();
            forceLoad();
        }

        @Override
        public String loadInBackground() {
            f = false;
            result = null;
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://192.168.0.113:8080")
                    .build();
            ClientAPI clientAPI = retrofit.create(ClientAPI.class);
            Log.d("DEBUG", "CREATED");
            clientAPI.register(TOKEN_VALUE, user)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            result = response.body();
                            f = true;
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            result = "";
                            f = true;
                        }
                    });
            Log.d("DEBUG", "loadInBackground: " + result);
            while (!f) {
                Log.d("DEBUG", "loadInBackground: " + 1);
            }
            Log.d("DEBUG", "loadInBackground: " + result);
            return result;
        }
    }

    private class RegistrationLoaderCallbacks implements LoaderManager.LoaderCallbacks<String> {

        private User user;

        public RegistrationLoaderCallbacks(User user) {
            this.user = user;
        }

        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            return new RegistrationLoader(getContext(), user);
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {
            Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {
            loader.startLoading();
        }
    }
}