package com.example.recipeSE.search.utils;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class SharedViewModel extends ViewModel {
    private MutableLiveData<List<Recipe>> data;
    private String currentQuery;


    public LiveData<List<Recipe>> getRecipes(String query) {
        if (data == null) { // if this is the firs query i submit

            this.currentQuery = query; // save the query string
            data = new MutableLiveData<List<Recipe>>(); // init data structure
            loadRecipes(query);

            return this.data;
        }
        else{
            //if I already have data and the new query is different from the old one re-fetch data
            if( ! this.currentQuery.equals(query) ){
                loadRecipes(query);
                this.currentQuery = query; //update the query that generates data
            }

            return this.data;
        }

    }


    private void loadRecipes(final String query) {
        // fetch resources from API Asynchronously:
        ExecutorService executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        Future<List<Recipe>> result = executor.submit(new AsynkQuery(query));

        try {
            this.data.setValue( result.get() );
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}


