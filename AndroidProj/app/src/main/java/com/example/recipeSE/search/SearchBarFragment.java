package com.example.recipeSE.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.recipeSE.R;
import com.example.recipeSE.login.MainActivity;
import com.example.recipeSE.search.utils.Recipe;
import com.example.recipeSE.search.utils.RecipeDeserializer;
import com.example.recipeSE.search.utils.SharedViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.WorkInfo;

public class SearchBarFragment extends Fragment {
    private Button mSearchButton;
    private TextInputEditText mTextInput;
    private SharedViewModel model;
    private ProgressBar pBar;
    private Observer<String> errorObserver;
    private Observer<List<Recipe>> queryObserver;
    private Boolean submitted;


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
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        return inflater.inflate(R.layout.fragment_search_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.submitted = false;
        this.mSearchButton = (Button) view.findViewById(R.id.search);
        this.mTextInput = (TextInputEditText) view.findViewById(R.id.materialInputText);
        this.pBar = (ProgressBar) view.findViewById(R.id.searchBarProgress);
        hideProgressBar();
        this.model = new ViewModelProvider( requireActivity() ).get(SharedViewModel.class);

        //define UX behaviour when query arrives
        queryObserver = new Observer<List<Recipe>>() {
            //perform the query and when results are available switch fragment to display result fragment
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if(recipes != null && submitted) {
                    //Stop loading animation
                    hideProgressBar();
                    /*switch frame [load the result fragment]*/
                    DisplayRecipesFragment nextFrag = new DisplayRecipesFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.search_activity, nextFrag, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                }

            }
        };

        //if there is an exception display an error message
        errorObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable String status) {
                Log.d("Status Observer","Called, Status: "+status);
                if (status.equals("FAIL")){
                    String errorMsg="Something went wrong, check your connection and retry.";
                    Toast.makeText(getContext(),errorMsg,Toast.LENGTH_LONG).show();

                    hideProgressBar();


                }
            }
        };

        this.mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Start loading animation*/
                showProgressBar();
                //get the query
                String query = mTextInput.getText().toString();
                /*TODO: parse and sanitize query*/
                //perform the query and when results are available switch fragment to display result fragment
                //select activity as method to prevent crash [instead of standard getViewLifecycleOwner()]
                model.performQuery(query);//request async data
                submitted=true;
            }
        });
        //observe data to update view when loaded
        model.getRecipes().observe(getViewLifecycleOwner(), queryObserver);
        //observe the status and display error if something goes wrong
        model.status.observe(getViewLifecycleOwner(),errorObserver );

        // Show work [query] status
        model.getOutputWorkInfo().observe(getViewLifecycleOwner(), listOfWorkInfos -> {

            // If there are no matching work info, do nothing
            if (listOfWorkInfos == null || listOfWorkInfos.isEmpty()) {
                return;
            }

            // We only care about the first (and only) work.
            WorkInfo workInfo = listOfWorkInfos.get(0);

            boolean finished = workInfo.getState().isFinished();
            if (!finished) {
                Log.d("ShareVM","not finished");
            } else {
                Log.d("ShareVM","finished");
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
        mSearchButton.setVisibility(View.GONE);
    }
    private void hideProgressBar(){
        pBar.setVisibility(View.GONE);
        mSearchButton.setVisibility(View.VISIBLE);
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

    public static List<Recipe> resultStringToList(String res){

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Recipe.class, new RecipeDeserializer());

        //create an instance of gson able to correctly parse the server response
        Gson customGson = gsonBuilder.create();
        //set the response type as list of recipes
        Type listRecipesType = new TypeToken<List<Recipe>>() {}.getType();
        //parse data
        List<Recipe> list;
        list = customGson.fromJson(res, listRecipesType);
        return list;

    }


}
