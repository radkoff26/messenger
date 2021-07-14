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
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.R;
import com.example.messenger.adapters.ChatRecyclerViewAdapter;
import com.example.messenger.models.Chat;
import com.example.messenger.models.UserLoggedIn;
import com.example.messenger.rest.ClientAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.messenger.fragments.MainFragment.mChats;
import static com.example.messenger.models.Constants.BASE_URL;
import static com.example.messenger.models.Constants.TOKEN_VALUE;

public class ChatFragment extends Fragment {

    private RecyclerView chats;
    private ImageView loader;
    private ChatRecyclerViewAdapter adapter;
    private Runnable updateMessages;
    private Handler handler;
    private boolean isWorking = true;
    private List<Chat> mChats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        chats = view.findViewById(R.id.chats);
        loader = view.findViewById(R.id.loader);

        handler = new Handler();

        if (mChats == null) {
            loader.setVisibility(View.VISIBLE);
            loader.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.loader_rotating));
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        isWorking = true;

        updateMessages = () -> {
            if (isWorking) {

                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(BASE_URL)
                        .build();
                ClientAPI clientAPI = retrofit.create(ClientAPI.class);

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

                handler.postDelayed(updateMessages, 15000);
            }
        };

        updateMessages.run();
    }

    @Override
    public void onPause() {
        super.onPause();
        isWorking = false;
        handler.removeCallbacks(updateMessages);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void checkAdapter() {
        if (mChats != null) {
            if (chats.getAdapter() == null) {
                chats.setAdapter(new ChatRecyclerViewAdapter(mChats, getContext()));
                loader.clearAnimation();
                loader.setVisibility(View.GONE);
            } else {
                chats.swapAdapter(new ChatRecyclerViewAdapter(mChats, getContext()), true);
            }
        }
    }
}
