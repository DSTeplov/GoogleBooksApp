package ru.dmitry.googlebooksapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import br.com.bloder.magic.view.MagicButton;

public class ResultsActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = MainActivity.class.getSimpleName();

    final String LOG_TAG = "TAG";
    public static final String TITLETEXT_STATE = "titleText";
    public static final String AUTHORTEXT_STATE = "authorText";
    private int number;
    private TextView mAuthorText, mTitleText, mInfoUrl, mImageUrl, mPublisher, mBookNumber;
    private ImageButton btnPrev, btnNext;
    private ImageView bookImage;
    MagicButton mbtn_book;
    ArrayList<Book> bookList;
    String urlInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        OnTaskCompleted listner = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted() {
                if (bookList.size() != 0){
                    setComponents();
                    mBookNumber.setText("1/" + bookList.size());
                } else {
                    mTitleText.setText("");
                    Toast.makeText(getApplicationContext(),
                            "Результаты не найдены", Toast.LENGTH_LONG).show();
                    mAuthorText.setText("");
                }
                btnNext.setEnabled(true);
                btnPrev.setEnabled(true);
            }
        };

        bookList = new ArrayList<>();
        number = 0;
        mAuthorText = findViewById(R.id.authorText);
        mTitleText = findViewById(R.id.tittleText);
        mbtn_book = (MagicButton) findViewById(R.id.magic_button);
        mbtn_book.setMagicButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mTitleText.getText().toString().equals("")) {
                    Intent intent;
                    if (urlInfo != null) {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlInfo));
                        startActivity(intent);
                    }
                }
            }
        });
        mPublisher = findViewById(R.id.publisher);
        mBookNumber = findViewById(R.id.bookNumber);
        bookImage = findViewById(R.id.bookImage);

        btnPrev = findViewById(R.id.previous_button);
        btnPrev.setImageDrawable(getResources().getDrawable(R.drawable.arrow_left_bold));
        btnPrev.setOnClickListener(this);
        btnPrev.setEnabled(false);

        btnNext = findViewById(R.id.next_button);
        btnNext.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_bold));
        btnNext.setOnClickListener(this);
        btnNext.setEnabled(false);
        String queryString = getIntent().getStringExtra("BookInput");

        //For checking the network state and empty search field
        ConnectivityManager conMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() && queryString != null){
            new FetchBook(bookList, listner).execute(queryString);
            mAuthorText.setText("");
            mTitleText.setText(R.string.loading);
        } else{
            mAuthorText.setText("");
            mTitleText.setHint("Please check your network connection and try again.");
        }
        if(savedInstanceState != null){
            mTitleText.setText(savedInstanceState.getString(TITLETEXT_STATE));
            mAuthorText.setText(savedInstanceState.getString(AUTHORTEXT_STATE));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITLETEXT_STATE, mTitleText.getText().toString());
        outState.putString(AUTHORTEXT_STATE, mAuthorText.getText().toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.previous_button:
                Log.d(LOG_TAG, Integer.toString(bookList.size()));
                if (number > 0) number--;
                else break;
                setComponents();
                break;
            case R.id.next_button:
                if (number < 9 && !bookList.isEmpty()) number++;
                else break;
                setComponents();
                break;
        }
    }

    private void setComponents() {
        mTitleText.setText(bookList.get(number).getTitle());
        mAuthorText.setText(bookList.get(number).getAuthor());
        urlInfo = bookList.get(number).getInfoUrl();
        mPublisher.setText(bookList.get(number).getPublisher());
        Picasso.with(this).load(bookList.get(number).getImageUrl()).into(bookImage);
        mBookNumber.setText((number + 1) + "/" + bookList.size());
    }
}
