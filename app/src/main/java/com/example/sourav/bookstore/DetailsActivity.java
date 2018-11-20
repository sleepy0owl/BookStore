package com.example.sourav.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sourav.bookstore.data.BookContract.BookEntry;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private TextView mBookName;

    private TextView mBookPrice;

    private TextView mBookCopies;

    private TextView mSupplierName;

    private TextView mContactNo;

    private Uri mCurrentUri;

    /**
     * variables for button click events
     **/
    private int BOOK_COPIES;
    private String CONTACT_NUMBER;

    private static final int BOOK_LOADER_ID = 0;

    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setTitle(getString(R.string.book_details));

        //get the intent sent from MainActivity
        Intent intent = getIntent();
        //get the uri of clicked list item
        mCurrentUri = intent.getData();

        //initialise loader manager
        getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
        //get the text views to set the values
        mBookName = findViewById(R.id.set_book_name);
        mBookPrice = findViewById(R.id.set_book_price);
        mBookCopies = findViewById(R.id.set_book_copies);
        mSupplierName = findViewById(R.id.set_supplier_name);
        mContactNo = findViewById(R.id.set_supplier_contact);

        //query the database using the mCurrentUri to get the stored contact no.
        //send that as phone number
        String[] projection = {BookEntry.BOOK_SUPPLIER_CONTACT, BookEntry.BOOK_COPIES};
        final String selection = BookEntry._ID + "=?";
        final String[] selectionArgs = {String.valueOf(ContentUris.parseId(mCurrentUri))};
        Cursor cursor = getContentResolver().query(mCurrentUri, projection, selection, selectionArgs,null);
        if (cursor.moveToFirst()) {
            int copiesIndex = cursor.getColumnIndex(BookEntry.BOOK_COPIES);
            int contactIndex = cursor.getColumnIndex(BookEntry.BOOK_SUPPLIER_CONTACT);
            CONTACT_NUMBER = cursor.getString(contactIndex);
            BOOK_COPIES = cursor.getInt(copiesIndex);
            Log.v(LOG_TAG, "book copies index is :" + copiesIndex);
            Log.v(LOG_TAG, "contact no index is : " + contactIndex);
            Log.v(LOG_TAG, "contact no is :" + CONTACT_NUMBER);
            //Log.v(LOG_TAG, "decrementing the no copies to " + decreaseCopies(BOOK_COPIES, selection, selectionArgs));
        }
        //get the value using the index
        //int bookCopies = cursor.getInt(copiesIndex);

        //final String phoneNumber = "7278415439";

        ImageButton callButton = findViewById(R.id.call_supplier);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + CONTACT_NUMBER));
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }
            }
        });

       Button incrementButton = findViewById(R.id.increase_quantity);
       incrementButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               BOOK_COPIES = increaseCopies(BOOK_COPIES, selection, selectionArgs);
           }
       });

       Button decrementButton = findViewById(R.id.decrease_quantity);
       decrementButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               BOOK_COPIES = decreaseCopies(BOOK_COPIES, selection, selectionArgs);
           }
       });

       ImageButton deleteButton = findViewById(R.id.delete_book);
       deleteButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               deleteBook();
           }
       });

       cursor.close();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.BOOK_NAME,
                BookEntry.BOOK_PRICE,
                BookEntry.BOOK_COPIES,
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
            //get column indexes
            int bookNameIndex = cursor.getColumnIndex(BookEntry.BOOK_NAME);
            int bookPriceIndex = cursor.getColumnIndex(BookEntry.BOOK_PRICE);
            int bookCopiesIndex = cursor.getColumnIndex(BookEntry.BOOK_COPIES);
            int supplierIndex = cursor.getColumnIndex(BookEntry.BOOK_SUPPLIER);
            int contactIndex = cursor.getColumnIndex(BookEntry.BOOK_SUPPLIER_CONTACT);

            //get the values using indexes
            String bookName = cursor.getString(bookNameIndex);
            int bookPrice = cursor.getInt(bookPriceIndex);
            int bookCopies = cursor.getInt(bookCopiesIndex);
            String supplierName = cursor.getString(supplierIndex);
            String contactNo = cursor.getString(contactIndex);

            //set the values to respective TextViews
            mBookName.setText(bookName);
            mBookPrice.setText(Integer.toString(bookPrice));
            mBookCopies.setText(Integer.toString(bookCopies));
            mSupplierName.setText(supplierName);
            mContactNo.setText(contactNo);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //get the current copies --> already have
    //when button click increase the copies and update to the data base
    private int increaseCopies(int currentCopies, String selection, String[] selectionArgs){
        currentCopies = currentCopies + 1;
        ContentValues values = new ContentValues();
        values.put(BookEntry.BOOK_COPIES, currentCopies);
        getContentResolver().update(mCurrentUri, values, selection, selectionArgs);
        return currentCopies;
    }

    private int decreaseCopies(int currentCopies, String selection, String[] selectionArgs){
        if (currentCopies >= 1) {
            currentCopies = currentCopies - 1;
            ContentValues values = new ContentValues();
            values.put(BookEntry.BOOK_COPIES, currentCopies);
            getContentResolver().update(mCurrentUri, values, selection, selectionArgs);
        }else {
            //current no of copies is 0
            //no more decrement operation is allowed
            Toast.makeText(this, getString(R.string.stock_zero), Toast.LENGTH_SHORT).show();
        }
        return currentCopies;
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

}
