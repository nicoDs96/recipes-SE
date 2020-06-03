package com.example.recipeSE.shoppinglist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.recipeSE.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppigListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlanetAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> planetList=new ArrayList();
    private MaterialToolbar mToolbar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shoppinglist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences session = this.getActivity().getSharedPreferences("sessionuser", Context.MODE_PRIVATE);
        final String key = session.getString("sessionkey", null);
        //init the view with available ingredients and init the adapter
        displayIngr(key);

        getActivity().findViewById(R.id.addIngr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText bar = getActivity().findViewById(R.id.addBar);
                if (!bar.getText().toString().equals("")) {
                    String ingr = bar.getText().toString();
                    addToSet(key,ingr);
                    adapter.insertNewIngredient(ingr);
                    //displayIngr(key);
                }
            }
        });

    }

    public void addToSet(String key,String ingr){
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> updatedSet = getTheSet(key);
        updatedSet.add(ingr);
        editor.clear();
        editor.putStringSet(key, updatedSet);
        editor.commit();
    }

    public HashSet<String> getTheSet(String key){
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE );
        return (HashSet<String>) prefs.getStringSet(key, new HashSet<String>());
    }

    public void displayIngr(String key){
        planetList = new ArrayList();
        HashSet<String> list = getTheSet(key);
        for (String str : list) {
            planetList.add(str);
        }
        recyclerView = getActivity().findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PlanetAdapter(planetList,getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter);
    }
}
