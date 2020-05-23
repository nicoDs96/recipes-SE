package com.example.recipeSE.search.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView cal;
        public LinearLayout ingredientsQuantityContainer; //?
        public Button href;
        public Context context;

        public RecipeViewHolder(@NonNull View itemView, TextView title, TextView cal,
                                LinearLayout ingredientsQuantityContainer, Button href, Context context) {
            super(itemView);
            this.title = title;
            this.cal = cal;
            this.ingredientsQuantityContainer = ingredientsQuantityContainer;
            this.href = href;
            this.context = context; //needed to add element to the parent view programmatically
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
                .inflate(R.layout.view_search_results_card, parent, false);

        //parent.getContext

        RecipeViewHolder vh = new RecipeViewHolder(v,
                (TextView) v.findViewById(R.id.Title),
                (TextView) v.findViewById(R.id.cal),
                (LinearLayout) v.findViewById(R.id.ing_quantity_container),
                (Button) v.findViewById(R.id.go2site),
                parent.getContext() );

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Recipe rec = recipes.get(position);

        for (Map.Entry e : rec.getIngredient_quantity().entrySet()){
            //create a linear layout containing two text views, one for for ingredient one for quantity
            holder.ingredientsQuantityContainer.addView(
                    createIngredientQuantityLayout(e.getKey().toString(),e.getValue().toString(),holder.context)
            );
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

    private LinearLayout createIngredientQuantityLayout(String ingredient, String quantity, Context context){

        LinearLayout container = new LinearLayout(context);
        //define parameters
        container.setLayoutParams( new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
                ));
        container.setOrientation(LinearLayout.HORIZONTAL);

        //define text views
        TextView ingredientTV = new TextView(context);
        ingredientTV.setText(ingredient);
        LinearLayout.LayoutParams textPar = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        textPar.gravity= Gravity.LEFT;
        ingredientTV.setLayoutParams(textPar);

        TextView quantityTV = new TextView(context);
        quantityTV.setText(quantity);
        LinearLayout.LayoutParams quantityPar = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        quantityPar.gravity = Gravity.RIGHT;
        quantityTV.setLayoutParams(quantityPar);
        quantityTV.setGravity(Gravity.RIGHT);

        container.addView(ingredientTV);
        container.addView(quantityTV);

        return container;


    }



}
