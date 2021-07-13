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
import android.widget.Toast;

import com.example.messenger.adapters.FragmentAdapter;
import com.example.messenger.fragments.LoaderFragment;
import com.example.messenger.fragments.LoginFragment;
import com.example.messenger.fragments.MainFragment;
import com.example.messenger.fragments.RegisterFragment;
import com.example.messenger.models.User;
import com.example.messenger.models.UserLoggedIn;
import com.example.messenger.rest.ClientAPI;
import com.example.messenger.rest.ServerInteraction;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

    private FragmentAdapter fragmentAdapter;
    private ViewPager2 viewPager;
    private Retrofit retrofit;
    private ClientAPI clientAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (!fragments.isEmpty() && fragments.get(fragments.size() - 1).getClass() == LoaderFragment.class) {
            finish();
            return;
        }
        if (fragments.size() == 2) {
            if (fragments.get(0).getClass() == MainFragment.class) {
                finish();
                return;
            }
        }
        if (fragments.size() == 1) {
            finish();
            return;
        }
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .remove(fragments.get(fragments.size() - 1))
                .commit();
    }
}