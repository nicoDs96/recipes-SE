package com.example.recipeSE.search.utils;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.recipeSE.R;
import com.google.android.material.card.MaterialCardView;


import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    //TODO: replace textview vith cards with cards
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView cal;
        public TextView ingredients;
        public TextView quantities;
        public Button href;

        public RecipeViewHolder(@NonNull View itemView, TextView title, TextView cal,
                                TextView ingredients, TextView quantities, Button href) {
            super(itemView);
            this.title = title;
            this.cal = cal;
            this.ingredients = ingredients;
            this.quantities = quantities;
            this.href = href;
        }
    }

    public SearchResultsAdapter(List<Recipe> recipes) {
        this.recipes=recipes;
    }

    @NonNull
    @Override
    // Create new views (invoked by the layout manager)
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        // TODO: replace textview vith cards with cards
        MaterialCardView v = (MaterialCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_search_text, parent, false);



        RecipeViewHolder vh = new RecipeViewHolder(v,
                (TextView) v.findViewById(R.id.Title),
                (TextView) v.findViewById(R.id.cal),
                (TextView) v.findViewById(R.id.ingredients),
                (TextView) v.findViewById(R.id.quantities),
                (Button) v.findViewById(R.id.go2site) );

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Recipe rec = recipes.get(position);
        String ingredients = "";
        String quantities ="";
        for (Map.Entry e : rec.getIngredient_quantity().entrySet()){
            ingredients += e.getKey()+"\n";
            quantities += e.getValue()+"\n";
        }

        holder.title.setText(rec.getTitle());
        Integer kc= rec.getCalories();
        String kcString="";
        if(kc == null){
            kcString="Non Disponibile";
        }else{
            kcString = kc.toString();
        }
        holder.cal.setText("Kcal:\t"+kcString);
        holder.ingredients.setText(ingredients);
        holder.quantities.setText( quantities );
        holder.href.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(rec.getHref()));
                ContextCompat.startActivity(v.getContext(), browserIntent,null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.recipes.size();
    }




}
