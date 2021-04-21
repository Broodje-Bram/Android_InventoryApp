package com.example.inventoryapp;


import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.inventoryapp.data.StockContract.StockEntry;

import java.io.ByteArrayOutputStream;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int STOCK_LOADER = 0;

    StockCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        ListView stockListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        stockListView.setEmptyView(emptyView);

        mCursorAdapter = new StockCursorAdapter(this, null);
        stockListView.setAdapter(mCursorAdapter);

        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentStockUri = ContentUris.withAppendedId(StockEntry.CONTENT_URI, id);
                intent.setData(currentStockUri);
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(STOCK_LOADER, null,  this);
    }

    private void insertStock() {
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_STONK_NAME, "Camera");
        values.put(StockEntry.COLUMN_STONK_SUPPLIER, "Gerrit");
        values.put(StockEntry.COLUMN_STONK_PRICE, 50);
        values.put(StockEntry.COLUMN_STONK_WEIGHT, 7);
        values.put(StockEntry.COLUMN_STONK_QUANTITY, 69);
        Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);
    }

    private void deleteAllStock() {
        int rowsDeleted = getContentResolver().delete(StockEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    private void insertIntoDatabase(String name,
                                    String supplier,
                                    byte[] photo,
                                    int price,
                                    int quantity,
                                    int weight) {
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_STONK_NAME, name);
        values.put(StockEntry.COLUMN_STONK_SUPPLIER, supplier);
        values.put(StockEntry.COLUMN_STONK_PRICE, price);
        values.put(StockEntry.COLUMN_STONK_QUANTITY, quantity);
        values.put(StockEntry.COLUMN_STONK_WEIGHT, weight);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertStock();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllStock();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                StockEntry._ID,
                StockEntry.COLUMN_STONK_NAME,
                StockEntry.COLUMN_STONK_PRICE,
                StockEntry.COLUMN_STONK_SUPPLIER,
                StockEntry.COLUMN_STONK_QUANTITY,
                StockEntry.COLUMN_STONK_WEIGHT};
        return new CursorLoader(this,
                StockEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mCursorAdapter.swapCursor(null);
    }
}