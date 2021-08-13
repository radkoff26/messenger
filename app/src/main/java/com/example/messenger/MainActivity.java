package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.messenger.fragments.ChatFragment;
import com.example.messenger.fragments.UsersFragment;
import com.example.messenger.fragments.LoaderFragment;
import com.example.messenger.fragments.MainFragment;
import com.example.messenger.models.DateConverter;
import com.example.messenger.models.User;
import com.example.messenger.models.UserLoggedIn;
import com.example.messenger.rest.ClientAPI;
import com.example.messenger.view_models.RoundedImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.messenger.models.Constants.*;

public class MainActivity extends AppCompatActivity {

    private FrameLayout actionBar;
    private ClientAPI clientAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = findViewById(R.id.action_bar);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        actionBar.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build();

        clientAPI = retrofit.create(ClientAPI.class);

        // Transaction and setting arguments up
        LoaderFragment fragment = new LoaderFragment();
        Bundle arguments = new Bundle();
        arguments.putString(BUNDLE_TYPE, BUNDLE_TYPE_IS_LOGIN_CHECK);
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, fragment)
                .commit();
    }

    public void removeButLastFragment() {
        // If the first fragment is MainFragment, then it's not going to be removed
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Resolving avatar updating
            Uri content_describer = data.getData();

            try {
                InputStream in = getContentResolver().openInputStream(content_describer);

                byte[] dataFile = IOUtil.toByteArray(in);

                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), dataFile);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", "file", requestBody);

                User mUser = UserLoggedIn.getUser(getApplicationContext());

                // Sending avatar to the server
                if (mUser != null) {
                    clientAPI.setAvatar(TOKEN_VALUE, mUser.getId(), fileToUpload)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    String url = response.body();

                                    // Saving avatar url to the SharedPreferences
                                    SharedPreferences sp = getSharedPreferences(USER_DATA, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString(AVATAR_URL, url);
                                    editor.apply();
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                }
                            });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                    && fragments.get(1).getClass() != UsersFragment.class) {
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

            ll.findViewById(R.id.back).setOnClickListener(v -> onBackPressed());

            ((TextView) ll.findViewById(R.id.nickname)).setText(user.getNickname());

            try {
                ((TextView) ll.findViewById(R.id.lastOnlineStatus)).setText(user.getIsOnline() ? "online" : DateConverter.lastOnline(user.getLastOnline()));
            } catch (ParseException e) {
                ((TextView) ll.findViewById(R.id.lastOnlineStatus)).setText("-");
            }

            if (user.getAvatarUrl() != null && !user.getAvatarUrl().equals("null")) {
                Picasso.with(getApplicationContext())
                        .load(BASE_URL + "/getAvatar?url=" + user.getAvatarUrl())
                        .placeholder(R.drawable.user_round)
                        .into((RoundedImageView) ll.findViewById(R.id.user_avatar));
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

        ll.findViewById(R.id.back).setOnClickListener(v -> onBackPressed());

        actionBar.removeAllViews();
        actionBar.setVisibility(View.VISIBLE);
        actionBar.addView(ll);
    }
}