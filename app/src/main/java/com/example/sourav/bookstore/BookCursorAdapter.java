package com.example.sourav.bookstore;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.sourav.bookstore.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);

    }

    //creates new list item with defined layout
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
    }

    //binds content of each row of the cursor object to each list item
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //get the text views
        TextView nameTextView = view.findViewById(R.id.list_book_name);
        TextView priceTextView = view.findViewById(R.id.list_book_price);
        TextView copiesTextView = view.findViewById(R.id.list_book_copies);
        TextView supplierTextView = view.findViewById(R.id.list_book_supplier);
        TextView contactTextView = view.findViewById(R.id.list_book_supplier_no);

        //get the column indexes
        int nameIndex = cursor.getColumnIndex(BookEntry.BOOK_NAME);
        int priceIndex = cursor.getColumnIndex(BookEntry.BOOK_PRICE);
        int copiesIndex = cursor.getColumnIndex(BookEntry.BOOK_COPIES);
        int supplierIndex = cursor.getColumnIndex(BookEntry.BOOK_SUPPLIER);
        int contactIndex = cursor.getColumnIndex(BookEntry.BOOK_SUPPLIER_CONTACT);

        //use the data stored in respective column
        String bookName = cursor.getString(nameIndex);
        int bookPriceInt = cursor.getInt(priceIndex);
        String bookPrice = Integer.toString(bookPriceInt);
        int copiesInt = cursor.getInt(copiesIndex);
        String bookCopies = Integer.toString(copiesInt);
        String supplierName = cursor.getString(supplierIndex);
        String contactNo = cursor.getString(contactIndex);

        //set the values to textViews
        nameTextView.setText(bookName);
        priceTextView.setText(bookPrice);
        copiesTextView.setText(bookCopies);
        supplierTextView.setText(supplierName);
        contactTextView.setText(contactNo);
    }
}
