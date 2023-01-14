//DAVID COMMENT THIS PLS

package com.spotify.sdk.demo;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class StepPerMinServices extends Service implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor mStepDetector;

    private boolean isCounterSensorPresent, isDetectorSensorPresent;



    int stepDetect , currentStepDetect, deltaStepDetect;

    Timer myTimer;

    TimerTask timerTask;

    Double time;

    private final static int fiveSecondCount = 5;

    int storeValue0, storeValue1, storeValue2 , storeValue3 , storeValue4 , storeValue5 ;

    long arrStepPerMin;;

    int stepPerMin ;
    int stepPerMinFin;
    long humanArrangeStepLength;
    long distanceTravelCM;
    float distanceTravelM;


    boolean serviceStopped; // Boolean variable to control the repeating timer.


    // --------------------------------------------------------------------------- \\
    // _ (1) declare broadcasting element variables _ \\
    // Declare an instance of the Intent class.
    Intent intent;
    // A string that identifies what kind of action is taking place.
    private static final String TAG = "StepService";
    public static final String BROADCAST_ACTION = "com.websmithing.yusuf.mybroadcast";
    // Create a handler - that will be used to broadcast our data, after a specified amount of time.
    private final Handler handler = new Handler();
    // Declare and initialise counter - for keeping a record of how many times the service carried out updates.
    int counter = 0;
    // ___________________________________________________________________________ \\


    public StepPerMinServices(){

    }


    @Override
    public void onCreate(){
        super.onCreate();

        // --------------------------------------------------------------------------- \\
        // ___ (2) create/instantiate intent. ___ \\
        // Instantiate the intent declared globally, and pass "BROADCAST_ACTION" to the constructor of the intent.
        intent = new Intent(BROADCAST_ACTION);
        // ___________________________________________________________________________ \\
    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Toast.makeText(this, "Service Start", Toast.LENGTH_SHORT).show();


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)
        {
            mStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            isDetectorSensorPresent = true;
        } else
        {
            Toast.makeText(this, "Counter Sensor is not present", Toast.LENGTH_SHORT).show();

            isDetectorSensorPresent = false;
        }

        // 0 means SENSOR_DELAY_FASTEST
        //SENSOR_DELAY_FASTEST: 18-20 ms
        //SENSOR_DELAY_NORMAL: 215-230 ms
        sensorManager.registerListener(this, mStepDetector, 3);

        //currentStepCount = 0;

        stepDetect = 0;
        currentStepDetect = 0;
        deltaStepDetect = 0;
        time = 0.0;
        storeValue0 = 0;
        storeValue1 = 0;
        storeValue2 = 0;
        storeValue3 = 0;
        storeValue4 = 0;
        storeValue5 = 0;

        arrStepPerMin = 0;
        stepPerMin = 0;
        stepPerMinFin = 0;
        humanArrangeStepLength = 70;
        distanceTravelCM = 0;
        distanceTravelM = 0;


        serviceStopped = false;

        // --------------------------------------------------------------------------- \\
        // ___ (3) start handler ___ \\
        /////if (serviceStopped == false) {
        // remove any existing callbacks to the handler
        handler.removeCallbacks(updateBroadcastData);
        // call our handler with or without delay.
        handler.post(updateBroadcastData); // 0 seconds
        /////}
        // ___________________________________________________________________________ \\

        return START_STICKY;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("Service", "Stop");

        serviceStopped = true;

    }

    /** Called when the overall system is running low on memory, and actively running processes should trim their memory usage. */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /////////////////__________________ Sensor Event. __________________//////////////////
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;


        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR)
        {
            stepDetect = (int) (stepDetect + sensorEvent.values[0]);


        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    // ___________________________________________________________________________ \\

    // --------------------------------------------------------------------------- \\
    // ___ (4) repeating timer ___ \\
    private Runnable updateBroadcastData = new Runnable() {
        public void run() {
            if (!serviceStopped) { // Only allow the repeating timer while service is running (once service is stopped the flag state will change and the code inside the conditional statement here will not execute).
                // Call the method that broadcasts the data to the Activity..
                broadcastSensorValue();
                // Call "handler.postDelayed" again, after a specified delay.
                handler.postDelayed(this, 1000);
            }
        }
    };
    // ___________________________________________________________________________ \\


    //method of calculating the step per min.Setting up 5 containers for 5 second results
    //and add the initial container for connecting the results from before this 5s. Finally, arrange the containers to get the final result.
    private void stepPerMin()
    {
        time++;

        deltaStepDetect = stepDetect - currentStepDetect;
        currentStepDetect = stepDetect;

        stepPerMin = deltaStepDetect * 60;


        if (time % fiveSecondCount == 0)
        {
            storeValue1 = stepPerMin;


        } else if (time % fiveSecondCount == 1)
        {

            storeValue2 = stepPerMin;


        } else if (time % fiveSecondCount == 2)
        {
            storeValue3 = stepPerMin;


        } else if (time % fiveSecondCount == 3)
        {
            storeValue4 = stepPerMin;

        } else if (time % fiveSecondCount == 4)
        {
            storeValue5 = stepPerMin;

        }

        arrStepPerMin = (storeValue0 + storeValue1 + storeValue2 + storeValue3 + storeValue4 + storeValue5)/6;
        storeValue0 = (int) arrStepPerMin;


    }

    private void distanceCalculation() {
        distanceTravelCM = stepDetect * humanArrangeStepLength;
        distanceTravelM = distanceTravelCM / 100 ;



    }

    // ___ (5) add  data to intent ___ \\
    private void broadcastSensorValue() {
        stepPerMin();

        distanceCalculation();
        int distanceM = (int) distanceTravelM;


        // add step counter to intent.
//        intent.putExtra("Counted_Step", String.valueOf(newStepCounter));
        // add step detector to intent.
        intent.putExtra("Detected_Step", String.valueOf(stepDetect));
//        intent.putExtra("Detected_Step", String.valueOf(currentStepsDetected));
        // call sendBroadcast with that intent  - which sends a message to whoever is registered to receive it.
        intent.putExtra("time",String.valueOf(time));
        intent.putExtra("stepPerMin", String.valueOf(arrStepPerMin));
        intent.putExtra("distance", String.valueOf(distanceM));


        sendBroadcast(intent);
    }
    // ___________________________________________________________________________ \\


}
