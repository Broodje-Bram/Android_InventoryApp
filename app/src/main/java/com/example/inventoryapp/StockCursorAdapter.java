package com.example.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventoryapp.data.StockContract;

public class StockCursorAdapter extends CursorAdapter {

    public StockCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // find individual views
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView supplierTextView = (TextView) view.findViewById(R.id.supplier);
        TextView weightTextView = (TextView) view.findViewById(R.id.weight);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_text_view);

        int nameColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STONK_NAME);
        int supplierColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STONK_SUPPLIER);
        int priceColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STONK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STONK_QUANTITY);
        int weightColumnIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_STONK_WEIGHT);

        // Extract properties from cursor
        String stockName = cursor.getString(nameColumnIndex);
        String stockSupplier = cursor.getString(supplierColumnIndex);
        int stockPrice = cursor.getInt(priceColumnIndex);
        int stockQuantity = cursor.getInt(quantityColumnIndex);
        int stockWeight = cursor.getInt(weightColumnIndex);

        if (TextUtils.isEmpty(stockSupplier)) {
            stockSupplier = context.getString(R.string.supplier_unknown);
        }
        // Populate fields with extracted properties
        nameTextView.setText(stockName);
        priceTextView.setText("€" + Integer.toString(stockPrice));
        weightTextView.setText( Integer.toString(stockWeight) + " kg");
        supplierTextView.setText(String.valueOf(stockSupplier));
    }
}
