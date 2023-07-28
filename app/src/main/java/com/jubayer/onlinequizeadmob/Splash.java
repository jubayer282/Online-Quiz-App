package com.jubayer.onlinequizeadmob;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //fAuth = FirebaseAuth.getInstance();
        quiz = findViewById(R.id.fire);
        Handler handler = new Handler();
        progressBarSp = findViewById(R.id.progressBar5);
        progressBarSp.setVisibility(View.VISIBLE);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
/*
                if (user != auth.getCurrentUser()) {
                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                    startActivity(intent);
                    finish();
                }*/
               startActivity(new Intent(Splash.this, SignIn.class));
                progressBarSp.setVisibility(View.VISIBLE);
                finish();

            }
        }, 3000);

    }
}