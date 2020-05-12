package com.example.login;

import com.google.gson.annotations.SerializedName;

public class LoginResult {

    private String name;


    // SerName mi serve quando la mia variabile ha un nome diverso
   // @SerializedName("email")
    private  String email;
    

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }


}
