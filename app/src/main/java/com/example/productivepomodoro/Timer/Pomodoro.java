package com.example.productivepomodoro.Timer;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.productivepomodoro.PomodoroInputDialog;
import com.example.productivepomodoro.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;

public class Pomodoro extends Fragment {
    public float focusTimerMinutes = 0.25f;
    public float breakTimerMinutes = 0.125f;
    public float longBreakTimerMinutes = 0.5f;
    public long countDownInterval = 1000;
    public int longBreakInterval = 4;
    public int completedFocusSetsCount = 0;

    private ProgressBar progressBar;
    private TextView timerTextMinutes;
    private TextView timerTextSeconds;
    private FloatingActionButton timerControlButton;
    private FloatingActionButton skipButton;
    private FloatingActionButton restButton;
    private ImageButton settingsButton;
    private CountDownTimer countDownTimer;
    private ProgressBar spinner;
    private TextView timerStateText;
    private TextView pomodoroCountText;

    private float focusTimerMilliseconds;
    private float breakTimerMilliseconds;
    private float longBreakTimerMilliseconds;

    private boolean timerRunning;
    private boolean resetTimer = false;

    private float timeLeftInMilliseconds;
    private int pomodoroSets = 0;

    private enum TimerState {FOCUS, BREAK, LONG_BREAK}
    public enum ButtonIcons {
        PLAY(R.drawable.ic_baseline_play_arrow_24),
        PAUSE(R.drawable.ic_baseline_pause_24),
        NEXT(R.drawable.ic_baseline_navigate_next_24),
        SKIP(R.drawable.ic_baseline_skip_next_24),
        RESET(R.drawable.ic_baseline_undo_24);
        final int i;
        ButtonIcons(int i) {
            this.i = i;
        }
    };
    private TimerState currentState;

    public Pomodoro(){
        focusTimerMilliseconds = focusTimerMinutes * 60 * countDownInterval;
        breakTimerMilliseconds = breakTimerMinutes * 60 * countDownInterval;
        longBreakTimerMilliseconds = longBreakTimerMinutes * 60 * countDownInterval;

        timeLeftInMilliseconds = focusTimerMilliseconds;
        currentState = TimerState.FOCUS;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View pomodoroView = inflater.inflate(R.layout.pomodoro, container, false);

        progressBar = pomodoroView.findViewById(R.id.progressBar);
        timerTextMinutes = pomodoroView.findViewById(R.id.minutes);
        timerTextSeconds = pomodoroView.findViewById(R.id.seconds);

        timerControlButton = pomodoroView.findViewById(R.id.startPauseBtn);
        spinner = pomodoroView.findViewById(R.id.spinner);
        skipButton = pomodoroView.findViewById(R.id.skipButton);
        restButton = pomodoroView.findViewById(R.id.resetButton);
        settingsButton = pomodoroView.findViewById(R.id.pomodoroSettings);
        timerStateText = pomodoroView.findViewById(R.id.timerStateText);
        pomodoroCountText = pomodoroView.findViewById(R.id.focusSetsCompletedCount);

        pomodoroButtonState();

        timerControlButton.setOnClickListener(view -> startStop());
        skipButton.setOnClickListener(view -> skipToNext());
        restButton.setOnClickListener(view -> resetTimer(false));
        settingsButton.setOnClickListener(view -> openSettingsDialog());

        setTimer(false);
        setTimerStateText();
        pomodoroCountText.setText("#" + completedFocusSetsCount);

        return pomodoroView;
    }

    private void openSettingsDialog(){
        Toast.makeText(getContext(), "Settings Opened!", Toast.LENGTH_SHORT).show();
        PomodoroInputDialog dialog = new PomodoroInputDialog(this);
    }

    public void startStop(){
        if(resetTimer){
            resetTimer(true);
        }
        else if(timerRunning) pauseTimer();
        else startTimer();
    }

    public void startTimer(){
        countDownTimer = new CountDownTimer((int) timeLeftInMilliseconds, countDownInterval) {
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
                playAnimation(progressBar, Techniques.RubberBand, 500, 2);
                // playAnimation(,Techniques.RubberBand, 500, 2);
                timerControlButton.setImageResource(ButtonIcons.NEXT.i);
            }
        }.start();

        timerRunning = true;
        Log.w("Pomodoro", "Timer Started!");
        pomodoroButtonState();
    }

    public void pauseTimer(){
        countDownTimer.cancel();
        timerRunning = false;
        Log.w("Pomodoro", "Timer Paused!");
        pomodoroButtonState();

    }

    public void pomodoroButtonState(){
        if(timerRunning){
            spinner.setVisibility(View.VISIBLE);
            playAnimation(spinner,Techniques.FadeIn, 700);
            timerControlButton.setImageResource(ButtonIcons.PAUSE.i);

            restButton.setEnabled(false);
            skipButton.setEnabled(false);

            playAnimation(restButton,Techniques.FadeOutDown, 500);
            playAnimation(skipButton,Techniques.FadeOutDown, 500);
        }
        else{
            spinner.setVisibility(View.INVISIBLE);
            timerControlButton.setImageResource(ButtonIcons.PLAY.i);

            restButton.setEnabled(true);
            skipButton.setEnabled(true);

            playAnimation(restButton,Techniques.FadeInUp, 500);
            playAnimation(skipButton,Techniques.FadeInUp, 500);
        }
    }

    public void resetTimer(boolean goToNextState) {
        resetTimer = false;
        Log.w("Pomodoro", "Timer Reset Successful!");
        timerControlButton.setImageResource(ButtonIcons.PLAY.i);

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
        }
        else{
            // If current state is BREAK or LONG_BREAK depending, then next state is FOCUS state only!
            currentState = TimerState.FOCUS;
            progressMaxVal = (int) focusTimerMilliseconds;
        }

        setTimerStateText();
        setTimer(progressMaxVal);
    }

    public void skipToNext(){
        timerControlButton.setImageResource(ButtonIcons.PLAY.i);
        // If timer ends and user taps skip btn, count will increase
        setTimerToNextState(resetTimer);
        Log.w("Pomodoro", "Skipped to next state!");
        resetTimer = false;
    }

    public void setTimer(int time){
        timeLeftInMilliseconds = time;
        progressBar.setMax(time * 100);
        setProgressAnimate(progressBar, time);
        UpdateTimerText();
    }

    public void setTimer(boolean setProgressOnly){
        int val = 0;
        switch (currentState){
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
        progressBar.setMax(val * 100);
        setProgressAnimate(progressBar, (resetTimer) ? 0:(int)timeLeftInMilliseconds);
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

    public void setTimerStateText(){
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
        String state = "";
        switch (currentState){
            case FOCUS:
                state = "FOCUS";
                break;
            case BREAK:
                state = "BREAK";
                break;
            case LONG_BREAK:
                state = "LONG BREAK";
                break;
        }
        timerStateText.setText(state);
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
