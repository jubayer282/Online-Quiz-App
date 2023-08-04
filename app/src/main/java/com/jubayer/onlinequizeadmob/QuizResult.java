package com.jubayer.onlinequizeadmob;

import static android.content.Intent.EXTRA_TEXT;

import static androidx.constraintlayout.motion.widget.TransitionBuilder.validate;

import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class QuizResult extends AppCompatActivity {
    private List<QuestionsList> questionsLists = new ArrayList<>();

    public String correctScore, inCorrectScore;

    private AppCompatButton saveScoreBtn;

    FirebaseAuth auth;

    private String deviceID;
    private DatabaseReference reference;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        getSupportActionBar().setTitle("Result Page");

        final TextView scoreTV = findViewById(R.id.scoreTV);
        final TextView totalScoreTV = findViewById(R.id.totalScoreTV);
        final TextView correctTV = findViewById(R.id.correctTV);
        final TextView incorrectTV = findViewById(R.id.inCorrectTV);
        final AppCompatButton shareBtn = findViewById(R.id.shareBtn);
        final AppCompatButton retakeBtn = findViewById(R.id.reTakeQuizBtn);

        saveScoreBtn = findViewById(R.id.saveScoreBtn);

     //   deviceID = Settings.Secure.getString(QuizResult.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        auth = FirebaseAuth.getInstance();

        /*get database name*/
        reference = FirebaseDatabase.getInstance().getReference().child("Score");


        // getting questions list from MainActivity
        questionsLists = (List<QuestionsList>) getIntent().getSerializableExtra("questions");

        totalScoreTV.setText("/" + questionsLists.size());
        scoreTV.setText(getCorrectAnswers() + "");
        correctTV.setText(getCorrectAnswers() + "");
        incorrectTV.setText(String.valueOf(questionsLists.size() - getCorrectAnswers()));


        /*get correct score and get data from result*/
        correctScore = correctTV.getText().toString().trim();
        inCorrectScore = incorrectTV.getText().toString().trim();

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // open share intent
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "My score = " + scoreTV.getText());

                Intent shareIntent = Intent.createChooser(sendIntent, "Share Via");
                //Uri uri = Uri.parse("https://mail.google.com");
                //sendIntent.setPackage("https://mail.google.com");
                startActivity(shareIntent);
            }
        });

        retakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //re start quiz go to MainActivity
                startActivity(new Intent(QuizResult.this, MainActivity.class));
                finish();
            }
        });


        saveScoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                savePoint();

            }
        });


    }

    private void savePoint() {

        signUpUser(user, correctScore, inCorrectScore);
    }


    private void signUpUser(FirebaseUser user, String correctScore, String inCorrectScore) {


        Query query = reference.orderByChild("deviceID").equalTo(deviceID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    String userId = auth.getCurrentUser().getUid();

                    HashMap<String, Object> map = new HashMap<>();

                    map.put("correctScore", correctScore);
                    map.put("incorrectScore", inCorrectScore);
                    map.put("userId", userId);

                    reference.child(userId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(QuizResult.this, "Value added Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(QuizResult.this, SignIn.class));
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private int getCorrectAnswers() {
        int correctAnswer = 0;

        for (int i = 0; i < questionsLists.size(); i++) {
            int getUserSelectedOption = questionsLists.get(i).getUserSelectedAnswer(); // get user selected option
            int getQuestionAnswer = questionsLists.get(i).getAnswer(); // get Correct answer for the question

            // check of user selected answer matches with correct answer
            if (getQuestionAnswer == getUserSelectedOption) {

                correctAnswer++; // increase correct answers
            }


        }
        return correctAnswer;
    }
}