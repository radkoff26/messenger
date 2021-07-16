package com.example.messenger.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.example.messenger.adapters.ChatsRecyclerViewAdapter;
import com.example.messenger.models.Chat;
import com.example.messenger.models.UserLoggedIn;
import com.example.messenger.rest.ClientAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.messenger.models.Constants.BASE_URL;
import static com.example.messenger.models.Constants.TOKEN_VALUE;

public class ChatsFragment extends Fragment {

    private RecyclerView chats;
    private ImageView loader;
    private Runnable updateMessages;
    private Handler handler;
    private List<Chat> mChats;
    private Retrofit retrofit;
    private ClientAPI clientAPI;
    private FloatingActionButton search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        chats = view.findViewById(R.id.chats);
        loader = view.findViewById(R.id.loader);
        search = view.findViewById(R.id.search);

        handler = new Handler();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        clientAPI = retrofit.create(ClientAPI.class);

        updateMessages = () -> {
            updateData();
            handler.postDelayed(updateMessages, 2000);
        };

        if (mChats == null) {
            loader.setVisibility(View.VISIBLE);
            loader.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.loader_rotating));
        }

        search.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.fragment, new ContactFragment())
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        handler.post(updateMessages);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateMessages);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void updateData() {
        clientAPI.getChats(TOKEN_VALUE, UserLoggedIn.getUser(getContext()).getId())
                .enqueue(new Callback<List<Chat>>() {
                    @Override
                    public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {
                        mChats = response.body();
                        checkAdapter();
                    }

                    @Override
                    public void onFailure(Call<List<Chat>> call, Throwable t) {
                        mChats = new ArrayList<>();
                        checkAdapter();
                    }
                });
    }

    public void checkAdapter() {
        if (mChats != null) {
            chats.setAdapter(new ChatsRecyclerViewAdapter(mChats, getContext(), (MainActivity) getActivity()));
            loader.clearAnimation();
            loader.setVisibility(View.GONE);
        }
    }
}
