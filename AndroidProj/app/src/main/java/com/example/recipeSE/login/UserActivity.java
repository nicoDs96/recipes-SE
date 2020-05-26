package com.example.recipeSE.login;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.recipeSE.R;
import com.example.recipeSE.ShowMarkets;
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

        //TODO: copiare anche su altre classi della navbar
        findViewById(R.id.showmenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = findViewById(R.id.drawernavbar);
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        NavigationView navigationView =  findViewById(R.id.navigation);
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
                else if (id == R.id.menu_map)
                {
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
