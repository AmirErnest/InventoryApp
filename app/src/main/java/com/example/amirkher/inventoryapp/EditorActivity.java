package com.example.amirkher.inventoryapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import android.app.AlertDialog;
import android.app.LoaderManager;

import com.example.amirkher.inventoryapp.data.ProductContract.ProductEntry;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.Integer.parseInt;



/**
 * Allows user to create a new product or edit an existing one.
 */

public class EditorActivity extends AppCompatActivity implements
    LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private static final int PICK_IMAGE_REQUEST = 0;

    /**
     * Identifier for the product data loader
     */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /**
     * Content URI for the existing product (null if it's a new Product)
     */
    private Uri mCurrentProductUri;

    private static final String STATE_URI = "STATE_URI";

    private Uri mUri;

    /** ImageView **/
    private ImageView mImageView;

    /** EditText field to enter the product's name */
    private EditText mNameEditText;

    /** EditText field to enter the product's price */
    private EditText mProductPriceEditText;

    /** EditText field to check if product in stock or not*/
    private Spinner mInStockSpinner;

    /**
    * In Stock status of the product. The possible valid values are in the ProductContract.java file:
     * {@link ProductEntry#COMING_SOON}, {@link ProductEntry#IN_STOCK_YES}, or
     * {@link ProductEntry#IN_STOCK_NO}.
     */
    private int mInStock = ProductEntry.COMING_SOON;

        /** Button to decrease the product quantity*/
        private Button mButtonDecrease;

        /** EditText field to edit product quantity*/
        private EditText mProductQuantity;

        /** Button to increase the product quantity*/
        private Button mButtonIncrease;

        /** order from Supplier **/
        private TextView mOrderFromSupplier;

        /** EditText field to edit supplier name*/
        private EditText mSupplierName;

        /** EditText field to edit supplier phone number*/
        private EditText mSupplierPhoneNumber;

        /** EditText field to edit supplier Email*/
        private EditText mSupplierEmail;

        /** ImageButton field to contact supplier by phone*/
        private ImageButton mContactPhone;

        /** ImageButton field to contact supplier by Email*/
        private ImageButton mContactEmail;

        /**Delete Button to delete product **/
        private Button mDeleteButton;


    /**
     * Boolean flag that keeps track of whether the product has been edited (true) or not (false)
     */
    private boolean mProductChanged = false;


    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mProductChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.editor_activity);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product.
        if (mCurrentProductUri == null) {
            // This is a new product, so change the app bar to say "Add Product"
            setTitle(getString(R.string.editor_activity_title_new_product));


            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu();

            mOrderFromSupplier.setVisibility(View.GONE);
            mContactPhone.setVisibility(View.GONE);
            mContactEmail.setVisibility(View.GONE);
            mDeleteButton.setVisibility((View.GONE));

        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle(getString(R.string.editor_activity_title_edit_product));


            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mImageView = (ImageView) findViewById(R.id.edit_product_image);
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mProductPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mInStockSpinner = (Spinner) findViewById(R.id.spinner_in_stock);
        mButtonDecrease = (Button) findViewById(R.id.button_minus);
        mProductQuantity = (EditText) findViewById(R.id.edit_product_quantity);
        mButtonIncrease = (Button) findViewById(R.id.button_plus);
        mOrderFromSupplier = (TextView) findViewById(R.id.order_from_supplier);
        mSupplierName = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumber = (EditText) findViewById(R.id.edit_supplier_phone);
        mSupplierEmail = (EditText) findViewById(R.id.edit_supplier_email);
        mContactPhone = (ImageButton) findViewById(R.id.button_telephone);
        mContactEmail = (ImageButton) findViewById(R.id.button_email);
        mDeleteButton = (Button) findViewById(R.id.button_delete);


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mImageView.setOnTouchListener(mTouchListener);
        mButtonDecrease.setOnTouchListener(mTouchListener);
        mButtonIncrease.setOnTouchListener(mTouchListener);
        mContactPhone.setOnTouchListener(mTouchListener);
        mContactEmail.setOnTouchListener(mTouchListener);
        mNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mInStockSpinner.setOnTouchListener(mTouchListener);
        mProductQuantity.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumber.setOnTouchListener(mTouchListener);
        mSupplierEmail.setOnTouchListener(mTouchListener);
        mDeleteButton.setOnTouchListener(mTouchListener);
        setupSpinner();



        // Set a clickListener on minus button
        mButtonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mProductQuantity.getText().toString())) {
                    int quantity = parseInt(mProductQuantity.getText().toString().trim());
                    if (quantity == 0) {
                        quantity = 0;
                        Toast.makeText(view.getContext(), getString(R.string.no_negative_quantity),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        quantity--;
                    }
                    mProductQuantity.setText(Integer.toString(quantity));
                }
            }
        });

        // Set a clickListener on plus button
        mButtonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity;
                if (!TextUtils.isEmpty(mProductQuantity.getText().toString())) {
                    quantity = parseInt(mProductQuantity.getText().toString().trim());
                    quantity++;
                } else {
                    quantity = 1;
                }
                mProductQuantity.setText(Integer.toString(quantity));
            }
        });

        // Set a clickListener on telephone button
        mContactPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mSupplierPhoneNumber.getText().toString()));
                startActivity(intent);
            }
        });

        // Set a clickListener on email button
        mContactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + mSupplierEmail.getText().toString().trim())); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, "Order more " + mNameEditText.getText().toString().trim());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        // Set a clickListener on delete button
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageSelector();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mUri != null)
            outState.putString(STATE_URI, mUri.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(STATE_URI) &&
                !savedInstanceState.getString(STATE_URI).equals("")) {
            mUri = Uri.parse(savedInstanceState.getString(STATE_URI));

            ViewTreeObserver viewTreeObserver = mImageView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mImageView.setImageBitmap(getBitmapFromUri(mUri));
                }
            });
        }
    }



    /**
     * Setup the dropdown spinner that allows the user to select the In-Stock status.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter inStockSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_inStock_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        inStockSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mInStockSpinner.setAdapter(inStockSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mInStockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.product_yes))) {
                        mInStock = ProductEntry.IN_STOCK_YES; // Yes
                    } else if (selection.equals(getString(R.string.product_no))) {
                        mInStock = ProductEntry.IN_STOCK_NO; // No
                    } else {
                        mInStock = ProductEntry.COMING_SOON; // Coming soon
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mInStock = ProductEntry.COMING_SOON; // Coming soon
            }
        });
    }

    /**
     * Get user input from the editor and save new product into the database.
     */

    private boolean saveProduct(){
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String titleString = mNameEditText.getText().toString().trim();
        String priceString = mProductPriceEditText.getText().toString().trim();
        String quantityString = mProductQuantity.getText().toString().trim();
        String supplierString = mSupplierName.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneNumber.getText().toString().trim();
        String supplierEmailString = mSupplierEmail.getText().toString().trim();


        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        if (mCurrentProductUri == null && mUri == null &&
                TextUtils.isEmpty(titleString) && mInStock == ProductEntry.COMING_SOON &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierString) &&
                TextUtils.isEmpty(supplierPhoneString) && TextUtils.isEmpty(supplierEmailString)) {
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return true;
        } else if (TextUtils.isEmpty(titleString)) {
            Toast.makeText(this, R.string.no_title_error,
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(priceString) || parseInt(priceString) <= 0) {
            Toast.makeText(this, R.string.no_price_error,
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(supplierEmailString)) {
            Toast.makeText(this, R.string.no_email_error,
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mUri == null) {
            Toast.makeText(this, R.string.no_image_error,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, titleString);
        values.put(ProductEntry.COLUMN_PRODUCT_INSTOCK, mInStock);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
        // If the quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = parseInt(quantityString);
        }
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplierString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhoneString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, supplierEmailString);

        if (mUri == null) {
            mUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(R.drawable.no_image)
                    + '/' + getResources().getResourceTypeName(R.drawable.no_image) + '/' + getResources().getResourceEntryName(R.drawable.no_image));
        }

        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, mUri.toString());

        // Determine if this is a new or existing product
        // by checking if mCurrentProductUri is null or not
        if (mCurrentProductUri == null) {
            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();

            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();

            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();

            }
        }

        return true;

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }


    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
               saveProduct();
               finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the product table
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_INSTOCK,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE,
                ProductEntry.COLUMN_PRODUCT_IMAGE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int inStockColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_INSTOCK);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int supplierEmailColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int inStock = cursor.getInt(inStockColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String supplierTelephone = cursor.getString(supplierPhoneColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);
            String image = cursor.getString(imageColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(title);
            mProductPriceEditText.setText(price);
            mProductQuantity.setText(quantity);
            // Type is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Novel, 2 is Technical).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (inStock) {
                case ProductEntry.IN_STOCK_YES:
                    mInStockSpinner.setSelection(1);
                    break;
                case ProductEntry.IN_STOCK_NO:
                    mInStockSpinner.setSelection(2);
                    break;
                default:
                    mInStockSpinner.setSelection(0);
                    break;
            }
            mProductPriceEditText.setText(Integer.toString(price));
            mProductQuantity.setText(Integer.toString(quantity));
            mSupplierName.setText(supplier);
            mSupplierPhoneNumber.setText(supplierTelephone);
            mSupplierEmail.setText(supplierEmail);
            mUri = Uri.parse(image);
            mImageView.setImageURI(mUri);

            if (TextUtils.isEmpty(mSupplierPhoneNumber.getText()))
                mContactPhone.setVisibility(View.GONE);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mProductPriceEditText.setText("");
        mProductQuantity.setText("");
        mInStockSpinner.setSelection(0); // Select "Unknown" type
        mUri = null;
        mSupplierName.setText("");
        mSupplierPhoneNumber.setText("");
        mSupplierPhoneNumber.setText("");
    }


    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    /**
     * Prompt the user to confirm that they want to delete this product
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing book.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    public void openImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                mUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mUri.toString());

                //mImageView.setText(mUri.toString());
                mImageView.setImageBitmap(getBitmapFromUri(mUri));
            }
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

}
