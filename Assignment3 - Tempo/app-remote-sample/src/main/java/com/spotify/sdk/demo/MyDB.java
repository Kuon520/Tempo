package com.spotify.sdk.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.net.URL;
import java.util.ArrayList;

//Code adapted from lecture

public class MyDB {

    public static SQLiteDatabase db;
    private Context context;
    private final MyHelper helper;

    public MyDB(Context c){
        context = c;
        helper = new MyHelper(context);
    }

    public long insertData (String tId, String tUri, String name, String artist,
    String tempo, String energy, String valence, String cLoud, String cStartTime, String imageUrl)
    {
        try{
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstant.TRACK_ID, tId);
        contentValues.put(DBConstant.TRACK_URI, tUri);
        contentValues.put(DBConstant.TRACK_NAME,name);
        contentValues.put(DBConstant.TRACK_ARTIST,artist);
        contentValues.put(DBConstant.TRACK_TEMPO,tempo);
        contentValues.put(DBConstant.TRACK_ENERGY,energy);
        contentValues.put(DBConstant.TRACK_VALENCE,valence);
        contentValues.put(DBConstant.TRACK_CHORUS_LOUDNESS,cLoud);
        contentValues.put(DBConstant.TRACK_CHORUS_START_TIME,cStartTime);
        contentValues.put(DBConstant.TRACK_ART_IMAGE,imageUrl);
        long id = db.insertOrThrow(DBConstant.TABLE_NAME, null, contentValues);
        return id;}
        catch (Exception e){
        }
        return -1;
    }



    //TODO: getData getSelectedData deleteRow
    public ArrayList<String> getSelectedData(String trackName)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DBConstant.ENTRY_ID, DBConstant.TRACK_URI, DBConstant.TRACK_ID,
        DBConstant.TRACK_NAME,DBConstant.TRACK_ARTIST,DBConstant.TRACK_TEMPO,DBConstant.TRACK_ENERGY,
        DBConstant.TRACK_VALENCE,DBConstant.TRACK_CHORUS_LOUDNESS,DBConstant.TRACK_CHORUS_START_TIME,DBConstant.TRACK_ART_IMAGE};

        String selection = DBConstant.TRACK_NAME + "='"+trackName+"'";
        Cursor cursor = db.query(DBConstant.TABLE_NAME, columns,selection,null,null,null,null);

        ArrayList<String> returnArrString = new ArrayList<String>();

        while (cursor.moveToNext()) {
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_URI)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ID)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_NAME)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ARTIST)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_TEMPO)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ENERGY)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_VALENCE)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_CHORUS_LOUDNESS)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_CHORUS_START_TIME)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ART_IMAGE)));
        }
        return returnArrString;
    }

    public ArrayList<ArrayList<String>>getAllData(){
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DBConstant.ENTRY_ID, DBConstant.TRACK_URI, DBConstant.TRACK_ID,
                DBConstant.TRACK_NAME,DBConstant.TRACK_ARTIST,DBConstant.TRACK_TEMPO,DBConstant.TRACK_ENERGY,
                DBConstant.TRACK_VALENCE,DBConstant.TRACK_CHORUS_LOUDNESS,DBConstant.TRACK_CHORUS_START_TIME,DBConstant.TRACK_ART_IMAGE};

        Cursor cursor = db.query(DBConstant.TABLE_NAME, columns,null,null,null,null,null);

        ArrayList<ArrayList<String>> returnArrString = new ArrayList<ArrayList<String>>();

        while (cursor.moveToNext()) {
            ArrayList<String> arrString = new ArrayList<String>();
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_URI)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ID)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_NAME)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ARTIST)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_TEMPO)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ENERGY)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_VALENCE)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_CHORUS_LOUDNESS)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_CHORUS_START_TIME)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ART_IMAGE)));

            returnArrString.add(arrString);
        }
        return returnArrString;
    }

    public ArrayList<String> getSelectedData(int entryID)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DBConstant.ENTRY_ID, DBConstant.TRACK_URI, DBConstant.TRACK_ID,
                DBConstant.TRACK_NAME,DBConstant.TRACK_ARTIST,DBConstant.TRACK_TEMPO,DBConstant.TRACK_ENERGY,
                DBConstant.TRACK_VALENCE,DBConstant.TRACK_CHORUS_LOUDNESS,DBConstant.TRACK_CHORUS_START_TIME,DBConstant.TRACK_ART_IMAGE};

        String selection = DBConstant.ENTRY_ID + "='"+(entryID+1)+"'";
        Cursor cursor = db.query(DBConstant.TABLE_NAME, columns,selection,null,null,null,null);

        ArrayList<String> returnArrString = new ArrayList<String>();

        while (cursor.moveToNext()) {
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_URI)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ID)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_NAME)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ARTIST)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_TEMPO)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ENERGY)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_VALENCE)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_CHORUS_LOUDNESS)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_CHORUS_START_TIME)));
            returnArrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ART_IMAGE)));
        }
        return returnArrString;
    }

    public ArrayList<ArrayList<String>> getSelectedDataTempoRange(float lowerLim, float upperLim)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DBConstant.ENTRY_ID, DBConstant.TRACK_URI, DBConstant.TRACK_ID,
                DBConstant.TRACK_NAME,DBConstant.TRACK_ARTIST,DBConstant.TRACK_TEMPO,DBConstant.TRACK_ENERGY,
                DBConstant.TRACK_VALENCE,DBConstant.TRACK_CHORUS_LOUDNESS,DBConstant.TRACK_CHORUS_START_TIME,DBConstant.TRACK_ART_IMAGE};

        String selection = DBConstant.TRACK_TEMPO + " BETWEEN "+ ((float)lowerLim) + " AND " + (float)upperLim;
        Log.i("getSelectedDataTempoRan",selection);
        Cursor cursor = db.query(DBConstant.TABLE_NAME, columns,selection,null,null,null,null);
        Log.i("getSelectedDataTempoRan",cursor.toString());
        ArrayList<ArrayList<String>> returnArrString = new ArrayList<ArrayList<String>>();

        while (cursor.moveToNext()) {
            ArrayList<String> arrString = new ArrayList<String>();
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_URI)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ID)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_NAME)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ARTIST)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_TEMPO)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ENERGY)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_VALENCE)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_CHORUS_LOUDNESS)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_CHORUS_START_TIME)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ART_IMAGE)));
            returnArrString.add(arrString);
        }
        return returnArrString;
    }

    public ArrayList<ArrayList<String>> getSelectedDataEnergyRange(float lowerLim, float upperLim)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DBConstant.ENTRY_ID, DBConstant.TRACK_URI, DBConstant.TRACK_ID,
                DBConstant.TRACK_NAME,DBConstant.TRACK_ARTIST,DBConstant.TRACK_TEMPO,DBConstant.TRACK_ENERGY,
                DBConstant.TRACK_VALENCE,DBConstant.TRACK_CHORUS_LOUDNESS,DBConstant.TRACK_CHORUS_START_TIME,DBConstant.TRACK_ART_IMAGE};

        String selection = DBConstant.TRACK_ENERGY + " BETWEEN "+ ((float)lowerLim) + " AND " + (float)upperLim;
        Log.i("getSelectedDataTempoRan",selection);
        Cursor cursor = db.query(DBConstant.TABLE_NAME, columns,selection,null,null,null,null);
        Log.i("getSelectedDataTempoRan",cursor.toString());
        ArrayList<ArrayList<String>> returnArrString = new ArrayList<ArrayList<String>>();

        while (cursor.moveToNext()) {
            ArrayList<String> arrString = new ArrayList<String>();
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_URI)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ID)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_NAME)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ARTIST)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_TEMPO)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ENERGY)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_VALENCE)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_CHORUS_LOUDNESS)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_CHORUS_START_TIME)));
            arrString.add(cursor.getString(cursor.getColumnIndex(DBConstant.TRACK_ART_IMAGE)));
            returnArrString.add(arrString);
        }
        return returnArrString;
    }

}
