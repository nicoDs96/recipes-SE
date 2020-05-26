package com.example.recipeSE.search;

import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;

import com.example.recipeSE.R;
import com.example.recipeSE.search.utils.SharedViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;


public class SearchActivity extends AppCompatActivity {

    private SharedViewModel model;
    private MaterialToolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Just in case it is needed TODO: remove it
        this.model = new ViewModelProvider(this ).get(SharedViewModel.class);

        mToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        mToolbar.setTitle(Html.fromHtml("<font color='#ffffff'>Search</font>"));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = findViewById(R.id.drawernavbar);
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        //mToolbar.setOnMenuItemClickListener();

    }



}
