package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory.data.ToolContract.ToolEntry;

/**
 * Database helper for Inventory app. Manages database creation and version management.
 */
public class ToolDbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "inventory.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ToolDbHelper}.
     *
     * @param context of the app
     */
    public ToolDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the products table
        String SQL_CREATE_PRODUCT_TABLE =  "CREATE TABLE " + ToolEntry.TABLE_NAME + " ("
                + ToolEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ToolEntry.COLUMN_TOOL_NAME + " TEXT NOT NULL, "
                + ToolEntry.COLUMN_TOOL_PRICE + " REAL NOT NULL, "
                + ToolEntry.COLUMN_TOOL_QUANTITY + " INTEGER, "
                + ToolEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ToolEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}