package com.example.recipeSE.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.recipeSE.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class PlanetAdapter extends RecyclerView.Adapter<PlanetAdapter.PlanetViewHolder> {

    ArrayList<String> planetList;
    private final Context context;

    public PlanetAdapter(ArrayList<String> planetList, Context context) {
        this.planetList = planetList;
        this.context = context;
    }

    @Override
    public PlanetAdapter.PlanetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.planet_row,parent,false);
        PlanetViewHolder viewHolder=new PlanetViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PlanetAdapter.PlanetViewHolder holder, int position) {
        //TODO: checkbox backend (not mandatory)
        holder.text.setText(planetList.get(position));
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromSet("ingredients",holder.text.getText().toString()); //TODO: sostituire costante
                Intent intent  = new Intent(context, Shoppinglist.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return planetList.size();
    }

    public static class PlanetViewHolder extends RecyclerView.ViewHolder{
        protected CheckBox box;
        protected TextView text;
        protected ImageView img;


        public PlanetViewHolder(View itemView) {
            super(itemView);
            box= itemView.findViewById(R.id.checkBox2);
            text= itemView.findViewById(R.id.text_id);
            img= itemView.findViewById(R.id.imageButton);

        }
    }

    public void removeFromSet(String key,String ingr){
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> updatedSet = getTheSet(key);
        updatedSet.remove(ingr);
        editor.clear();
        editor.putStringSet(key, updatedSet);
        editor.commit();
    }

    public HashSet<String> getTheSet(String key){
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE );
        return (HashSet<String>) prefs.getStringSet(key, new HashSet<String>());
    }

}