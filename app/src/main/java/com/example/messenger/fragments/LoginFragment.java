package com.example.messenger.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.messenger.R;
import com.example.messenger.crypt.Crypt;
import com.example.messenger.crypt.RestCrypt;
import com.example.messenger.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import static com.example.messenger.models.Constants.*;

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
}