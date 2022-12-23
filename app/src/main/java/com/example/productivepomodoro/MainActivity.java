package com.example.productivepomodoro;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public long focusTimerMinutes = 25;
    public long breakTimerMinutes = 5;
    public long longBreakTimerMinutes = 30;
    public long countDownInterval = 1000;
    public int longBreakInterval = 4;
    public int completedFocusSetsCount = 0;

    private ProgressBar progressBar;
    private TextView timerTextMinutes;
    private TextView timerTextSeconds;
    private FloatingActionButton timerControlButton;
    private FloatingActionButton skipButton;
    private FloatingActionButton restButton;
    private CountDownTimer countDownTimer;
    private ProgressBar spinner;
    private TextView timerStateText;
    private TextView pomodoroCountText;

    private long focusTimerMilliseconds;
    private long breakTimerMilliseconds;
    private long longBreakTimerMilliseconds;

    private boolean timerRunning;
    private boolean resetTimer = false;

    private long timeLeftInMilliseconds;
    private int pomodoroSets = 0;

    private enum TimerState {FOCUS, BREAK, LONG_BREAK}
    private TimerState currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        focusTimerMilliseconds = focusTimerMinutes * 60 * countDownInterval;
        breakTimerMilliseconds = breakTimerMinutes * 60 * countDownInterval;
        longBreakTimerMilliseconds = longBreakTimerMinutes * 60 * countDownInterval;

        progressBar = findViewById(R.id.progressBar);
        timerTextMinutes = findViewById(R.id.minutes);
        timerTextSeconds = findViewById(R.id.seconds);

        timerControlButton = findViewById(R.id.startPauseBtn);
        spinner = findViewById(R.id.spinner);
        skipButton = findViewById(R.id.skipButton);
        restButton = findViewById(R.id.resetButton);
        timerStateText = findViewById(R.id.timerStateText);
        pomodoroCountText = findViewById(R.id.focusSetsCompletedCount);

        spinner.setVisibility(View.INVISIBLE);

        currentState = TimerState.FOCUS;
        setTimer((int) focusTimerMilliseconds);

        setTimerStateText("FOCUS");
        pomodoroCountText.setText("#" + completedFocusSetsCount);

        timerControlButton.setOnClickListener(view -> startStop());
        skipButton.setOnClickListener(view -> skipToNext());
        restButton.setOnClickListener(view -> resetTimer(false));
    }

    public void startStop(){
        if(resetTimer){
            resetTimer(true);
        }
        else if(timerRunning) pauseTimer();
        else startTimer();
    }

    public void startTimer(){
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, countDownInterval) {
            @Override
            public void onTick(long l) {
                timeLeftInMilliseconds = l;
                UpdateTimerText();
                setProgressAnimate(progressBar, (int)timeLeftInMilliseconds - 1000);
            }
            @Override
            public void onFinish() {
                resetTimer = true;
                pauseTimer();
                progressBar.setProgress(0);
                playAnimation(progressBar,Techniques.RubberBand, 500, 2);
                // playAnimation(,Techniques.RubberBand, 500, 2);
                timerControlButton.setImageResource(android.R.drawable.ic_popup_sync);
            }
        }.start();

        spinner.setVisibility(View.VISIBLE);
        playAnimation(spinner,Techniques.FadeIn, 700);
        playAnimation(progressBar,Techniques.Tada, 500);
        timerControlButton.setImageResource(android.R.drawable.ic_media_pause);

        playAnimation(restButton,Techniques.FadeOutDown, 500);
        playAnimation(skipButton,Techniques.FadeOutDown, 500);

        timerRunning = true;
    }

    public void pauseTimer(){
        playAnimation(progressBar,Techniques.RubberBand, 500);
        spinner.setVisibility(View.INVISIBLE);

        restButton.setVisibility(View.VISIBLE);
        skipButton.setVisibility(View.VISIBLE);
        playAnimation(restButton,Techniques.FadeInUp, 500);
        playAnimation(skipButton,Techniques.FadeInUp, 500);

        timerControlButton.setImageResource(android.R.drawable.ic_media_play);
        countDownTimer.cancel();
        timerRunning = false;
    }

    public void resetTimer(boolean goToNextState) {
        resetTimer = false;
        timerControlButton.setImageResource(android.R.drawable.ic_media_play);

        if (goToNextState) {
            setTimerToNextState(true);
        }
        else{
            setTimer(currentState);
        }
        UpdateTimerText();
    }

    public void setTimerToNextState(boolean countPomodoro){
        int progressMaxVal;
        if(currentState == TimerState.FOCUS){
            pomodoroSets++;
            completedFocusSetsCount = countPomodoro ? completedFocusSetsCount + 1 : completedFocusSetsCount;
            pomodoroCountText.setText("#" + completedFocusSetsCount);
            // set currentState to BREAK or LONG_BREAK depending on no. of sets completed.
            currentState = (pomodoroSets % longBreakInterval == 0) ? TimerState.LONG_BREAK : TimerState.BREAK;
            progressMaxVal = (pomodoroSets % longBreakInterval == 0) ? (int)longBreakTimerMilliseconds : (int)breakTimerMilliseconds;
            setTimerStateText((currentState == TimerState.BREAK) ? "BREAK" : "LONG BREAK");
        }
        else{
            // If current state is BREAK or LONG_BREAK depending, then next state is FOCUS state only!
            currentState = TimerState.FOCUS;
            setTimerStateText("FOCUS");
            progressMaxVal = (int) focusTimerMilliseconds;
        }
        setTimer(progressMaxVal);
    }

    public void skipToNext(){
        resetTimer = false;
        timerControlButton.setImageResource(android.R.drawable.ic_media_play);
        setTimerToNextState(false);
    }

    public void setTimer(int time){
        progressBar.setMax(time * 100);
        setProgressAnimate(progressBar, time);
        timeLeftInMilliseconds = time ;
        UpdateTimerText();
    }

    public void setTimer(TimerState time){
        int val = 0;
        switch (time){
            case FOCUS:
                val = (int)focusTimerMilliseconds;
                break;
            case BREAK:
                val = (int)breakTimerMilliseconds;
                break;
            case LONG_BREAK:
                val = (int) longBreakTimerMilliseconds;
                break;
        }
        setTimer(val);
        UpdateTimerText();
    }

    public void setTimerStateText(String stateText){
        Techniques[] techniques = new Techniques[]{
                Techniques.Bounce,
                Techniques.Flash,
                Techniques.RubberBand,
                Techniques.Pulse,
                Techniques.Wave,
                Techniques.Wobble,
                Techniques.Tada,
                Techniques.StandUp,
                Techniques.Swing,
                Techniques.Shake
        };
        int index = new Random().nextInt(techniques.length);
        playAnimation(timerStateText, techniques[index], 500);
        timerStateText.setText(stateText);
    }

    public void UpdateTimerText(){
        int minutes = (int) timeLeftInMilliseconds / 60000;
        int seconds = (int) (timeLeftInMilliseconds % 60000 / countDownInterval);

        String min, sec;
        min = (minutes < 10) ? ("0" + minutes) : ("" + minutes);
        sec = (seconds < 10) ? ("0" + seconds) : ("" + seconds);

        if(seconds == 0){
            playAnimation(timerTextMinutes, Techniques.StandUp, 1000);
        }
        playAnimation(timerTextSeconds, Techniques.StandUp, 1000);

        timerTextSeconds.setText(sec);
        timerTextMinutes.setText(min);
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

    private void setProgressAnimate(ProgressBar pb, int progressTo)
    {
        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", pb.getProgress(), progressTo * 100);
        animation.setDuration( 500);
        animation.setAutoCancel( true);
        animation.setInterpolator( new DecelerateInterpolator());
        animation.start();
    }
}