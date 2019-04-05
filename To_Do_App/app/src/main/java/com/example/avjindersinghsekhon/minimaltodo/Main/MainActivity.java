package com.example.avjindersinghsekhon.minimaltodo.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.support.v4.view.GravityCompat;

import com.example.avjindersinghsekhon.minimaltodo.About.AboutActivity;
import com.example.avjindersinghsekhon.minimaltodo.AppDefault.AppDefaultActivity;
import com.example.avjindersinghsekhon.minimaltodo.Board.BoardActivity;
import com.example.avjindersinghsekhon.minimaltodo.Board.BoardFragment;
import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.Settings.SettingsActivity;
import com.example.avjindersinghsekhon.minimaltodo.Utility.BoardItem;

public class MainActivity extends AppDefaultActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set the toolbar text to be the name of the corresponding board
        Intent i = getIntent();
        Bundle b = i.getExtras();
        BoardItem board = (BoardItem) b.get(BoardFragment.BOARDITEM);
        getSupportActionBar().setTitle(board.getBoardTitle());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId()) {
                    case R.id.hide_completed:
                        // add code to hide completed
                        break;
                    case R.id.boards_home:
                        menuItem.setChecked( true );
                        drawerLayout.closeDrawers();
                        intent = new Intent( MainActivity.this, BoardActivity.class );
                        startActivity( intent );
                        break;
                    case R.id.settings:
                        drawerLayout.closeDrawers();
                        intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.aboutMeMenuItem:
                        drawerLayout.closeDrawers();
                        intent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        drawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Respond when the drawer's position changes
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Respond when the drawer is opened
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Respond when the drawer is closed
                // Jackson Firth 5928
                Switch hide_completed = drawerView.findViewById(R.id.hide_switch);

                /*
                 * Tamara Charchoghlyan
                 * Find current fragment by ID and send message to show/hide completed todos
                 */
                MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if (hide_completed.isChecked()) {
                    fragment.hideCompletedToDos();
                } else {
                    fragment.showCompletedToDos();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Respond when the drawer motion state changes
            }
        });
    }

    @Override
    protected int contentViewLayoutRes() {
        return R.layout.activity_todo;
    }

    @NonNull
    @Override
    protected Fragment createInitialFragment() {
        return MainFragment.newInstance();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch (item.getItemId()) {
            case R.id.aboutMeMenuItem:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.home:
                drawerLayout.openDrawer( GravityCompat.START );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
