package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract;
import com.example.android.inventory.data.ProductContract.ProductEntry;
import com.example.android.inventory.data.ProductCursorAdapter;
import com.example.android.inventory.data.ProductDBHelper;

import java.io.ByteArrayOutputStream;

;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private ProductDBHelper dbhelper;
    private ProductCursorAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private ListView listView;
    private Menu menu;
    private boolean isDeleteAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty);

        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                startActivity(intent);
            }
        });

        dbhelper = new ProductDBHelper(this);

        listView = (ListView) findViewById(R.id.list_view);

        mAdapter = new ProductCursorAdapter(this, null);

        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Uri currentProduct = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                intent.setData(currentProduct);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            deleteAll();
        } else if (item.getItemId() == R.id.dummy_data) {
            insertDummyData();
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.delete_all);
        if (isDeleteAll == false) {
            menuItem.setVisible(false);
        } else {
            menuItem.setVisible(true);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductEntry._ID,
                ProductContract.ProductEntry.COLOUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLOUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLOUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLOUMN_PRODUCT_PIC
        };

        return new CursorLoader(this, ProductEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            listView.setEmptyView(mEmptyStateTextView);
            mEmptyStateTextView.setText(R.string.empty_des);
            isDeleteAll=false;
        } else {
            mEmptyStateTextView.setVisibility(View.INVISIBLE);
            isDeleteAll=true;
       }
        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }


    private void insertDummyData() {
        Drawable myDrawable = getResources().getDrawable(R.drawable.ic_note_add);
        Bitmap prodPic = ((BitmapDrawable) myDrawable).getBitmap();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        prodPic.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        byte[] data = outputStream.toByteArray();

        ContentValues values = new ContentValues();

        values.put(ProductContract.ProductEntry.COLOUMN_PRODUCT_NAME, "test");
        values.put(ProductContract.ProductEntry.COLOUMN_PRODUCT_PIC, data);
        values.put(ProductContract.ProductEntry.COLOUMN_PRODUCT_QUANTITY, 12);
        values.put(ProductContract.ProductEntry.COLOUMN_PRODUCT_PRICE, 12.20);

        Uri uri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);


        if (uri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, " Row not inserted",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, " Row inserted",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAll() {

        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle("Delete All")
                .setMessage("Do you want to delete all products?")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
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
        } else {
            Toast.makeText(this, "Row is not deleted", Toast.LENGTH_SHORT).show();
        }
    }
}
