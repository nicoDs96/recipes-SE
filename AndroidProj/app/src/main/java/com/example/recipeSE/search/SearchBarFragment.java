package com.example.recipeSE.search;

import android.content.Intent;
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
import com.example.recipeSE.search.utils.SharedViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class SearchBarFragment extends Fragment {
    private Button mSearchButton;
    private TextInputEditText mTextInput;
    private SharedViewModel model;
    private ProgressBar pBar;
    private Observer<String> errorObserver;
    private Observer<List<Recipe>> queryObserver;


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

                //Stop loading animation
                hideProgressBar();
                /*switch frame [load the result fragment]*/
                DisplayRecipesFragment nextFrag= new DisplayRecipesFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.search_activity, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();

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

                model.getRecipes(query).observe(getViewLifecycleOwnerLiveData().getValue(), queryObserver);
            }
        });

        //observe the status and display error if something goes wrong
        model.status.observe(getViewLifecycleOwner(),errorObserver );

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
}
