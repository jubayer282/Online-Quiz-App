package com.jubayer.onlinequizeadmob;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Splash extends AppCompatActivity {
    //FirebaseAuth fAuth;
    TextView quiz;
    ProgressBar progressBarSp;

    FirebaseUser user;

    FirebaseAuth auth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().setTitle("Welcome to our app");

        //fAuth = FirebaseAuth.getInstance();
        quiz = findViewById(R.id.textView);
        quiz = findViewById(R.id.textView3);
        Handler handler = new Handler();
        progressBarSp = findViewById(R.id.progressBar5);
        progressBarSp.setVisibility(View.VISIBLE);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

               startActivity(new Intent(Splash.this, SignIn.class));
                progressBarSp.setVisibility(View.VISIBLE);
                finishAffinity();

            }
        }, 3000);

    }
}