package com.example.recipeSE.search.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RecipeDeserializer implements JsonDeserializer<Recipe> {
    @Override
    public Recipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonRecipe = json.getAsJsonObject();
        String id = jsonRecipe.get("_id").getAsString();
        Integer calories = null;
        try{
            calories = jsonRecipe.get("calories").getAsInt();
        }catch (Exception e){

        }
        String href = jsonRecipe.get("href").getAsString();
        String title = jsonRecipe.get("title").getAsString();

        //create Map<String,String> from ingredients:[{ingredient:xxx,quantity:xxxx},{ingredient:xxxx,quantity:xxx}...]
        Map<String, String> ing_qty_map = new HashMap<>();
        for (Object object : jsonRecipe.get("ingredients").getAsJsonArray()){
            JsonObject ing_qty_pair = (JsonObject) object;
            ing_qty_map.put(
                    ing_qty_pair.get("ingredient").getAsString(),
                    ing_qty_pair.get("quantity").getAsString()
            );
        }

        Recipe recipe = new Recipe(id, calories, ing_qty_map, href, title);
        return recipe;
    }
}
