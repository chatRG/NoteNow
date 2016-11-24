package com.notenow.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.notenow.model.Note;

import java.util.List;

public class DBManager {
    private DBManager instance;
    private Context context;
    private NoteDBOpenHelper databaseOpenHelper;
    private SQLiteDatabase dbReader;
    private SQLiteDatabase dbWriter;

    public DBManager(Context context) {
        this.context = context;
        databaseOpenHelper = new NoteDBOpenHelper(context);

        dbReader = databaseOpenHelper.getReadableDatabase();
        dbWriter = databaseOpenHelper.getWritableDatabase();
    }


    public synchronized DBManager getInstance(Context context) {
        if (instance == null) {
            instance = new DBManager(context);
        }
        return instance;
    }

    /*private void addToDB(Cursor mCursor) {
        ContentValues cv = new ContentValues();
        try {
            while (mCursor.moveToNext()) {
                cv.put(NoteDBOpenHelper.TITLE, mCursor.getString(
                        mCursor.getColumnIndex(NoteDBOpenHelper.TITLE)));
                cv.put(NoteDBOpenHelper.CONTENT, mCursor.getString(
                        mCursor.getColumnIndex(NoteDBOpenHelper.CONTENT)));
                cv.put(NoteDBOpenHelper.TIME, mCursor.getString(
                        mCursor.getColumnIndex(NoteDBOpenHelper.TIME)));
                cv.put(NoteDBOpenHelper.RANK, mCursor.getString(
                        mCursor.getColumnIndex(NoteDBOpenHelper.RANK)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbWriter.insert(NoteDBOpenHelper.TABLE_NAME, null, cv);
    }*/


    public void addToDB(String title, String content, String time, String rank) {
        ContentValues cv = new ContentValues();
        cv.put(NoteDBOpenHelper.TITLE, title);
        cv.put(NoteDBOpenHelper.CONTENT, content);
        cv.put(NoteDBOpenHelper.TIME, time);
        cv.put(NoteDBOpenHelper.RANK, rank);
        dbWriter.insert(NoteDBOpenHelper.TABLE_NAME, null, cv);
    }


    public void readFromDB(List<Note> noteList) {
        Cursor cursor = dbReader.query(NoteDBOpenHelper.TABLE_NAME,
                null, null, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(NoteDBOpenHelper.ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(NoteDBOpenHelper.TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndex(NoteDBOpenHelper.CONTENT)));
                note.setTime(cursor.getString(cursor.getColumnIndex(NoteDBOpenHelper.TIME)));
                note.setRank(cursor.getString(cursor.getColumnIndex(NoteDBOpenHelper.RANK)));
                noteList.add(note);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateNote(int noteID, String title, String content, String time, String rank) {
        ContentValues cv = new ContentValues();
        cv.put(NoteDBOpenHelper.ID, noteID);
        cv.put(NoteDBOpenHelper.TITLE, title);
        cv.put(NoteDBOpenHelper.CONTENT, content);
        cv.put(NoteDBOpenHelper.TIME, time);
        cv.put(NoteDBOpenHelper.RANK, rank);
        dbWriter.update(NoteDBOpenHelper.TABLE_NAME, cv, "_id = ?", new String[]{noteID + ""});
    }


    public void deleteNote(int noteID) {
        dbWriter.delete(NoteDBOpenHelper.TABLE_NAME, "_id = ?", new String[]{noteID + ""});
    }

    public void deleteAllNote() {
        dbWriter.delete(NoteDBOpenHelper.TABLE_NAME, null, null);
    }


    public Note readData(int noteID) {
        Cursor cursor = dbReader.rawQuery(
                "SELECT * FROM note WHERE _id = ?", new String[]{noteID + ""});
        cursor.moveToFirst();
        Note note = new Note();
        note.setId(cursor.getInt(cursor.getColumnIndex(NoteDBOpenHelper.ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(NoteDBOpenHelper.TITLE)));
        note.setContent(cursor.getString(cursor.getColumnIndex(NoteDBOpenHelper.CONTENT)));
        note.setRank(cursor.getString(cursor.getColumnIndex(NoteDBOpenHelper.RANK)));
        return note;
    }

    public boolean sortby(String orderBy) {
        /*Cursor mCursor;

        String create_Table="create table note2 like " + NoteDBOpenHelper.TABLE_NAME;
        dbWriter.execSQL(create_Table);
        dbWriter.execSQL("INSERT INTO note2 ("
                                + NoteDBOpenHelper.ID+", "
                                + NoteDBOpenHelper.CONTENT+", "
                                + NoteDBOpenHelper.TITLE+", "
                                + NoteDBOpenHelper.RANK+", "
                                + NoteDBOpenHelper.TIME+")" +
                " SELECT "
                + NoteDBOpenHelper.ID+", "
                + NoteDBOpenHelper.CONTENT+", "
                + NoteDBOpenHelper.TITLE+", "
                + NoteDBOpenHelper.RANK+", "
                + NoteDBOpenHelper.TIME+
        " FROM "+NoteDBOpenHelper.TABLE_NAME+" ORDER BY "+orderBy);
        dbWriter.execSQL("RENAME TABLE "+NoteDBOpenHelper.TABLE_NAME+" TO note1");
        dbWriter.execSQL("RENAME TABLE note2 TO "+NoteDBOpenHelper.TABLE_NAME);*/

        /*
        mCursor = dbWriter.query(NoteDBOpenHelper.TABLE_NAME,
                null, null, null, null, null, orderBy);
        if (mCursor == null)
            return false;
        deleteAllNote();
        addToDB(mCursor);
        dbWriter.execSQL("");*/
        return true;
    }
}


