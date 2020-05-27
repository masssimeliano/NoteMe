package com.example.noteme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table NotesTable (" + "ID integer primary key autoincrement," + "HeadLine text," + "Text text" + ");");

        Log.d("DataBase", "Created DataBase");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DBHelper(Context context) {
        super(context, "NotesTable", null, 1);
    }

}
