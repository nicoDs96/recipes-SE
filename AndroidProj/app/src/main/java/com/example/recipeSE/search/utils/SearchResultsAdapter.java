package com.example.recipeSE.search.utils;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.recipeSE.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    //TODO: replace textview vith cards with cards
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public RecipeViewHolder(TextView v) {
            super(v);
            textView = v;
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
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_search_text, parent, false);

        RecipeViewHolder vh = new RecipeViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText( recipes.get(position).toString() );
    }

    @Override
    public int getItemCount() {
        return this.recipes.size();
    }




}
