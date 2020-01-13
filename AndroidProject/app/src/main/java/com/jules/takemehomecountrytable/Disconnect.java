package com.jules.takemehomecountrytable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class Disconnect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();

        Log.d("Prefs", PreferenceManager.getDefaultSharedPreferences(this).getString("username", "empty"));

        startActivity(new Intent(this, LoginActivity.class));
    }
}
