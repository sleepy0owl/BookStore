package com.example.sourav.bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDbHelper extends SQLiteOpenHelper {

    //setting database name
    private static final String DATABASE_NAME = "books.db";

    //setting database version
    private static final int DATABASE_VERSION = 1;

    //public constructor
    public BookDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //creates the database
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME
                + " ("
                + BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookContract.BookEntry.BOOK_NAME + " TEXT NOT NULL, "
                + BookContract.BookEntry.BOOK_PRICE + " INTEGER NOT NULL, "
                + BookContract.BookEntry.BOOK_COPIES + " INTEGER DEFAULT 0, "
                + BookContract.BookEntry.BOOK_SUPPLIER + " TEXT NOT NULL, "
                + BookContract.BookEntry.BOOK_SUPPLIER_CONTACT + " TEXT NOT NULL);";
        //creates the database
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
