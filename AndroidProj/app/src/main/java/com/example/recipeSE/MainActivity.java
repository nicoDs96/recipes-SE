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
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

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
    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";
    private static final String AUTH_TYPE = "request";
    private final String TAG = this.getClass().getName();
    private CallbackManager callbackManager;
    private SharedPreferences prefs;
    private String mAuthProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuthProvider="fb";
        prefs = getSharedPreferences("sessionuser", Context.MODE_PRIVATE );
        Log.i(TAG,"SESSIONKEY: " + prefs.getString("sessionkey", null));
        if(prefs.getString("sessionkey", null) != null){
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        }

        //Facebook
        loginF = (LoginButton) findViewById(R.id.login_fb);
        loginF.setPermissions(Arrays.asList(PUBLIC_PROFILE, EMAIL));
        //loginF.setAuthType(AUTH_TYPE);
        // If using in a fragment
        //loginButton.setFragment(this);



        callbackManager = CallbackManager.Factory.create();

        loginF.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
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
                            Log.i(TAG,"SESSIONKEY: " + prefs.getString("sessionkey", null));

                        }catch (JSONException e){

                        }

                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivity(intent);

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,middle_name,last_name,email,name_format,picture");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.i(TAG,"request cancelled");
                setResult(RESULT_CANCELED);
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG,"facebook login Error");
                Log.i(TAG, Log.getStackTraceString(error));
            }
        });

        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuthProvider="gl";
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
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

        switch (mAuthProvider){
            case "fb":
                callbackManager.onActivityResult(requestCode , resultCode, data);
                break;

            case "gl":
                // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
                if (requestCode == 1) {
                    // The Task returned from this call is always completed, no need to attach
                    // a listener.
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                }
                break;

            default:
                new GeneralErrorDialog().showNow(getSupportFragmentManager(), TAG);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.i("handleSignInResultGOOGL","account ottenuto");
            // Signed in successfully
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.putString("sessionkey", account.getEmail());
            editor.apply();
            Log.i(TAG,"SESSIONKEY: " + prefs.getString("sessionkey", null));
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            e.printStackTrace();
            //Log.d(TAG, );
        }
    }

    private void logGraphInfos(JSONObject object){
        try {
            String email = object.getString("email");
            String id = object.getString("id");
            String first_name = object.getString("first_name");
            String last_name = object.getString("last_name");
            Log.d("GraphRESPONSE", "Email: "+email+"\nid: "+id+"\nFirstName: "+first_name
                    +"\nLastName: "+last_name);
        } catch (JSONException e) {
            Log.e("GraphRESPONSE catch",e.getMessage());
            Log.e("GraphRESPONSE json resp",object.toString());
        }

    }

}
