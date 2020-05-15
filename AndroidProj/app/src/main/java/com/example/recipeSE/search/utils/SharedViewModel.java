package com.example.recipeSE.search.utils;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


public class SharedViewModel extends AndroidViewModel {
    private MutableLiveData<List<Recipes>> data;
    private String currentQuery;

    public SharedViewModel(@NonNull Application application) {
        super(application);
    }

    public void setRecipes(String query) {
        if (data == null) { // if this is the firs query i submit
            this.currentQuery = query; // save the query string
            data = new MutableLiveData<List<Recipes>>(); // init data structure
            loadRecipes(query); // fetch resources from API
        }
        else{
            //if I already have data and the new query is different from the old one re-fetch data
            if( ! this.currentQuery.equals(query) ){
                loadRecipes(query);
                this.currentQuery = query; //update the query that generates data
            }
        }

    }

    public MutableLiveData<List<Recipes>> getRecipes() {
        return this.data;
    }

    private void loadRecipes(String query) {
        // Do an asynchronous operation to fetch data.
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this.getApplication());
        String url ="http://www.google.com";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //textView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


}
