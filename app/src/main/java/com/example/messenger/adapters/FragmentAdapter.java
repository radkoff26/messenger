package com.example.messenger.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.messenger.fragments.Contacts;
import com.example.messenger.fragments.Messages;
import com.example.messenger.fragments.Profile;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Messages();
            case 1:
                return new Contacts();
            case 2:
                return new Profile();
        }
        return new Fragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}