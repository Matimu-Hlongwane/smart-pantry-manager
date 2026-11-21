package com.example.smartpantrymanagerjava.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantrymanagerjava.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for pantry_items table.
 */
public class PantryDao {

    public static final String TABLE_NAME = "pantry_items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_QUANTITY + " REAL NOT NULL, " +
            COLUMN_UNIT + " TEXT NOT NULL, " +
            COLUMN_EXPIRY_DATE + " TEXT" +
            ");";

    private final DatabaseHelper dbHelper;

    public PantryDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insertPantryItem(PantryItem item, SQLiteDatabase writeDb) {
        SQLiteDatabase db = writeDb != null ? writeDb : dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName().trim());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_UNIT, item.getUnit().trim());
        values.put(COLUMN_EXPIRY_DATE, item.getExpiryDate() != null ? item.getExpiryDate().trim() : "");

        long id = db.insert(TABLE_NAME, null, values);
        item.setId(id);
        return id;
    }

    public long insertPantryItem(PantryItem item) {
        return insertPantryItem(item, null);
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName().trim());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_UNIT, item.getUnit().trim());
        values.put(COLUMN_EXPIRY_DATE, item.getExpiryDate() != null ? item.getExpiryDate().trim() : "");

        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(item.getId())});
    }

    public int deletePantryItem(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteAllPantryItems() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    public PantryItem getPantryItemById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        PantryItem item = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                item = cursorToPantryItem(cursor);
            }
            cursor.close();
        }
        return item;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_NAME + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToPantryItem(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public List<PantryItem> searchPantryItems(String query) {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_NAME + " LIKE ?",
                new String[]{"%" + query.trim() + "%"}, null, null, COLUMN_NAME + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToPantryItem(cursor));
            }
            cursor.close();
        }
        return list;
    }

    private PantryItem cursorToPantryItem(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
        double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
        String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIT));
        String expiryDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPIRY_DATE));

        return new PantryItem(id, name, quantity, unit, expiryDate);
    }
}
