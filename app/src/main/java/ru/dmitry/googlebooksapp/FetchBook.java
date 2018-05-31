package ru.dmitry.googlebooksapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by dmitry on 28.05.18.
 */

public class FetchBook extends AsyncTask<String, Void, String> {
    private OnTaskCompleted listner;
    ArrayList<Book> bookList;

    FetchBook(ArrayList<Book> bookList, OnTaskCompleted listner){
        this.bookList = bookList;
        this.listner = listner;
    }
    @Override
    protected String doInBackground(String... params) {
        return NetworkUtils.getBookInfo(params[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        String title = null;
        String author = null;
        String infoUrl = null;
        String imageUrl = null;
        String publisher = null;

        try{
            JSONObject jsonObject = new JSONObject(s);
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject book = itemsArray.getJSONObject(i);

                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                try{
                    title = volumeInfo.getString("title");
                    if(volumeInfo.has("authors")){
                        JSONArray authors = volumeInfo.getJSONArray("authors");
                        author = authors.getString(0);
                    }
                    if(volumeInfo.has("publisher")){
                        publisher = volumeInfo.getString("publisher");
                    }
                    if(volumeInfo.has("infoLink")) {
                        infoUrl = volumeInfo.getString("infoLink");
                    }
                    if(volumeInfo.has("imageLinks")){
                        JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                        imageUrl = imageLinks.getString("smallThumbnail");
                    }
                    if (!title.isEmpty()) {
                        Book bookItem = new Book(title, author, infoUrl, imageUrl, publisher);
                        bookList.add(bookItem);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listner.onTaskCompleted();
    }
}
