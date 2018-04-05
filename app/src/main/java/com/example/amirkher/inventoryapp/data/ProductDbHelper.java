package com.example.amirkher.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import com.example.amirkher.inventoryapp.data.ProductContract.ProductEntry;
/**
 * Created by Amirkher on 07.03.2018.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "products.db";


    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ProductDbHelper}.
     *
     * @param context of the app
     */
    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the products table
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_INSTOCK + " TEXT, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT, "
                + ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + " TEXT, "
                + ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_IMAGE + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
