//COMMENT: UNUSED CLASS, I WROTE IT BUT I ENDED UP SCRAPPING IT.
// DON'T WORRY ABOUT IT :)

package com.spotify.sdk.demo.fragments;

import static android.content.Context.SENSOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.spotify.android.appremote.demo.R;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.sdk.demo.DataImporter;
import com.spotify.sdk.demo.MyDB;
import com.spotify.sdk.demo.MyHelper;
import com.spotify.sdk.demo.MyRVAdapter;
import com.spotify.sdk.demo.RecyclerViewClickInterface;
import com.spotify.sdk.demo.activity.RemotePlayerActivity;
import com.spotify.sdk.demo.backup.MyRunViewPagerAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class RunFragment extends Fragment implements SensorEventListener,AdapterView.OnItemClickListener, RecyclerViewClickInterface,View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    private final String TAG = "RUN_FRAGMENT";
    private final ErrorCallback mErrorCallback = this::logError;

    MyDB db;
    MyHelper helper;

    protected View thisView;

    RecyclerView myRV;
    RecyclerViewClickInterface myRVInterface;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MyRunViewPagerAdapter myRunViewPagerAdapter;
    SeekBar manualBpmSeekbar;

    ArrayList<ArrayList<String>> filteredBPMList;
    private int currentBPM;


    //SENSORRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
    private TextView stepCounter, stepDetector, timerText, stepPreM;
    private SensorManager sensorManager;
    private Sensor mStepCounter, mStepDetector;

    private boolean isCounterSensorPresent, isDetectorSensorPresent;

    int stepCount = 0;

    int stepDetect = 0, currentStepDetect = 0, deltaStepDetect = 0;

    Timer myTimer;

    TimerTask timerTask;

    Double time = 0.0;


    int fiveSecondCount = 5;

    int storeValue0 = 0, storeValue1 = 0, storeValue2 = 0, storeValue3 =0, storeValue4 =0, storeValue5 =0;

    long footStepTotal = 0, arrStepPerMin = 0;;

    int stepPerMin = 0;

    boolean timerStarted = false;

//    step counter - The step counter sensor provides the number of steps taken by the user since the last reboot while the sensor was activated.
//    step detector - The step detector sensor triggers an event each time the user takes a step.





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        db = new MyDB(getContext());
        helper = new MyHelper(getContext());

        return inflater.inflate(R.layout.fragment_run, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
//        tabLayout = view.findViewById(R.id.run_tab_layout);
//        viewPager2= view.findViewById(R.id.run_view_pager);
//        myRunViewPagerAdapter=new MyRunViewPagerAdapter(getActivity());
//        viewPager2.setAdapter(myRunViewPagerAdapter);

        manualBpmSeekbar =view.findViewById(R.id.bpmSeekBar);
        thisView = view;
        manualBpmSeekbar.setOnSeekBarChangeListener(this);

        myRV = view.findViewById(R.id.runPlaylistRV);
        myRV.setLayoutManager(new LinearLayoutManager(getContext()));
        myRV.setHasFixedSize(true);
        MyRVAdapter myRVAdapter = new MyRVAdapter(DataImporter.list,getContext(),this,false);
        myRV.setAdapter(new MyRVAdapter(DataImporter.list,getContext(),this,false));
        Log.e("LIST",DataImporter.list.toString());
        myRV.setOnClickListener(this);
        myRVInterface = this;
        myRVAdapter.notifyDataSetChanged();

        //SENSORRRRRRRRRRRRRRRRR
        if(ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){ //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }


//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//
//        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
//        {
//            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//
//            isCounterSensorPresent = true;
//        } else {
//            stepCounter.setText("Counter Sensor is not present");
//
//            isCounterSensorPresent = false;
//        }
//
//        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)
//        {
//            mStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
//            isDetectorSensorPresent = true;
//        } else
//        {
//            stepDetector.setText("Detector Sensor is not Present");
//            isDetectorSensorPresent = false;
//        }
//
//        myTimer = new Timer();

//        startTimer();
    }

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


    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        try {
            seekBar.setMax(150);
            currentBPM = seekBar.getProgress()*seekBar.getMax()/100;
            ((TextView) thisView.findViewById(R.id.bpmSeekbarValueTV)).setText(currentBPM + "");
            Log.i("RUN_FRAGMENT", "yeah that's good");
            filteredBPMList = db.getSelectedDataTempoRange(currentBPM - 5, currentBPM + 5);

            myRV = thisView.findViewById(R.id.runPlaylistRV);
            myRV.setLayoutManager(new LinearLayoutManager(getContext()));
            myRV.setHasFixedSize(true);
            MyRVAdapter myRVAdapter = new MyRVAdapter(filteredBPMList, getContext(), this,false);
            myRV.setAdapter(new MyRVAdapter(filteredBPMList, getContext(), this,false));
            Log.e("LIST", DataImporter.list.toString());
            myRV.setOnClickListener(this);
            myRVAdapter.notifyDataSetChanged();
        }
        catch (Exception e){
            Log.e("RUN_FRAGMENT","U STOOPID");
        }



    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER)
        {
            stepCount = (int) sensorEvent.values[0];

            stepCounter.setText(String.valueOf(stepCount));



        }


        else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR)
        {
            stepDetect = (int) (stepDetect + sensorEvent.values[0]);

            stepDetector.setText(String.valueOf(stepDetect));


        }
    }

//    public void onResume()
//    {
//        super.onResume();
//
//        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
//        {
//            sensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
//
//
//        }
//
//        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)
//        {
//            sensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
//        }
//    }

//    private void startTimer() {
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        time++;
//
//                        timerText.setText(time + "");
//
//                        stepPerMin();
//
//
//                        if (time % fiveSecondCount == 0)
//                        {
//                            storeValue1 = stepPerMin;
//
//
//                        } else if (time % fiveSecondCount == 1)
//                        {
//
//                            storeValue2 = stepPerMin;
//
//
//                        } else if (time % fiveSecondCount == 2)
//                        {
//                            storeValue3 = stepPerMin;
//
//
//                        } else if (time % fiveSecondCount == 3)
//                        {
//                            storeValue4 = stepPerMin;
//
//                        } else if (time % fiveSecondCount == 4)
//                        {
//                            storeValue5 = stepPerMin;
//
//                        }
//
//                        arrStepPerMin = (storeValue0 + storeValue1 + storeValue2 + storeValue3 + storeValue4 + storeValue5)/6;
//                        storeValue0 = (int) arrStepPerMin;
//                        stepPreM.setText(arrStepPerMin + "");
//
//                    }
//                });
//            }
//        };
//        myTimer.scheduleAtFixedRate(timerTask, 0 , 1000);
//    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}