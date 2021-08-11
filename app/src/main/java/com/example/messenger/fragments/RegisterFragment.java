package com.example.messenger.fragments;

import android.os.Bundle;

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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import static com.example.messenger.models.Constants.*;

public class RegisterFragment extends Fragment {

    private TextInputLayout password_repeat;
    private TextInputEditText login_input, password_input, nickname_input, password_repeat_input;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        Button toRegister = view.findViewById(R.id.toRegister);
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
}