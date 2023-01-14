//COMMENT: UNUSED CLASS, I WROTE IT BUT I ENDED UP SCRAPPING IT.
// DON'T WORRY ABOUT IT :)
//BOY I SURE HAVE A LOT OF FREE TIME TO WRITE CLASSES AND NOT USING THEM

package com.spotify.sdk.demo.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.spotify.android.appremote.demo.R;
import com.spotify.protocol.client.ErrorCallback;

import com.spotify.sdk.demo.DataImporter;
import com.spotify.sdk.demo.MyDB;
import com.spotify.sdk.demo.MyHelper;
import com.spotify.sdk.demo.MyMainViewPagerAdapter;
import com.spotify.sdk.demo.RecyclerViewClickInterface;
import com.spotify.sdk.demo.activity.RemotePlayerActivity;

public class RunManualFragment extends Fragment implements AdapterView.OnItemClickListener,RecyclerViewClickInterface,View.OnClickListener {

    private final String TAG = "PLAYLIST_TAB";

    private final ErrorCallback mErrorCallback = this::logError;
    MyDB db;
    MyHelper helper;

    RecyclerView myRV;



    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MyMainViewPagerAdapter myViewPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("LIST",DataImporter.list.toString());
        db = new MyDB(getContext());
        helper = new MyHelper(getContext());

        return inflater.inflate(R.layout.fragment_run_manual, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        myRV = view.findViewById(R.id.runPlaylistRV);
//        myRV.setLayoutManager(new LinearLayoutManager(getContext()));
//        myRV.setHasFixedSize(true);
//        MyRVAdapter myRVAdapter = new MyRVAdapter(DataImporter.list,getContext(),this);
//        myRV.setAdapter(new MyRVAdapter(DataImporter.list,getContext(),this));
//        Log.e("LIST",DataImporter.list.toString());
//        myRV.setOnClickListener(this);
//        myRVAdapter.notifyDataSetChanged();
    }


    //    public void getDataOnButtonClicked(View view){
////        playUri("spotify:track:0qwLyQTJsp1MyPIuFpLd9f");
//        Log.i(TAG,"getDataOnButtonClicked");
//        try {
//            DataImporter.loadFiveEntries(getContext());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void onPlayMusicButtonClicked(View view){
//        Log.i(TAG,"onPlayMusicButtonClicked");
//        Log.i(TAG,db.getSelectedData("The Phantom"));
//    }

    private void playUri(String uri) {
        RemotePlayerActivity.mSpotifyAppRemote
                .getPlayerApi()
                .play(uri)
                .setResultCallback(empty -> logMessage(getString(R.string.command_feedback, "play")))
                .setErrorCallback(mErrorCallback);
    }

    private void logMessage(String msg) {
        logMessage(msg, Toast.LENGTH_SHORT);
    }

    private void logMessage(String msg, int duration) {
        Toast.makeText(getContext(), msg, duration).show();
        Log.d(TAG, msg);
    }

    private void logError(Throwable throwable) {
        Toast.makeText(getContext(), R.string.err_generic_toast, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "", throwable);
    }

    @Override
    public void onRecyclerItemClick(int position) {
        Log.e(TAG,db.getSelectedData(position+1).toString());
        playUri(db.getSelectedData(position+1).get(1));
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}