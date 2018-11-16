package com.example.sourav.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.sourav.bookstore.data.BookContract.BookEntry;
import com.example.sourav.bookstore.data.BookDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_ID = 0;
    BookCursorAdapter mBookCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up fab to open add activity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        ListView booksListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_list);

        booksListView.setEmptyView(emptyView);

        mBookCursorAdapter = new BookCursorAdapter(this, null);

        booksListView.setAdapter(mBookCursorAdapter);

        //set up a click listener when a user clicks on a book
        //it leads to edit activity
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);

                //get the uri of the clicked item
                Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);

    }

    //dummy method
    //insert method to insert data into table
    private void insertData(){

        //SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //create a content value object to pass
        //pass to the insert method of the database
        ContentValues values = new ContentValues();

        //put values to content objects
        values.put(BookEntry.BOOK_NAME, "Introduction to Algorithms");
        values.put(BookEntry.BOOK_PRICE, 400);
        values.put(BookEntry.BOOK_COPIES, 50);
        values.put(BookEntry.BOOK_SUPPLIER, "xyz_company");
        values.put(BookEntry.BOOK_SUPPLIER_CONTACT, "xyz@companydomain.com");

        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        Log.v(LOG_TAG, "the newly inserted book uri " + newUri);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                BookEntry._ID,
                BookEntry.BOOK_NAME,
                BookEntry.BOOK_COPIES,
                BookEntry.BOOK_PRICE,
                BookEntry.BOOK_SUPPLIER,
                BookEntry.BOOK_SUPPLIER_CONTACT
        };

        return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBookCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookCursorAdapter.swapCursor(null);
    }
}
