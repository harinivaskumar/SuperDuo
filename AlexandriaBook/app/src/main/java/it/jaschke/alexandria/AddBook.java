package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import it.jaschke.alexandria.barcode.BarcodeCaptureActivity;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class AddBook extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final String LOG_TAG = AddBook.class.getSimpleName();

    private final int LOADER_ID = 1;
    private static final int RC_BARCODE_CAPTURE = 9001;

    private final int E_ARTICLE_NUMBER_10 = 10;
    private final int E_ARTICLE_NUMBER_13 = 13;
    private final int BOOK_TITLE_ID = 0;
    private final int BOOK_SUB_TITLE_ID = 1;
    private final int BOOK_COVER_ID = 2;
    private final int BOOK_AUTHOR_ID = 3;
    private final int BOOK_CATEGORY_ID = 4;
    private final int BOOK_ID_MAX = 5;

    private final String EAN_CONTENT = "eanContent";
    private final String E_ARTICLE_NUMBER_USA_PREFIX = "978";
    private String eArticleNumber;

    private View rootView;
    private TextView bookTitle, bookSubTitle, author, category;
    private EditText eArticleNumberEditText;
    private ImageView bookCoverImage;
    private Button scanButton;

    public AddBook() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);

        eArticleNumberEditText = (EditText) rootView.findViewById(R.id.ean);
        bookTitle = (TextView) rootView.findViewById(R.id.bookTitle);
        bookSubTitle = (TextView) rootView.findViewById(R.id.bookSubTitle);
        author = (TextView) rootView.findViewById(R.id.authors);
        category = (TextView) rootView.findViewById(R.id.categories);
        bookCoverImage = (ImageView) rootView.findViewById(R.id.bookCover);
        scanButton = (Button) rootView.findViewById(R.id.scan_button);

        eArticleNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable editableString) {
                seteArticleNumber(editableString.toString());

                if (checkAndValidateEArticleNumber()) {
                    if (isNetworkAvailable()) {
                        startBookService(BookService.FETCH_BOOK);
                        AddBook.this.restartLoader();
                    }
                }
            }
        });

        scanButton.setOnClickListener(this);
        (rootView.findViewById(R.id.save_button)).setOnClickListener(this);
        (rootView.findViewById(R.id.delete_button)).setOnClickListener(this);

        if (savedInstanceState != null) {
            seteArticleNumber(savedInstanceState.getString(EAN_CONTENT));
            eArticleNumberEditText.setText(geteArticleNumber());
            eArticleNumberEditText.setHint("");
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (eArticleNumberEditText != null) {
            outState.putString(EAN_CONTENT, eArticleNumberEditText.getText().toString());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            seteArticleNumber(savedInstanceState.getString(EAN_CONTENT));
            eArticleNumberEditText.setText(geteArticleNumber());
            if (geteArticleNumber().isEmpty()){
                eArticleNumberEditText.setHint(R.string.input_hint);
                Log.e(LOG_TAG, "onViewStateRestored : No Value stored in Bundle! Hint set!");
            }else {
                eArticleNumberEditText.setHint("");
                Log.e(LOG_TAG, "onViewStateRestored : Value restored from Bundle! Hint not required!");
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_button:
                Intent scanBarcodeIntent = new Intent(AddBook.this.getActivity(), BarcodeCaptureActivity.class);
                scanBarcodeIntent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                scanBarcodeIntent.putExtra(BarcodeCaptureActivity.UseFlash, false);

                startActivityForResult(scanBarcodeIntent, RC_BARCODE_CAPTURE);
                break;
            case R.id.save_button:
                cleareArticleNumberText();
                break;
            case R.id.delete_button:
                startBookService(BookService.DELETE_BOOK);

                cleareArticleNumberText();
                Snackbar.make(getView(), "Book Deleted Successfully!", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String barcodeStr = getBarcodeData(data);
                    eArticleNumberEditText.setText(barcodeStr);
                    //Snackbar.make(getView(), barcodeStr, Snackbar.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, "Barcode read: " + barcodeStr);
                } else {
                    Snackbar.make(getView(), R.string.barcode_failure, Snackbar.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, "No barcode captured, intent data is null");
                }
            } else {
                String failureMessage = String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode));
                Snackbar.make(getView(), failureMessage, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Added from StackOverFlow
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        Log.e(LOG_TAG, "No Internet Connection available.");
        Snackbar.make(getView(), R.string.you_are_offline, Snackbar.LENGTH_SHORT).show();
        return false;
    }

    private Editable geteArticleNumberFromEditText() {
        return eArticleNumberEditText.getText();
    }

    private void cleareArticleNumberText() {
        eArticleNumberEditText.setText("");
        eArticleNumberEditText.setHint(R.string.input_hint);
        Log.e(LOG_TAG, "cleareArticleNumberText : EAN Hint is Required!");
    }

    private void seteArticleNumber(String eArticleNumber) {
        this.eArticleNumber = eArticleNumber;
    }

    private String geteArticleNumber() {
        return this.eArticleNumber;
    }

    private String getBarcodeData(Intent data) {
        Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
        if (barcode != null)
            return barcode.displayValue;
        else
            return null;
    }

    private void checkAndChangeToUSAEArticleNumber() {
        if (geteArticleNumber().length() == E_ARTICLE_NUMBER_10 &&
                !geteArticleNumber().startsWith(E_ARTICLE_NUMBER_USA_PREFIX)) {
            seteArticleNumber(E_ARTICLE_NUMBER_USA_PREFIX + geteArticleNumber());
        }
    }

    private boolean checkAndValidateEArticleNumber() {
        if (geteArticleNumberFromEditText().length() == 0) {
            clearUIFields();
            return false;
        }

        checkAndChangeToUSAEArticleNumber();

        if (geteArticleNumber().length() < E_ARTICLE_NUMBER_13) {
            clearUIFields();
            return false;
        }
        return true;
    }

    private void startBookService(String serviceType) {
        if (geteArticleNumberFromEditText().length() == 0) {
            clearUIFields();
            return;
        }

        Intent bookServiceIntent = new Intent(getActivity(), BookService.class);

        bookServiceIntent.putExtra(BookService.EAN, geteArticleNumber());
        bookServiceIntent.setAction(serviceType);

        getActivity().startService(bookServiceIntent);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (checkAndValidateEArticleNumber()) {
            return new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.
                            buildFullBookUri(Long.parseLong(geteArticleNumber())),
                    null,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        } else {
            Snackbar.make(getView(), "Book Saved Successfully!", Snackbar.LENGTH_SHORT).show();
        }

        String[] fieldValues = new String[BOOK_ID_MAX];

        fieldValues[BOOK_TITLE_ID] = data.getString(
                data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        fieldValues[BOOK_SUB_TITLE_ID] = data.getString(
                data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        fieldValues[BOOK_AUTHOR_ID] = data.getString(
                data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        fieldValues[BOOK_COVER_ID] = data.getString(
                data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        fieldValues[BOOK_CATEGORY_ID] = data.getString(
                data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));

        updateUIFields(fieldValues);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void updateUIFields(String[] fieldValues) {
        bookTitle.setText(fieldValues[BOOK_TITLE_ID]);
        bookSubTitle.setText(fieldValues[BOOK_SUB_TITLE_ID]);
        category.setText(fieldValues[BOOK_CATEGORY_ID]);

        if (fieldValues[BOOK_AUTHOR_ID] != null) {
            String[] authorsArr = fieldValues[BOOK_AUTHOR_ID].split(",");
            author.setLines(authorsArr.length);
            author.setText(fieldValues[BOOK_AUTHOR_ID].replace(",", "\n"));
        }else {
            author.setText(R.string.no_author_found);
            author.setTextColor(Color.RED);
        }

        if ((fieldValues[BOOK_COVER_ID] != null) &&
                (Patterns.WEB_URL.matcher(fieldValues[BOOK_COVER_ID]).matches()))
        {
            new DownloadImage(bookCoverImage).execute(fieldValues[BOOK_COVER_ID]);
        }else {
            bookCoverImage.setImageResource(R.drawable.ic_launcher);
        }

        bookCoverImage.setVisibility(View.VISIBLE);
        (rootView.findViewById(R.id.save_button)).setVisibility(View.VISIBLE);
        (rootView.findViewById(R.id.delete_button)).setVisibility(View.VISIBLE);
    }

    private void clearUIFields() {
        bookTitle.setText("");
        bookSubTitle.setText("");
        author.setText("");
        category.setText("");

        bookCoverImage.setVisibility(View.INVISIBLE);
        (rootView.findViewById(R.id.save_button)).setVisibility(View.INVISIBLE);
        (rootView.findViewById(R.id.delete_button)).setVisibility(View.INVISIBLE);
    }
}