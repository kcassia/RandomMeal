package com.example.randommeal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 계현 on 2015-06-21.
 */
public class DBManager extends SQLiteOpenHelper
{
    private static DBManager dbManager;

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static DBManager getInstance(Context context)
    {
        if(dbManager == null)
            dbManager = new DBManager(context, "MEAL.db", null, 1);
        return dbManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table MEAL (" +
                "_id integer primary key autoincrement, " +
                "name text, " +
                "region text, " +
                "style text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists MEAL";
        db.execSQL(sql);

        onCreate(db);
    }


}
