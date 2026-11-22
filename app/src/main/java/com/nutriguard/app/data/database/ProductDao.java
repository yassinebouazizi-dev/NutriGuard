package com.nutriguard.app.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.gson.Gson;
import com.nutriguard.app.data.model.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Offline Storage (Favorites and History).
 */
public class ProductDao {

    private final DatabaseHelper dbHelper;
    private final Gson gson;

    public ProductDao(Context context) {
        this.dbHelper = new DatabaseHelper(context);
        this.gson = new Gson();
    }

    // ==================== FAVORITES ====================

    public synchronized boolean addFavorite(Product product) {
        if (product == null || product.getCode().isEmpty()) return false;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_BARCODE, product.getCode());
        values.put(DatabaseHelper.COLUMN_NAME, product.getNameOrFallback());
        values.put(DatabaseHelper.COLUMN_BRAND, product.getBrandOrFallback());
        values.put(DatabaseHelper.COLUMN_IMAGE_URL, product.getBestImageUrl());
        values.put(DatabaseHelper.COLUMN_NUTRISCORE, product.getNutriscoreGrade());
        values.put(DatabaseHelper.COLUMN_NOVA_GROUP, product.getNovaGroup());
        values.put(DatabaseHelper.COLUMN_ECOSCORE, product.getEcoscoreGrade());
        values.put(DatabaseHelper.COLUMN_JSON_DATA, gson.toJson(product));
        values.put(DatabaseHelper.COLUMN_TIMESTAMP, System.currentTimeMillis());

        long result = db.insertWithOnConflict(DatabaseHelper.TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public synchronized boolean removeFavorite(String barcode) {
        if (barcode == null) return false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_FAVORITES, DatabaseHelper.COLUMN_BARCODE + " = ?", new String[]{barcode});
        return rows > 0;
    }

    public synchronized boolean isFavorite(String barcode) {
        if (barcode == null) return false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_FAVORITES,
                new String[]{DatabaseHelper.COLUMN_BARCODE},
                DatabaseHelper.COLUMN_BARCODE + " = ?",
                new String[]{barcode}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public synchronized List<Product> getAllFavorites() {
        return getProductsFromTable(DatabaseHelper.TABLE_FAVORITES);
    }

    // ==================== HISTORY ====================

    public synchronized void addToHistory(Product product) {
        if (product == null || product.getCode().isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_BARCODE, product.getCode());
        values.put(DatabaseHelper.COLUMN_NAME, product.getNameOrFallback());
        values.put(DatabaseHelper.COLUMN_BRAND, product.getBrandOrFallback());
        values.put(DatabaseHelper.COLUMN_IMAGE_URL, product.getBestImageUrl());
        values.put(DatabaseHelper.COLUMN_NUTRISCORE, product.getNutriscoreGrade());
        values.put(DatabaseHelper.COLUMN_NOVA_GROUP, product.getNovaGroup());
        values.put(DatabaseHelper.COLUMN_ECOSCORE, product.getEcoscoreGrade());
        values.put(DatabaseHelper.COLUMN_JSON_DATA, gson.toJson(product));
        values.put(DatabaseHelper.COLUMN_TIMESTAMP, System.currentTimeMillis());

        db.insertWithOnConflict(DatabaseHelper.TABLE_HISTORY, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        // Keep at most 50 recent items in history
        db.execSQL("DELETE FROM " + DatabaseHelper.TABLE_HISTORY + " WHERE " + DatabaseHelper.COLUMN_BARCODE + " NOT IN ("
                + "SELECT " + DatabaseHelper.COLUMN_BARCODE + " FROM " + DatabaseHelper.TABLE_HISTORY
                + " ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP + " DESC LIMIT 50)");
    }

    public synchronized List<Product> getAllHistory() {
        return getProductsFromTable(DatabaseHelper.TABLE_HISTORY);
    }

    public synchronized void clearHistory() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_HISTORY, null, null);
    }

    private List<Product> getProductsFromTable(String tableName) {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(tableName,
                new String[]{DatabaseHelper.COLUMN_JSON_DATA},
                null, null, null, null,
                DatabaseHelper.COLUMN_TIMESTAMP + " DESC");

        if (cursor != null) {
            try {
                int jsonIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_JSON_DATA);
                while (cursor.moveToNext()) {
                    String json = cursor.getString(jsonIndex);
                    if (json != null && !json.isEmpty()) {
                        Product p = gson.fromJson(json, Product.class);
                        if (p != null) {
                            list.add(p);
                        }
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }
}
