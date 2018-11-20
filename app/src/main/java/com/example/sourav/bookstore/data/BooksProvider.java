package com.example.sourav.bookstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.IntentFilter;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.example.sourav.bookstore.data.BookContract.BookEntry;

public class BooksProvider extends ContentProvider {

    private static final String LOG_TAG = BooksProvider.class.getSimpleName();

    private BookDbHelper mBookDbHelper;

    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;

    //set up a uri matcher to check uris
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //add the uri to check
    static {
        //generic uri or uri for the table
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        //uri for single row with # as wild card
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
    }
    @Override
    public boolean onCreate() {
        mBookDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //get a readable database using mBookDbHelper
        SQLiteDatabase db = mBookDbHelper.getReadableDatabase();

        //declare the cursor object to store the returned value
        Cursor cursor;

        //check the uri match
        final int match = sUriMatcher.match(uri);

        //perform query depending upon the uri
        switch (match){
            //uri for the entire table
            case BOOKS:
                cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            //in case of books id set the id as selection
            //get the selectionArgs and query accordingly
            case BOOKS_ID:
                selection = BookEntry._ID +"=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            //default case throw an Illegal argument exception
            default:
                throw new IllegalArgumentException("Can not query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            case BOOKS:
                //uri for the entire table
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                //uri for a particular row id
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException(" Unknown URI " + uri + "with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);
        switch (match){
            //case match is BOOKS call helper method insertBook
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {

        //get a writable database to write to
        SQLiteDatabase db = mBookDbHelper.getWritableDatabase();

        //check whether the inserted value is valid or not
        String bookName = values.getAsString(BookEntry.BOOK_NAME);
        if (bookName == null){
            throw new IllegalArgumentException("Book requires a title.");
        }

        Integer bookPrice = values.getAsInteger(BookEntry.BOOK_PRICE);
        if (bookPrice == null || bookPrice < 0){
            throw new IllegalArgumentException("Book requires a valid price.");
        }

        Integer bookCopies = values.getAsInteger(BookEntry.BOOK_COPIES);
        if (bookCopies == null || bookCopies < 0){
            throw new IllegalArgumentException("Book copies should be positive no.");
        }

        String supplierName = values.getAsString(BookEntry.BOOK_SUPPLIER);
        if (supplierName == null){
            throw new IllegalArgumentException("Supplier should have a name.");
        }

        String supplierContact = values.getAsString(BookEntry.BOOK_SUPPLIER_CONTACT);
        if (supplierContact == null){
            throw new IllegalArgumentException("Supplier should have a valid phone no.");
        }

        //insert the new book to the table with the given value object
        long id = db.insert(BookEntry.TABLE_NAME, null, values);

        //check for correct insertion
        if (id == -1){
            Log.e(LOG_TAG, "failed to insert the book for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        //get a writable database
        SQLiteDatabase db = mBookDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        int rowsDeleted;

        switch (match){
            case BOOKS:
                //drop all the rows that matches the selection and selectionArgs
                rowsDeleted = db.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:
                //get the id of the row to be deleted
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }

        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            //update the entire table
            case BOOKS:
                updateBooks(uri, values, selection, selectionArgs);
            case BOOKS_ID:
                //get the row id to which the update should be performed
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updateBooks(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBooks(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //get a writable database
        SQLiteDatabase db = mBookDbHelper.getWritableDatabase();

        //check that updated values are valid or not
        //get the column name for which new value is inserted
        if (values.containsKey(BookEntry.BOOK_NAME)){
            String bookName = values.getAsString(BookEntry.BOOK_NAME);
            if (bookName == null){
                throw new IllegalArgumentException("Book requires a title.");
            }

        }

        if (values.containsKey(BookEntry.BOOK_PRICE)){
            Integer bookPrice = values.getAsInteger(BookEntry.BOOK_PRICE);
            if (bookPrice == null || bookPrice < 0){
                throw new IllegalArgumentException("Book requires a valid price.");
            }
        }

        if (values.containsKey(BookEntry.BOOK_COPIES)){
            Integer bookCopies = values.getAsInteger(BookEntry.BOOK_COPIES);
            if (bookCopies == null || bookCopies < 0){
                throw new IllegalArgumentException("Book copies should be positive no.");
            }
        }

        if (values.containsKey(BookEntry.BOOK_SUPPLIER)){
            String supplierName = values.getAsString(BookEntry.BOOK_SUPPLIER);
            if (supplierName == null){
                throw new IllegalArgumentException("Supplier should have a name.");
            }
        }

        if (values.containsKey(BookEntry.BOOK_SUPPLIER_CONTACT)){
            String supplierContact = values.getAsString(BookEntry.BOOK_SUPPLIER_CONTACT);
            if (supplierContact == null){
                throw new IllegalArgumentException("Supplier should have a valid phone no.");
            }
        }

        //if the values object is null then no need to update
        if (values.size() == 0){
            return 0;
        }

        int rowUpdated =  db.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowUpdated;
    }
}
