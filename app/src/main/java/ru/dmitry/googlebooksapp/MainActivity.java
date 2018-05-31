package ru.dmitry.googlebooksapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final String LOG_TAG = "TAG";
    private Button btnSearch;
    private EditText mBookInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch = findViewById(R.id.searchButton);
        btnSearch.setOnClickListener(this);
        mBookInput = findViewById(R.id.bookInput);
    }

    @Override
    public void onClick(View v) {
        String queryString = mBookInput.getText().toString();
        //For hiding the keyboard when  the search button is clicked
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //For checking the network state and empty search field
        ConnectivityManager conMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();

        if (queryString.length() == 0) {
            mBookInput.setText("Please enter a search term.");
        } else if (networkInfo == null && !networkInfo.isConnected()) {
            mBookInput.setText("Please check your network connection and try again.");
        } else {
            Log.d(LOG_TAG, "YES");
            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra("BookInput", mBookInput.getText().toString());
            startActivity(intent);
        }
    }
}
