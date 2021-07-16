package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.adapters.FragmentAdapter;
import com.example.messenger.fragments.ChatFragment;
import com.example.messenger.fragments.ContactFragment;
import com.example.messenger.fragments.LoaderFragment;
import com.example.messenger.fragments.LoginFragment;
import com.example.messenger.fragments.MainFragment;
import com.example.messenger.fragments.RegisterFragment;
import com.example.messenger.models.DateConverter;
import com.example.messenger.models.User;
import com.example.messenger.models.UserLoggedIn;
import com.example.messenger.rest.ClientAPI;
import com.example.messenger.rest.ServerInteraction;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.messenger.models.Constants.BASE_URL;
import static com.example.messenger.models.Constants.BUNDLE_TYPE;
import static com.example.messenger.models.Constants.BUNDLE_TYPE_IS_LOGIN_CHECK;
import static com.example.messenger.models.Constants.TOKEN_VALUE;

public class MainActivity extends AppCompatActivity {

    private FrameLayout actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = findViewById(R.id.action_bar);

        actionBar.setVisibility(View.GONE);

        LoaderFragment fragment = new LoaderFragment();
        Bundle arguments = new Bundle();
        arguments.putString(BUNDLE_TYPE, BUNDLE_TYPE_IS_LOGIN_CHECK);
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, fragment)
                .commit();
    }

    public void removeButLastFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        int k = 0;
        if (fragments.size() == 2) {
            if (fragments.get(0).getClass() == MainFragment.class) {
                k = 1;
            }
        }
        for (int i = k; i < fragments.size() - 1; i++) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragments.get(i))
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        User user = UserLoggedIn.getUser(getApplicationContext());
        if (user != null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build();
            ClientAPI clientAPI = retrofit.create(ClientAPI.class);
            clientAPI.goOnline(TOKEN_VALUE, user.getId())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        User user = UserLoggedIn.getUser(getApplicationContext());
        if (user != null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build();
            ClientAPI clientAPI = retrofit.create(ClientAPI.class);
            clientAPI.goOffline(TOKEN_VALUE, user.getId())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (!fragments.isEmpty() && fragments.get(fragments.size() - 1).getClass() == LoaderFragment.class) {
            finish();
            return;
        }
        if (fragments.size() == 2) {
            if (fragments.get(0).getClass() == MainFragment.class && fragments.get(1).getClass() != ChatFragment.class
                    && fragments.get(1).getClass() != ContactFragment.class) {
                System.out.println(fragments.get(0).getClass());
                System.out.println(fragments.get(1).getClass());
                finish();
                return;
            }
        }
        if (fragments.size() == 1) {
            finish();
            return;
        }
        if (fragments.get(fragments.size() - 1).getClass() == ChatFragment.class) {
            setDefaultActionBar();
        }
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .remove(fragments.get(fragments.size() - 1))
                .commit();
    }

    public void setChatActionBar(User user) {
        if (user != null) {
            actionBar.removeAllViews();
            actionBar.setVisibility(View.VISIBLE);
            LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.chat_action_bar, new FrameLayout(getBaseContext()), false);
            ll.findViewById(R.id.back).setOnClickListener(v -> {
                onBackPressed();
            });
            ((TextView) ll.findViewById(R.id.nickname)).setText(user.getNickname());
            try {
                ((TextView) ll.findViewById(R.id.lastOnlineStatus)).setText(user.getIsOnline() ? "online" : DateConverter.lastOnline(user.getLastOnline()));
            } catch (ParseException e) {
                ((TextView) ll.findViewById(R.id.lastOnlineStatus)).setText("-");
            }
            actionBar.addView(ll);
        }
    }

    public void setDefaultActionBar() {
        actionBar.removeAllViews();
        actionBar.setVisibility(View.VISIBLE);
        actionBar.addView(getLayoutInflater().inflate(R.layout.default_action_bar, new FrameLayout(getBaseContext()), false));
    }

    public void setDefaultBackActionBar() {
        LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.default_back_action_bar, new FrameLayout(getBaseContext()), false);
        ll.findViewById(R.id.back).setOnClickListener(v -> {
            onBackPressed();
        });
        actionBar.removeAllViews();
        actionBar.setVisibility(View.VISIBLE);
        actionBar.addView(ll);
    }
}