package com.example.recipeSE.shoppinglist;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeSE.R;
import com.example.recipeSE.shoppinglist.PlanetAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Shoppinglist extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> planetList=new ArrayList();
    private MaterialToolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppinglist);
        // setup toolbar
        mToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        mToolbar.setTitle(Html.fromHtml("<font color='#ffffff'>Search</font>"));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = findViewById(R.id.drawernavbar);
                drawer.openDrawer(Gravity.LEFT);
            }
        });


        final String key="ingredients"; //TODO: sostituire costante con variabile utente
        displayIngr(key);

        findViewById(R.id.addIngr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText bar = findViewById(R.id.addBar);
                if (!bar.getText().toString().equals("")) {
                    String ingr = bar.getText().toString();
                    addToSet(key,ingr);
                    displayIngr(key);
                }
            }
        });

    }
    public void addToSet(String key,String ingr){
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> updatedSet = getTheSet(key);
        updatedSet.add(ingr);
        editor.clear();
        editor.putStringSet(key, updatedSet);
        editor.commit();
    }

    public HashSet<String> getTheSet(String key){
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE );
        return (HashSet<String>) prefs.getStringSet(key, new HashSet<String>());
    }

    public void displayIngr(String key){
        planetList = new ArrayList();
        HashSet<String> list = getTheSet(key);
        for (String str : list) {
            planetList.add(str);
        }
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PlanetAdapter(planetList,getApplicationContext());
        recyclerView.setAdapter(adapter);
    }
}
