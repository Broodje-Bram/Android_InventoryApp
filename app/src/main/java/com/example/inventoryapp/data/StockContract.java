package com.example.inventoryapp.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class StockContract {

    private StockContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_STOCKS = "inventoryapp";

    public static final class StockEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STOCKS);

        public final static String TABLE_NAME = "stonks";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_STONK_NAME = "name";
        public final static String COLUMN_STONK_PHOTO = "photo";
        public final static String COLUMN_STONK_SUPPLIER = "supplier";
        public final static String COLUMN_STONK_QUANTITY = "quantity";
        public final static String COLUMN_STONK_WEIGHT = "weight";
        public final static String COLUMN_STONK_PRICE = "price";


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCKS;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCKS;
    }
}

