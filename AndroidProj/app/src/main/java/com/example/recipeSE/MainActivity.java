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

import java.util.Arrays;
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
    private CallbackManager mCallbackManager;
    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";
    private static final String AUTH_TYPE = "rerequest";
    private final String TAG = this.getClass().getName();

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

        loginF = (LoginButton) findViewById(R.id.login_fb);
        loginF.setPermissions(Arrays.asList(PUBLIC_PROFILE, EMAIL));
        loginF.setAuthType(AUTH_TYPE);

        callbackManager = CallbackManager.Factory.create();

        loginF.registerCallback(callbackManager, new FacebookCallback<com.facebook.login.LoginResult>() {
            @Override
            public void onSuccess(com.facebook.login.LoginResult loginResult) {
                Log.i(TAG,"inside on success");
                String accesstoken = loginResult.getAccessToken().getToken();
                setResult(RESULT_OK);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken() , new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i(TAG,"Graph request completed!");
                        logGraphInfos(object);
                        try {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.clear();
                            editor.putString("sessionkey", object.getString("email"));
                            editor.apply();

                        }catch (JSONException e){
                            Log.i("AAAAAAAAA","dentro il catch");

                        }

                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivity(intent);

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,middle_name,last_name,email,name_format,picture");
                request.setParameters(parameters);
                request.executeAsync();
                //TODO: vedere se serve intent anche qui

            }

            @Override
            public void onCancel() {
                Log.i(TAG,"request cancelled");
                setResult(RESULT_CANCELED);
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG,"facebook login Error");
                Log.getStackTraceString(error);
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

    private void logGraphInfos(JSONObject object){
        try {
            String email = object.getString("email");
            String id = object.getString("id");
            String first_name = object.getString("first_name");
            //String middle_name = object.getString("middle_name");
            String last_name = object.getString("last_name");
            Log.d("GraphRESPONSE", "Email: "+email+"\nid: "+id+"\nFirstName: "+first_name
                    +"\nLastName: "+last_name);
        } catch (JSONException e) {
            Log.e("GraphRESPONSE catch",e.getMessage());
            Log.e("GraphRESPONSE json resp",object.toString());
        }

    }

}
