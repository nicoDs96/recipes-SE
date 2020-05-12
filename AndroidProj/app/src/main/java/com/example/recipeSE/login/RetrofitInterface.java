package com.example.recipeSE.login;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/recipeSE")
    Call<LoginResult> executeLogin(@Body HashMap<String, String> map);


    @POST("/signup")
    Call<Void> executeSignup(@Body HashMap<String, String> map);

}