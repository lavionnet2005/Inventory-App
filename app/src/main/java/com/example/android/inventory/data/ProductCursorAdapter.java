package com.example.android.inventory.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.R;

/**
 * Created by lkatta on 3/20/17.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.product_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView name = (TextView) view.findViewById(R.id.name_view);
        TextView quantity = (TextView) view.findViewById(R.id.quantity_view);
        TextView price = (TextView) view.findViewById(R.id.price_view);
        ImageView image = (ImageView) view.findViewById(R.id.image_view);
        final Button saleButton = (Button) view.findViewById(R.id.sale_button);

        final Integer row_id = cursor.getInt(
                cursor.getColumnIndex(ProductContract.ProductEntry._ID));
        String prodName = cursor.getString(
                cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLOUMN_PRODUCT_NAME));
        final Integer prodQuantity = cursor.getInt(
                cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLOUMN_PRODUCT_QUANTITY));
        Float prodPrice = cursor.getFloat(
                cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLOUMN_PRODUCT_PRICE));
        byte[] imgByte = cursor.getBlob(
                cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLOUMN_PRODUCT_PIC));
        Bitmap prodpic = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);

        name.setText(prodName);
        quantity.setText(prodQuantity.toString());
        price.setText(prodPrice.toString());
        image.setImageBitmap(prodpic);

        final Context contextUpdate = context;

        saleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Uri currentProduct = ContentUris.withAppendedId(
                        ProductContract.ProductEntry.CONTENT_URI, row_id);

                if (prodQuantity > 0) {
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLOUMN_PRODUCT_QUANTITY,
                            prodQuantity - 1);
                    contextUpdate.getContentResolver().update(currentProduct,
                            values, null, null);
                } else {
                    Toast.makeText(contextUpdate, "Quatity is already 0.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
