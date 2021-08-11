package com.example.messenger.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.messenger.fragments.ChatsFragment;
import com.example.messenger.fragments.ProfileFragment;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Managing fragments on the TabLayout
        switch (position) {
            case 0:
                return new ChatsFragment();
            case 1:
                return new ProfileFragment();
        }
        return new Fragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}