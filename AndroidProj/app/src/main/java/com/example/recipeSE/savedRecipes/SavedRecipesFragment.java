package com.example.recipeSE.savedRecipes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipeSE.R;
import com.example.recipeSE.search.utils.Recipe;
import com.example.recipeSE.search.utils.SearchResultsAdapter;

import java.util.LinkedList;
import java.util.List;


public class SavedRecipesFragment extends Fragment {

    private SavedRecipesViewModel mSavedRecipesVM;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSavedRecipesVM = new ViewModelProvider(getActivity()).get(SavedRecipesViewModel.class);
        mRecyclerView = view.findViewById(R.id.saved_recipes_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final SearchResultsAdapter adapter = new SearchResultsAdapter(new LinkedList<Recipe>());
        mRecyclerView.setAdapter(adapter);

        mSavedRecipesVM.getAllSavedrecipe().observe(getActivity(), new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {

                // update the adapter and notify

            }
        });


    }
}
