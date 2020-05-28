package com.example.recipeSE.search.utils;

import android.os.Build;

import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_recipes")
public class Recipe {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name="id")
    private String _id;
    @ColumnInfo(name="calories")
    private Integer calories;
    @ColumnInfo(name="ingredient_quantity")
    private Map<String,String> ingredient_quantity;
    @ColumnInfo(name="href")
    private String href;
    @ColumnInfo(name="title")
    private String title;

    public Recipe(String id, Integer calories, Map<String, String> ingredient_quantity, String href, String title) {
        this._id = id;
        this.calories = calories;
        this.ingredient_quantity = ingredient_quantity;
        this.href = href;
        this.title = title;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Map<String, String> getIngredient_quantity() {
        return ingredient_quantity;
    }

    public void setIngredient_quantity(Map<String, String> ingredient_quantity) {
        this.ingredient_quantity = ingredient_quantity;
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
        Recipe recipe = (Recipe) o;
        return _id.equals(recipe._id);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }

    @Override
    public String toString() {
        return "Recipes{" +
                "_id='" + _id + '\'' +
                ", calories=" + calories +
                ", ingrediet_quantity=" + ingredient_quantity +
                ", href='" + href + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
