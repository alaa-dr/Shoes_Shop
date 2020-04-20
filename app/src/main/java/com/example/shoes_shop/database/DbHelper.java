package com.example.shoes_shop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.Currency;

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION=1;
    public static final String CREATE_ORDER_TABLE="CREATE TABLE "+DbContract.TABLE_ORDER_NAME
            +"("+DbContract.ORDER_ID+" INTEGER,"+DbContract.USER_ID+" INTEGER,"
            +DbContract.ORDER_TYPE +" INTEGER,"+DbContract.SUPPLIER_CLIENT_NAME+" VARCHAR,"
            +DbContract.SYNC_STATUS+" INTEGER,"+DbContract.STATUS+" VARCHAR);";
    public static final String DROP_ORDER_TABLE="DROP TABLE IF EXISTS "
            +DbContract.TABLE_ORDER_NAME;

    public DbHelper(@Nullable Context context) {
        super(context, DbContract.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ORDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DROP_ORDER_TABLE);
        onCreate(db);
    }




//TODO updata debending on name and that is wrong
    public void updataOrderDatabase(int user_id,int order_type,String supplier_client_name
            ,int sync_status,String status,SQLiteDatabase db){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.SYNC_STATUS,sync_status);
        String selection =DbContract.SUPPLIER_CLIENT_NAME+" LIKE ?";
        String[] selection_args={supplier_client_name};
        db.update(DbContract.TABLE_ORDER_NAME,contentValues,selection,selection_args);
    }
}
