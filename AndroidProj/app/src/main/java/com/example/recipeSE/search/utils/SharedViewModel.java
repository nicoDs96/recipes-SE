package com.example.recipeSE.search.utils;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<List<Recipes>> data;
    private String currentQuery;

    public void setRecipes(String query) {
        if (data == null) {
            this.currentQuery = query;
            data = new MutableLiveData<List<Recipes>>();
            loadRecipes(query);
        }
        else{
            if( ! this.currentQuery.equals(query) ){
                loadRecipes(query);
                this.currentQuery = query;
            }
        }

    }

    public MutableLiveData<List<Recipes>> getRecipes() {
        return this.data;
    }

    private void loadRecipes(String query) {
        // Do an asynchronous operation to fetch users.
    }


}
