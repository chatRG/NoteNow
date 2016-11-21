package com.notenow.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDBOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "noteDB";
    public static final String TABLE_NAME = "note";
    public static final int VERSION = 1;
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String TIME = "time";
    public static final String ID = "_id";
    public static final String RANK = "rank";

    public NoteDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String DB_Create = "create table " + TABLE_NAME + " ("
                + ID + " integer primary key autoincrement  ,"
                + CONTENT + " TEXT NOT NULL,"
                + TITLE + " TEXT NOT NULL,"
                + RANK + " TEXT NOT NULL,"
                + TIME + " TEXT NOT NULL)";
        sqLiteDatabase.execSQL(DB_Create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
