package com.example.ctfung.exchangerateproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by CTFung on 16/5/16.
 */
public class DBOperations extends SQLiteOpenHelper{
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "LocalDB";
    public static SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.ctfung.exchangerateproject/LocalDB", null);

    DBOperations(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        Log.d("Database operations","Database created...");

    }

    public static void initialize() {
        String [] exchangeRate = {"currencyUnit TEXT","targetUnit TEXT","targetRate TEXT","date DATE"};
        createTable("ExchangeRate",exchangeRate);
    }

    private static void createTable(String tableName, String[] cols) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(";
        for (int i = 0; i < cols.length - 1; i++)
            sql += cols[i] + ", ";
        sql += cols[cols.length - 1];
        sql += ");";

        db.execSQL(sql);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
