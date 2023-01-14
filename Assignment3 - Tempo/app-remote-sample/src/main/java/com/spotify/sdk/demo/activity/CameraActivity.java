

package com.spotify.sdk.demo.activity;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.android.appremote.demo.R;
import com.spotify.sdk.demo.StepPerMinServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener{


    ImageButton cameraButton;
    ImageView cameraImage;

    Button shareButton;

    Bitmap photo = null;

    Intent share;

    TextView reminderText;

    private int REQUEST_CODE_PERMISSIONS = 101;

    private static final int REQUEST_EXTERNAL_STORAGE_RESULT = 1;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};


    File pictureDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraDemo");
    private Uri fileUri;

    //service define
    private TextView stepDetector, timerText, stepPreM, distance;

    //int

    boolean isServiceStopped;
    private Intent intent;
    private static final String TAG = "SensorEvent";

    // value calculate
    int stepMove;
    int secTime, minTime, hourTime, distanceM;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){ //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        stepDetector = (TextView) findViewById(R.id.stepDetectorTextView);
        timerText = (TextView) findViewById(R.id.timerTextView);
        stepPreM = (TextView)findViewById(R.id.stepPerMinutesTextView);
        distance = (TextView) findViewById(R.id.distanceTextView);


        intent = new Intent(this, StepPerMinServices.class);

        isServiceStopped = true; // variable for managing service state - required to invoke "stopService" only once to avoid Exception.

        cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);

        cameraImage = (ImageView) findViewById(R.id.cameraImage);

        shareButton = (Button) findViewById(R.id.shareButton);

        reminderText = (TextView) findViewById(R.id.reminderTextView);

//        if (!pictureDir.exists()) {
//            pictureDir.mkdirs();
//        }

    }


    //camera image info change to bitmap
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    try{
                        photo = (Bitmap) result.getData().getExtras().get("data");

                        cameraImage.setImageBitmap(photo);
                    } catch(Exception e){

                    }

                }
            });

    //check the camera permission
    private boolean allPermissionsGranted()
    {
        for (String permission : REQUIRED_PERMISSIONS)
        {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED)
            {
                return false;
            }
        }

        return true;
    }

    //return image information from camera to the application, implicit intent
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if(allPermissionsGranted()){

                try {
                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    resultLauncher.launch(camera_intent);
                } catch(Exception e){

                }
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch(Exception e){
            Toast.makeText(this, "abc", Toast.LENGTH_SHORT).show();
        }

    }



    public void onClick (View view)
    {
        if(allPermissionsGranted()){
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            resultLauncher.launch(camera_intent);
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    //share button
    public void shareActivities(View view)
    {
        shareImage(photo);
    }

    //when the share event start, it can show the text and image
    private void shareImage(Bitmap photo) {
        try {
            if (photo == null) {

                reminderText.setVisibility(View.VISIBLE);

            } else {
                share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                Uri bitmapUri;


                String textToShare = "#StepMove: " + stepMove + " Steps " + " #ExerciseTime: " + hourTime + ":" +
                        minTime + ":" + secTime + " #DistanceTravel: " + distanceM + " Meter ";

                bitmapUri = saveImage(photo, getApplicationContext());

                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                //        share.putExtra(Intent.EXTRA_SUBJECT, "cde");
                share.putExtra(Intent.EXTRA_TEXT, textToShare);

                //giving premission to share to read and write the uri
                share.setClipData(ClipData.newRawUri("", bitmapUri));
                share.addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                startActivity(Intent.createChooser(share, "Share Image"));
            }
        }catch (Exception e) {

        }
    }

    //store the image in the external path and return the uri
    private Uri saveImage (Bitmap image, Context context)
    {
        File imageFile = new File(context.getCacheDir(), "images");
        Uri uri = null;

        try {
            imageFile.mkdir();
            File file = new File(imageFile, "shared_images.jpg");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 90, stream);

            stream.flush();
            stream.close();

            uri = FileProvider.getUriForFile(this,"com.spotify.android.appremote.demo" + ".provider", file);


        }
        catch (IOException error)
        {
            Log.d("Exception", error.getMessage());
        }
        return uri;
    }
    //share image code study from: https://www.youtube.com/watch?v=BWZv0iynWkE

    //button click to start the services
    public void startServices(View view){
        // start Service.
        startService(new Intent(getBaseContext(), StepPerMinServices.class));
        // register our BroadcastReceiver by passing in an IntentFilter. * identifying the message that is broadcasted by using static string "BROADCAST_ACTION".
        registerReceiver(broadcastReceiver, new IntentFilter(StepPerMinServices.BROADCAST_ACTION));
        isServiceStopped = false;

    }

    //button click to stop the services when it's not using
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
            updateViews(intent);

        }
    };
    // ___________________________________________________________________________ \\

    //update the text view by receiving services
    private void updateViews(Intent intent){



        stepMove = Integer.parseInt(intent.getStringExtra("Detected_Step"));
        secTime = (int) Double.parseDouble(intent.getStringExtra("time")) % 60;
//        int secTimeCalculate = secTime % 60;
//        Toast.makeText(this, secTimeCalculate+ "", Toast.LENGTH_SHORT).show();
        minTime = (int) Double.parseDouble(intent.getStringExtra("time")) / 60;
        hourTime = (int) Double.parseDouble(intent.getStringExtra("time")) / 3600;
//
        distanceM = Integer.parseInt(intent.getStringExtra("distance"));

        stepDetector.setText("Step Detect: " + intent.getStringExtra("Detected_Step"));
        timerText.setText("Time: " + String.valueOf(hourTime) + ":" + String.valueOf(minTime) + ":" + String.valueOf(secTime));
        //timerText.setText(intent.getStringExtra("time"));
        stepPreM.setText("Step Per Minutes: " + intent.getStringExtra("stepPerMin"));

        distance.setText("Distance: " + intent.getStringExtra("distance") + "  meter");



    }


}