package com.example.android.inventory.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by lkatta on 3/20/17.
 */

public class ProductContract {

    private ProductContract() {

    }

    public static final String CONTENT_AUTHORITY = "com.example.android.products";
    public static final String PATH_PRODUCTS = "products";
    public static final String PRE_SCHEME = "content://";

    public static final Uri BASE_CONTENT_URI = Uri.parse(PRE_SCHEME + CONTENT_AUTHORITY);

    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String _ID = BaseColumns._ID;
        public static final String TABLE_NAME = "products";
        public static final String COLOUMN_PRODUCT_NAME = "name";
        public static final String COLOUMN_PRODUCT_PIC = "pic";
        public static final String COLOUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLOUMN_PRODUCT_PRICE = "price";

    }
}
