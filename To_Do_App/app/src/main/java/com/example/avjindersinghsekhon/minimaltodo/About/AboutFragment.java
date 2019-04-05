package com.example.avjindersinghsekhon.minimaltodo.About;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.example.avjindersinghsekhon.minimaltodo.AppDefault.AppDefaultFragment;
import com.example.avjindersinghsekhon.minimaltodo.R;

public class AboutFragment extends AppDefaultFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String appVersion = "2.0";
        TextView mVersionTextView = view.findViewById(R.id.aboutVersionTextView);
        mVersionTextView.setText(String.format(getResources().getString(R.string.app_version), appVersion));
    }

    @LayoutRes
    protected int layoutRes() {
        return R.layout.fragment_about;
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }
}
