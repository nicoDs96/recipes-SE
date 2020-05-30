package com.example.recipeSE.savedRecipes.utils;

import android.app.Application;

import com.example.recipeSE.search.utils.Recipe;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class SavedRecipesViewModel extends AndroidViewModel {
    private SavedRecipeRepository mRepository;
    private LiveData<List<Recipe>> mSavedRecipes;

    public SavedRecipesViewModel(@NonNull Application application) {
        super(application);
        mRepository = new SavedRecipeRepository(application);
        mSavedRecipes =  mRepository.getAllSavedrecipes();
    }

    public LiveData<List<Recipe>> getAllSavedrecipe(){ return mSavedRecipes; }

    public void save(Recipe recipe){ mRepository.save(recipe); }

    public void delete(Recipe recipe){ mRepository.delete(recipe); }
}
