package com.example.recipeSE.search.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipeSE.R;
import com.example.recipeSE.savedRecipes.utils.SavedRecipesViewModel;
import com.google.android.material.card.MaterialCardView;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;
    private SavedRecipesViewModel mSavedRecipesViewModel;
    private Boolean showDeleteButton;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView cal;
        public LinearLayout ingredientsQuantityContainer;
        public Button href;
        public Button save;
        public Button delete;
        public Context context;


        //overload method
        public RecipeViewHolder(@NonNull View itemView, TextView title, TextView cal,
                                LinearLayout ingredientsQuantityContainer, Button href,Button save,
                                Button delete, Context context) {
            super(itemView);
            this.title = title;
            this.cal = cal;
            this.ingredientsQuantityContainer = ingredientsQuantityContainer;
            this.href = href;
            this.save = save;
            this.delete = delete;
            this.context = context; //needed to add element to the parent view programmatically
        }
    }


    public SearchResultsAdapter(List<Recipe> recipes, SavedRecipesViewModel savedRecipesViewModel,
                                Boolean showDeleteButton) {
        this.recipes=recipes;
        this.mSavedRecipesViewModel=savedRecipesViewModel;
        this.showDeleteButton = showDeleteButton;
    }

    @NonNull
    @Override
    // Create new views (invoked by the layout manager)
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view: a card containing all details
        MaterialCardView v = (MaterialCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_search_results_card, parent, false);

        //parent.getContext

        RecipeViewHolder vh = new RecipeViewHolder(v,
                (TextView) v.findViewById(R.id.Title),
                (TextView) v.findViewById(R.id.cal),
                (LinearLayout) v.findViewById(R.id.ing_quantity_container),
                (Button) v.findViewById(R.id.go2site),
                (Button) v.findViewById(R.id.add_2_fav),
                (Button) v.findViewById(R.id.delete),
                parent.getContext() );

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Recipe rec = recipes.get(position);

        for (Map.Entry e : rec.getIngredient_quantity().entrySet()) {
            //create a linear layout containing two text views, one for for ingredient one for quantity
            holder.ingredientsQuantityContainer.addView(
                    createIngredientQuantityLayout(e.getKey().toString(), e.getValue().toString(), holder.context)
            );
        }
        //set card title
        holder.title.setText(rec.getTitle());
        //set calories if available
        Integer kc = rec.getCalories();
        String kcString = "";
        if (kc == null) {
            kcString = "Non Disponibile";
        } else {
            kcString = kc.toString();
        }
        holder.cal.setText("Kcal:\t" + kcString);

        //add listener to go to the website
        holder.href.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(rec.getHref()));
                ContextCompat.startActivity(v.getContext(), browserIntent, null);
            }
        });
        // add listener to save the recipe
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSavedRecipesViewModel.save(rec);
                Toast.makeText(holder.context,rec.getTitle()+" Saved",Toast.LENGTH_SHORT).show();
            }
        });

        //add listener to delete button.
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSavedRecipesViewModel.delete(rec);
                notifyItemRemoved(position);
            }
        });
        //we show delete button only into Saved recipes fragment
        // while we show save button only into DisplayrecipesFragment
        if (this.showDeleteButton) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.save.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return this.recipes.size();
    }

    /**
     * @param ingredient the ingredient name
     * @param quantity the quantity associated to @param ingredient
     * @param context the contex of this View
     * @return a linearLayout containing two textView and an onclick listener associated to the
     *   returned layout. Whenever a user click on a displayed line (on the returned linearLayout)
     *   they add the ingredient to the shopping list.
     */
    private LinearLayout createIngredientQuantityLayout(String ingredient, String quantity, final Context context){

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

        container.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                addToShoppinglist(context, (LinearLayout) v);
            }
        });
        return container;


    }

    //called when clicking on a row of the card to add the respective ingredient to the shoppinglist
    private void addToShoppinglist(Context c, LinearLayout layout){
        if(layout.getChildCount() >0){
            TextView ingredient = (TextView) layout.getChildAt(0);
            appendToSharedPreference(c, ingredient.getText().toString());
            Toast.makeText(c,ingredient.getText().toString()+" added to shopping list",Toast.LENGTH_SHORT).show();
        }
    }
    //utility called to add a new value into the shared preference persistent storage
    private void appendToSharedPreference(Context c, String newIngredient){
        String key = "ingredients";
        //get a file with the ingredient list
        SharedPreferences sharedPref = c.getSharedPreferences(
                c.getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //if it is not empty list a set of string will be returned else an empty set (2nd param of
        // getStringSet method)
        HashSet<String> list = (HashSet<String>) sharedPref.getStringSet(key, new HashSet<String>());
        list.add(newIngredient);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putStringSet(key, list);
        editor.apply();

    }


}
