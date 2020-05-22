package com.example.recipeSE.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;

import com.example.recipeSE.R;
import com.example.recipeSE.search.utils.SharedViewModel;

import androidx.appcompat.app.ActionBar;
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

        // set actionbar title for the whole activity
        ActionBar bar = getSupportActionBar();
        bar.setTitle(Html.fromHtml("<font color='#ffffff'>Search</font>"));


    }



}
