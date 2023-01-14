//LONG COMMENT: THIS CLASS IS THE MAIN SCREEN OF THE APPLICATION
//IT'S RESPONSIBLE FOR THE BPM FILTER, THE QUEUE OPTIONS, THE QUEUE DISPLAY
//AND PARTIAL PLAYER STATE MANAGEMENT
//MANY FUNCTIONS ARE RIPPED FROM SPOTIFY SDK, BUT MAINLY FOR PLAYER STATE MANAGEMENT
//MOST THINGS IN THIS PROJECT IS BUILT FROM THE GROUND UP :)


package com.spotify.sdk.demo.activity;

import static java.lang.Float.parseFloat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.spotify.android.appremote.demo.R;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.sdk.demo.DataImporter;
import com.spotify.sdk.demo.MyDB;
import com.spotify.sdk.demo.MyHelper;
import com.spotify.sdk.demo.MyMainViewPagerAdapter;
import com.spotify.sdk.demo.MyRVAdapter;
import com.spotify.sdk.demo.RecyclerViewClickInterface;

import java.io.File;
import java.util.ArrayList;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.PlayerState;

import android.graphics.Color;

import androidx.appcompat.app.AlertDialog;

import com.spotify.protocol.types.Image;
import com.spotify.sdk.demo.StepPerMinServices;

import java.util.Collection;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, SensorEventListener, AdapterView.OnItemClickListener,
        RecyclerViewClickInterface, SeekBar.OnSeekBarChangeListener, GestureDetector.OnGestureListener,
        View.OnTouchListener, View.OnLongClickListener, TextWatcher {

    private final String TAG = "I AM A PIRATE AAAAAAAA";
    private final ErrorCallback mErrorCallback = this::logError;
    MyDB db;
    MyHelper helper;

    public static RecyclerView myRV;
    public static RecyclerViewClickInterface myRVInterface;

    private boolean isSongQueueExpanded = false;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText runningModeHintET;
    boolean isServiceStopped;
    MediaPlayer mp = null;

    SeekBar manualBpmSeekbar;
    boolean isRunningModeManual = true;
    static ArrayList<ArrayList<String>> filteredBPMList;
    static ArrayList<ArrayList<String>> forcedQueueList = null;
    static ArrayList<ArrayList<String>> currentQueueList;

    public static ArrayList<String> currentSong;
    public static ArrayList<String> prevSong;
    public static ArrayList<String> nextSong;

    public static int currentSongPos=0;
    public static int prevSongPos=0;
    public static int nextSongPos =0;
    public static boolean nextSongLoaded = false;
    public static boolean isCurrentSongInList = true;

    public Timer timer;
    public TimerTask timerTask;
    double time = 0.0;
    double timeSongStarts = 0;
    double timeSongEnds = 0;
    public static long currentSongDuration = 0;
    public static long currentSongPlaybackPosition = 0;
    public static long currentSongDurationOnce = -1;

    public static boolean isDoubleBeat;
    private PlayerState mainActivityPlayerState;

    private int currentBPM = 1;

    public float swipex1, swipex2, swipey1, swipey2;
    public GestureDetector gestureDetector;
    public float MIN_SWIPE_DIST = 300;
    public boolean isSwipedRight = false;
    public boolean isSwipedLeft = false;
    public boolean isSwiping = false;

    ImageButton cameraButton;

    Subscription<PlayerState> mPlayerStateSubscription;

    private Intent intent;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //COMMENT: SETTING UP SERVICE
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){ //ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
            }
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        intent = new Intent(this, StepPerMinServices.class);
        isServiceStopped = true;

        //COMMENT: SETTING UP RECYCLERVIEW AND ARRAYLISTARRAYLISTSTRING FOR QUEUE MANAGEMENT
        db = new MyDB(this);
        helper = new MyHelper(this);

        manualBpmSeekbar = findViewById(R.id.bpmSeekBar);
        manualBpmSeekbar.setOnSeekBarChangeListener(this);

        MyRVAdapter myRVAdapter = new MyRVAdapter(DataImporter.list, this, this, isDoubleBeat);
        myRV = findViewById(R.id.runPlaylistRV);
        myRV.setLayoutManager(new LinearLayoutManager(this));
        myRV.setHasFixedSize(true);

        myRV.setAdapter(new MyRVAdapter(DataImporter.list, this, this, isDoubleBeat));
        Log.e("DATAIMPORTER_LIST", DataImporter.list.toString());
        myRV.setOnClickListener(this);
        myRVInterface = this;
        myRVAdapter.notifyDataSetChanged();
        filteredBPMList = DataImporter.list;
        currentQueueList = filteredBPMList;

        //COMMENT: CONNECT TO SPOTIFY AND SET UP BASIC UI STUFF
        onSubscribedToPlayerStateButtonClicked(null);
        TextView scrollingText = (TextView) findViewById(R.id.smallplayerSongNameTV);
        scrollingText.setSelected(true);
        Log.i("DB_CHECK", db.getAllData().toString());
        cameraButton = (ImageButton) findViewById(R.id.cameraButtonIntent);
        (((RadioButton) findViewById(R.id.runningModeAutoButton))).setOnClickListener(this);
        (((RadioButton) findViewById(R.id.runningModeManualButton))).setOnClickListener(this);

        //COMMENT: METRONOME SETUP
        mp=MediaPlayer.create(getApplicationContext(),R.raw.step);
        timer = new Timer();
        startTimer();
        //COMMENT: GESTURE SETUP
        this.gestureDetector = new GestureDetector(MainActivity.this, this);
        ((TextView)findViewById(R.id.bpmSeekbarValueTV)).addTextChangedListener(this);
        //play();


    }

    //COMMENT: ENABLE RECYCLERVIEW CLICKING
    public void onRecyclerItemClick(int position) {
        try {
            Log.i("DB_CHECK", db.getAllData().toString());
            Log.i("DB_CHECK", db.getSelectedData(position).toString());
//        Log.i("RECYCLER_CLICK",filteredBPMList.toString());
            String selectedSongName = currentQueueList.get(position).get(2);
            Log.i("DB_CHECK", selectedSongName);
            Log.i("DB_CHECK", db.getSelectedData(selectedSongName).get(1).toString());
            playUri(db.getSelectedData(selectedSongName).get(1));
        } catch (Exception e) {
            Toast.makeText(this, "Song not available", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "But in actuality, the damn apostrophe in the song title crashes the " +
                    "program, I can't let this happen lmao ", Toast.LENGTH_SHORT).show();
        }

    }
    //COMMENT:
    //SPOTIFY SDK STUFF BELOW
    //SPOTIFY SDK STUFF BELOW
    //SPOTIFY SDK STUFF BELOW
    //SPOTIFY SDK STUFF BELOW
    //VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV

    public static void playUri(String uri) {
        RemotePlayerActivity.mSpotifyAppRemote
                .getPlayerApi()
                .play(uri);
    }

    private void logMessage(String msg) {
        logMessage(msg, Toast.LENGTH_SHORT);
    }

    private void logMessage(String msg, int duration) {
 //       Toast.makeText(this, msg, duration).show();
//        Log.d(TAG, msg);
    }

    private void logError(Throwable throwable) {
//        Toast.makeText(this, R.string.err_generic_toast, Toast.LENGTH_SHORT).show();
//        Log.e(TAG, "", throwable);
    }

    //COMMENT: Very important function that suscribes to playerstate, returning all kinds of information everytime you interact with the player
    private final Subscription.EventCallback<PlayerState> mPlayerStateEventCallback =
            new Subscription.EventCallback<PlayerState>() {
                @Override
                public void onEvent(PlayerState playerState) {
                    mainActivityPlayerState = playerState;
                    if (playerState.track != null) {
                        ((TextView) findViewById(R.id.smallplayerSongNameTV)).setText(playerState.track.name);
                        ((TextView) findViewById(R.id.smallplayerSongArtistTV)).setText(playerState.track.artist.name);
                        RemotePlayerActivity.mSpotifyAppRemote
                                .getImagesApi()
                                .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                                .setResultCallback(
                                        bitmap -> {
                                            ((ImageView) findViewById(R.id.smallplayerSongArtIV)).setImageBitmap(bitmap);

                                        });
//                        autoplayNext(playerState);
                    }
                    //UPDATE NEXT PREV AND CURRENT SONGS
                    updateNextPrevCurrSongs(playerState);
                    currentSongDuration = playerState.track.duration;


                    if (playerState.isPaused) {
                        ((ImageButton) findViewById(R.id.smallplayerPlayPauseButton)).setImageResource(R.drawable.btn_play);
                    } else {
                        ((ImageButton) findViewById(R.id.smallplayerPlayPauseButton)).setImageResource(R.drawable.btn_pause);
                    }
                }
            };

    //COMMENT: USELESS, BUT I STILL PASTED IT JUST IN CASE
    public void onSubscribedToPlayerStateButtonClicked(View view) {

        if (mPlayerStateSubscription != null && !mPlayerStateSubscription.isCanceled()) {
            mPlayerStateSubscription.cancel();
            mPlayerStateSubscription = null;
        }

        mPlayerStateSubscription =
                (Subscription<PlayerState>)
                        RemotePlayerActivity.mSpotifyAppRemote
                                .getPlayerApi()
                                .subscribeToPlayerState()
                                .setEventCallback(mPlayerStateEventCallback)
                                .setLifecycleCallback(
                                        new Subscription.LifecycleCallback() {
                                            @Override
                                            public void onStart() {
                                                logMessage("Event: start");
                                            }

                                            @Override
                                            public void onStop() {
                                                logMessage("Event: end");
                                            }
                                        })
                                .setErrorCallback(
                                        throwable -> {

                                        });
    }
    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    //COMMENT:
    //SPOTIFY SDK STUFF ABOVE
    //SPOTIFY SDK STUFF ABOVE
    //SPOTIFY SDK STUFF ABOVE
    //SPOTIFY SDK STUFF ABOVE

    //COMMENT:
    //ACTION-BASED FUNCTIONS BELOW
    //ACTION-BASED FUNCTIONS BELOW
    //ACTION-BASED FUNCTIONS BELOW
    //ACTION-BASED FUNCTIONS BELOW
    //VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
    @Override
    public void onClick(View view) {
//        if (view == cameraButton) {
//            if (allPermissionsGranted()) {
//                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                resultLauncher.launch(camera_intent);
//            } else {
//                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
//            }
//        }

        if (view != findViewById(R.id.smallPlayerLL)) {
            isSwiping = false;
        }
        Log.i("Disableswipe", isSwiping + "");

        //SWITCHING BETWEEN MANUAL AND AUTOMATIC RUNNING
        if ((view == (((RadioButton) findViewById(R.id.runningModeAutoButton)))) || (view == ((RadioButton) findViewById(R.id.runningModeManualButton)))) {
            if (((RadioButton) findViewById(R.id.runningModeManualButton)).isChecked()) {
                ((TextView) findViewById(R.id.speedHeaderTV)).setText("Set speed");
                ((SeekBar) findViewById(R.id.bpmSeekBar)).setClickable(true);
                ((SeekBar) findViewById(R.id.bpmSeekBar)).setFocusable(true);
                ((SeekBar) findViewById(R.id.bpmSeekBar)).setEnabled(true);
                ((SeekBar) findViewById(R.id.bpmSeekBar)).setThumbTintList(ColorStateList.valueOf(Color.GREEN));
                ((SeekBar) findViewById(R.id.bpmSeekBar)).setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                isRunningModeManual = true;
                stopServices(null);


            } else if (((RadioButton) findViewById(R.id.runningModeAutoButton)).isChecked()) {
                ((TextView) findViewById(R.id.speedHeaderTV)).setText("Current speed");
                ((SeekBar) findViewById(R.id.bpmSeekBar)).setFocusable(false);
                ((SeekBar) findViewById(R.id.bpmSeekBar)).setClickable(false);
                ((SeekBar) findViewById(R.id.bpmSeekBar)).setThumbTintList(ColorStateList.valueOf(Color.DKGRAY));
                ((SeekBar) findViewById(R.id.bpmSeekBar)).setProgressTintList(ColorStateList.valueOf(Color.DKGRAY));
                ((SeekBar) findViewById(R.id.bpmSeekBar)).setEnabled(false);
                ((TextView) findViewById(R.id.bpmSeekbarValueTV)).setText("Calibrating your speed... Keep running!");
                isRunningModeManual = false;
                startServices(null);

            }
            Log.i("RUNNING_MODE", String.valueOf(((RadioButton) findViewById(R.id.runningModeAutoButton)).isChecked()));
            Log.i("RUNNING_MODE", String.valueOf(((RadioButton) findViewById(R.id.runningModeManualButton)).isChecked()));
        }
    }
    //COMMENT: DISABLE GOING BACK TO LOGIN SCREEN
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    //COMMENT: MAGICAL FUCNTION
    public void onClickSeekToChorus(View view) {
        Log.i("CHORUS_CHECK_4", "CHORUS_CHECK_4");
        Log.i("CHORUS_CHECK_4", (long) RemotePlayerActivity.chorusTime + "");
        RemotePlayerActivity.mSpotifyAppRemote
                .getPlayerApi().seekTo((long) RemotePlayerActivity.chorusTime * 1000);

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
    //queue updater BASED ON CHANGING MANUAL BPM FILTER
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        {
            String bpmComment = "";
            currentBPM = seekBar.getProgress() * 10 + 30;
            bpmComment = getBPMComment(currentBPM);
            //COMMENT: TAPPING INTO DATABASE USING GETSLECETEDDATA FUNCTION
            ((TextView) findViewById(R.id.bpmSeekbarValueTV)).setText(currentBPM + " - " + (currentBPM + 10) + " (" + bpmComment + ")");
            Log.i("RUN_FRAGMENT", "yeah that's good");
            filteredBPMList = db.getSelectedDataTempoRange(currentBPM, (float) (currentBPM + 9.9999));
            filterSongOnOptions(null);

            currentQueueList = filteredBPMList;
            updateNextPrevCurrSongs(mainActivityPlayerState);
            Log.i("FILTERED_BPM_CHECK", filteredBPMList.toString());

            //update recyclerview
            updateNextPrevCurrSongs(mainActivityPlayerState);
            MyRVAdapter myRVAdapter = new MyRVAdapter(currentQueueList, this, this, isDoubleBeat);
            myRV = findViewById(R.id.runPlaylistRV);
            myRV.setLayoutManager(new LinearLayoutManager(this));
            myRV.setHasFixedSize(true);
            myRV.setAdapter(new MyRVAdapter(currentQueueList, this, this, isDoubleBeat));
            Log.e("LIST", DataImporter.list.toString());
            myRV.setOnClickListener(this);
            myRVAdapter.notifyDataSetChanged();

            //play();
        }


    }
    //COMMENTS: ADDING COMMENTS FOR BPM
    private String getBPMComment(int currentBPM) {
        String bpmComment = "";
        if (currentBPM < 60) {
            bpmComment = "Tutel";
        } else if (60 <= currentBPM && currentBPM < 80) {
            bpmComment = "Strolling";
        } else if (80 <= currentBPM && currentBPM < 110) {
            bpmComment = "Walking";
        } else if (110 <= currentBPM && currentBPM < 130) {
            bpmComment = "Brisk Walk";
        } else if (130 <= currentBPM && currentBPM < 150) {
            bpmComment = "Jogging";
        } else if (150 <= currentBPM && currentBPM < 170) {
            bpmComment = "Running";
        } else if (170 <= currentBPM && currentBPM < 210) {
            bpmComment = "Sprinting";
        } else if (210 <= currentBPM && currentBPM < 230) {
            bpmComment = "Running for your life";
        } else {
            bpmComment = "Cheeto";
        }
        return bpmComment;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    //share image code study from: https://www.youtube.com/watch?v=BWZv0iynWkE

    public void OnClickSwitchToPlayerIntent(View view) {
        startActivity(new Intent(this, MyRemotePlayerActivity.class));
    }

    public void OnClickSwitchToCameraIntent(View view){
        startActivity(new Intent(this, CameraActivity.class));
    }

    //COMMENT: CHANGE PLAY AND PAUSE SHAPE BASED ON PLAYER STATE
    public void onPlayPauseButtonClicked(View view) {
        RemotePlayerActivity.mSpotifyAppRemote
                .getPlayerApi()
                .getPlayerState()
                .setResultCallback(
                        playerState -> {
                            if (playerState.isPaused) {
                                RemotePlayerActivity.mSpotifyAppRemote
                                        .getPlayerApi()
                                        .resume()
                                        .setResultCallback(
                                                empty -> logMessage(getString(R.string.command_feedback, "play")))
                                        .setErrorCallback(mErrorCallback);
                            } else {
                                RemotePlayerActivity.mSpotifyAppRemote
                                        .getPlayerApi()
                                        .pause()
                                        .setResultCallback(
                                                empty -> logMessage(getString(R.string.command_feedback, "pause")))
                                        .setErrorCallback(mErrorCallback);
                            }
                        });
    }



    //COMMENT: SOME EXTRA FUNCTIONALITY FUNCTION
    //COMMENT:SHOW HINT
    public void onClickShowRunningModeHint(View view) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View runningModeHintPopup = getLayoutInflater().inflate(R.layout.running_mode_popup, null);

        dialogBuilder.setView(runningModeHintPopup);
        dialog = dialogBuilder.create();
        dialog.show();


    }
//COMMENT: EXPAND AND SHRINK QUEUE TAB, LEANRED FROM STACKOVERFLOW
    public void onClickToggleSongQueueSize(View view) {
        if (isSongQueueExpanded == true) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0.1f
            );
            ((LinearLayout) findViewById(R.id.SongQueueLL)).setLayoutParams(params);
            Log.i("TOGGLE_QUEUE", "Shrink queue");

            ((LinearLayout) findViewById(R.id.runPlaylistRVLL)).setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0));

            ((LinearLayout) findViewById(R.id.mainActivityHeaderLL)).setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0.1f));
            Log.i("TOGGLE_QUEUE", "Expand");

            ((LinearLayout) findViewById(R.id.mainActivityHeaderLL)).setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0.06f));
            Log.i("TOGGLE_QUEUE", "Expand");

            isSongQueueExpanded = false;

        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
            );
            ((LinearLayout) findViewById(R.id.SongQueueLL)).setLayoutParams(params);
            Log.i("TOGGLE_QUEUE", "Expand");


            ((LinearLayout) findViewById(R.id.runPlaylistRVLL)).setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    6f));
            Log.i("TOGGLE_QUEUE", "Expand");

            ((LinearLayout) findViewById(R.id.mainActivityHeaderLL)).setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0.03f));
            Log.i("TOGGLE_QUEUE", "Expand");


            isSongQueueExpanded = true;
        }
    }


    //COMMENT: queue updater BASED ON CHECK BOX, DOUBLEBEAT MEANS 200 AND 100 AND 50 SONGS ARE SHOWN IF YOU SELECT 100BPM AS A FILTER
    public void filterSongOnOptions(View view) {
        ArrayList<ArrayList<String>> extraBPMList = filteredBPMList;
        ArrayList<ArrayList<String>> returnList = new ArrayList<ArrayList<String>>();
        if (((CheckBox) findViewById(R.id.doubleBeatBox)).isChecked() == true) {
            for (ArrayList<String> each : db.getSelectedDataTempoRange(currentBPM * 2, currentBPM * 2 + 5)
            ) {
                extraBPMList.add(each);
            }
            for (ArrayList<String> each : db.getSelectedDataTempoRange(currentBPM / 2, currentBPM / 2 + 5)
            ) {
                extraBPMList.add(each);
            }
            Log.i("OPTIONS", "Double beat");
            isDoubleBeat = true;
        } else {
//            extraBPMList = filteredBPMList;
//            returnList = filteredBPMList;
            isDoubleBeat = false;
        }
        Log.i("OPTIONS", "Double beat: " + ((CheckBox) findViewById(R.id.doubleBeatBox)).isChecked());
        if (((CheckBox) findViewById(R.id.calmBox)).isChecked() == true) {
            if (isDoubleBeat == true) {
                for (ArrayList<String> each : extraBPMList
                ) {
                    if (parseFloat(each.get(5)) < 0.6)
                        returnList.add(each);
                }
                Log.i("OPTIONS", "calm");
            }
            else if(isDoubleBeat== false){
                for (ArrayList<String> each : filteredBPMList
                ) {
                    if (parseFloat(each.get(5)) < 0.6)
                        returnList.add(each);
                }
                Log.i("OPTIONS", "calm");
            }
        }
        if (((CheckBox) findViewById(R.id.energeticBox)).isChecked() == true) {
            if (isDoubleBeat == true) {
                for (ArrayList<String> each : extraBPMList
                ) {
                    if (parseFloat(each.get(5)) >= 0.6f)
                        returnList.add(each);
                }
            }
            else if (isDoubleBeat == false){
                for (ArrayList<String> each : filteredBPMList
                ) {
                    if (parseFloat(each.get(5)) >= 0.6f)
                        returnList.add(each);
                }
            }
            Log.i("OPTIONS", "energetic");
        }
        if (((CheckBox) findViewById(R.id.energeticBox)).isChecked() == false
                && ((CheckBox) findViewById(R.id.calmBox)).isChecked() == false) {
            returnList = extraBPMList;
        }
        if (((CheckBox) findViewById(R.id.energeticBox)).isChecked() == false
                && ((CheckBox) findViewById(R.id.calmBox)).isChecked() == false
                && isDoubleBeat ==false) {
            returnList = filteredBPMList;
        }
        //update recyclerView
        currentQueueList = returnList;
        updateNextPrevCurrSongs(mainActivityPlayerState);

        MyRVAdapter myRVAdapter = new MyRVAdapter(currentQueueList, this, this, isDoubleBeat);
        myRV = findViewById(R.id.runPlaylistRV);
        myRV.setLayoutManager(new LinearLayoutManager(this));
        myRV.setHasFixedSize(true);
        myRV.setAdapter(new MyRVAdapter(currentQueueList, this, this, isDoubleBeat));
        Log.e("LIST", DataImporter.list.toString());
        myRV.setOnClickListener(this);
        myRVAdapter.notifyDataSetChanged();
    }

    //COMMENT: QUALITY OF LIFE FUNCTION
    public void onClickScrollToPlaying(View view) {
        try {
            int currentSongPos = (currentQueueList.indexOf(db.getSelectedData(String.valueOf(((TextView) findViewById(R.id.smallplayerSongNameTV)).getText()))));
            myRV.scrollToPosition(currentSongPos);
        } catch (Exception e) {
            Toast.makeText(this, "Song is not in list", Toast.LENGTH_SHORT).show();
        }
    }

    //COMMENT: UPDATE WHAT SONG COMES NEXT AND BEFORE. SOMETIMES WHEN YOU ALTER THE BPM AND CHECKBOXES, THE CURRENT SONGIS NO LONGER IN THE LIST
    //AND NEARBY SONGS ARE ALSO CHANGED, SO THIS RUNS EVERY TIME WHEN THERE'S A ALTERNATION IN BPM FILTER, OPTIONS, ON QUEUE SHUFFLE
    public void updateNextPrevCurrSongs(PlayerState playerState){
        //Check if song is still in list
        try {
            if (currentQueueList.size() > 0) {
                if (currentQueueList.indexOf(db.getSelectedData(playerState.track.name)) == -1) {
                    Log.i("songlistqueuerefresh", db.getSelectedData(playerState.track.name) + "");
                    prevSongPos = 0;
//            currentSongPos = 0;
                    nextSongPos = 1;
                    Log.i("Songlistqueue", "caserefresh");
                }
                //special cases(beginning and end of queue)
                else if (currentSongPos == 0) {
                    currentSongPos = currentQueueList.indexOf(db.getSelectedData(playerState.track.name));
                    nextSongPos = currentSongPos + 1;
                    prevSongPos = currentSongPos;
                    Log.i("Songlistqueue", "casefront");
                } else if (currentSongPos == currentQueueList.size() - 1) {
                    currentSongPos = currentQueueList.indexOf(db.getSelectedData(playerState.track.name));
                    nextSongPos = currentSongPos;
                    prevSongPos = currentSongPos - 1;
                    Log.i("Songlistqueue", "caseend");
                } else {
                    currentSongPos = currentQueueList.indexOf(db.getSelectedData(playerState.track.name));
                    nextSongPos = currentSongPos + 1;
                    prevSongPos = currentSongPos - 1;
                    Log.i("Songlistqueue", "casenormal");
                }

                //assigning songs to pos
                if (prevSongPos == -1) {
                    nextSong = currentQueueList.get(0);
                } else {
                    currentSong = currentQueueList.get(currentSongPos);
                    prevSong = currentQueueList.get(prevSongPos);
                    nextSong = currentQueueList.get(nextSongPos);

                }
                Log.i("Songlistqueue", prevSong.get(2) + "-" + currentSong.get(2) + "-" + nextSong.get(2));
            }
        }catch (Exception e){
            Log.wtf("WTF","WTF");
        }


    }

//    //This function is run along with
//    public void updateNextPrevCurrSongs(PlayerState playerState) {
//
//
//        Log.e("songlistqueue", "songname " + playerState.track.name);
//
////try
//         {
//            //COMMENTS: If currently playing song is no longer in list, make the currentSongPos to -1
//            if(currentQueueList.indexOf(db.getSelectedData(playerState.track.name))==-1){
//                currentSongPos = -1;
//            }
//            //COMMENTS: If currently playing song is in list after sorting, set the next song and previous song to the variables
//            else if (currentQueueList.indexOf(db.getSelectedData(playerState.track.name))!=-1) {
//                currentSongPos = (currentQueueList.indexOf(db.getSelectedData(playerState.track.name)));
//            }
//            nextSongPos = (currentSongPos + 1);
//            prevSongPos = (currentSongPos - 1);
//
//            //currentSong = currentQueueList.get(currentSongPos);
//            //play next if there are still songs in queue
//            if (currentSongPos < currentQueueList.size() && nextSongPos != 0) {
//                nextSong = currentQueueList.get(nextSongPos);
//            }
//            //don't play next if there are no new song in queue
//            else if (nextSongPos != 0) {
//                nextSong = null;
//            }
//            else if (nextSongPos == 0) {
//                prevSongPos = 0;
//                currentSongPos = 0;
//                nextSongPos = 1;
//                Log.i("songlistqueuenext", "songname " + currentQueueList.get(currentSongPos + 1));
//            }
//            if (currentSongPos >= 1 && nextSongPos != 0) {
//                prevSong = currentQueueList.get(currentSongPos - 1);
//                Log.i("songlistqueueprev", "songname " + currentQueueList.get(currentSongPos - 1));
//            } else if (currentSongPos != 0) {
//                prevSong = null;
//            }
//            Log.i("Songlistqueue", prevSong.get(2) + "-" + currentSong.get(2) + "-" + nextSong.get(2));
//        }
////        catch (Exception e) {
//////            Log.e("songlistqueue", "cant find song idx");
//////            Log.e("Songlistqueue", prevSong.get(2) + "-" + currentSong.get(2));
////        }
//        Log.i("songlistqueue", currentSongPos + "pos");
//        try {
//        } catch (Exception e) {
//            Log.e("autoplay", "autoplay fail");
//        }
//    }

    //COMMENT: SWIPING MINI PLAYER FOR NEXT AND PREV
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        gestureDetector.onTouchEvent(motionEvent);
        try {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    swipex1 = motionEvent.getX();
                    swipey1 = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    swipex2 = motionEvent.getX();
                    swipey2 = motionEvent.getY();
                    Log.i("swipey2",swipey2+"");

                    float valx = swipex2 - swipex1;
                    float valy = swipey2 - swipey1;
                    Point size = new Point();
                    getWindowManager().getDefaultDisplay().getSize(size);

                    if (Math.abs(valx) > MIN_SWIPE_DIST && (size.y)-600 < swipey1) {
                        if (swipex2 > swipex1) {
                            isSwipedLeft = true;
                            playUri(prevSong.get(1));
                        }
                        if (swipex1 > swipex2 && (size.y)-600 < swipey1) {
                            isSwipedRight = true;
                            playUri(nextSong.get(1));
                        }

                    } else {
                        isSwipedLeft = false;
                        isSwipedRight = false;

                    }

                    if (swipey1 - swipey2 > MIN_SWIPE_DIST) {
                        OnClickSwitchToPlayerIntent(null);
                    }
                    isSwiping = false;
            }
        } catch (Exception e) {
            Log.e("SWIPE", "bad swipe");
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onLongClick(View view) {
        if (view == findViewById(R.id.smallPlayerLL)) {
            Log.i("ontouch", "view");
            Log.i("ontouch", "inaction");
            if (!isSwipedLeft && !isSwipedRight) {
                OnClickSwitchToPlayerIntent(null);
                Log.i("ontouch", "inexecute");
            }
        }
        return true;
    }

    //COMMENT: MAKING A METRONOME THAT SHOWS HOW MAST YOU SHOULD BE WALKING
    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                time=time+50;
                //Log.i("time", time + "");
                try {
                    if(((CheckBox)findViewById(R.id.metronomeBox)).isChecked() == true && currentBPM != 0 && time!=0)
                    {
                        mp = MediaPlayer.create(getApplicationContext(), R.raw.step);
                        Log.i("metronome",time%(int)(60000/currentBPM)+"");
                        if(time%((int)60000/currentBPM)<50){

                            mp.start();
                            Log.i("metronome","tick");
                        }
                    }
                    else if (mp.isPlaying()) {

                        mp.stop();
                        mp.release();
                    }

                    else if(!mainActivityPlayerState.isPaused){

                    }
                } catch (Exception e)
                {
                    Log.wtf("WTF","WHY METRONOME CRASHES");
                }

            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 50);
    }

    public void playMetronome(double time, int currentBPM) {
        mp = MediaPlayer.create(getApplicationContext(), R.raw.step);
        if (((CheckBox) findViewById(R.id.metronomeBox)).isChecked() == true && currentBPM != 0 && time != 0) {
            Log.i("metronome", time % (int) (60000 / currentBPM) + "");
            if (time % ((int) 60000 / currentBPM) == 0) {

                mp.start();
                Log.i("metronome", "tick");
            }
        } else {
            mp.stop();
        }

    }



    //STEP MEASURING SERVICES
    //SERVICES////////////////////////////
    public void startServices(View view){
        // start Service.
        startService(new Intent(getBaseContext(), StepPerMinServices.class));
        // register our BroadcastReceiver by passing in an IntentFilter. * identifying the message that is broadcasted by using static string "BROADCAST_ACTION".
        registerReceiver(broadcastReceiver, new IntentFilter(StepPerMinServices.BROADCAST_ACTION));
        isServiceStopped = false;
    }


    public void stopServices(View view){
        if (!isServiceStopped) {
            // call unregisterReceiver - to stop listening for broadcasts.
            unregisterReceiver(broadcastReceiver);
            // stop Service.
            stopService(new Intent(getBaseContext(), StepPerMinServices.class));
            isServiceStopped = true;

        }
    }

    // --------------------------------------------------------------------------- \\
    // ___ create Broadcast Receiver ___ \\
    // create a BroadcastReceiver - to receive the message that is going to be broadcast from the Service
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // call updateUI passing in our intent which is holding the data to display.
            if(isRunningModeManual == false){
                ((TextView) findViewById(R.id.bpmSeekbarValueTV)).setText(currentBPM + getBPMComment(currentBPM) + ")");
                currentBPM = Integer.parseInt(intent.getStringExtra("stepPerMin"));

            }
        }
    };

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    //COMMENT: UPDATE QUEUE BASED ON AUTOMATIC RUNNING BPM
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        filteredBPMList = db.getSelectedDataTempoRange(currentBPM, (float) (currentBPM + 9.9999));
        filterSongOnOptions(null);

        currentQueueList = filteredBPMList;
        updateNextPrevCurrSongs(mainActivityPlayerState);
        MyRVAdapter myRVAdapter = new MyRVAdapter(currentQueueList, this, this, isDoubleBeat);
        myRV = findViewById(R.id.runPlaylistRV);
        myRV.setLayoutManager(new LinearLayoutManager(this));
        myRV.setHasFixedSize(true);
        myRV.setAdapter(new MyRVAdapter(currentQueueList, this, this, isDoubleBeat));
        Log.e("TEXTCHANGE", "GOOD");
        myRV.setOnClickListener(this);
        myRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    //COMMENT: BONUS SHUFFLING FUNCTION
    public void onClickShuffle(View view){
        Collections.shuffle(currentQueueList);
        MyRVAdapter myRVAdapter = new MyRVAdapter(currentQueueList, this, this, isDoubleBeat);
        myRV = findViewById(R.id.runPlaylistRV);
        myRV.setLayoutManager(new LinearLayoutManager(this));
        myRV.setHasFixedSize(true);
        myRV.setAdapter(new MyRVAdapter(currentQueueList, this, this, isDoubleBeat));
        Log.e("TEXTCHANGE", "GOOD");
        myRV.setOnClickListener(this);
        myRVAdapter.notifyDataSetChanged();
    }
}

//    public void play() {
//        Timer mainTimer = new Timer();
//        Timer subTimer = new Timer();
//        TimerTask mainTimerTask = new MyTimerTask();
//
//        mainTimer.schedule(mainTimerTask, 0, 60000 / currentBPM);
//
//    }
//
//    private void  playSound() {
//        final MediaPlayer mp = MediaPlayer.create(this, R.raw.step);
//        mp.start();
//        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            public void onCompletion(MediaPlayer mp) {
//                mp.release();
//            };
//        });
//    }
//
//    class MyTimerTask extends TimerTask {
//
//        @Override
//        public void run() {
//            playSound();
//        }
//    }








