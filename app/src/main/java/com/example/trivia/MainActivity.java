package com.example.trivia;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView questionTextview;
    private TextView questionCounterTextview;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private int currentQuestionIndex = 0;
    private String CURRENTQUESTIONINDEX = "currentQuestionIndex";
    private List<Question> questionList;
    SoundPool soundpool;
    public static int MAX_STREAMS = 4;
    public static int SOUND_PRIORITY = 1;
    public static int SOUND_QUALITY = 100;
    private TextView scoreText;
    private static int score;
    private static final String SCORE = "score";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nextButton = findViewById(R.id.next_btn);
        prevButton = findViewById(R.id.prev_btn);
        trueButton = findViewById(R.id.true_btn);
        falseButton = findViewById(R.id.false_btn);
        questionCounterTextview = findViewById(R.id.counter_text);
        questionTextview = findViewById(R.id.question_textView);
        scoreText = findViewById(R.id.scoreTextView);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);


        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {

                SharedPreferences indexPreferences = getSharedPreferences(CURRENTQUESTIONINDEX,MODE_PRIVATE);
                int i = indexPreferences.getInt(CURRENTQUESTIONINDEX,0);
                String index = String.valueOf(i);

                questionTextview.setText(questionArrayList.get(i).getAnswer());
                questionCounterTextview.setText(index + " / " + questionArrayList.size()); // 0 / 234
                currentQuestionIndex = i;
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(SCORE,MODE_PRIVATE);
        scoreText.setText(String.valueOf(sharedPreferences.getInt(SCORE,0)));

        score = Integer.valueOf(scoreText.getText().toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev_btn:
                prevQ();
                break;
            case R.id.next_btn:
                nextQ();
                break;
            case R.id.true_btn:
                checkAnswer(true);
                updateQuestion();
                updateScore();
                break;
            case R.id.false_btn:
                checkAnswer(false);
                updateQuestion();
                break;
        }
    }

    private void nextQ() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestion();
        setQuestionIndexToSharedPref();
    }

    private void prevQ() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
            updateQuestion();
        }
        setQuestionIndexToSharedPref();
    }

    private void updateScore(){
        score += 10;
        scoreText.setText(String.valueOf(score));
        SharedPreferences sharedPreferences = getSharedPreferences(SCORE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SCORE,score);

        editor.apply();
    }

    private void checkAnswer(boolean userChooseCorrect) {
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageId = 0;
        if (userChooseCorrect == answerIsTrue) {
            nextQ();
            fadeView();
            updateScore();
            setQuestionIndexToSharedPref();
            toastMessageId = R.string.correct_answer;

        } else {
            shakeAnimation();
            toastMessageId = R.string.incorrect_answer;
        }
        Toast.makeText(MainActivity.this, toastMessageId,Toast.LENGTH_SHORT).show();
    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionTextview.setText(question);
        questionCounterTextview.setText(currentQuestionIndex + " / " + questionList.size());
    }

    private void setQuestionIndexToSharedPref(){
        SharedPreferences QuestionIndexPref = getSharedPreferences(CURRENTQUESTIONINDEX,MODE_PRIVATE);
        SharedPreferences.Editor editor = QuestionIndexPref.edit();
        editor.putInt(CURRENTQUESTIONINDEX,currentQuestionIndex);

        editor.apply();
    }

    private void fadeView() {
        final CardView cardView = findViewById(R.id.cardView2);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView2);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}