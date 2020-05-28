package com.example.recipeSE.savedRecipes;

import com.example.recipeSE.search.utils.Recipe;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

    //When Room queries return LiveData, the queries are automatically run asynchronously on a background thread.
    @Query("SELECT * from saved_recipes")
    LiveData<List<Recipe>> getSavedRecipes();

}
