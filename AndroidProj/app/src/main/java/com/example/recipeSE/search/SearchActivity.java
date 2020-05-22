package com.example.recipeSE.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import com.example.recipeSE.R;
import com.example.recipeSE.search.utils.SharedViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


public class SearchActivity extends AppCompatActivity {

    private SharedViewModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Just in case it is needed TODO: remove it
        this.model = new ViewModelProvider(this ).get(SharedViewModel.class);

    }



}
