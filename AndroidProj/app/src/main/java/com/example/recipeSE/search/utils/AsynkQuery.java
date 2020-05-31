package com.example.recipeSE.search.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.recipeSE.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AsynkQuery extends Worker  {

    private String query;

    public AsynkQuery(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /*Calls the api and returns the result*/
    public String fetchData() throws Exception {
        /*
        * SEND THE QUERY TO THE API AND WAIT THE RESPONSE
        * */
        Gson gson = new Gson();

        // Make the query to Json (BodyQuery is only a Wrapper to allow gson.toJson work properly)
        String req = gson.toJson(new BodyQuery(query));
        System.out.println("req: "+ req);

        //TODO replace with heroku address
        String address = "http://192.168.1.63:3000/recipes";
        //build the http client
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        //create a body to append at request
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req);
        //create the HTTP request, set the header,attach the body
        Request request = new Request.Builder()
                .url(address)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        //wait the response and convert it from JsonArray to List<Recipe>
        List<Recipe> list = new LinkedList<>();
        String resString="";
        try {
            Response response = client.newCall(request).execute();
            resString = response.body().string();
            //Log.d("Server Response",resString.substring(0,100)+"... too long"); //log info

        } catch (IOException e) {
            throw  e;
        }
        return resString;
    }

    private void serializeResult(String result) {
        String key = "query_result";
        //get a shared preference file
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getApplicationContext().getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putString(key, result);
        editor.apply();
    }

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();

        // Get the query performed by the user as Work Input data
        // NOTE that if query is not initialized the user query will be ignored
        this.query = getInputData().getString("query");

        String result;
        try {
            result = fetchData();
        } catch (Exception e) {
            return Result.failure();
        }
        //write the result into the shared preference
        this.serializeResult(result);


        Data outputData = new Data.Builder()
                .putString("query_result", "query_result")
                .build();

        return Result.success(outputData);
    }
}
