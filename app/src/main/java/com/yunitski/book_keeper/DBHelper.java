package com.yunitski.book_keeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, InputData.DB_NAME, null, InputData.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + InputData.TaskEntry.TABLE + " (" + InputData.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + InputData.TaskEntry.DATE + " TEXT, " + InputData.TaskEntry.TOTAL_VALUE + " TEXT, " + InputData.TaskEntry.VALUE + " TEXT, " + InputData.TaskEntry.OP_NAME + " TEXT);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + InputData.TaskEntry.TABLE);
        onCreate(db);
    }
}
