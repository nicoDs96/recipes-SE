package com.example.recipeSE.search.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AsynkQuery implements Callable<List<Recipe>>  {

    private String query;
    public AsynkQuery(String query) {
        this.query = query;
    }


    @Override
    public List<Recipe> call() throws Exception {
        Gson gson = new Gson();

        // Make the query to Json (BodyQuery is only a Wrapper to allow gson.toJson work properly)
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

        List<Recipe> list = null;
        try {
            Response response = client.newCall(request).execute();
            String resString = response.body().string();
            System.out.println(resString); //log info

            // Register a custom deserializer with gson
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Recipe.class, new RecipeDeserializer());

            //create an instance of gson able to correctly parse the server response
            Gson customGson = gsonBuilder.create();
            //set the response type as list of recipes
            Type listRecipesType = new TypeToken<List<Recipe>>() {}.getType();
            //parse data
            list = customGson.fromJson(resString, listRecipesType);

            System.out.println(list.get(10).toString());
            //update ViewModel

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
