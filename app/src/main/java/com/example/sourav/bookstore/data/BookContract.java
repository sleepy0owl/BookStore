package com.example.sourav.bookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {

    private BookContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.sourav.bookstore";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS= "books";

    //book entry class defines the schema and constants for the data-base
    public static final class BookEntry implements BaseColumns{

        //defining the schema
        public static final String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;
        public static final String BOOK_NAME = "product_name";
        public static final String BOOK_PRICE = "price";
        public static final String BOOK_COPIES = "quantity";
        public static final String BOOK_SUPPLIER = "supplier_name";
        public static final String BOOK_SUPPLIER_CONTACT = "supplier_contact";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_BOOKS;
    }
}
