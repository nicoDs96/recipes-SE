package com.example.recipeSE.shoppinglist;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeSE.R;
import com.example.recipeSE.shoppinglist.PlanetAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Shoppinglist extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> planetList=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppinglist);

        final String key="ingredients";
        displayIngr(key); //TODO: sostituire costante con sessionkey var

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
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE ); //PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> updatedSet = getTheSet(key);
        updatedSet.add(ingr);
        editor.clear(); //needed otherwise sometimes changes get lost
        editor.putStringSet(key, updatedSet);
        editor.commit();
    }

    public HashSet<String> getTheSet(String key){
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE ); //PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
