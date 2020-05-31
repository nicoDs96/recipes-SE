package com.example.recipeSE.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.recipeSE.R;
import com.example.recipeSE.savedRecipes.utils.SavedRecipesViewModel;
import com.example.recipeSE.search.utils.Recipe;
import com.example.recipeSE.search.utils.SearchResultsAdapter;
import com.example.recipeSE.search.utils.SharedViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.WorkInfo;

public class DisplayRecipesFragment extends Fragment {
    private SharedViewModel model;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar pBar;
    private Observer<String> errorObserver;
    private SavedRecipesViewModel mSavedRecipesViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Customize Andorid backbutton action
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                SearchBarFragment nextFrag= new SearchBarFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.search_activity, nextFrag, "searchBarFragment")
                        .addToBackStack(null)
                        .commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        //create a view and attach to it a layout
        return inflater.inflate(R.layout.fragment_display_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //use the view with layout created by onCreateView(...)
        model = new ViewModelProvider( requireActivity() ).get(SharedViewModel.class);
        mSavedRecipesViewModel = new ViewModelProvider( requireActivity() ).get(SavedRecipesViewModel.class);
        //init recycler view
        recyclerView = (RecyclerView) getView().findViewById(R.id.search_result_recycler_view);
        //to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        pBar = getView().findViewById(R.id.displayProgressBar);

        //create the adapter and attach it to RV when the data arrives from the ViewModel (hence from API)
        model.getRecipes().observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                Log.d("DisplayRecipesFrag Obs","Called!");

                //get the current query and display it inside the searchbar of this fragment
                TextInputEditText fragmentSearchBar=(TextInputEditText) getView().findViewById(R.id.searchbarRecView);
                fragmentSearchBar.setText(model.getCurrentQuery());

                if(recipes!= null && recipes.size()<1){
                    //Disaply no resoult found text
                    getView().findViewById(R.id.noResultErorrTextView).setVisibility(View.VISIBLE);
                    //  hide the progress bar
                    hideProgressBar();
                }else{
                    getView().findViewById(R.id.noResultErorrTextView).setVisibility(View.GONE);

                }

                //configure recycler view adapter with the observed object from the viewmodel
                mAdapter = new SearchResultsAdapter(recipes,mSavedRecipesViewModel,false);
                recyclerView.setAdapter(mAdapter);

                //remove the progress bar when the recycler view has finished to load all elements
                recyclerView.getViewTreeObserver()
                        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                //At this point the layout is complete and the
                                //dimensions of recyclerView and any child views are known.
                                //Remove listener after changed RecyclerView's height to prevent infinite loop
                                hideProgressBar();
                                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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
                    showProgressBar();
                }
                model.performQuery(inputQuery);
            }
        });

        //define the error observer
        errorObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable String status) {
                Log.d("Status Observer","Called, Status: "+status);
                if (status.equals("FAIL")){
                    //display error message
                    String errorMsg="Something went wrong, check your connection and retry.";
                    Toast.makeText(getContext(),errorMsg,Toast.LENGTH_LONG).show();

                    //Stop loading animation
                    hideProgressBar();

                }
            }
        };
        //Observe status and display errors eventually
        model.status.observe(getViewLifecycleOwner(),errorObserver );

        //observe background query status
        model.getOutputWorkInfo().observe(getViewLifecycleOwner(), listOfWorkInfos -> {

            // If there are no matching work info, do nothing
            if (listOfWorkInfos == null || listOfWorkInfos.isEmpty()) {
                return;
            }

            // We only care about the first (and only) work.
            WorkInfo workInfo = listOfWorkInfos.get(0);

            boolean finished = workInfo.getState().isFinished();
            if (!finished) {
                Log.d("SharedVM","not finished");
            } else {
                Log.d("SharedVM","finished");
                Data outputData = workInfo.getOutputData();

                String key  = outputData.getString("query_result");
                SharedPreferences prefs = getContext().getSharedPreferences(getContext().getString(R.string.preference_file_key_query), Context.MODE_PRIVATE );
                String result  = prefs.getString(key,null);
                if(result!=null) {
                    List<Recipe> res = SearchBarFragment.resultStringToList(result);
                    model.setresult(res);

                    //once the result in in memory delete it from persistence
                    SharedPreferences.Editor editor = getContext()
                            .getSharedPreferences(getContext().getString(R.string.preference_file_key_query), Context.MODE_PRIVATE )
                            .edit();
                    //editor.clear();
                    editor.remove("query_result");
                    editor.apply();

                }else{
                    Log.wtf(this.getClass().getName(),"No result present");
                }

            }
        });



    }

    private void showProgressBar(){
        pBar.setVisibility(View.VISIBLE);
    }
    private void hideProgressBar(){
        pBar.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        model.status.removeObserver(errorObserver);
        super.onPause();

    }

    @Override
    public void onResume() {
        model.status.observe( getViewLifecycleOwner(), errorObserver);
        super.onResume();
    }

}
