package com.jubayer.onlinequizeadmob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //Creating question List
    private final List<QuestionsList> questionsLists = new ArrayList<>();

    private TextView quizTimer;
    private RelativeLayout option1Layout, option2Layout, option3Layout, option4Layout;
    private TextView option1TV, option2TV, option3TV, option4TV;
    private ImageView option1Icon, option2Icon, option3Icon, option4Icon;

    private TextView questionTV;
    private TextView totalQuestionTV;
    private TextView currentQuestion;
    private TextView startQuizBtn;


    // Creating database reference from Url
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://quizappadmob-14f3a-default-rtdb.firebaseio.com/");

    // CountDown Timer for Quiz
    private CountDownTimer countDownTimer;

    // current question position ny default 0 (first question)
    private int currentQuestionPosition = 0;

    // selected option number. Value must be between 1-4 (We have 4 option) and 0 means no option selected.
    private int selectedOption = 0;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        quizTimer = findViewById(R.id.quizTimer);
        startQuizBtn = findViewById(R.id.startQuizBtn);

        option1Layout = findViewById(R.id.option1Layout);
        option2Layout = findViewById(R.id.option2Layout);
        option3Layout = findViewById(R.id.option3Layout);
        option4Layout = findViewById(R.id.option4Layout);


        option1TV = findViewById(R.id.option1TV);
        option2TV = findViewById(R.id.option2TV);
        option3TV = findViewById(R.id.option3TV);
        option4TV = findViewById(R.id.option4TV);

        option1Icon = findViewById(R.id.option1Icon);
        option2Icon = findViewById(R.id.option2Icon);
        option3Icon = findViewById(R.id.option3Icon);
        option4Icon = findViewById(R.id.option4Icon);

        questionTV = findViewById(R.id.questionTV);
        totalQuestionTV = findViewById(R.id.totalQuestionsTV);
        currentQuestion = findViewById(R.id.currentQuestionTV);



        final AppCompatButton nextBtn = findViewById(R.id.nextQuestionBtn);

        // show instructions dialog
         InstructionsDialog instructionsDialog = new InstructionsDialog(MainActivity.this);
         instructionsDialog.setCancelable(false);
         instructionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
         instructionsDialog.show();

        // Getting data (questions and quiz timer) from firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


             //  int getQuizTime = Integer.parseInt(snapshot.child("time").getValue(String.class));

               final int getQuizTime = Integer.parseInt(snapshot.child("time").getValue(String.class));

                for(DataSnapshot questions : snapshot.child("questions").getChildren()){

                    String getQuestion = questions.child("question").getValue(String.class);
                    String getOption1 = questions.child("option1").getValue(String.class);
                    String getOption2 = questions.child("option2").getValue(String.class);
                    String getOption3 = questions.child("option3").getValue(String.class);
                    String getOption4 = questions.child("option4").getValue(String.class);

                    // is y=using String here to database value must be in String form
                    int getAnswer = Integer.parseInt(questions.child("answer").getValue(String.class));



                    // Creating questions List objects and add details
                    QuestionsList questionsList = new QuestionsList(getQuestion, getOption1, getOption2, getOption3, getOption4, getAnswer);

                    // Adding question List object into the list
                    questionsLists.add(questionsList);

                }
                //setting total questions to textView
                totalQuestionTV.setText("/"+questionsLists.size());

                // start quiz timer and pass max time in seconds
                startQuizTimer(getQuizTime);

                // select first question by default
                selectQuestion(currentQuestionPosition);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this, "Failed to get data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        option1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // assign 1 as first option is selected
                selectedOption = 1;
                // select option
                selectedOption(option1Layout, option1Icon);
            }
        });
        option2Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // assign 2 as second option is selected
                selectedOption = 2;

                // select option
                selectedOption(option2Layout, option2Icon);

            }
        });
        option3Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // assign 3 as third option is selected
                selectedOption = 3;

                // select option
                selectedOption(option3Layout, option3Icon);
            }
        });
        option4Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // assign 4 as fourth option is selected
                selectedOption = 4;

                // select option
                selectedOption(option4Layout, option4Icon);
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check if user has select an option or not
                if(selectedOption != 0){

                    // set user selected answer
                    questionsLists.get(currentQuestionPosition).setUserSelectedAnswer(selectedOption);

                    // reset selected option to default value (0)
                    selectedOption = 0;

                    // increase current question value to getting nes=xt question
                    currentQuestionPosition++;


                    // check if list has more questions
                    if(currentQuestionPosition < questionsLists.size()){
                        selectQuestion(currentQuestionPosition); // select question / next question

                    }
                    else{

                        // list has no questions left to finish the quiz
                        countDownTimer.cancel(); // stop countdown timer
                        finishQuiz(); //finish quiz
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "please select an option", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private  void finishQuiz(){

        // Creating intent to open QuizResult activity
        Intent intent = new Intent(MainActivity.this, QuizResult.class);

        // creating bundle to pass quizQuestionLists
        Bundle bundle = new Bundle();
        bundle.putSerializable("questions", (Serializable) questionsLists);

        // add bundle to intent
        intent.putExtras(bundle);

        // start activity to open QuizResult activity
        startActivity(intent);

        // destroy current activity
        finish();
    }
    private void startQuizTimer(int maxTimeInSeconds){
        countDownTimer = new CountDownTimer(maxTimeInSeconds * 1000, 1000) {
            @Override
            public void onTick(long milliUntilFinished) {

                long getHour = TimeUnit.MICROSECONDS.toHours(milliUntilFinished);
                long getMinute = TimeUnit.MICROSECONDS.toMinutes(milliUntilFinished);
                long getSecond = TimeUnit.MICROSECONDS.toSeconds(milliUntilFinished);

                String generateTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", getHour,
                getMinute - TimeUnit.HOURS.toMinutes(getHour),
                        getSecond - TimeUnit.MINUTES.toSeconds(getMinute));

                quizTimer.setText(generateTime);

            }

            @Override
            public void onFinish() {

                // finish quiz when time i finished
               finishQuiz();

            }
        };

        // start Timer
        countDownTimer.start();
    }
    private void selectQuestion(int questionListPosition){

        // reset options for new questions
        resetOptions();

        // getting question details and set to TextView
        questionTV.setText(questionsLists.get(questionListPosition).getQuestion());
        option1TV.setText(questionsLists.get(questionListPosition).getOption1());
        option2TV.setText(questionsLists.get(questionListPosition).getOption2());
        option3TV.setText(questionsLists.get(questionListPosition).getOption3());
        option4TV.setText(questionsLists.get(questionListPosition).getOption4());

        // setting current question number to TextView
        currentQuestion.setText("Question"+(questionListPosition+1));


    }

    private void resetOptions(){
        option1Layout.setBackgroundResource(R.drawable.round_back_white_10);
        option2Layout.setBackgroundResource(R.drawable.round_back_white_10);
        option3Layout.setBackgroundResource(R.drawable.round_back_white_10);
        option4Layout.setBackgroundResource(R.drawable.round_back_white_10);

        option1Icon.setImageResource(R.drawable.round_back_white50);
        option2Icon.setImageResource(R.drawable.round_back_white50);
        option3Icon.setImageResource(R.drawable.round_back_white50);
        option4Icon.setImageResource(R.drawable.round_back_white50);
    }

    private void selectedOption(RelativeLayout selectedOptionLayout, ImageView selectedOptionIcon){

        // reset options to select new option
        resetOptions();

        selectedOptionIcon.setImageResource(R.drawable.check);
        selectedOptionLayout.setBackgroundResource(R.drawable.round_back_selected_option);

    }
}