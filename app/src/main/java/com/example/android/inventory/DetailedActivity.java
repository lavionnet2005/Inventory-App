package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.example.android.inventory.data.ProductContract;
import com.example.android.inventory.data.ProductDBHelper;

import java.io.ByteArrayOutputStream;

/**
 * Created by lkatta on 3/20/17.
 */

public class DetailedActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProductDBHelper dbHelper;
    private Button save;
    private TextView name;
    private TextView quantity;
    private TextView price;
    private ImageButton image;
    private Uri currentProdUri;
    private static final int EXISTING_PRODUCT_LOADER = 1;
    private ImageButton upButton;
    private ImageButton downButton;
    private Button orderMore;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_activity);
        currentProdUri = getIntent().getData();
        save = (Button) findViewById(R.id.save);
        name = (TextView) findViewById(R.id.edit_prod_name);
        quantity = (TextView) findViewById(R.id.edit_prod_quantity);
        price = (TextView) findViewById(R.id.edit_prod_price);
        image = (ImageButton) findViewById(R.id.edit_prod_image);
        dbHelper = new ProductDBHelper(this);
        upButton = (ImageButton) findViewById(R.id.up_button);
        downButton = (ImageButton) findViewById(R.id.down_button);
        orderMore = (Button) findViewById(R.id.order_more);

        if (currentProdUri == null) {
            setTitle(getString(R.string.add));
            orderMore.setVisibility(View.INVISIBLE);
            invalidateOptionsMenu();

        } else {
            setTitle(getString(R.string.edit));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer noOfItems = Integer.parseInt(quantity.getText().toString().trim());
                quantity.setText((++noOfItems).toString());
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer noOfItems = Integer.parseInt(quantity.getText().toString().trim());
                if (noOfItems > 0) {
                    quantity.setText((--noOfItems).toString());
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePet();

            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentProdUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            deleteOption();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageButton imgView = (ImageButton) findViewById(R.id.edit_prod_image);
                // Set the Image in ImageView after decoding the String
                Bitmap image = BitmapFactory
                        .decodeFile(imgDecodableString);
                imgView.setImageBitmap(image);

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void savePet() {
        String prodName = "";
        int prodQuantity = 0;
        Float prodPrice = null;

        if (!TextUtils.isEmpty(name.getText().toString())) {
            prodName = name.getText().toString().trim();
        } else {
            Toast.makeText(this, "Please enter name of the product.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        prodQuantity = Integer.parseInt(quantity.getText().toString().trim());

        if (prodQuantity == 0) {
            Toast.makeText(this, "Please enter quantity of the product.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!price.getText().toString().isEmpty()) {
            prodPrice = Float.parseFloat(price.getText().toString().trim());
        } else {
            Toast.makeText(this, "Please enter price of the product.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap prodPic = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        prodPic.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        byte[] data = outputStream.toByteArray();

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLOUMN_PRODUCT_NAME, prodName);
        values.put(ProductContract.ProductEntry.COLOUMN_PRODUCT_PIC, data);
        values.put(ProductContract.ProductEntry.COLOUMN_PRODUCT_QUANTITY, prodQuantity);
        values.put(ProductContract.ProductEntry.COLOUMN_PRODUCT_PRICE, prodPrice);

        if (currentProdUri == null) {
            Uri uri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
            if (uri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, " Row not inserted",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, " Row inserted",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentProdUri, values, null, null);
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Row not updated",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Row updated",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLOUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLOUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLOUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLOUMN_PRODUCT_PIC
        };

        return new CursorLoader(this, currentProdUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            String prodName = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLOUMN_PRODUCT_NAME));
            Integer prodQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLOUMN_PRODUCT_QUANTITY));
            Float prodPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLOUMN_PRODUCT_PRICE));
            byte[] imgByte = cursor.getBlob(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLOUMN_PRODUCT_PIC));
            Bitmap prodpic = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);

            name.setText(prodName);
            quantity.setText(prodQuantity.toString());
            price.setText(prodPrice.toString());
            image.setImageBitmap(prodpic);
        }

        orderMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String subject = "Order Form :" + "\n" + "Product: " +
                        name.getText().toString().toUpperCase() +
                        " Quantity: " + quantity.getText().toString().toUpperCase();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra("sms_body", subject);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Order Form");
                intent.putExtra(Intent.EXTRA_TEXT, subject);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        name.setText("");
        quantity.setText("");
        price.setText("");
        image.setImageResource(R.drawable.ic_note_add);
    }

    private void deleteOption() {

        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Do you want to delete?")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        int rowsDeleted = getContentResolver().delete(currentProdUri, null, null);
                        toast(rowsDeleted);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        myQuittingDialogBox.show();
    }

    private void toast(int rows) {
        if (rows != 0) {
            Toast.makeText(this, " Row is deleted.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Row is not deleted", Toast.LENGTH_SHORT).show();
        }
    }
}
