//FUNCTION FOR ACTIVITY 2, MANY FUNCTIONS ARE FROM SPOTIFY SDK, BUT I BUILT THE UI AND QUEUE MANAGEMENT MYSELF


package com.spotify.sdk.demo.activity;

import static java.lang.Float.parseFloat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.demo.R;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.CrossfadeState;
import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.PlayerState;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.FragmentActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.ContentApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.demo.R;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.PlaybackSpeed;
import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Repeat;
import com.spotify.protocol.types.CrossfadeState;
import com.spotify.sdk.demo.MyRVAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import java.util.List;

public class MyRemotePlayerActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static String DEBUG_TAG = "MyRemotePlayerActivity";

    private static final String CLIENT_ID = "089d841ccc194c10a77afad9e1c11d54";
    private static final String REDIRECT_URI = "comspotifytestsdk://callback";
    public static CrossfadeState crossfadeState = new CrossfadeState(true, 3);
    private static final String MY_CLIENT_ID = "d418eb0b26114fa4b9c050dedece0116";


    public static SpotifyAppRemote mSpotifyAppRemote = RemotePlayerActivity.mSpotifyAppRemote;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Button mConnectButton, mConnectAuthorizeButton;
    Button mSubscribeToPlayerContextButton;
    Button mPlayerContextButton;
    Button mSubscribeToPlayerStateButton;
    Button mPlayerStateButton;
    ImageView mCoverArtImageView;
    AppCompatTextView mImageLabel;
    AppCompatTextView mImageScaleTypeLabel;
    ImageButton mToggleShuffleButton;
    ImageButton mPlayPauseButton;
    ImageButton mToggleRepeatButton;
    AppCompatSeekBar mSeekBar;
    AppCompatImageButton mPlaybackSpeedButton;

    List<View> mViews;
    MyRemotePlayerActivity.MyTrackProgressBar mTrackProgressBar;

    Subscription<PlayerState> mPlayerStateSubscription;
    Subscription<PlayerContext> mPlayerContextSubscription;
    Subscription<Capabilities> mCapabilitiesSubscription;

    TextView mSongTitle;

    static public PlayerState universalPlayerState;

    private final ErrorCallback mErrorCallback = this::logError;
    private TextView mSongArtist;
    private GestureDetector gestureDetector;
    public float swipex1, swipex2, swipey1, swipey2;
    public float MIN_SWIPE_DIST = 300;
    public boolean isSwipedRight = false;
    public boolean isSwipedLeft = false;
    public boolean isSwiping = false;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_remote_player);

        mPlayPauseButton = findViewById(R.id.playerPlayButton);
        mCoverArtImageView = findViewById(R.id.songArtIV);
        mToggleShuffleButton = findViewById(R.id.playerShuffleButton);
        mToggleRepeatButton = findViewById(R.id.playerRepeatButton);

        mSongTitle = findViewById(R.id.playerSongNameTV);
        mSongArtist = findViewById(R.id.playerArtistNameTV);

        mSeekBar = findViewById(R.id.playerSongSeekBar);
        mSeekBar.setEnabled(false);
        mSeekBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        mSeekBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);


        mTrackProgressBar = new MyRemotePlayerActivity.MyTrackProgressBar(mSeekBar);



        mViews =
                Arrays.asList(
                        mPlayPauseButton,
                        mCoverArtImageView,
                        mToggleShuffleButton,
                        mToggleRepeatButton,
                        mSongTitle,
                        mSongArtist

                        );
//        try {
//            URL imageUrl = new URL("https://i.scdn.co/image/ab67616d0000b2737359994525d219f64872d3b1");
//            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) imageUrl.getContent());
//            mCoverArtImageView.setImageBitmap(bitmap);
//        } catch (MalformedURLException e) {
//
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        this.gestureDetector = new GestureDetector(MyRemotePlayerActivity.this, this);
        onSubscribedToPlayerStateButtonClicked(null);
        onSubscribedToPlayerContextButtonClicked(null);

    }


    public void onImageClicked(View view) {
        if (mSpotifyAppRemote != null) {
            mSpotifyAppRemote
                    .getPlayerApi()
                    .getPlayerState()
                    .setResultCallback(
                            playerState -> {
                                PopupMenu menu = new PopupMenu(this, view);

                                menu.getMenu().add(720, 720, 0, "Large (720px)");
                                menu.getMenu().add(480, 480, 1, "Medium (480px)");
                                menu.getMenu().add(360, 360, 2, "Small (360px)");
                                menu.getMenu().add(240, 240, 3, "X Small (240px)");
                                menu.getMenu().add(144, 144, 4, "Thumbnail (144px)");

                                menu.show();

                                menu.setOnMenuItemClickListener(
                                        item -> {
                                            mSpotifyAppRemote
                                                    .getImagesApi()
                                                    .getImage(
                                                            playerState.track.imageUri, Image.Dimension.values()[item.getOrder()])
                                                    .setResultCallback(
                                                            bitmap -> {
                                                                mCoverArtImageView.setImageBitmap(bitmap);
                                                                mImageLabel.setText(
                                                                        String.format(
                                                                                Locale.ENGLISH,
                                                                                "%d x %d",
                                                                                bitmap.getWidth(),
                                                                                bitmap.getHeight()));
                                                            });
                                            return false;
                                        });
                            })
                    .setErrorCallback(mErrorCallback);

        }
    }

    public void onImageScaleTypeClicked(View view) {
        if (mSpotifyAppRemote != null) {
            mSpotifyAppRemote
                    .getPlayerApi()
                    .getPlayerState()
                    .setResultCallback(
                            playerState -> {
                                PopupMenu menu = new PopupMenu(this, view);

                                menu.getMenu().add(0, ImageView.ScaleType.CENTER.ordinal(), 0, "CENTER");
                                menu.getMenu().add(1, ImageView.ScaleType.CENTER_CROP.ordinal(), 1, "CENTER_CROP");
                                menu.getMenu()
                                        .add(2, ImageView.ScaleType.CENTER_INSIDE.ordinal(), 2, "CENTER_INSIDE");
                                menu.getMenu().add(3, ImageView.ScaleType.MATRIX.ordinal(), 3, "MATRIX");
                                menu.getMenu().add(4, ImageView.ScaleType.FIT_CENTER.ordinal(), 4, "FIT_CENTER");
                                menu.getMenu().add(4, ImageView.ScaleType.FIT_XY.ordinal(), 5, "FIT_XY");

                                menu.show();

                                menu.setOnMenuItemClickListener(
                                        item -> {
                                            mCoverArtImageView.setScaleType(
                                                    ImageView.ScaleType.values()[item.getItemId()]);
                                            mImageScaleTypeLabel.setText(
                                                    ImageView.ScaleType.values()[item.getItemId()].toString());
                                            return false;
                                        });
                            })
                    .setErrorCallback(mErrorCallback);
        }
    }

    private final Subscription.EventCallback<PlayerContext> mPlayerContextEventCallback =
            new Subscription.EventCallback<PlayerContext>() {
                @Override
                public void onEvent(PlayerContext playerContext) {
//                    mPlayerContextButton.setText(
//                            String.format(Locale.US, "%s\n%s", playerContext.title, playerContext.subtitle));
//                    mPlayerContextButton.setTag(playerContext);
                }
            };
    private final Subscription.EventCallback<PlayerState> mPlayerStateEventCallback =
            new Subscription.EventCallback<PlayerState>() {
                @Override
                public void onEvent(PlayerState playerState) {
                    universalPlayerState = playerState;
                    Drawable drawable =
                            ResourcesCompat.getDrawable(
                                    getResources(), R.drawable.mediaservice_shuffle, getTheme());
                    if (!playerState.playbackOptions.isShuffling) {
                        mToggleShuffleButton.setImageDrawable(drawable);
                        DrawableCompat.setTint(mToggleShuffleButton.getDrawable(), Color.WHITE);
                    } else {
                        mToggleShuffleButton.setImageDrawable(drawable);
                        DrawableCompat.setTint(
                                mToggleShuffleButton.getDrawable(),
                                getResources().getColor(R.color.cat_medium_green));
                    }

                    if (playerState.playbackOptions.repeatMode == Repeat.ALL) {
                        mToggleRepeatButton.setImageResource(R.drawable.mediaservice_repeat_all);
                        DrawableCompat.setTint(
                                mToggleRepeatButton.getDrawable(),
                                getResources().getColor(R.color.cat_medium_green));
                    } else if (playerState.playbackOptions.repeatMode == Repeat.ONE) {
                        mToggleRepeatButton.setImageResource(R.drawable.mediaservice_repeat_one);
                        DrawableCompat.setTint(
                                mToggleRepeatButton.getDrawable(),
                                getResources().getColor(R.color.cat_medium_green));
                    } else {
                        mToggleRepeatButton.setImageResource(R.drawable.mediaservice_repeat_off);
                        DrawableCompat.setTint(mToggleRepeatButton.getDrawable(), Color.WHITE);
                    }

                    if (playerState.track != null) {
                        mSongTitle.setText(playerState.track.name);
                        mSongArtist.setText(playerState.track.artist.name.toString());
                    }
                    // Update progressbar
                    if (playerState.playbackSpeed > 0) {
                        mTrackProgressBar.unpause();
                    } else {
                        mTrackProgressBar.pause();
                    }

                    // Invalidate play / pause
                    if (playerState.isPaused) {
                        mPlayPauseButton.setImageResource(R.drawable.btn_play);
                    } else {
                        mPlayPauseButton.setImageResource(R.drawable.btn_pause);
                    }

                    // Invalidate playback speed
//                    mPlaybackSpeedButton.setVisibility(View.VISIBLE);
//                    if (playerState.playbackSpeed == 0.5f) {
//                        mPlaybackSpeedButton.setImageResource(R.drawable.ic_playback_speed_50);
//                    } else if (playerState.playbackSpeed == 0.8f) {
//                        mPlaybackSpeedButton.setImageResource(R.drawable.ic_playback_speed_80);
//                    } else if (playerState.playbackSpeed == 1f) {
//                        mPlaybackSpeedButton.setImageResource(R.drawable.ic_playback_speed_100);
//                    } else if (playerState.playbackSpeed == 1.2f) {
//                        mPlaybackSpeedButton.setImageResource(R.drawable.ic_playback_speed_120);
//                    } else if (playerState.playbackSpeed == 1.5f) {
//                        mPlaybackSpeedButton.setImageResource(R.drawable.ic_playback_speed_150);
//                    } else if (playerState.playbackSpeed == 2f) {
//                        mPlaybackSpeedButton.setImageResource(R.drawable.ic_playback_speed_200);
//                    } else if (playerState.playbackSpeed == 3f) {
//                        mPlaybackSpeedButton.setImageResource(R.drawable.ic_playback_speed_300);
//                    }
//                    if (playerState.track != null && playerState.track.isPodcast && playerState.track.isEpisode) {
//                        mPlaybackSpeedButton.setEnabled(true);
//                        mPlaybackSpeedButton.clearColorFilter();
//                    } else {
//                        mPlaybackSpeedButton.setEnabled(false);
//                        mPlaybackSpeedButton.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
//                    }

                    if (playerState.track != null) {
                        // Get image from track
                        mSpotifyAppRemote
                                .getImagesApi()
                                .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                                .setResultCallback(
                                        bitmap -> {
                                            mCoverArtImageView.setImageBitmap(bitmap);
                                            mImageLabel.setText(
                                                    String.format(
                                                            Locale.ENGLISH, "%d x %d", bitmap.getWidth(), bitmap.getHeight()));
                                        });
                        // Invalidate seekbar length and position
                        mSeekBar.setMax((int) playerState.track.duration);
                        mTrackProgressBar.setDuration(playerState.track.duration);
                        mTrackProgressBar.update(playerState.playbackPosition);

                    }
                    MainActivity.currentSongDuration = playerState.track.duration;

                    updateSongQueue();
                    mSeekBar.setEnabled(true);
                }
            };
    //COMMENT: VERY IMPORTANT FUNCTION THAT TOOK ME HALF A DAY TO WRITE THIS. SPOTIFY SDK IS SO LACKING THAT I HAVE TO DEVIATE TO HACKS FOR THIS AUTOPLAY FUNCTION
    public void autoplayNextv3(PlayerState playerState){
 //       Log.i("autoplay", MainActivity.currentSongPlaybackPosition +"-" + playerState.track.duration + "=" + Math.subtractExact(playerState.track.duration,MainActivity.currentSongPlaybackPosition));
        if(MainActivity.currentSongPlaybackPosition >= playerState.track.duration-3000 && playerState.track.duration-MainActivity.currentSongPlaybackPosition > 100 && playerState.playbackPosition>100){
            MainActivity.playUri(MainActivity.nextSong.get(1));
  //          Log.i("autoplay","yosss");
        }

    }
    //COMMENT: 6 HOURS IN
    public void autoplayNextv2(PlayerState playerState){
        try{
            //trapdoor
            //Log.i("AUTOPLAY","Before trapdoor " +playerState.playbackPosition +"/"+playerState.track.duration + "LOADED: " + MainActivity.nextSongLoaded);
        //    Log.i("AUTOPLAY","Before trapdoor " +MainActivity.currentSongDurationOnce +"/"+ MainActivity.currentSongDuration +"/"+ playerState.track.duration + "LOADED: " + MainActivity.nextSongLoaded);
            if(playerState.track.duration!=MainActivity.currentSongDurationOnce && playerState.playbackPosition > 0){
                MainActivity.currentSongDurationOnce = playerState.track.duration;
                MainActivity.nextSongLoaded = true;
            }
            if(MainActivity.nextSongLoaded == true && MainActivity.currentSongDurationOnce == playerState.track.duration){
                Log.i("AUTOPLAY","play next song");
                //playUri(MainActivity.currentQueueList.get(MainActivity.nextSongPos).get(2));
                MainActivity.nextSongLoaded = false;
            }
           // Log.i("AUTOPLAY","After trapdoor " +playerState.playbackPosition +"/"+playerState.track.duration + "LOADED: " + MainActivity.nextSongLoaded);
            Log.i("AUTOPLAY","After trapdoor " +MainActivity.currentSongDurationOnce +"/"+ MainActivity.currentSongDuration+"/"+ playerState.track.duration + "LOADED: " + MainActivity.nextSongLoaded);
        }catch(Exception e){
            Log.e("autoplay","bad autoplay");
        }
    }

    //COMMENT: 2 HOURS IN
    public void autoplayNextv1(PlayerState playerState){
        try{
        Log.i("AUTOPLAY",playerState.playbackPosition +"/"+playerState.track.duration + "LOADED: " + MainActivity.nextSongLoaded);
        long currentDuration = 0;

        if (playerState.playbackPosition > 1){
            MainActivity.nextSongLoaded = true;
        }else{
            MainActivity.nextSongLoaded = false;
        }

        if(playerState.playbackPosition >= playerState.track.duration-100 ||
                (playerState.track.duration % 100 == 0) && (playerState.playbackPosition != 0) && (MainActivity.nextSongLoaded)) {
            Log.i("AUTOPLAY","play next song");
            MainActivity.playUri(MainActivity.currentQueueList.get(MainActivity.nextSongPos).get(2));
            MainActivity.nextSongLoaded = false;
        }
        }catch (Exception e)
        {
            Log.e("autoplay","Bad autoplay");
        }


    }

    protected void onStop() {
        super.onStop();
//        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
//        onDisconnected();
    }

    private void onConnected() {
        for (View input : mViews) {
            input.setEnabled(true);
        }
//        mConnectButton.setEnabled(false);
//        mConnectButton.setText(R.string.connected);
//        mConnectAuthorizeButton.setEnabled(false);
//        mConnectAuthorizeButton.setText(R.string.connected);

        onSubscribedToPlayerStateButtonClicked(null);
        onSubscribedToPlayerContextButtonClicked(null);
    }

    private void onConnecting() {
//        mConnectButton.setEnabled(false);
//        mConnectButton.setText(R.string.connecting);
//        mConnectAuthorizeButton.setEnabled(false);
//        mConnectAuthorizeButton.setText(R.string.connecting);
    }

    private void onDisconnected() {
        for (View view : mViews) {
            view.setEnabled(false);
        }
        //COMMENTOUT
//        mConnectButton.setEnabled(true);
//        mConnectButton.setText(R.string.connect);
//        mConnectAuthorizeButton.setEnabled(true);
//        mConnectAuthorizeButton.setText(R.string.authorize);
//        mCoverArtImageView.setImageResource(R.drawable.widget_placeholder);
//        mPlayerContextButton.setText(R.string.title_player_context);
//        mPlayerStateButton.setText(R.string.title_current_track);
//        mToggleRepeatButton.clearColorFilter();
//        mToggleRepeatButton.setImageResource(R.drawable.btn_repeat);
//        mToggleShuffleButton.clearColorFilter();
//        mToggleShuffleButton.setImageResource(R.drawable.btn_shuffle);
//        mPlayerContextButton.setVisibility(View.INVISIBLE);
//        mSubscribeToPlayerContextButton.setVisibility(View.VISIBLE);
//        mPlayerStateButton.setVisibility(View.INVISIBLE);
//        mSubscribeToPlayerStateButton.setVisibility(View.VISIBLE);
    }

    public void onConnectClicked(View v) {
        onConnecting();
        connect(false);
    }

    public void onConnectAndAuthorizedClicked(View view) {
        onConnecting();
        connect(true);
    }

    private void connect(boolean showAuthView) {

        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        SpotifyAppRemote.connect(
                getApplicationContext(),
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(showAuthView)
                        .build(),
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        MyRemotePlayerActivity.this.onConnected();
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        logError(error);
                        MyRemotePlayerActivity.this.onDisconnected();
                    }
                });
    }

    public void showCurrentPlayerContext(View view) {
        if (view.getTag() != null) {
            showDialog("PlayerContext", gson.toJson(view.getTag()));
            Log.i("PlayerContextaaaaaaa", gson.toJson(view.getTag()));
        }
    }

    public void showCurrentPlayerState(View view) {
        if (view.getTag() != null) {
            showDialog("PlayerState", gson.toJson(view.getTag()));
            Log.i("PlayerStateaaaaaaaaa", gson.toJson(view.getTag()));
        }
    }

    public void onToggleShuffleButtonClicked(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .toggleShuffle()
                .setResultCallback(
                        empty -> logMessage(getString(R.string.command_feedback, "toggle shuffle")))
                .setErrorCallback(mErrorCallback);
    }

    public void onToggleRepeatButtonClicked(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .toggleRepeat()
                .setResultCallback(
                        empty -> logMessage(getString(R.string.command_feedback, "toggle repeat")))
                .setErrorCallback(mErrorCallback);
    }

    public void onSkipPreviousButtonClicked(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .skipPrevious()
                .setResultCallback(
                        empty -> logMessage(getString(R.string.command_feedback, "skip previous")))
                .setErrorCallback(mErrorCallback);
    }

    public void onPlayPauseButtonClicked(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .getPlayerState()
                .setResultCallback(
                        playerState -> {
                            if (playerState.isPaused) {
                                mSpotifyAppRemote
                                        .getPlayerApi()
                                        .resume()
                                        .setResultCallback(
                                                empty -> logMessage(getString(R.string.command_feedback, "play")))
                                        .setErrorCallback(mErrorCallback);
                                mPlayPauseButton.setImageResource(R.drawable.btn_pause);
                            } else {
                                mSpotifyAppRemote
                                        .getPlayerApi()
                                        .pause()
                                        .setResultCallback(
                                                empty -> logMessage(getString(R.string.command_feedback, "pause")))
                                        .setErrorCallback(mErrorCallback);
                                mPlayPauseButton.setImageResource(R.drawable.btn_play);
                            }
                        });

    }

    public void onSkipNextButtonClicked(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .skipNext()
                .setResultCallback(data -> logMessage(getString(R.string.command_feedback, "skip next")))
                .setErrorCallback(mErrorCallback);
    }

    public void onSeekBack(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .seekToRelativePosition(-15000)
                .setResultCallback(data -> logMessage(getString(R.string.command_feedback, "seek back")))
                .setErrorCallback(mErrorCallback);
    }

    public void onSeekForward(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .seekToRelativePosition(15000)
                .setResultCallback(data -> logMessage(getString(R.string.command_feedback, "seek fwd")))
                .setErrorCallback(mErrorCallback);
    }

    public void onSubscribeToCapabilitiesClicked(View view) {

        if (mCapabilitiesSubscription != null && !mCapabilitiesSubscription.isCanceled()) {
            mCapabilitiesSubscription.cancel();
            mCapabilitiesSubscription = null;
        }

        mCapabilitiesSubscription =
                (Subscription<Capabilities>)
                        mSpotifyAppRemote
                                .getUserApi()
                                .subscribeToCapabilities()
                                .setEventCallback(
                                        capabilities ->
                                                logMessage(
                                                        getString(
                                                                R.string.on_demand_feedback,
                                                                capabilities.canPlayOnDemand)))
                                .setErrorCallback(mErrorCallback);

        mSpotifyAppRemote
                .getUserApi()
                .getCapabilities()
                .setResultCallback(
                        capabilities ->
                                logMessage(
                                        getString(
                                                R.string.on_demand_feedback,
                                                capabilities.canPlayOnDemand)))
                .setErrorCallback(mErrorCallback);
    }

    //SKIP


    private void handleLatch(CountDownLatch latch, List<ListItem> combined) {
        latch.countDown();
        if (latch.getCount() == 0) {
            showDialog(
                    getString(R.string.command_response, getString(R.string.browse_content)),
                    gson.toJson(combined));
        }
    }

    public void onConnectSwitchToLocalClicked(View view) {
        mSpotifyAppRemote
                .getConnectApi()
                .connectSwitchToLocalDevice()
                .setResultCallback(
                        empty ->
                                logMessage(
                                        getString(
                                                R.string.command_feedback, getString(R.string.connect_switch_to_local))))
                .setErrorCallback(mErrorCallback);
    }

    public void onSubscribedToPlayerContextButtonClicked(View view) {
        if (mPlayerContextSubscription != null && !mPlayerContextSubscription.isCanceled()) {
            mPlayerContextSubscription.cancel();
            mPlayerContextSubscription = null;
//            mPlayerContextButton.setText(
//                    String.format(Locale.US, "%s\n%s", playerContext.title, playerContext.subtitle));
//            mPlayerContextButton.setTag(playerContext);

        }


//        mPlayerContextButton.setVisibility(View.VISIBLE);
//        mSubscribeToPlayerContextButton.setVisibility(View.INVISIBLE);

        mPlayerContextSubscription =
                (Subscription<PlayerContext>)
                        mSpotifyAppRemote
                                .getPlayerApi()
                                .subscribeToPlayerContext()
                                .setEventCallback(mPlayerContextEventCallback)
                                .setErrorCallback(
                                        throwable -> {
//                                            mPlayerContextButton.setVisibility(View.INVISIBLE);
//                                            mSubscribeToPlayerContextButton.setVisibility(View.VISIBLE);
                                            logError(throwable);
                                        });
    }

    public void onSubscribedToPlayerStateButtonClicked(View view) {

        if (mPlayerStateSubscription != null && !mPlayerStateSubscription.isCanceled()) {
            mPlayerStateSubscription.cancel();
            mPlayerStateSubscription = null;
        }
//
//        mPlayerStateButton.setVisibility(View.VISIBLE);
//        mSubscribeToPlayerStateButton.setVisibility(View.INVISIBLE);

        mPlayerStateSubscription =
                (Subscription<PlayerState>)
                        mSpotifyAppRemote
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
//                                            mPlayerStateButton.setVisibility(View.INVISIBLE);
//                                            mSubscribeToPlayerStateButton.setVisibility(View.VISIBLE);
                                            logError(throwable);
                                        });
    }

    private void logError(Throwable throwable) {
//        Toast.makeText(this, R.string.err_generic_toast, Toast.LENGTH_SHORT).show();
//        Log.e(DEBUG_TAG, "", throwable);
    }

    private void logMessage(String msg) {
//        logMessage(msg, Toast.LENGTH_SHORT);
    }

    private void logMessage(String msg, int duration) {
//        Toast.makeText(this, msg, duration).show();
//        Log.d(DEBUG_TAG, msg);
    }

    private void showDialog(String title, String message) {
//        new AlertDialog.Builder(this).setTitle(title).setMessage(message).create().show();
    }

    public void onPlaybackSpeedButtonClicked(View view) {
        PopupMenu menu = new PopupMenu(this, view);

        menu.getMenu().add(50, 50, 0, "0.5x");
        menu.getMenu().add(80, 80, 1, "0.8x");
        menu.getMenu().add(100, 100, 2, "1x");
        menu.getMenu().add(120, 120, 3, "1.2x");
        menu.getMenu().add(150, 150, 4, "1.5x");
        menu.getMenu().add(200, 200, 5, "2x");
        menu.getMenu().add(300, 300, 6, "3x");

        menu.show();

        menu.setOnMenuItemClickListener(
                item -> {
                    mSpotifyAppRemote
                            .getPlayerApi()
                            .setPodcastPlaybackSpeed(PlaybackSpeed.PodcastPlaybackSpeed.values()[item.getOrder()])
                            .setResultCallback(
                                    empty ->
                                            logMessage(
                                                    getString(
                                                            R.string.command_feedback,
                                                            getString(R.string.play_podcast_button_label))))
                            .setErrorCallback(mErrorCallback);
                    return false;
                });
    }
//COMMENT: SWIPE TO NAVIGATE
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

                    if (Math.abs(valx) > MIN_SWIPE_DIST ) {
                        if (swipex2 > swipex1) {
                            isSwipedLeft = true;
                            MainActivity.playUri(MainActivity.prevSong.get(1));
                        }
                        if (swipex1 > swipex2 ) {
                            isSwipedRight = true;
                            MainActivity.playUri(MainActivity.nextSong.get(1));
                        }

                    } else {
                        isSwipedLeft = false;
                        isSwipedRight = false;

                    }

                    if (swipey2 - swipey1 > MIN_SWIPE_DIST) {
                        startActivity(new Intent(this, MainActivity.class));
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

    private class MyTrackProgressBar {

        private static final int LOOP_DURATION = 500;
        private final SeekBar mSeekBar;
        private final Handler mHandler;

        private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener =
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                      //  Log.i("onseekbarchange",seekBar.getProgress() + "/" + universalPlayerState.track.duration);
                        MainActivity.currentSongPlaybackPosition = seekBar.getProgress();
                        autoplayNextv3(universalPlayerState);
                        try{
                        updateSongQueue();
                        }catch (Exception e)
                        {
                            Log.e("haha","funny");
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mSpotifyAppRemote
                                .getPlayerApi()
                                .seekTo(seekBar.getProgress())
                                .setErrorCallback(mErrorCallback);
                    }
                };

        private final Runnable mSeekRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        int progress = mSeekBar.getProgress();
                        mSeekBar.setProgress(progress + LOOP_DURATION);
                        mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
                    }
                };

        private MyTrackProgressBar(SeekBar seekBar) {
            mSeekBar = seekBar;
            mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
            mHandler = new Handler();
        }

        private void setDuration(long duration) {
            mSeekBar.setMax((int) duration);
        }

        private void update(long progress) {
            mSeekBar.setProgress((int) progress);
        }

        private void pause() {
            mHandler.removeCallbacks(mSeekRunnable);
        }

        private void unpause() {
            mHandler.removeCallbacks(mSeekRunnable);
            mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
        }

    }

    public void onClickSeekToChorus(View view){
        RemotePlayerActivity.mSpotifyAppRemote
                .getPlayerApi().seekTo((long) RemotePlayerActivity.chorusTime*1000);

    }

//    public void onClickSeekToChorus(View view){
////        mSpotifyAppRemote
////                .getPlayerApi().seekTo((long) chorusTime);
//        Log.e("ASS","ASS");
//    }


    //COMMENT: OVERWRITE NEXT
    public void PlayNextNative (View view){
        try {
            MainActivity.playUri(MainActivity.nextSong.get(1));
        }catch (Exception e){
            Toast.makeText(this, "End of Playlist, Can't play next", Toast.LENGTH_SHORT).show();
        }

    }
    // COMMENTS: OVERWRITE PREV
    public void PlayPrevNative(View view){
        try {
            MainActivity.playUri(MainActivity.prevSong.get(1));

        }catch (Exception e){
            Toast.makeText(this, "Beginning of Playlist, Can't play prev", Toast.LENGTH_SHORT).show();
        }
    }

    // COMMENTS: UPDATE NEXT AND PREV SONG DETAILS
    public void updateSongQueue(){
        //spotify:track:2BHj31ufdEqVK5CkYDp9mA,,,2BHj31ufdEqVK5CkYDp9mA,,,Mayonaka no Door / Stay With Me,,,Miki Matsubara,,,108.259,,,0.747,,,0.792,,,-4.177,,,267.3792,,,https://i.scdn.co/image/ab67616d0000b27381052badd62d5e14c3377786
        Glide.with(this)
                .load(MainActivity.nextSong.get(9))
                .placeholder(R.drawable.ic__logo)
                .into((ImageView)findViewById(R.id.songArtNextIV));
        ((TextView)findViewById(R.id.playerSongNameNextTV)).setText(MainActivity.nextSong.get(2));
        ((TextView)findViewById(R.id.playerArtistNameNextTV)).setText(MainActivity.nextSong.get(3));

        Glide.with(this)
                .load(MainActivity.prevSong.get(9))
                .placeholder(R.drawable.ic__logo)
                .into((ImageView)findViewById(R.id.songArtPrevIV));
        ((TextView)findViewById(R.id.playerSongNamePrevTV)).setText(MainActivity.prevSong.get(2));
        ((TextView)findViewById(R.id.playerArtistNamePrevTV)).setText(MainActivity.prevSong.get(3));

        ((TextView)findViewById(R.id.playerLoudnessTV)).setText("Loudness: " + (int)(parseFloat(MainActivity.currentSong.get(7))+12));
        ((ProgressBar)findViewById(R.id.playerLoudnessBar)).setProgress(((int)parseFloat(MainActivity.currentSong.get(7))+12));

        ((TextView)findViewById(R.id.playerEnergyTV)).setText("Energy: " + (int)(parseFloat(MainActivity.currentSong.get(5))*10));
        ((ProgressBar)findViewById(R.id.playerEnergyBar)).setProgress((int)(parseFloat(MainActivity.currentSong.get(5))*10));

        ((TextView)findViewById(R.id.playerBPMTV)).setText("BPM: " + (int)parseFloat(MainActivity.currentSong.get(4)));
        ((ProgressBar)findViewById(R.id.playerBPMBar)).setProgress((int)parseFloat(MainActivity.currentSong.get(4)));

    }

//    public void onClickShuffle(View view){
//        Collections.shuffle(MainActivity.currentQueueList);
//        MyRVAdapter myRVAdapter = new MyRVAdapter(MainActivity.currentQueueList, this,  MainActivity.myRVInterface, MainActivity.isDoubleBeat);
//        MainActivity.myRV = findViewById(R.id.runPlaylistRV);
//        MainActivity.myRV.setLayoutManager(new LinearLayoutManager(this));
//        MainActivity.myRV.setHasFixedSize(true);
//        MainActivity.myRV.setAdapter(new MyRVAdapter(MainActivity.currentQueueList, this, MainActivity.myRVInterface, MainActivity.isDoubleBeat));
//        myRVAdapter.notifyDataSetChanged();
//    }



//    void playUri(String uri) {
//        RemotePlayerActivity.mSpotifyAppRemote
//                .getPlayerApi()
//                .play(uri)
//                .setResultCallback(empty -> logMessage(getString(R.string.command_feedback, "play")))
//                .setErrorCallback(mErrorCallback);
//        MainActivity.nextSongLoaded = false;
//        if(MainActivity.nextSongLoaded == false){
//            MainActivity.currentSongDurationOnce = MainActivity.currentSongDuration;
//        }
//    }


}