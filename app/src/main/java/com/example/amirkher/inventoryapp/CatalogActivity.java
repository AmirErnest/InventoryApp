package com.example.amirkher.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.database.Cursor;
import com.example.amirkher.inventoryapp.data.ProductContract.ProductEntry;

import android.widget.AdapterView;
import android.widget.ListView;
import android.content.ContentValues;
import android.widget.Toast;

import com.example.amirkher.inventoryapp.data.ProductDbHelper;

/**
 * Displays list of products that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    ProductCursorAdapter mCursorAdapter;

    /** Database helper that will provide us access to the database */
    private ProductDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_activity);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView productListView = (ListView) findViewById(R.id.product_list);

        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new ProductDbHelper(this);

        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        // Set a clickListener on that View
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Create a new intent
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                //Start the new activity
                startActivity(intent);
            }
        });

        // Connect with an existing loader
        // or start a new one.
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

    }


    /**
     * Helper method to insert hardcoded products data into the database.
     */
    private void insertProducts() {
        insertProduct("Calculator", 3, 1, 26, "Tedi GmbH", "+08213456789", "tediofficestuff@tedi.de", R.drawable.ic_calculator);
        insertProduct("Folder", 1, 2, 0, "Tami GmbH", "+03847838477", "tamiofficestuff@tami.de", R.drawable.ic_folder);
        insertProduct("Hot Glue Gun", 6, 0, 12, "Tofi GmbH", "+95756384766", "tofiofficestuff@tedi.de", R.drawable.ic_gluegun);
    }

    private void insertProduct(String title, int price, int inStock, int quantity, String supplier, String supplierPhone, String supplierEmail, int imageId) {
        // Create a ContentValues object where column names are the keys,
        // and product's attributes are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, title);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductEntry.COLUMN_PRODUCT_INSTOCK, inStock);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplier);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhone);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, supplierEmail);


        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(imageId)
                + '/' + getResources().getResourceTypeName(imageId) + '/' + getResources().getResourceEntryName(imageId) );

        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imageUri.toString());

        // Insert a new row for Calculator into the provider using the ContentResolver.
        // Use the {@link ProductEntry#CONTENT_URI} to indicate that we want to insert
        // into the products database table.
        // Receive the new content URI that will allow us to access Calculator's data in the future.
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful.
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.insertion_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.insertion_successful),
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProducts();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteAllConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_INSTOCK,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE,
                ProductEntry.COLUMN_PRODUCT_IMAGE,
               };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ProductEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Update {@link ProductCursorAdapter} with this new cursor containing updated products data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    /**
     * Prompt the user to confirm that they want to delete everything
     */
    private void showDeleteAllConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
                Log.v("CatalogActivity", rowsDeleted + " rows deleted from books database");
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
