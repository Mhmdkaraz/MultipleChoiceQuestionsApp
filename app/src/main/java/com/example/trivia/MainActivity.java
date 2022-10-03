package com.example.trivia;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Question> questionList;
    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        score = new Score();
        prefs = new Prefs(MainActivity.this);
        //Retrieve the last state
        currentQuestionIndex = prefs.getState();

        binding.scoreTextView.setText(MessageFormat.format("Current Score : {0}", String.valueOf(score.getScore())));
        binding.highScoreText.setText(MessageFormat.format("Highest : {0}", String.valueOf(prefs.getHighestScore())));
        questionList = new Repository().getQuestions(questionArrayList -> {
                    binding.questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                }
        );
        binding.buttonShare.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,MessageFormat.format("My Current Score is : {0} ,and My highest score is : {1}", String.valueOf(score.getScore()),String.valueOf(prefs.getHighestScore())));
            intent.putExtra(Intent.EXTRA_SUBJECT,"I am playing Trivia");
            startActivity(intent);
        });
        binding.buttonNext.setOnClickListener(view -> {
            getNextQuestion();
        });
        binding.buttonTrue.setOnClickListener(view -> {
            checkAnswer(true);
            updateQuestions();
        });
        binding.buttonFalse.setOnClickListener(view -> {
            checkAnswer(false);
            updateQuestions();
        });
    }

    private void getNextQuestion() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestions();
    }

    private void checkAnswer(boolean userChoseCorrect) {
        boolean answer = questionList.get(currentQuestionIndex).isAnswerTrue();
        int snackMessageId = 0;
        if (userChoseCorrect == answer) {
            snackMessageId = R.string.correct_answer;
            fadeAnimation();
            addPoints();
        } else {
            snackMessageId = R.string.incorrect;
            shakeAnimation();
            deductPoints();
        }
        Snackbar.make(binding.cardView, snackMessageId, Snackbar.LENGTH_SHORT).show();
    }

    private void updateCounter(ArrayList<Question> questionArrayList) {
        binding.textViewOutOf.setText(String.format(getString(R.string.text_formatted), currentQuestionIndex, questionArrayList.size()));
    }

    private void updateQuestions() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        binding.questionTextView.setText(question);
        updateCounter((ArrayList<Question>) questionList);
    }

    private void fadeAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        binding.cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        binding.cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void deductPoints() {
        if (scoreCounter > 0) {
            scoreCounter -= 100;
            score.setScore(scoreCounter);
            binding.scoreTextView.setText(MessageFormat.format("Current Score : {0}", String.valueOf(score.getScore())));
        } else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
        }

    }

    private void addPoints() {
        scoreCounter += 100;
        score.setScore(scoreCounter);
        binding.scoreTextView.setText(MessageFormat.format("Current Score : {0}", String.valueOf(score.getScore())));
    }

    @Override
    protected void onPause() {
        prefs.saveHighestScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        Log.d("Pause", "onPause: Saving Score " + prefs.getHighestScore());
        super.onPause();

    }
}