package com.example.messenger.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.example.messenger.adapters.ChatRecyclerViewAdapter;
import com.example.messenger.adapters.FragmentAdapter;
import com.example.messenger.models.Chat;
import com.example.messenger.models.UserLoggedIn;
import com.example.messenger.rest.ClientAPI;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.messenger.models.Constants.BASE_URL;
import static com.example.messenger.models.Constants.TOKEN_VALUE;

public class MainFragment extends Fragment {

    private FragmentAdapter fragmentAdapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Integer sumOfNotChecked;
    private Runnable updateServer;
    private Handler handler;
    private ImageView loader;
    public static List<Chat> mChats = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        loader = view.findViewById(R.id.loader);

        handler = new Handler();

        updateServer = () -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build();
            ClientAPI clientAPI = retrofit.create(ClientAPI.class);

            clientAPI.getNotCheckedNumber(TOKEN_VALUE, UserLoggedIn.getUser(getContext()).getId())
                    .enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            Integer sum = response.body();

                            if (sum != null && !sum.equals(sumOfNotChecked)) {
                                setMessageBadge(sum);
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {

                        }
                    });
            handler.postDelayed(updateServer, 20000);
        };

        updateServer.run();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fragmentAdapter = new FragmentAdapter(this);
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(0);
        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((MainActivity) getActivity()).removeButLastFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Messages");
                            break;
                        case 1:
                            tab.setText("Contacts");
                            break;
                        case 2:
                            tab.setText("Profile");
                            break;
                    }
                }
        ).attach();
    }

    public void setMessageBadge(int n) {
        sumOfNotChecked = n;
        if (n > 0) {
            tabLayout.getTabAt(0).getOrCreateBadge().setNumber(n);
            return;
        }
        tabLayout.getTabAt(0).removeBadge();
    }
}
