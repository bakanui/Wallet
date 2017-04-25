/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.vct.wallet.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "vctwallet";

    // Table name
    private static final String TABLE_USER = "users";
    private static final String TABLE_TRANSACTION = "transactions";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_BALANCE = "balance";

    private static final String KEY_ID2 = "id";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_UID2 = "uid";
    private static final String KEY_CREATED_AT2 = "created_at";
    private static final String KEY_CATEGORY_ID = "category_id";
    private static final String KEY_RECEIPT_ID = "receipt_id";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT," + KEY_BALANCE + " TEXT" + ")";
        String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_TRANSACTION + "("
                + KEY_ID2 + " INTEGER PRIMARY KEY," + KEY_DESCRIPTION + " TEXT,"
                + KEY_AMOUNT + " TEXT UNIQUE," + KEY_UID2 + " TEXT,"
                + KEY_CREATED_AT2 + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_TRANSACTION_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String uid, String created_at, String balance) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At
        values.put(KEY_BALANCE, balance); // Balance

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Storing transactions details in database
     * */
    public void addTransactions(String description, String amount, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, description); // description
        values.put(KEY_AMOUNT, amount); // amount
        values.put(KEY_UID2, uid); // uid
        values.put(KEY_CREATED_AT2, created_at); // Created At
        // Inserting Row
        long id = db.insert(TABLE_TRANSACTION, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New transaction inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
            user.put("balance", cursor.getString(5));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getTransactionDetails() {
        HashMap<String, String> transaction = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            transaction.put("description", cursor.getString(1));
            transaction.put("amount", cursor.getString(2));
            transaction.put("uid", cursor.getString(3));
            transaction.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + transaction.toString());

        return transaction;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public void deleteTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_TRANSACTION, null, null);
        db.close();

        Log.d(TAG, "Deleted all transactions info from sqlite");
    }

    public ArrayList<String> getRecentTransactions(String uid) {
        SQLiteDatabase newDB = this.getWritableDatabase();
        ArrayList<String> results = new ArrayList<String>();
        try {
            Cursor c = newDB.rawQuery("SELECT description FROM transactions LIMIT 5", null);

            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        String description = c.getString(c.getColumnIndex("description"));
                        results.add(description);
                    }while (c.moveToNext());
                }
            }
        } catch (SQLiteException se ) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } finally {
            if (newDB != null)
                newDB.execSQL("DELETE FROM " + TABLE_TRANSACTION);
            newDB.close();
        }
        Log.d(TAG, "Fetching user from Sqlite: " + results);
        return results;
    }
}
