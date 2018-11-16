package com.example.sourav.bookstore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sourav.bookstore.data.BookContract.BookEntry;


public class AddActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText mBookName;

    private EditText mBookPrice;

    private EditText mBookCopies;

    private EditText mSupplierName;

    private EditText mSupplierContact;

    private Uri mCurrentUri;

    private static final int BOOK_LOADER_ID = 0;

    private boolean mHasFieldsChanged = false;

    private static final String LOG_TAG = AddActivity.class.getSimpleName();

	private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mHasFieldsChanged = true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        Log.v(LOG_TAG, "START OF mCurrentUri");
        if (mCurrentUri == null) {
            setTitle(getString(R.string.add_book));
            invalidateOptionsMenu();
        }else {
            setTitle(getString(R.string.edit_book));
        }
        Log.v(LOG_TAG, "End of mCurrentUri");

        mBookName = findViewById(R.id.edit_book_name);
        mBookPrice = findViewById(R.id.edit_book_price);
        mBookCopies = findViewById(R.id.edit_book_copies);
        mSupplierName = findViewById(R.id.edit_book_supplier_name);
        mSupplierContact = findViewById(R.id.edit_book_supplier_no);

        //set up on touch listener to every edit text fields
        mBookName.setOnTouchListener(mOnTouchListener);
        mBookPrice.setOnTouchListener(mOnTouchListener);
        mBookCopies.setOnTouchListener(mOnTouchListener);
        mSupplierName.setOnTouchListener(mOnTouchListener);
        mSupplierContact.setOnTouchListener(mOnTouchListener);

        getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
        Log.v(LOG_TAG, "Loader is initialized");
    }

    //method to insert new book to the database
    private void saveBooks(){

        //read from the edit text fields
        String bookName = mBookName.getText().toString().trim();
        String bookPriceString = mBookPrice.getText().toString().trim();
        String bookCopiesString = mBookCopies.getText().toString().trim();
        String supplierName = mSupplierName.getText().toString().trim();
        String supplierContact = mSupplierContact.getText().toString().trim();
        int bookPrice = Integer.parseInt(bookPriceString);
        int bookCopies = Integer.parseInt(bookCopiesString);

        //check whether this is a new book
        //and also check whether all fields are empty
        if (mCurrentUri == null && TextUtils.isEmpty(bookName) && TextUtils.isEmpty(bookCopiesString)
                && TextUtils.isEmpty(bookCopiesString) && TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierContact)){
            return;
        }

        //create a content values object where column names are keys
        //and text from edit text fields are the values
        ContentValues values = new ContentValues();
        values.put(BookEntry.BOOK_NAME, bookName);
        values.put(BookEntry.BOOK_COPIES, bookCopies);
        values.put(BookEntry.BOOK_PRICE, bookPrice);
        values.put(BookEntry.BOOK_SUPPLIER, supplierName);
        values.put(BookEntry.BOOK_SUPPLIER_CONTACT, supplierContact);

        //check if this is new book then insert in to the database
        //if this a old book then update that particular row
        if (mCurrentUri == null) {

            //insert the new book to the database using books provider
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.new_book_insertion_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.new_book_insertion_successful), Toast.LENGTH_SHORT).show();
            }
        }else {

            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

            if (rowsAffected == 0){
                Toast.makeText(this, getString(R.string.new_book_insertion_failed), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, getString(R.string.new_book_insertion_successful), Toast.LENGTH_SHORT).show();
            }
        }

    }

    //deletes a book
    private void deleteBook(){
        //only perform delete when there are books in the database
        if (mCurrentUri != null){
            //call content resolver delete method to delete
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);

            if (rowsDeleted == 0){
                Toast.makeText(this, getString(R.string.delete_book_failed), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, getString(R.string.delete_book_successful), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, getString(R.string.empty_store), Toast.LENGTH_SHORT).show();
        }
        finish();
    }
    //set up the menu for the adding and editing activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        return true;
    }

    //method to perform when menu options are selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get the user selected item using menu item's
        //id, switch over the id to perform the particular action
        switch (item.getItemId()){
            //respond to the click on done icon
            case R.id.action_save:
                saveBooks();
                finish();
                return true;
            //respond to the click on delete option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            //respond to the click on Up arrow
            case android.R.id.home:
                //perform back to parent activity (MainActivity)
                if (!mHasFieldsChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(AddActivity.this);
                    }
                };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //delete the book
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });

        //create and show the alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!mHasFieldsChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_message);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(LOG_TAG, "Calling on create loader");
        String[] projection = {
                BookEntry._ID,
                BookEntry.BOOK_NAME,
                BookEntry.BOOK_COPIES,
                BookEntry.BOOK_PRICE,
                BookEntry.BOOK_SUPPLIER,
                BookEntry.BOOK_SUPPLIER_CONTACT
        };

        return new CursorLoader(this, mCurrentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1){
            return;
        }

        if (cursor.moveToFirst()){
            //get the column index
            int bookNameIndex = cursor.getColumnIndex(BookEntry.BOOK_NAME);
            int bookCopiesIndex = cursor.getColumnIndex(BookEntry.BOOK_COPIES);
            int bookPriceIndex = cursor.getColumnIndex(BookEntry.BOOK_PRICE);
            int supplierIndex = cursor.getColumnIndex(BookEntry.BOOK_SUPPLIER);
            int contactIndex = cursor.getColumnIndex(BookEntry.BOOK_SUPPLIER_CONTACT);

            //get the values using the indexes
            String bookName = cursor.getString(bookNameIndex);
            int bookCopies = cursor.getInt(bookCopiesIndex);
            int bookPrice = cursor.getInt(bookPriceIndex);
            String supplier = cursor.getString(supplierIndex);
            int contactNo = cursor.getInt(contactIndex);

            //set the values to edit text fields
            mBookName.setText(bookName);
            mBookCopies.setText(Integer.toString(bookCopies));
            mBookPrice.setText(Integer.toString(bookPrice));
            mSupplierName.setText(supplier);
            mSupplierContact.setText(Integer.toString(contactNo));
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mBookName.setText("");
        mBookCopies.setText(Integer.toString(0));
        mBookPrice.setText(Integer.toString(0));
        mSupplierName.setText("");
    }
}
