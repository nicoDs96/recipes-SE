package com.example.login;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagment {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String SESSION_KEY = "session_user";


            public SessionManagment(Context context) {
                sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();

            }

            public void saveSession(User user){

                // save session of user when is logged
               String name = user.getName();
               String email = user.getEmail();

               editor.putString(SESSION_KEY,email).commit();

                }


             public String getSession(){

               // return user whose session is saved

              return sharedPreferences.getString(SESSION_KEY,"none");

                }




             public  void    removeSession(){

                editor.putString(SESSION_KEY,"").commit();

             }
}
