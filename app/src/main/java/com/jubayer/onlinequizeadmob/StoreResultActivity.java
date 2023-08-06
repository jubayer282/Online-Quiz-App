package com.jubayer.onlinequizeadmob;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class StoreResultActivity extends AppCompatActivity {

    private TextView correctTv, incorrectTv;
    String correct , incorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_result);

        getSupportActionBar().setTitle("Score Dashboard");

        /*init views*/
        correctTv = findViewById(R.id.correctTv);
        incorrectTv = findViewById(R.id.incorrectTv);

        correct = getIntent().getStringExtra("correct");
        incorrect = getIntent().getStringExtra("incorrect");

        correctTv.setText(correct);
        incorrectTv.setText(incorrect);

    }
}