package com.example.solazodev;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SubmitActivity extends SherlockFragmentActivity {

    private static final String LOGTAG = "com.example.solazodev.SubmitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentById(android.R.id.content) == null) {
            SubmitFragment submitResults = new SubmitFragment();
            fm.beginTransaction().add(android.R.id.content, submitResults).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static class SubmitFragment extends SherlockFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.activity_submit, container, false);
        }

    }
}
