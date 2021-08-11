package com.example.messenger.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.example.messenger.adapters.UsersRecyclerViewAdapter;
import com.example.messenger.models.User;
import com.example.messenger.models.UserLoggedIn;
import com.example.messenger.rest.ClientAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.messenger.models.Constants.*;

public class UsersFragment extends Fragment {

    private EditText searchInput;
    private RecyclerView users;
    private List<User> receivedUsers;
    private Retrofit retrofit;
    private ClientAPI clientAPI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        clientAPI = retrofit.create(ClientAPI.class);

        searchInput = view.findViewById(R.id.searchInput);
        users = view.findViewById(R.id.users);

        ((MainActivity) getActivity()).setDefaultBackActionBar();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = searchInput.getText().toString();
                if (text.isEmpty()) {
                    receivedUsers = new ArrayList<>();
                    updateData();
                    return;
                }
                clientAPI.getUsers(TOKEN_VALUE, text + " " + UserLoggedIn.getUser(getContext()).getLogin())
                        .enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                receivedUsers = response.body();
                                updateData();
                            }

                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {

                            }
                        });
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        ((MainActivity) getActivity()).setDefaultBackActionBar();
        super.onResume();
    }

    @Override
    public void onPause() {
        ((MainActivity) getActivity()).setDefaultActionBar();
        super.onPause();
    }

    public void updateData() {
        users.setAdapter(new UsersRecyclerViewAdapter(receivedUsers, getContext(), (MainActivity) getActivity()));
    }
}
