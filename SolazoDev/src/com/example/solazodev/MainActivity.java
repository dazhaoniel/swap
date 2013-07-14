package com.example.solazodev;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.solazodev.utils.AppState;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends AbstractFragmentActivity {

    private AsyncTask<String, Void, String> mMainTask;
    private Solazo mSolazo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSolazo = Solazo.getInstance();

        if (AppState.getInstance(this).isOnline(this)) {
            mMainTask = new MainTask().execute();
        } else {
            setContentView(R.layout.activity_no_internet);
        }
    }

    @Override
    protected void onDestroy() {
        if (mMainTask != null && mMainTask.getStatus() == AsyncTask.Status.RUNNING) {
            mMainTask.cancel(true);

            mMainTask = null;
        }

        super.onDestroy();
    }

    private class MainTask extends AsyncTask<String, Void, String> {

//        @Override
//        protected void onPreExecute() {
////            showProgressBarIndeterminate();
//        }

        @Override
        protected String doInBackground(String... params) {
            try {
                if (isCancelled())
                    return null;

//                int count = 0;
//                while ( count < mSolazo.LOCATION_SERVICE_TIME_OUT_COUNT && mSolazo.getLocation() == null ) {
//                        wait(10000);
//                    count++;
//                }
//                return "success";
            } catch (Exception ignored) {
            }

            return null;

        } // end do in background

        @Override
        protected void onPostExecute(String result) {
//            if (mSolazo.getLocation() != null ) {
            Intent intent = new Intent(MainActivity.this, TabActivity.class);
            startActivity(intent);
            finish();
//            } else {
//                finish();
//            }
        }
    }
}