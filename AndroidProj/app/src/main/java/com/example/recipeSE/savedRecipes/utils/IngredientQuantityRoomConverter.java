package com.example.recipeSE.savedRecipes.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import androidx.room.TypeConverter;

public class IngredientQuantityRoomConverter {
    @TypeConverter
    public static String fromMap(Map<String,String> igrs){
        String out ="";
        for(Map.Entry e: igrs.entrySet()){
            out += e.getKey() +","+e.getValue()+";";
        }
        Log.d("Type Converter","DB Entrty: "+ out);
        return out;
    }
    @TypeConverter
    public static Map<String,String> fromString(String roomEntry){
        Map<String, String> map = new HashMap<>();
        String[] list = roomEntry.split(";");
        for(String tuple : list){
            String[] couple = tuple.split(",");
            map.put(couple[0],couple[1]);
        }
        Log.d("Type Converter","DB 2 MAP: "+ list.toString());

        return map;
    }
}
