package com.example.messenger.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.example.messenger.adapters.FragmentAdapter;
import com.example.messenger.models.Chat;
import com.example.messenger.models.UserLoggedIn;
import com.example.messenger.rest.ClientAPI;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.messenger.models.Constants.*;

public class MainFragment extends Fragment {

    private FragmentAdapter fragmentAdapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Integer sumOfNotChecked;
    private Runnable updateServer;
    private Handler handler;
    private Retrofit retrofit;
    private ClientAPI clientAPI;
    public static List<Chat> mChats = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ((MainActivity) getActivity()).setDefaultActionBar();

        handler = new Handler();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        clientAPI = retrofit.create(ClientAPI.class);

        updateServer = () -> {
            updateData();
            handler.postDelayed(updateServer, 2000);
        };

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        clientAPI.goOnline(TOKEN_VALUE, UserLoggedIn.getUser(getContext()).getId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
        handler.post(updateServer);
    }

    @Override
    public void onPause() {
        clientAPI.goOffline(TOKEN_VALUE, UserLoggedIn.getUser(getContext()).getId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
        super.onPause();
        handler.removeCallbacks(updateServer);
    }

    public void updateData() {

        clientAPI.getNotCheckedNumber(TOKEN_VALUE, UserLoggedIn.getUser(getContext()).getId())
                .enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        Integer sum = response.body();

                        if (sum != null) {
                            setMessageBadge(sum);
                        }

                        if (sum != null && !sum.equals(sumOfNotChecked)) {
                            // TODO: 16.07.2021 Here is gonna be notification call
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {

                    }
                });
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
