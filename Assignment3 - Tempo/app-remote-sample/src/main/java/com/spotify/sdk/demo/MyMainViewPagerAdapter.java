//I DONT CARE ABOUT FRAGMENTS ANYMORE

package com.spotify.sdk.demo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.spotify.sdk.demo.fragments.PlaylistFragment;
import com.spotify.sdk.demo.fragments.RunFragment;
import com.spotify.sdk.demo.fragments.YouFragment;

public class MyMainViewPagerAdapter extends FragmentStateAdapter {
    public MyMainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new RunFragment();
            case 1:
                return new PlaylistFragment();
            case 2:
                return new YouFragment();
            default:
                return new RunFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
