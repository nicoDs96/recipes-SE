package com.example.recipeSE.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.recipeSE.R;
import com.example.recipeSE.search.utils.Recipe;
import com.example.recipeSE.search.utils.SearchResultsAdapter;
import com.example.recipeSE.search.utils.SharedViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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


        //create the adapter and attach it to RV when the data arrives from the API
        model.getRecipes().observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                Log.d("DisplayRecipesFrag Obs","Called!");
                //get the current query and display it inside the searchbar of this fragment
                TextInputEditText fragmentSearchBar=(TextInputEditText) getView().findViewById(R.id.searchbarRecView);
                fragmentSearchBar.setText(model.getCurrentQuery());

                if(recipes.size()<1){
                    //Disaply no resoult found text
                    getView().findViewById(R.id.noResultErorrTextView).setVisibility(View.VISIBLE);
                    //  hide the progress bar
                    getView().findViewById(R.id.displayProgressBar).setVisibility(View.GONE);
                }else{
                    getView().findViewById(R.id.noResultErorrTextView).setVisibility(View.GONE);

                }


                //configure recycler view adapter with the observed object from the viewmodel
                mAdapter = new SearchResultsAdapter(recipes);
                recyclerView.setAdapter(mAdapter);

                //remove the progress bar when the recycler view has finished to load all elements
                recyclerView.getViewTreeObserver()
                        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                //At this point the layout is complete and the
                                //dimensions of recyclerView and any child views are known.
                                //Remove listener after changed RecyclerView's height to prevent infinite loop
                                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                getView().findViewById(R.id.displayProgressBar).setVisibility(View.GONE);
                            }
                        });


            }
        });

        //provide search functionalities even in this view
        getView().findViewById(R.id.searchButtonRecView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //get the query and pass it to the viewmodel
                TextInputEditText fragmentSearchBar = (TextInputEditText)  getView().findViewById(R.id.searchbarRecView);
                String inputQuery = fragmentSearchBar.getText().toString();

                if(!inputQuery.equals(model.getCurrentQuery())){
                   //if the query are the same the ViewModel won't change its internal data -> no
                    // observer is notified -> The progress bar does not disappear.
                    // Make the progress bar visible iff a new (different from the prev.) query is inserted

                    //show progress bar waiting for the results
                    getView().findViewById(R.id.displayProgressBar).setVisibility(View.VISIBLE);
                }
                model.getRecipes(inputQuery);

            }
        });


    }

}
