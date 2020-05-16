package com.example.recipeSE.search.utils;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SharedViewModel extends ViewModel {
    private MutableLiveData<List<Recipes>> data;
    private String currentQuery;


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

    private void loadRecipes(final String query) {
        // Do an asynchronous operation to fetch data.
        // Instantiate the RequestQueue.

        Gson gson = new Gson();


        String req = gson.toJson(new BodyQuery(query));

        System.out.println("req: "+ req);

        String address = "http://127.0.0.1:3000/recipes";

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req);
        Request request = new Request.Builder()
                .url(address)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}


