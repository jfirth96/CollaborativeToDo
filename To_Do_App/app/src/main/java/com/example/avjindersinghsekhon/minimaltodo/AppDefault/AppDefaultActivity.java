package com.example.avjindersinghsekhon.minimaltodo.AppDefault;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.avjindersinghsekhon.minimaltodo.Main.MainFragment;
import com.example.avjindersinghsekhon.minimaltodo.About.AboutActivity;
import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.Settings.SettingsActivity;

public abstract class AppDefaultActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String theme = getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME);
        if (theme != null && theme.equals(MainFragment.LIGHTTHEME)) {
            setTheme(R.style.CustomStyle_LightTheme);
        }
        else setTheme(R.style.CustomStyle_DarkTheme);

        super.onCreate(savedInstanceState);
        setContentView(contentViewLayoutRes());
        setUpInitialFragment(savedInstanceState);
    }

    private void setUpInitialFragment(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, createInitialFragment())
                    .commit();
        }
    }

    @LayoutRes
    protected abstract int contentViewLayoutRes();

    @NonNull
    protected abstract Fragment createInitialFragment();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutMeMenuItem:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                drawerLayout.openDrawer( GravityCompat.START );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
