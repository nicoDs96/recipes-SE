package com.example.recipeSE.savedRecipes;

import android.app.Application;
import android.util.Log;

import com.example.recipeSE.search.utils.Recipe;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SavedRecipesViewModel extends AndroidViewModel {
    private SavedRecipeRepository mRepository;
    private MutableLiveData<List<Recipe>> mSavedRecipes;

    public SavedRecipesViewModel(@NonNull Application application) {
        super(application);
        mRepository = new SavedRecipeRepository(application);
        mSavedRecipes = new MutableLiveData( mRepository.getAllSavedrecipes().getValue() );
        if(mSavedRecipes==null){
          mSavedRecipes = new MutableLiveData<>(new LinkedList<Recipe>() );
        }else {
            Log.d("SavedRecipesVM", mSavedRecipes.toString());
        }
    }

    public MutableLiveData<List<Recipe>> getAllSavedrecipe(){ return mSavedRecipes; }

    public void save(Recipe recipe){ mRepository.save(recipe); }

    public void delete(Recipe recipe){ mRepository.delete(recipe); }
}
