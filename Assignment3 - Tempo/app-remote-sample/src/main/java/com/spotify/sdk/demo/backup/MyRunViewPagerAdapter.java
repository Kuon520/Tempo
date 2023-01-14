package com.spotify.sdk.demo.backup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.spotify.sdk.demo.fragments.PlaylistFragment;
import com.spotify.sdk.demo.fragments.RunFragment;
import com.spotify.sdk.demo.fragments.RunManualFragment;
import com.spotify.sdk.demo.fragments.YouFragment;

public class MyRunViewPagerAdapter extends FragmentStateAdapter {
    public MyRunViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new RunManualFragment();
            case 1:
                return new PlaylistFragment();
            default:
                return new RunFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
