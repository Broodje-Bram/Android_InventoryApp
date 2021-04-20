package com.example.inventoryapp.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.inventoryapp.data.StockContract.StockEntry;

public class StockDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = StockDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "stonks.db";
    private static final int DATABASE_VERSION = 1;


    public StockDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_STOCK_TABLE =  "CREATE TABLE " + StockEntry.TABLE_NAME + " ("
                + StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StockEntry.COLUMN_STONK_NAME + " TEXT NOT NULL, "
                + StockEntry.COLUMN_STONK_SUPPLIER + " TEXT, "
                + StockEntry.COLUMN_STONK_QUANTITY + " INTEGER NOT NULL, "
                + StockEntry.COLUMN_STONK_PRICE + " INTEGER NOT NULL, "
                + StockEntry.COLUMN_STONK_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        Log.i(LOG_TAG, SQL_CREATE_STOCK_TABLE);
        db.execSQL(SQL_CREATE_STOCK_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StockEntry.TABLE_NAME);
        onCreate(db);
    }
}