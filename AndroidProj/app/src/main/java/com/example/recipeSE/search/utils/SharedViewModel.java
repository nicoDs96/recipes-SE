package com.example.recipeSE.search.utils;

import android.app.Application;
import android.util.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;


public class SharedViewModel extends AndroidViewModel {
    private MutableLiveData<List<Recipe>> data;
    private String currentQuery;
    public MutableLiveData<String> status;
    private WorkManager mWorkManager;
    private LiveData<List<WorkInfo>> mSavedWorkInfo;

    public SharedViewModel(@NonNull Application application) {
        super(application);
        this.status = new MutableLiveData<String>();
        this.status.setValue("OK");
        this.data = new MutableLiveData<>();
        mWorkManager = WorkManager.getInstance(application);
        mSavedWorkInfo = mWorkManager.getWorkInfosForUniqueWorkLiveData("QUERY_REQ");
    }

    // A getter method for mSavedWorkInfo
    public LiveData<List<WorkInfo>> getOutputWorkInfo() { return mSavedWorkInfo; }

    public LiveData<List<Recipe>> getRecipes(String query) {
        this.status.setValue("OK");
        if (this.currentQuery==null) { // if this is the firs query i submit

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

    public void performQuery(String query){
        this.status.setValue("OK");
         // if this is the firs query i submit
        this.currentQuery = query; // save the query string
        loadRecipes(query);
    }

    //overload the method (non-remote getter)
    public LiveData<List<Recipe>> getRecipes() {
        return this.data;
    }

    public String getCurrentQuery(){return this.currentQuery;}

    public void setresult(List<Recipe> list){
        try {
            this.data.setValue( list );
        } catch (Exception e) {
            e.printStackTrace();
            this.status.setValue("FAIL");
        }
    }


    private void loadRecipes(final String query) {
        // fetch resources from API Asynchronously by running HTTP GET into a separate thread:
        Data.Builder queryBuilder = new Data.Builder();
        queryBuilder.putString("query",query);
        Data qry = queryBuilder.build();

        OneTimeWorkRequest qry_req= new OneTimeWorkRequest.Builder(AsynkQuery.class)
                .setInputData(qry)
                .addTag("QUERY")
                .build();

        mWorkManager.enqueueUniqueWork("QUERY_REQ", ExistingWorkPolicy.REPLACE,qry_req);

    }


}


