package com.example.recipeSE.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.example.recipeSE.MainActivity;
import com.example.recipeSE.R;
import com.example.recipeSE.ShowMarkets;
import com.example.recipeSE.savedRecipes.SavedRecipesFragment;
import com.example.recipeSE.search.utils.SharedViewModel;
import com.example.recipeSE.shoppinglist.ShoppigListFragment;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


public class SearchActivity extends AppCompatActivity {

    private SharedViewModel model;
    private MaterialToolbar mToolbar;
    private DrawerLayout mDrawer;
    private String TAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getLocalClassName();
        setContentView(R.layout.activity_search);

        //From map
        Intent intent = getIntent();
        if(intent.getStringExtra("frommap")!=null && intent.getStringExtra("frommap").equals("savedrecipes")){
            SavedRecipesFragment nextFrag= new SavedRecipesFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.search_activity, nextFrag, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
        }
        if(intent.getStringExtra("frommap")!=null && intent.getStringExtra("frommap").equals("shoppinglist")){
            ShoppigListFragment nextFrag= new ShoppigListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.search_activity, nextFrag, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
        }

        //Just in case it is needed
        this.model = new ViewModelProvider(this ).get(SharedViewModel.class);
        //get reference to sidebar
        mDrawer =  findViewById(R.id.drawernavbar);
        //Add custom toolbar
        mToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        mToolbar.setTitle(Html.fromHtml("<font color='#ffffff'>Tuna</font>"));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });

        //Set listeners for sidebar
        NavigationView navigationView =  findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_search) {
                    SearchBarFragment nextFrag= new SearchBarFragment();
                    mDrawer.closeDrawer(Gravity.LEFT); //close the sidebar
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.search_activity, nextFrag, "SearchBarFragment")
                            .addToBackStack(null)
                            .commit();
                }
                else if (id == R.id.menu_savedrecipes)
                {
                    SavedRecipesFragment nextFrag= new SavedRecipesFragment();
                    mDrawer.closeDrawer(Gravity.LEFT); //close the sidebar
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.search_activity, nextFrag, "SavedRecipesFragment")
                            .addToBackStack(null)
                            .commit();

                }
                else if (id == R.id.menu_shoppinglist)
                {
                    ShoppigListFragment nextFrag= new ShoppigListFragment();
                    mDrawer.closeDrawer(Gravity.LEFT); //close the sidebar
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.search_activity, nextFrag, "ShoppigListFragment")
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

                    //TODO: cancellare variabile sessione e fare logout e riportare al fragment di login
                    //If facebook
                    LoginManager.getInstance().logOut();

                    //if google
                    GoogleSignInOptions gso = new GoogleSignInOptions.
                            Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                            build();

                    GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(getApplicationContext(),gso);
                    googleSignInClient.signOut();

                    SharedPreferences prefs = getSharedPreferences("sessionuser", Context.MODE_PRIVATE );
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.putString("sessionkey", null);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);


                }
                return true;
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(getSupportFragmentManager().getBackStackEntryCount() >0){
            //save the current fragment if any
            String currFrag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            outState.putString("fragment_displayed",currFrag);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String fragTag = savedInstanceState.getString("fragment_displayed");
        if (fragTag !=null){
            Fragment nextFrag;
            switch (fragTag){
                case "SearchBarFragment":
                    nextFrag= new SearchBarFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.search_activity, nextFrag, "SearchBarFragment")
                            .addToBackStack(null)
                            .commit();
                    break;
                case "SavedRecipesFragment":
                    nextFrag= new SavedRecipesFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.search_activity, nextFrag, "SavedRecipesFragment")
                            .addToBackStack(null)
                            .commit();
                    break;
                case "ShoppigListFragment":
                    nextFrag= new ShoppigListFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.search_activity, nextFrag, "ShoppigListFragment")
                            .addToBackStack(null)
                            .commit();
                    break;
                default:
                    Log.e(TAG,"Not recoginzed fragment");
            }
        }
    }
}
