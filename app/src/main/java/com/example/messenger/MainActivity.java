package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;
import org.apache.commons.io.IOUtils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import com.example.messenger.adapters.FragmentAdapter;
import com.example.messenger.fragments.LoginFragment;
import com.example.messenger.fragments.MainFragment;
import com.example.messenger.fragments.RegisterFragment;
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

public class MainActivity extends AppCompatActivity {

    private FragmentAdapter fragmentAdapter;
    private ViewPager2 viewPager;
    private Retrofit retrofit;
    private ClientAPI clientAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ServerInteraction.isLoggedIn(this)) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new MainFragment())
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new LoginFragment())
                    .commit();
        }

    }

    public void removeButLastFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (int i = 0; i < fragments.size() - 1; i++) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragments.get(i))
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() == 1) {
            finish();
            return;
        }
        if (fragments.get(fragments.size() - 1).getClass() == RegisterFragment.class) {
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .remove(fragments.get(fragments.size() - 1))
                    .commit();
            return;
        }
        getSupportFragmentManager().beginTransaction()
                .remove(fragments.get(fragments.size() - 1))
                .commit();
    }
}