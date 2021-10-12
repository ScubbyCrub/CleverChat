package edu.uw.tcss450.angelans.lab1myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(LOG_TAG, "Activity Create");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "Activity Start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "Activity Resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(LOG_TAG, "Activity Pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(LOG_TAG, "Activity Stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "Activity Destroy");
    }
}