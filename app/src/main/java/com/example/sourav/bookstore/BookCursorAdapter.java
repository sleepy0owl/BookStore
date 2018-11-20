package com.example.sourav.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    public void bindView(View view, final Context context, final Cursor cursor) {

        //get the text views
        TextView nameTextView = view.findViewById(R.id.list_book_name);
        TextView priceTextView = view.findViewById(R.id.list_book_price);
        TextView copiesTextView = view.findViewById(R.id.list_book_copies);
        ImageButton cartButton = view.findViewById(R.id.cart_button);

        //get the column indexes
        int bookIdColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int nameIndex = cursor.getColumnIndex(BookEntry.BOOK_NAME);
        int priceIndex = cursor.getColumnIndex(BookEntry.BOOK_PRICE);
        int copiesIndex = cursor.getColumnIndex(BookEntry.BOOK_COPIES);
        int supplierIndex = cursor.getColumnIndex(BookEntry.BOOK_SUPPLIER);
        int contactIndex = cursor.getColumnIndex(BookEntry.BOOK_SUPPLIER_CONTACT);

        //use the data stored in respective column
        final long bookId = cursor.getInt(bookIdColumnIndex);
        String bookName = cursor.getString(nameIndex);
        int bookPriceInt = cursor.getInt(priceIndex);
        String bookPrice = Integer.toString(bookPriceInt);
        final int copiesInt = cursor.getInt(copiesIndex);
        String bookCopies = Integer.toString(copiesInt);
        String supplierName = cursor.getString(supplierIndex);
        String contactNo = cursor.getString(contactIndex);

        //set the values to textViews
        nameTextView.setText(bookName);
        priceTextView.setText(bookPrice);
        copiesTextView.setText(bookCopies);
        //supplierTextView.setText(supplierName);
        //contactTextView.setText(contactNo);

        //set up the selection and selection ags
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);
                decreaseCopies(context, currentUri, copiesInt);
            }
        });
    }

    private void decreaseCopies(Context context, Uri currentUri, int currentCopies){

        if (currentCopies >= 1){
            currentCopies = currentCopies - 1;

            ContentValues values = new ContentValues();
            values.put(BookEntry.BOOK_COPIES, currentCopies);

            int rowsAffected = context.getContentResolver().update(currentUri, values, null, null);
            if (rowsAffected == 1){
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.add_to_cart), Toast.LENGTH_SHORT ).show();
            }
        }else {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.stock_zero), Toast.LENGTH_SHORT).show();
        }
    }

}
