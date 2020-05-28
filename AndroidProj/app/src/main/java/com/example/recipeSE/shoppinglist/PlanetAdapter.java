package com.example.recipeSE.shoppinglist;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipeSE.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import androidx.recyclerview.widget.RecyclerView;


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
    public void onBindViewHolder(final PlanetAdapter.PlanetViewHolder holder, final int position) {
        //TODO: checkbox backend (not mandatory)
        holder.text.setText(planetList.get(position));
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String elemToRemove = holder.text.getText().toString();
                Integer idx = planetList.indexOf(elemToRemove);
                if(idx > -1 ){
                    Log.d("Item Removed:",elemToRemove);
                    Log.d("Item Idx:",idx.toString());
                    Log.d("Received Pos:",((Integer) position).toString());
                    planetList.remove(elemToRemove);
                    notifyItemRemoved(idx);
                    removeFromSet("ingredients",holder.text.getText().toString()); //TODO: sostituire costante

                    //TODO remove debug code
                    Log.d("DEBUG","deleteOnclick");
                    printList(planetList);

                }else{
                    Log.e("Idx Error","Element Not Found");
                }



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

    public void insertNewIngredient(String ingredient){
        this.planetList.add(ingredient);
        notifyItemInserted(planetList.size()-1);
        //TODO remove debug code
        Log.d("DEBUG","insertNewIngred");
        printList(planetList);
    }

    private void printList(ArrayList<String> list){

        for(Integer i=0; i<list.size(); i++){
            Log.d(i.toString(),list.get(i));
        }
    }
}