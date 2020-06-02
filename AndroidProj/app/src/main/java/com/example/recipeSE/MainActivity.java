package com.example.recipeSE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipeSE.search.SearchActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private LoginButton loginF;
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("sessionuser", Context.MODE_PRIVATE );
        if(prefs.getString("email", null) != null){
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        }

        loginF= findViewById(R.id.login_fb);
        callbackManager = CallbackManager.Factory.create();
        loginF.registerCallback(callbackManager, new FacebookCallback<com.facebook.login.LoginResult>() {
            @Override
            public void onSuccess(com.facebook.login.LoginResult loginResult) {
            Log.i("AAAAAAAAA","dentro on success");
               String accesstoken = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken() , new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("AAAAAAAAA","dentro on completed");
                        try {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.clear();
                            editor.putString("sessionkey", object.getString("email"));
                            editor.apply();
                            Log.i("AAAAAAAAA","fatto tutto il try");
                        }catch (JSONException e){
                            Log.i("AAAAAAAAA","dentro il catch");
                            e.printStackTrace(); //TODO: gestire errore
                        }

                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivity(intent);


                    }
                });

                //TODO: vedere se serve intent anche qui

            }

            @Override
            public void onCancel() {
                Log.i("AAAAAAAAA","dentro on cancel"); //TODO:gestisci errore
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("AAAAAAAAA","dentro on error"); //TODO:gestisci errore

            }
        });



        //TODO: remove it when no more needed (clear app design is ready)
        findViewById(R.id.goto_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode , resultCode, data);
    }

}
