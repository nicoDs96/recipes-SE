package com.example.recipeSE.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.recipeSE.R;
import com.example.recipeSE.search.utils.Recipe;
import com.example.recipeSE.search.utils.SharedViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.mSearchButton = (Button) getView().findViewById(R.id.search);
        this.mTextInput = (TextInputEditText) getView().findViewById(R.id.materialInputText);
        this.pBar = (ProgressBar) getView().findViewById(R.id.searchBarProgress);
        this.model = new ViewModelProvider( requireActivity() ).get(SharedViewModel.class);

        this.mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Start loading animation*/

                getView().findViewById(R.id.searchBarProgress).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.search).setVisibility(View.GONE);


                String query = mTextInput.getText().toString();
                /*TODO: parse and sanitize query*/



                //select activity as method to prevent crash [instead of standard getViewLifecycleOwner()]
                model.getRecipes(query).observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
                            @Override
                            public void onChanged(@Nullable List<Recipe> recipes) {

                                //Stop loading animation
                                getView().findViewById(R.id.searchBarProgress).setVisibility(View.GONE);
                                getView().findViewById(R.id.search).setVisibility(View.VISIBLE);
                                /*switch frame [load the result fragment] */
                                DisplayRecipesFragment nextFrag= new DisplayRecipesFragment();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.search_activity, nextFrag, "findThisFragment")
                                        .addToBackStack(null)
                                        .commit();
                            }
                        });

            }
        });



    }
}
