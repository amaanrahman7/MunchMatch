package com.group6.tinderforfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    Button login, guest, signup;
    EditText username, password;
    TextView messagetext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login = (Button)findViewById(R.id.button);
        guest = (Button)findViewById(R.id.button2);
        signup = (Button)findViewById(R.id.button3);

        username = (EditText)findViewById(R.id.editText);
        password = (EditText)findViewById(R.id.editText2);

        messagetext = (TextView)findViewById(R.id.textView);
        messagetext.setVisibility(View.GONE);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

              // Intent intent = new Intent(v.getContext(), MainActivity.class);
                Intent intent = new Intent(v.getContext(), RestaurantSwipeActivity.class);
                startActivity(intent);
            }
        });

        guest.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View v){

               //Intent intent = new Intent(v.getContext(), MainActivity.class);
               Intent intent = new Intent(v.getContext(), FoodSwipeActivity.class);
                startActivity(intent);
            }
        });

    }
}
