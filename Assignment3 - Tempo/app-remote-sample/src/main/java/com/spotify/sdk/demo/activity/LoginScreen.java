

package com.spotify.sdk.demo.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.spotify.android.appremote.demo.R;
import com.spotify.sdk.demo.DataImporter;

public class LoginScreen extends AppCompatActivity  {

    private EditText inputUsername, inputPassword;

    private TextView hintText;



//    private static final String USERNAME = "not available";
//    private static final String PASSWORD = "not available";

    private static final String DEFAULT = "not available";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        //getSupportActionBar().hide();
        inputUsername = (EditText) findViewById(R.id.setUserNameEditText);
        inputPassword = (EditText) findViewById(R.id.setPasswordEditText);

        hintText = (TextView) findViewById(R.id.hintTextView);

//        ImageView iv3 = (ImageView)findViewById(R.id.imageView3);
//        Glide.with(this).load("https://i.scdn.co/image/ab67616d0000b273d964553c8844d4cbbed5e200").into(iv3);
        new DataImporter.ImportingTask().execute(this);
        new DataImporter.LoadingMusicToListTask().execute(this);

    }


    //situation confirm, only login to the main activity if the password is set and entering correctly
    public void startMainActivity(View view){

        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String username = sharedPrefs.getString("username", DEFAULT);
        String password = sharedPrefs.getString("password", DEFAULT);


        if ( username.equals(DEFAULT) || password.equals(DEFAULT) )
        {

            hintText.setText("Please Set Up Your Username and Password");

        } else if (! username.equals(inputUsername.getText().toString()) || ! password.equals(inputPassword.getText().toString()))
        {

            hintText.setText("Username or Password Invalid");


        } else if (username.equals(inputUsername.getText().toString()) && password.equals(inputPassword.getText().toString()))
        {
            Intent intent = new Intent(this, RemotePlayerActivity.class);
            startActivity(intent);
        }


    }

    //if the password and username is not setting up or want to change go to intent set or change info
    public void changeInfoActivity(View view){
        Intent intent = new Intent(this, SetOrChangeInfo.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


}