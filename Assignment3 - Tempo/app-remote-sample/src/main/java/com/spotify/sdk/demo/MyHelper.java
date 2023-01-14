//adapted from lab code

package com.spotify.sdk.demo;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyHelper extends SQLiteOpenHelper {

    private Context context;

    private static final String CREATE_TABLE =
            "CREATE TABLE " +
                    DBConstant.TABLE_NAME + " (" +
                    DBConstant.ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DBConstant.TRACK_ID + " TEXT, " +
                    DBConstant.TRACK_URI + " TEXT UNIQUE, " +
                    DBConstant.TRACK_NAME + " TEXT, " +
                    DBConstant.TRACK_ARTIST + " TEXT, " +
                    DBConstant.TRACK_TEMPO + " TEXT, " +
                    DBConstant.TRACK_ENERGY + " TEXT, " +
                    DBConstant.TRACK_VALENCE + " TEXT, " +
                    DBConstant.TRACK_CHORUS_LOUDNESS + " TEXT, " +
                    DBConstant.TRACK_CHORUS_START_TIME + " TEXT, " +
                    DBConstant.TRACK_ART_IMAGE + " TEXT);";
    private static final  String DROP_TABLE = "DROP TABLE IF EXISTS " + DBConstant.TABLE_NAME;

    public MyHelper(Context context){
        super(context,DBConstant.DATABASE_NAME,null,DBConstant.DATABASE_VERSION);
        this.context = context;


    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);

        } catch (SQLException e) {

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try {
            db.execSQL(DROP_TABLE);
            onCreate(db);

        } catch (SQLException e) {

        }
    }
}
