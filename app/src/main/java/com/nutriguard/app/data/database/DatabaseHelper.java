package com.nutriguard.app.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite Database Helper for managing Favorites and Scan History offline.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "nutriguard.db";
    private static final int DATABASE_VERSION = 1;

    // Tables
    public static final String TABLE_FAVORITES = "favorites";
    public static final String TABLE_HISTORY = "history";

    // Columns
    public static final String COLUMN_BARCODE = "barcode";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_BRAND = "brand";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_NUTRISCORE = "nutriscore";
    public static final String COLUMN_NOVA_GROUP = "nova_group";
    public static final String COLUMN_ECOSCORE = "ecoscore";
    public static final String COLUMN_JSON_DATA = "json_data";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String CREATE_TABLE_FAVORITES = "CREATE TABLE " + TABLE_FAVORITES + " ("
            + COLUMN_BARCODE + " TEXT PRIMARY KEY, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_BRAND + " TEXT, "
            + COLUMN_IMAGE_URL + " TEXT, "
            + COLUMN_NUTRISCORE + " TEXT, "
            + COLUMN_NOVA_GROUP + " INTEGER, "
            + COLUMN_ECOSCORE + " TEXT, "
            + COLUMN_JSON_DATA + " TEXT, "
            + COLUMN_TIMESTAMP + " INTEGER"
            + ");";

    private static final String CREATE_TABLE_HISTORY = "CREATE TABLE " + TABLE_HISTORY + " ("
            + COLUMN_BARCODE + " TEXT PRIMARY KEY, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_BRAND + " TEXT, "
            + COLUMN_IMAGE_URL + " TEXT, "
            + COLUMN_NUTRISCORE + " TEXT, "
            + COLUMN_NOVA_GROUP + " INTEGER, "
            + COLUMN_ECOSCORE + " TEXT, "
            + COLUMN_JSON_DATA + " TEXT, "
            + COLUMN_TIMESTAMP + " INTEGER"
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAVORITES);
        db.execSQL(CREATE_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }
}
