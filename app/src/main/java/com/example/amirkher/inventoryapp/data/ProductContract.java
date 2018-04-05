package com.example.amirkher.inventoryapp.data;

import android.content.ContentResolver;
import android.provider.BaseColumns;
import android.net.Uri;

/**
 * Created by Amirkher on 07.03.2018.
 */

public final class ProductContract {

    /**
     * Content authority
     * a string constant whose value is the same as that from the AndroidManifest**/
    public static final String CONTENT_AUTHORITY = "com.example.amirkher.inventoryapp";

    /**
     * concatenate the CONTENT_AUTHORITY constant with the scheme “content://”
     * we will create the BASE_CONTENT_URI which will be shared by every URI associated with ProductContract
     * To make this a usable URI, we use the parse method which takes in a URI string and returns a Uri.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /** This constants stores the path for each of the
     * tables which will be appended to the base content URI. **/
    public static final String PATH_PRODUCTS = "products";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ProductContract() {
    }

    /**
     * Inner class that defines constant values for the products database table.
     * Each entry in the table represents a single product.
     */
    public static class ProductEntry implements BaseColumns {

        /**
         * inside each of the Entry classes in the contract, we create a
         * full URI for the class as a constant called CONTENT_URI.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /** Name of database table for products */
        public final static String TABLE_NAME = "products";


        /**
         * Unique ID number for the product (only for use in the database table).
         *
         * Type: INTEGER
         */
         public final static String _ID = BaseColumns._ID;


        /**
         * Name of the product.
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_NAME = "name";


        /**
         * Price of the Product.
         * Type: Integer
         */
        public static final String COLUMN_PRODUCT_PRICE = "price";

        /**
         * In-Stock status.
         * The only possible values are {@link #COMING_SOON}, {@link #IN_STOCK_YES},
         * {@link #IN_STOCK_NO}.
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_INSTOCK = "in_stock";


        /**
         * Quantity of Product in stock.
         * Type: Integer
         */
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";


        /**
         * Product's supplier.
         * Type: String
         */
        public static final String COLUMN_PRODUCT_SUPPLIER = "supplier";
        /**
         * Supplier phone number.
         * Type: Integer
         */
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE = "supplier_telephone";
        /**
         * Supplier's email.
         * Type: String
         */
        public static final String COLUMN_PRODUCT_SUPPLIER_EMAIL = "supplier_email";

        /**
         * Book's image.
         * Type: String
         */
        public final static String COLUMN_PRODUCT_IMAGE = "image";

        /**
         * Possible values for the status of the Product(in Stock).
         */
        public static final int COMING_SOON = 0;
        public static final int IN_STOCK_YES = 1;
        public static final int IN_STOCK_NO = 2;

        /**
         * Returns whether or not the given status is {@link #IN_STOCK_YES}, {@link #IN_STOCK_NO},
         * or {@link #COMING_SOON}.
         */
        public static boolean isValidStatus(int in_stock) {
            if (in_stock == IN_STOCK_YES || in_stock == IN_STOCK_NO || in_stock == COMING_SOON) {
                return true;
            }
            return false;
        }
    }
}
