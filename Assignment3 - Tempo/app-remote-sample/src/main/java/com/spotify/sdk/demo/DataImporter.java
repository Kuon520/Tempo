//THIS CLASS IS FOR IMPORTING DATA FROM PREBUIOLT CSV TO SQLITE

package com.spotify.sdk.demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.spotify.android.appremote.demo.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DataImporter {

    public static ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
    static MyDB db;

    static public void loadFiveEntries(Context context) throws IOException {
        new ImportingTask().execute(context);
    }

    static public class LoadingMusicToListTask extends AsyncTask<Context,Integer,Void> {

        @Override
        protected Void doInBackground(Context... contexts) {
            list = db.getAllData();
            return null;
        }
    }

    static public class ImportingTask extends AsyncTask<Context,Integer,Void> {

        @Override
        protected Void doInBackground(Context... context) {
            try {
                db = new MyDB(context[0]);
                InputStream myIS = context[0].getResources().openRawResource(R.raw.spotifyoutdatatxt);
                if(myIS != null){
                    InputStreamReader inputStreamReader = new InputStreamReader(myIS);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        try {
                            String[] arr = receiveString.split(",,,");
                            ArrayList<String> tempArrList = new ArrayList<String>();
                            long id = db.insertData(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5],
                                    arr[6], arr[7], arr[8], arr[9]);
//                        for (String each:arr
//                             ) {
//                            tempArrList.add(each);
//                        }
//                        list.add(tempArrList);

                            //Read string to database

                        } catch (Exception e) {

                        }
                    }
                    Log.i("TEMPARRLIST",list.toString());

                    myIS.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TEMPARRLIST","I'm dead");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }
}

//https://stackoverflow.com/questions/14376807/read-write-string-from-to-a-file-in-android