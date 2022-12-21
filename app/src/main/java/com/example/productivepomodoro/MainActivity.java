package com.example.productivepomodoro;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView timerText;
    private FloatingActionButton timerControlButton;
    private CountDownTimer countDownTimer;
    private ProgressBar spinner;

    private final long timerMilliseconds =  30000;
    private long timeLeftInMilliseconds = 30000;
    private final long countDownInterval = 1000;

    private boolean timerRunning;
    private boolean resetTimer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        timerText = findViewById(R.id.textView);
        timerControlButton = findViewById(R.id.startPauseBtn);
        spinner = findViewById(R.id.spinner);

        spinner.setVisibility(View.INVISIBLE);
        progressBar.setMax((int) timerMilliseconds);
        progressBar.setProgress((int) timeLeftInMilliseconds);
        UpdateTimerText();

        timerControlButton.setOnClickListener(view -> startStop());
    }

    public void startStop(){
        if(resetTimer){
            resetTimer();
        }
        else if(timerRunning) pauseTimer();
        else startTimer();
    }

    public void startTimer(){
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, countDownInterval) {
            @Override
            public void onTick(long l) {
                timeLeftInMilliseconds = l;
                progressBar.setProgress((int) timeLeftInMilliseconds - 1000);
                UpdateTimerText();
            }
            @Override
            public void onFinish() {
                resetTimer = true;
                pauseTimer();
                progressBar.setProgress(0);
                playAnimation(progressBar,Techniques.Shake, 500, 4);
                playAnimation(timerText,Techniques.RubberBand, 500, 4);
                timerControlButton.setImageResource(android.R.drawable.ic_popup_sync);
            }
        }.start();

        spinner.setVisibility(View.VISIBLE);
        playAnimation(spinner,Techniques.FadeIn, 700);
        playAnimation(progressBar,Techniques.Tada, 500);
        timerControlButton.setImageResource(android.R.drawable.ic_media_pause);
        timerRunning = true;
    }

    public void pauseTimer(){
        spinner.setVisibility(View.INVISIBLE);
        timerControlButton.setImageResource(android.R.drawable.ic_media_play);
        countDownTimer.cancel();
        timerRunning = false;
    }

    public void resetTimer(){
        resetTimer = false;
        timerControlButton.setImageResource(android.R.drawable.ic_media_play);
        progressBar.setMax((int) timerMilliseconds);
        progressBar.setProgress((int) timerMilliseconds);
        timeLeftInMilliseconds = timerMilliseconds;
        UpdateTimerText();
    }

    public void UpdateTimerText(){
        int minutes = (int) timeLeftInMilliseconds / 60000;
        int seconds = (int) (timeLeftInMilliseconds % 60000 / countDownInterval);

        String timeLeftString;
        timeLeftString = (minutes < 10) ? ("0" + minutes) : ("" + minutes);
        timeLeftString += " : ";
        timeLeftString += (seconds < 10) ? ("0" + seconds) : ("" + seconds);

        timerText.setText(timeLeftString);
    }

    // Animations
    public void playAnimation(View target, Techniques techniques, long duration){
        YoYo.with(techniques)
                .duration(duration)
                .playOn(target);
    }
    public void playAnimation(View target, Techniques techniques, long duration, int repeat){
        YoYo.with(techniques)
                .duration(duration)
                .repeat(repeat)
                .playOn(target);
    }
}