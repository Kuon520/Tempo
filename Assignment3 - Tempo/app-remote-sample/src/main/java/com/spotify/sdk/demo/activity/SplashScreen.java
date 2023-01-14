package com.spotify.sdk.demo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.spotify.android.appremote.demo.R;

//splash screen
public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_LENGTH = 1000;

    //1s after and go to the login in activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this, LoginScreen.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }


}