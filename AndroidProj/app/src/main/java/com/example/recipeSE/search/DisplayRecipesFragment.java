package com.example.recipeSE.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.recipeSE.R;
import com.example.recipeSE.search.utils.Recipe;
import com.example.recipeSE.search.utils.SearchResultsAdapter;
import com.example.recipeSE.search.utils.SharedViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DisplayRecipesFragment extends Fragment {
    private SharedViewModel model;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //create a view and attach to it a layout
        return inflater.inflate(R.layout.fragment_display_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //use the view with layout created by onCreateView(...)
        this.model = new ViewModelProvider( requireActivity() ).get(SharedViewModel.class);
        recyclerView = (RecyclerView) getView().findViewById(R.id.search_result_recycler_view);

        //to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        //mAdapter = new SearchResultsAdapter(myDataset);
        //recyclerView.setAdapter(mAdapter);

        //create the adapter and attachit to RV when the data arrives from the API
        model.getRecipes().observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                Log.d("DisplayRecipesFrag Obs","Called!");
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Search");

                //get the current query and display it inside the searchbar of this fragment
                TextInputEditText fragmentSearchBar=(TextInputEditText) getView().findViewById(R.id.searchbarRecView);
                fragmentSearchBar.setText(model.getCurrentQuery());

                //configure recycler view adapter with the observed object from the viewmodel
                mAdapter = new SearchResultsAdapter(recipes);
                recyclerView.setAdapter(mAdapter);
            }
        });

        getView().findViewById(R.id.searchButtonRecView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedViewModel m =  new ViewModelProvider( requireActivity() ).get(SharedViewModel.class);
                TextInputEditText fragmentSearchBar = (TextInputEditText) (TextInputEditText) getView().findViewById(R.id.searchbarRecView);
                m.getRecipes(fragmentSearchBar.getText().toString());
            }
        });


    }

}
