package com.nutriguard.app.ui.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nutriguard.app.R;

/**
 * Main Activity hosting the BottomNavigationView and fragment transitions.
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private final Fragment homeFragment = new HomeFragment();
    private final Fragment favoritesFragment = new FavoritesFragment();
    private final Fragment profileFragment = new ProfileFragment();

    private Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Add all fragments and hide non-active ones to preserve scroll and search state
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, profileFragment, "profile").hide(profileFragment)
                .add(R.id.fragment_container, favoritesFragment, "favorites").hide(favoritesFragment)
                .add(R.id.fragment_container, homeFragment, "home")
                .commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                switchFragment(homeFragment);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                switchFragment(favoritesFragment);
                return true;
            } else if (itemId == R.id.nav_profile) {
                switchFragment(profileFragment);
                return true;
            }
            return false;
        });
    }

    private void switchFragment(Fragment targetFragment) {
        if (targetFragment == activeFragment) {
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .hide(activeFragment)
                .show(targetFragment)
                .commit();

        activeFragment = targetFragment;
    }
}
