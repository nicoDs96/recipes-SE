package com.example.recipeSE.savedRecipes.utils;

import com.example.recipeSE.search.utils.Recipe;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface SavedRecipesDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void save(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Query("SELECT * from saved_recipes")
    LiveData<List<Recipe>> getSavedRecipes();

}
