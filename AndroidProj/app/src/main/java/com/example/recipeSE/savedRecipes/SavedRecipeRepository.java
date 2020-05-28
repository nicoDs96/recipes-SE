package com.example.recipeSE.savedRecipes;

import android.app.Application;

import com.example.recipeSE.search.utils.Recipe;

import java.util.List;

import androidx.lifecycle.LiveData;

public class SavedRecipeRepository {
    private SavedRecipesDAO mSavedRecipesDAO;
    private LiveData<List<Recipe>> mSavedRecipes;

    SavedRecipeRepository(Application application){
        SavedRecipesRoomDB db = SavedRecipesRoomDB.getDatabase(application);
        mSavedRecipesDAO = db.savedRecipesDAO();
        mSavedRecipes = mSavedRecipesDAO.getSavedRecipes();
    }
    LiveData<List<Recipe>> getAllSavedrecipes(){
        return mSavedRecipes;
    }
    void save(Recipe recipe){
        SavedRecipesRoomDB.databaseWriteExecutor.execute(()->{mSavedRecipesDAO.save(recipe);});
    }
    void delete(Recipe recipe){
        SavedRecipesRoomDB.databaseWriteExecutor.execute(()->{mSavedRecipesDAO.delete(recipe);});
    }
}
