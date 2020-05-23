package com.example.recipeSE.search.utils;

import java.io.IOException;
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
    public MutableLiveData<String> status;

    public SharedViewModel() {
        this.status = new MutableLiveData<String>();
        this.status.setValue("OK");
    }

    public LiveData<List<Recipe>> getRecipes(String query) {
        this.status.setValue("OK");
        if (data == null) { // if this is the firs query i submit

            this.currentQuery = query; // save the query string
            data = new MutableLiveData<List<Recipe>>(); // init data structure
            loadRecipes(query);

            return this.data;
        }
        else{
            //if I already have data and the new query is different from the old one re-fetch data
            if( ! this.currentQuery.equals(query) ){
                this.currentQuery = query; //update the query that generates data
                //async data loading procedure
                loadRecipes(query);
            }

            return this.data;
        }

    }

    //overload the method (non-remote getter)
    public LiveData<List<Recipe>> getRecipes() {
        return this.data;
    }

    public String getCurrentQuery(){return this.currentQuery;}


    private void loadRecipes(final String query) {
        // fetch resources from API Asynchronously by running HTTP GET into a separate thread:
        ExecutorService executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        Future<List<Recipe>> result = executor.submit(new AsynkQuery(query));

        try {
            this.data.setValue( result.get() );
        }  catch (ExecutionException e) {
            e.printStackTrace();
            this.status.setValue("FAIL");
        } catch (InterruptedException e) {
            e.printStackTrace();
            this.status.setValue("FAIL");
        } catch (Exception e) {
            e.printStackTrace();
            this.status.setValue("FAIL");
        }

    }


}


