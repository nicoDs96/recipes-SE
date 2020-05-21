package com.example.recipeSE.login;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.recipeSE.R;
import com.example.recipeSE.search.SearchActivity;
import com.example.recipeSE.shoppinglist.Shoppinglist;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //TODO: c'è un modo di non ripetere questo pezzo di codice per ogni activity ma linkare il codice alla resource xml???
        DrawerLayout mDrawerLayout = (DrawerLayout)findViewById(R.id.drawernavbar) ;
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_search) {
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.menu_savedrecipes)
                {
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class); //TODO: inserire classe savedrecipes
                    startActivity(intent);
                }
                else if (id == R.id.menu_shoppinglist)
                {
                    Intent intent = new Intent(getApplicationContext(), Shoppinglist.class);
                    startActivity(intent);
                }
                else if (id == R.id.menu_settings)
                {
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class); //TODO: inserire classe settings
                    startActivity(intent);
                }
                return true;
            }
        } );
    }

    public void logout(View view) {
        // do the log out removing session -> come back to the Login interface

        // Check if logged with facebook
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn){

            LoginManager.getInstance().logOut();

        }

        moveToMain();


        SessionManagment sessionManagment = new SessionManagment(UserActivity.this);
        sessionManagment.removeSession();

        //moveToMain();

    }

    private void moveToMain() {
        Intent intent  = new Intent(UserActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
