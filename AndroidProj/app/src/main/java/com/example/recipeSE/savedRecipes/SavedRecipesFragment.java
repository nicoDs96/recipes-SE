package com.example.recipeSE.savedRecipes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.recipeSE.R;
import com.example.recipeSE.savedRecipes.utils.SavedRecipesViewModel;
import com.example.recipeSE.search.utils.Recipe;
import com.example.recipeSE.search.utils.SearchResultsAdapter;

import java.util.HashMap;
import java.util.List;


public class SavedRecipesFragment extends Fragment {

    private SavedRecipesViewModel mSavedRecipesVM;
    private RecyclerView mRecyclerView;
    private TextView emptyResultText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSavedRecipesVM = new ViewModelProvider( requireActivity() ).get(SavedRecipesViewModel.class);

        emptyResultText= getView().findViewById(R.id.no_saved_recipe_text_view);


        mRecyclerView = view.findViewById(R.id.saved_recipes_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mSavedRecipesVM.getAllSavedrecipe().observe(getActivity(), new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                final SearchResultsAdapter adapter = new SearchResultsAdapter(
                        mSavedRecipesVM.getAllSavedrecipe().getValue(), mSavedRecipesVM ,true);
                mRecyclerView.setAdapter(adapter);

                if(recipes.size() == 0){
                    emptyResultText.setVisibility(View.VISIBLE);
                }else{
                    emptyResultText.setVisibility(View.GONE);
                }
            }
        });



    }
}
