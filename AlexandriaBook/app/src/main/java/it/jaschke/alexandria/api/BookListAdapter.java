package it.jaschke.alexandria.api;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.DownloadImage;

/**
 * Created by saj on 11/01/15.
 */
public class BookListAdapter extends CursorAdapter {

    private final String LOG_TAG = BookListAdapter.class.getSimpleName();

    public static class ViewHolder {
        public final ImageView bookCover;
        public final TextView bookTitle;
        public final TextView bookSubTitle;

        public ViewHolder(View view) {
            bookCover = (ImageView) view.findViewById(R.id.listFullBookCover);
            bookTitle = (TextView) view.findViewById(R.id.listBookTitle);
            bookSubTitle = (TextView) view.findViewById(R.id.listBookSubTitle);
        }
    }

    public BookListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        /*
         * When recycling to avoid use of a different Book image to be
         * shown in the new entry, this is reset to the defaul image
         */
        viewHolder.bookCover.setImageResource(R.drawable.ic_launcher);

        String imgUrl = cursor.getString(
                cursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));

        if (Utility.isNetworkAvailable(context, null, LOG_TAG)) {
            if ((imgUrl != null) &&
                    (Patterns.WEB_URL.matcher(imgUrl).matches())) {
                new DownloadImage(viewHolder.bookCover).execute(imgUrl);
            }
        }else{
            viewHolder.bookCover.setImageResource(R.drawable.ic_launcher);
        }
        viewHolder.bookCover.setVisibility(View.VISIBLE);

        String bookTitle = cursor.getString(
                cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        viewHolder.bookTitle.setText(bookTitle);

        String bookSubTitle = cursor.getString(
                cursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        viewHolder.bookSubTitle.setText(bookSubTitle);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }
}
