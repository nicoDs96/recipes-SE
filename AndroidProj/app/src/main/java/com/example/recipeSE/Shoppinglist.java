package com.example.recipeSE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class Shoppinglist extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppinglist);

        findViewById(R.id.addIngr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText bar = (EditText) findViewById(R.id.addBar);
                if (!bar.getText().equals("")) {

                    ArrayList<String> ingr = new ArrayList<String>();
                    ingr = getArrayList("sessionkey"); //TODO: sostituire costante con sessionkey var
                    String barString = bar.getText();
                    ingr.add(barString);
                    saveArrayList(ingr,"sessionkey");
                    TextView testPref = findViewById(R.id.testpref);
                    testPref.setText(getArrayList("sessionkey").toString());
                }
            }

        });

    }
    public void saveArrayList(ArrayList<String> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<String> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
