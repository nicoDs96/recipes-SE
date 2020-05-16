package com.example.recipeSE.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.recipeSE.R;
import com.example.recipeSE.search.utils.Recipe;
import com.example.recipeSE.search.utils.SharedViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class SearchBarFragment extends Fragment {
    private Button mSearchButton;
    private EditText mTextInput;
    private SharedViewModel model;


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
        this.mTextInput = (EditText) getView().findViewById(R.id.editText);

        this.model = new ViewModelProvider( requireActivity() ).get(SharedViewModel.class);

        this.mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* TODO: load the result fragment */
                String query = mTextInput.getText().toString();
                /*TODO: parse and sanitize query*/

                /*TODO: Start loading animation*/

                model.getRecipes(query).observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
                            @Override
                            public void onChanged(@Nullable List<Recipe> recipes) {
                                Log.d( "Debug Recipe", recipes.get(0).toString());
                                /*switch frame*/
                            }
                        });
                /*1. start the loading animation inside this fragment.
                * 2. implement observer over SharedViewModel (HERE) st. when data arrives I can switch to
                *    DisplayRecipesFragment
                * 3. Implement a recycler view inside the DisplayrecipesFragment displaying the result
                *   */
            }
        });



    }
}
