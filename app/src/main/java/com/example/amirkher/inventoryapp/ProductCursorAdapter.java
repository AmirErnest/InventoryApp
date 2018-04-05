package com.example.amirkher.inventoryapp;

/**
 * Created by Amirkher on 30.03.2018.
 */

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amirkher.inventoryapp.data.ProductContract.ProductEntry;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    private static Context mContext;
    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */

    public ProductCursorAdapter(Context context, Cursor c){
        super(context, c, 0 /* flags */);
        mContext = context;

    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in product_list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
    }


    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder;
        holder = new ViewHolder();
        // Find individual views that we want to modify in the product list item layout
        holder.productImageView = (ImageView) view.findViewById(R.id.product_image);
        holder.nameTextView = (TextView) view.findViewById(R.id.title);
        holder.priceTextView = (TextView) view.findViewById(R.id.price);
        holder.quantityTextView = (TextView) view.findViewById(R.id.quantity);
        holder.saleButton = (Button) view.findViewById(R.id.button_sale);

        // Find the columns of product attributes that we're interested in
        int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);


        // Read the product attributes from the Cursor for the current product
        String productImage = cursor.getString(imageColumnIndex);
        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        final int productQuantity = cursor.getInt(quantityColumnIndex);


        // Update the Views with the attributes for the current product
        holder.productImageView.setImageURI(Uri.parse(productImage));
        holder.nameTextView.setText(productName);
        holder.priceTextView.setText("Price " + productPrice + "€");
        holder.quantityTextView.setText("Quantity " + productQuantity);
        final int productId = cursor.getInt(idColumnIndex);

        // Set a clickListener on sale button
        holder.saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, productId);
                reduceProductQuantity(view, productQuantity, currentProductUri);
            }
        });
    }

    private void reduceProductQuantity(View view, int quantity, Uri uri) {

        if (quantity > 0) {
            quantity--;

            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            mContext.getContentResolver().update(uri, values, null, null);
        }
        else {
            Toast.makeText(view.getContext(), "This product is not in stock", Toast.LENGTH_SHORT).show();
        }
    }


    public class ViewHolder {
        ImageView productImageView;
        TextView nameTextView;
        TextView priceTextView;
        TextView quantityTextView;
        Button saleButton;
    }
}
