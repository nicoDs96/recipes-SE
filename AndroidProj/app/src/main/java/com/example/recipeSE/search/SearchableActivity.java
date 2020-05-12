package com.example.recipeSE.search;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.recipeSE.R;


public class SearchableActivity extends ListActivity {

    /*

    1. Receiving the query
    2. Searching your data
    3. Presenting the results

    * */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("QUERY DEBUG","QUERY: "+ query);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {
        try {
            throw new Exception("------------Not Implemented----------------\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
