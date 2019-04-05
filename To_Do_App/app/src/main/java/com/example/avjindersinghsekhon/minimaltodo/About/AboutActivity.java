package com.example.avjindersinghsekhon.minimaltodo.About;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.avjindersinghsekhon.minimaltodo.AppDefault.AppDefaultActivity;
import com.example.avjindersinghsekhon.minimaltodo.Main.MainFragment;
import com.example.avjindersinghsekhon.minimaltodo.R;


public class AboutActivity extends AppDefaultActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar toolbar;
        String theme = getSharedPreferences( MainFragment.THEME_PREFERENCES, MODE_PRIVATE ).getString( MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME );
        if (theme != null && theme.equals( MainFragment.DARKTHEME )) {
            setTheme( R.style.CustomStyle_DarkTheme );
        }
        else {
            setTheme( R.style.CustomStyle_LightTheme );
        }

        super.onCreate(savedInstanceState);

        final Drawable backArrow = getResources().getDrawable( R.drawable.abc_ic_ab_back_material ); // Jackson Firth 5928
        if (backArrow != null) {
            backArrow.setColorFilter( Color.WHITE, PorterDuff.Mode.SRC_ATOP );
        }

        toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled( true );
            getSupportActionBar().setHomeAsUpIndicator( backArrow );
        }
    }

    @Override
    protected int contentViewLayoutRes() {
        return R.layout.about_layout;
    }

    @NonNull
    protected Fragment createInitialFragment() {
        return AboutFragment.newInstance();
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }
}
