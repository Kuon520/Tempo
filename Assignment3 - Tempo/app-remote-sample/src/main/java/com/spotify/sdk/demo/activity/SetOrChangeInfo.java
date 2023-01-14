
package com.spotify.sdk.demo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.spotify.android.appremote.demo.R;

public class SetOrChangeInfo extends AppCompatActivity {

    private EditText setUsername, setPassword;

    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_or_change_info);

        setUsername = (EditText) findViewById(R.id.setUserNameEditText);
        setPassword = (EditText) findViewById(R.id.setPasswordEditText);

        //getSupportActionBar().hide();
    }


    //user can set their own username and password and it's store in shared preference
    public void backToLoginScreen(View view){

        username = setUsername.getText().toString();
        password = setPassword.getText().toString();



        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("username", username);
        editor.putString("password", password);


        Toast.makeText(this, username + "  " + password, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();

        editor.commit();

        Intent intent = new Intent(this, LoginScreen.class);
        startActivity(intent);
    }
}