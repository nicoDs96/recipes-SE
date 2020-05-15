package com.example.recipeSE.search.utils;

import android.os.Build;

import java.util.Map;
import java.util.Objects;

import androidx.annotation.RequiresApi;

public class Recipes {
    private String id;
    private Integer calories;
    private Map<String,String> ingrediet_quantity;
    private String href;
    private String title;

    public Recipes(String id, Integer calories, Map<String, String> ingrediet_quantity, String href, String title) {
        this.id = id;
        this.calories = calories;
        this.ingrediet_quantity = ingrediet_quantity;
        this.href = href;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Map<String, String> getIngrediet_quantity() {
        return ingrediet_quantity;
    }

    public void setIngrediet_quantity(Map<String, String> ingrediet_quantity) {
        this.ingrediet_quantity = ingrediet_quantity;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipes recipes = (Recipes) o;
        return id.equals(recipes.id);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
