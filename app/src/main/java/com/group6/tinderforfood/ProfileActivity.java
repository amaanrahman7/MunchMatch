package com.group6.tinderforfood;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilelayout);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //shows back button

    }
    @Override
    public boolean onSupportNavigateUp(){ //this is for the back button
        finish(); //this sets the back button command to go back one screen
        return true;
    }
}
