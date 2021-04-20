package com.example.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


import com.example.inventoryapp.data.StockContract.StockEntry;

public class StockProvider extends ContentProvider {

    public static final String LOG_TAG = StockProvider.class.getSimpleName();

    // URI matcher code for the entire table
    private static final int STOCK = 100;
    // URI matcher code for a single entry
    private static final int STOCK_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_STOCKS, STOCK);
        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_STOCKS + "/#", STOCK_ID);
    }

    private StockDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new StockDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs,
                         String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                cursor = database.query(StockEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            case STOCK_ID:
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(StockEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return StockEntry.CONTENT_LIST_TYPE;
            case STOCK_ID:
                return StockEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return insertStock(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertStock(Uri uri, ContentValues values) {
        // Checks if name is null
        String name = values.getAsString(StockEntry.COLUMN_STONK_NAME);
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Stock requires a name");
        }

        Integer weight = values.getAsInteger(StockEntry.COLUMN_STONK_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("The stock Requires a Valid Weight");
        }

        Integer price = values.getAsInteger(StockEntry.COLUMN_STONK_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("The stock requires a valid Price");
        }

        Integer quantity = values.getAsInteger(StockEntry.COLUMN_STONK_QUANTITY);
        if (quantity == 0 ) {
            throw new IllegalArgumentException("Quantity needs to be atleast 1");
        }
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(StockEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to Insert Row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return updateStock(uri, contentValues, selection, selectionArgs);
            case STOCK_ID:
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateStock(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateStock(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(StockEntry.COLUMN_STONK_NAME)) {
            // Checks if name is null
            String name = values.getAsString(StockEntry.COLUMN_STONK_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Stonks requires a name");
            }
        }

        Integer price = values.getAsInteger(StockEntry.COLUMN_STONK_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("The stock requires a valid Price");
        }

        if (values.containsKey(StockEntry.COLUMN_STONK_WEIGHT)) {
            Integer weight = values.getAsInteger(StockEntry.COLUMN_STONK_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Stonks requires a valid weight");
            }
        }

        if (values.containsKey(StockEntry.COLUMN_STONK_SUPPLIER)) {
            Integer quantity = values.getAsInteger(StockEntry.COLUMN_STONK_QUANTITY);
            if (quantity == 0) {
                throw new IllegalArgumentException("Quantity needs to be atleast 1");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(StockEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
            return  rowsUpdated;
        } else {
            return database.update(StockEntry.TABLE_NAME, values, selection, selectionArgs);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STOCK_ID:
                // Delete a single row given by the ID in the URI
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }
}
