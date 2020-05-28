package com.example.recipeSE.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.example.recipeSE.R;
import com.example.recipeSE.ShowMarkets;
import com.example.recipeSE.search.SearchActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class UserActivity extends AppCompatActivity {

    private MaterialToolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        mToolbar.setTitle(Html.fromHtml("<font color='#ffffff'>Tuna</font>"));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
