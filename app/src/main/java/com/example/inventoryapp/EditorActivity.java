package com.example.inventoryapp;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.inventoryapp.data.StockContract.StockEntry;

import java.io.ByteArrayOutputStream;


/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_STOCK_LOADER = 0;
    private static final int IMAGE_REQUEST_CODE = 1;

    private Uri mCurrentStockUri;
    private boolean mHasImage;
    private Bitmap mBitmap;

    // image view
    private ImageView mImageView;

    // Edit Texts
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mWeightEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEditText;



    private boolean mStockHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mStockHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentStockUri = intent.getData();

        if (mCurrentStockUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_stock));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_stock));
            getLoaderManager().initLoader(EXISTING_STOCK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_stock_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_stonk_price);
        mWeightEditText = (EditText) findViewById(R.id.edit_stock_weight);
        mQuantityEditText = (EditText) findViewById(R.id.edit_stonk_quantity);
        mSupplierEditText = (EditText) findViewById(R.id.edit_stock_supplier);
        mImageView = (ImageView) findViewById(R.id.edit_stock_image);
        Button imagineButton = (Button) findViewById(R.id.editor_add_image_button);
        Button minButton = (Button) findViewById(R.id.minus_button);
        Button plusButton = (Button) findViewById(R.id.plus_button);
        mHasImage = false;
        mBitmap = null;

        mImageView.setOnTouchListener(mTouchListener);
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);

        imagineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(pictureIntent, IMAGE_REQUEST_CODE);
                }
            }
        });

        minButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                if (quantity == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_negative_quantity), Toast.LENGTH_SHORT).show();
                } else {
                    quantity--;
                    mQuantityEditText.setText(Integer.toString(quantity));
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                quantity++;
                mQuantityEditText.setText(Integer.toString(quantity));
            }
        });
    }

    private void saveStock() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();

        if (mCurrentStockUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(weightString) && TextUtils.isEmpty(quantityString)) {
            return;
        }

        ContentValues values = new ContentValues();
        int weight = 0;

        if (mHasImage) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] imageByte = byteArrayOutputStream.toByteArray();
            values.put(StockEntry.COLUMN_STONK_PHOTO, imageByte);
        } else {
            Toast.makeText(this, getString(R.string.photo_required), Toast.LENGTH_SHORT).show();
        }

        if (!TextUtils.isEmpty(weightString)) {
            weight = Integer.parseInt(weightString);
        }

        values.put(StockEntry.COLUMN_STONK_NAME, nameString);
        values.put(StockEntry.COLUMN_STONK_SUPPLIER, supplierString);
        values.put(StockEntry.COLUMN_STONK_QUANTITY, Integer.parseInt(quantityString));
        values.put(StockEntry.COLUMN_STONK_WEIGHT, weight);
        values.put(StockEntry.COLUMN_STONK_PRICE, Integer.parseInt(priceString));
        if (mCurrentStockUri == null) {
            Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_stock_not_successfull), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_stock_successfull), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentStockUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_insert_stock_not_successfull), Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_stock_successfull), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentStockUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveStock();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mStockHasChanged) {
                    // Navigate back to parent activity (CatalogActivity)
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mStockHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        if (mCurrentStockUri == null) {
            return null;
        }

        String[] projection = {
                StockEntry._ID,
                StockEntry.COLUMN_STONK_NAME,
                StockEntry.COLUMN_STONK_PHOTO,
                StockEntry.COLUMN_STONK_PRICE,
                StockEntry.COLUMN_STONK_QUANTITY,
                StockEntry.COLUMN_STONK_SUPPLIER,
                StockEntry.COLUMN_STONK_WEIGHT};
        return new CursorLoader(this, mCurrentStockUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STONK_NAME);
            int photoColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STONK_PHOTO);
            int priceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STONK_PRICE);
            int quantityIndex = cursor.getColumnIndex(StockEntry.COLUMN_STONK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STONK_SUPPLIER);
            int weightColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STONK_WEIGHT);

            byte[] image = cursor.getBlob(photoColumnIndex);
            if (image != null) {
                mHasImage = true;
                mBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            }

            String name = cursor.getString(nameColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityIndex);
            int weight = cursor.getInt(weightColumnIndex);

            mNameEditText.setText(name);
            mSupplierEditText.setText(supplier);
            mImageView.setImageBitmap(mBitmap);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mWeightEditText.setText(Integer.toString(weight));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mWeightEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
    }

    public void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.unsaved_changes_dialog_msg));
        builder.setPositiveButton(getString(R.string.discard), discardButtonListener);
        builder.setNegativeButton(getString(R.string.keep_editing), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteStock();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteStock() {
        getLoaderManager().destroyLoader(0);
        if (mCurrentStockUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentStockUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_stock_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_stock_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            mBitmap = (Bitmap) extras.get("data");
            mHasImage = true;
            mImageView.setImageBitmap(mBitmap);
        }
    }
}
