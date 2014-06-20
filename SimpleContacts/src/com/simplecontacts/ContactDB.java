package com.simplecontacts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDB extends SQLiteOpenHelper {
    private final static String DB_NAME = "contact";
    public final static String TABLE_NAME = "person";
    
    private final static int DB_VERSION = 2;
    private final static String CREATE_DB = "CREATE TABLE IF NOT EXISTS "
                                            + TABLE_NAME
                                            + "(name PRIMARY KEY, telephone, address, picture)";
    
    
    public ContactDB(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }
    
}


