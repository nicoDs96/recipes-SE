package com.example.recipeSE.search.utils;

public class BodyQuery {
    private String [] ingredients;

    public BodyQuery(String query) {
        this.ingredients = query.split(" ");
    }

}
