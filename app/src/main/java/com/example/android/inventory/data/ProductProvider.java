package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by lkatta on 3/20/17.
 */

public class ProductProvider extends ContentProvider {
    private ProductDBHelper dbHelper;
    private static final int PRODUCTS = 100;
    private static final int PRODUCTS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,
                ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,
                ProductContract.PATH_PRODUCTS + "/#", PRODUCTS_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new ProductDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case PRODUCTS_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("No other insertion available");
        }

    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId;

        String name = values.getAsString(ProductContract.ProductEntry.COLOUMN_PRODUCT_NAME);
        Float price = values.getAsFloat(ProductContract.ProductEntry.COLOUMN_PRODUCT_PRICE);
        Integer quantity = values.getAsInteger(ProductContract.ProductEntry.COLOUMN_PRODUCT_QUANTITY);
        byte[] pic = values.getAsByteArray(ProductContract.ProductEntry.COLOUMN_PRODUCT_PIC);

        if (name.isEmpty() || price == null || quantity == null || pic == null) {
            throw new IllegalArgumentException("Wrong product entry");
        }

        rowId = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);

        if (rowId == -1) {
            Log.e("log", "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return deleteProd(uri, selection, selectionArgs);
            case PRODUCTS_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                return deleteProd(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProd(uri, contentValues, selection, selectionArgs);
            case PRODUCTS_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                return updateProd(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int deleteProd(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(ProductContract.ProductEntry.TABLE_NAME,
                selection, selectionArgs);
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

        }
        return rowsDeleted;
    }

    private int updateProd(@NonNull Uri uri, @Nullable ContentValues contentValues,
                           @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsUpdated = -1;

        if (contentValues.containsKey(ProductContract.ProductEntry.COLOUMN_PRODUCT_PRICE)) {
            Float price = contentValues.getAsFloat(
                    ProductContract.ProductEntry.COLOUMN_PRODUCT_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("Price cannot be less than 0");
            }
        }

        if (contentValues.containsKey(ProductContract.ProductEntry.COLOUMN_PRODUCT_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(
                    ProductContract.ProductEntry.COLOUMN_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Quantity cannot be less than 0");
            }
        }

        if (contentValues.containsKey(ProductContract.ProductEntry.COLOUMN_PRODUCT_PIC)) {
            byte[] image = contentValues.getAsByteArray(
                    ProductContract.ProductEntry.COLOUMN_PRODUCT_PIC);
            if (image == null) {
                throw new IllegalArgumentException("Image cannot be empty");
            }
        }

        if (contentValues.containsKey(ProductContract.ProductEntry.COLOUMN_PRODUCT_NAME)) {
            String name = contentValues.getAsString(
                    ProductContract.ProductEntry.COLOUMN_PRODUCT_NAME);
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be null");
            }
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        rowsUpdated = db.update(ProductContract.ProductEntry.TABLE_NAME,
                contentValues, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
