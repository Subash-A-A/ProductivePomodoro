package com.example.productivepomodoro;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private Pomodoro pomodoroFragment;
    private TodoList todoFragment;
    private UserProfile profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        pomodoroFragment = new Pomodoro();
        todoFragment = new TodoList();
        profileFragment = new UserProfile();

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
            View menuItemView = findViewById(item.getItemId());
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, selectedPage).commit();
            return true;
        }
    };
    private NavigationBarView.OnItemReselectedListener navReselectListener = new NavigationBarView.OnItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull MenuItem item) {
            YoYo.with(Techniques.StandUp).duration(500).playOn(findViewById(item.getItemId()));
        }
    };
}