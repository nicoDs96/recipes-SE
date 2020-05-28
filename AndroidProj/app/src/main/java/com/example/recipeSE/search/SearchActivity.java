package com.example.recipeSE.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.example.recipeSE.R;
import com.example.recipeSE.ShowMarkets;
import com.example.recipeSE.savedRecipes.SavedRecipesFragment;
import com.example.recipeSE.savedRecipes.SavedRecipesViewModel;
import com.example.recipeSE.search.utils.SharedViewModel;
import com.example.recipeSE.shoppinglist.ShoppigListFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;


public class SearchActivity extends AppCompatActivity {

    private SharedViewModel model;
    private MaterialToolbar mToolbar;
    private DrawerLayout mDrawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Just in case it is needed TODO: remove it
        this.model = new ViewModelProvider(this ).get(SharedViewModel.class);
        //get reference to sidebar
        mDrawer =  findViewById(R.id.drawernavbar);
        //Add custom toolbar
        mToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        mToolbar.setTitle(Html.fromHtml("<font color='#ffffff'>Tunagit </font>"));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });

        //Set listeners for sidebar
        //TODO: replace intent with fragment switch
        NavigationView navigationView =  findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_search) {
                    SearchBarFragment nextFrag= new SearchBarFragment();
                    mDrawer.closeDrawer(Gravity.LEFT); //close the sidebar
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.search_activity, nextFrag, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                }
                else if (id == R.id.menu_savedrecipes)
                {
                    SavedRecipesFragment nextFrag= new SavedRecipesFragment();
                    mDrawer.closeDrawer(Gravity.LEFT); //close the sidebar
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.search_activity, nextFrag, "findThisFragment")
                            .addToBackStack(null)
                            .commit();

                }
                else if (id == R.id.menu_shoppinglist)
                {
                    ShoppigListFragment nextFrag= new ShoppigListFragment();
                    mDrawer.closeDrawer(Gravity.LEFT); //close the sidebar
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.search_activity, nextFrag, "findThisFragment")
                            .addToBackStack(null)
                            .commit();

                }
                else if (id == R.id.menu_map)
                {
                    mDrawer.closeDrawer(Gravity.LEFT); //close the sidebar
                    Intent intent = new Intent(getApplicationContext(), ShowMarkets.class);
                    startActivity(intent);

                }
                else if (id == R.id.menu_logout)
                {
                    //TODO: cancellare variabile sessione e fare logout
                }
                return true;
            }
        } );

    }



}
