package com.example.productivepomodoro;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.productivepomodoro.Timer.Pomodoro;
import com.example.productivepomodoro.Todo.TodoParent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private Pomodoro pomodoroFragment;
    private TodoParent todoFragment;
    private UserProfile profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        pomodoroFragment = new Pomodoro();
        todoFragment = new TodoParent();
        profileFragment = new UserProfile();

        YoYo.with(Techniques.BounceInUp).duration(700).playOn(bottomNav);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, pomodoroFragment).commit();
    }

    private NavigationBarView.OnItemSelectedListener navListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedPage = null;
            switch (item.getItemId()){
                case (R.id.nav_timer):
                    selectedPage = pomodoroFragment;
                    break;
                case (R.id.nav_todo):
                    selectedPage = todoFragment;
                    break;
                case (R.id.nav_profile):
                    selectedPage = profileFragment;
                    break;
            }

            clearSearchQuery();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, selectedPage).commit();
            return true;
        }
    };

    private void clearSearchQuery(){
        if(todoFragment.getTodoList(true) != null && todoFragment.getTodoList(false) != null){
            todoFragment.getTodoList(true).clearSearch();
            todoFragment.getTodoList(false).clearSearch();
        }
    }
}