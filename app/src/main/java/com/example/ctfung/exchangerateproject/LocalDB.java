package com.example.ctfung.exchangerateproject;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by andy199609 on 2/10/2015.
 */
public class LocalDB extends SQLiteOpenHelper {
    private Context context;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "LocalDB";
    public static SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.ctfung.exchangerateproject/LocalDB", null);

    public LocalDB(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context=context;
    }


    public static void initialize() {
        String [] exchangeRate = {"currencyUnit TEXT","targetUnit TEXT","targetRate TEXT","date DATE"};
        String [] moneyRecord = {"type TEXT","cash TEXT","currencyUnit TEXT","time TEXT","note TEXT","isIncome TEXT"};
        String [] settingRecord = {"language TEXT","currency TEXT","income TEXT","pay TEXT"};
        createTable("ExchangeRate",exchangeRate);
        createTable("MoneyRecord",moneyRecord);
        createTable("SettingRecord",settingRecord);
    }

    private static void createTable(String tableName, String[] cols) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(";
        for (int i = 0; i < cols.length - 1; i++)
            sql += cols[i] + ", ";
        sql += cols[cols.length - 1];
        sql += ");";

        db.execSQL(sql);
    }

    public static void fullInsert(String tableName, String[] data) {
        String sql = "INSERT INTO " + tableName + " VALUES(";
        for (int j = 0; j < data.length - 1; j++)
            sql += data[j] + ", ";
        sql += data[data.length - 1];
        sql += ");";

        db.execSQL(sql);
    }

    public static void partInsert(String tableName, String [] column, String [] data){
        String sql = "INSERT INTO" + tableName +"(";
        for (int i=0;i<column.length-1;i++)
            sql+=column[i]+",";
        sql+=column[column.length-1]+") VALUES(";
        for (int i=0;i<data.length;i++)
            sql+=data[i]+",";
        sql+=data[data.length-1]+");";
        db.execSQL(sql);
    }

    public static void update(String tableName, String[] update, String[] conditions) {
        String sql = "UPDATE " + tableName + " SET ";
        for (int i = 0; i < update.length - 1; i++) {
            sql += update[i] + ", ";
        }
        sql += update[update.length - 1] + " WHERE 1=1 ";
        for (String condition : conditions)
            sql += "AND " + condition + " ";
        db.execSQL(sql);
    }

    public static void delete(String tableName, String[] conditions) {
        String sql = "DELETE FROM " + tableName + " WHERE 1=1 ";
        for (String condition : conditions)
            sql += "AND " + condition + " ";
        db.execSQL(sql);
    }

    public static String getDataBySQL(String sql,int row,String colName) throws CursorIndexOutOfBoundsException {
        Cursor cursor=db.rawQuery(sql, null);
        cursor.moveToPosition(row);
        int colNum=cursor.getColumnIndex(colName);
        try {
            return cursor.getString(colNum);
        }catch(CursorIndexOutOfBoundsException e){
            return null;
        }
    }

    public static int getTableRowCount(String table){
        String sql = "SELECT  * FROM " + table;
        Cursor cursor=db.rawQuery(sql, null);
        try {
            return cursor.getCount();
        }catch(CursorIndexOutOfBoundsException e){
            return 0;
        }
    }

    public static void dropTable(String tableName){
        String sql = "DROP TABLE If exists "+tableName;
        db.execSQL(sql);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
