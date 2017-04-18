package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lkatta on 3/20/17.
 */

public class ProductDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_STATEMENT = "CREATE TABLE " +
            ProductContract.ProductEntry.TABLE_NAME +
            " (" + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ProductContract.ProductEntry.COLOUMN_PRODUCT_NAME + " TEXT NOT NULL, "
            + ProductContract.ProductEntry.COLOUMN_PRODUCT_PIC + " BLOB NOT NULL, "
            + ProductContract.ProductEntry.COLOUMN_PRODUCT_PRICE + " FLOAT NOT NULL DEFAULT 0, "
            + ProductContract.ProductEntry.COLOUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0);";

    public ProductDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
