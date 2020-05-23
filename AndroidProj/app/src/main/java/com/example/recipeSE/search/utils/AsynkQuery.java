package com.example.recipeSE.search.utils;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class AsynkQuery implements Callable<List<Recipe>>  {

    private String query;
    public AsynkQuery(String query) {
        this.query = query;
    }

    @Override
    public List<Recipe> call() throws Exception {
        /*
        * SEND THE QUERY TO THE API AND WAIT THE RESPONSE
        * */
        Gson gson = new Gson();

        // Make the query to Json (BodyQuery is only a Wrapper to allow gson.toJson work properly)
        String req = gson.toJson(new BodyQuery(query));
        System.out.println("req: "+ req);

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
        try {
            Response response = client.newCall(request).execute();
            String resString = response.body().string();
            Log.d("Server Response",resString); //log info

            // Register a custom deserializer with gson
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Recipe.class, new RecipeDeserializer());

            //create an instance of gson able to correctly parse the server response
            Gson customGson = gsonBuilder.create();
            //set the response type as list of recipes
            Type listRecipesType = new TypeToken<List<Recipe>>() {}.getType();
            //parse data
            list = customGson.fromJson(resString, listRecipesType);


        } catch (IOException e) {
            throw  e;
        }
        return list;
    }
}
