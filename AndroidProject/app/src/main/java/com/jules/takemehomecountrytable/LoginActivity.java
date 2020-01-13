package com.jules.takemehomecountrytable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.jules.takemehomecountrytable.Tools.AsyncResponse;
import com.jules.takemehomecountrytable.Tools.Internet.Authentication;
import com.jules.takemehomecountrytable.Tools.Internet.Internet;
import com.jules.takemehomecountrytable.Tools.Tools;

import java.io.File;

public class LoginActivity extends AppCompatActivity implements AsyncResponse {
    private static final String TAG = "loginActivity.debug";

    Authentication auth;

    // UI
    private TextInputEditText login, password;
    private Button loginButton;
    private TextView oups;
    private ProgressBar loading;
    private ImageView logo;

    // Variables
    String loginString, passString;
    boolean logging = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getString("username", "empty").equals("empty")) {
            launch(0, " ", " ");
        } else {
            setTheme(R.style.AppTheme);

            if (Internet.isOnline()) {
                setContentView(R.layout.activity_login);

                loginButton = findViewById(R.id.login_button);
                login = findViewById(R.id.login);
                password = findViewById(R.id.pass);
                oups = findViewById(R.id.oups);
                loading = findViewById(R.id.progress_bar);
                logo = findViewById(R.id.logo);

                loginButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (!logging && event.getAction() == MotionEvent.ACTION_DOWN) {
                            oups.setVisibility(View.INVISIBLE);
                            loading.setVisibility(View.VISIBLE);
                            Log.d("Button", "hey");
                            logging = true;
                            log(false);
                        }
                        return false;
                    }
                });

            } else {
                setContentView(R.layout.activity_login_no_internet);
            }
        }
    }

    private void log(Boolean anonym) {
        if (anonym) {
            launch(0, "anonym", "");
        } else {
            loginString = login.getText().toString();
            if (loginString.isEmpty()) {
                updateUI(0);
            } else {
                passString = password.getText().toString();
                if (passString.isEmpty()) {
                    updateUI(1);
                } else {
                    if (Internet.isOnline()) {
                        try {
                            auth = new Authentication();
                            auth.delegate = this;
                            auth.execute(loginString, passString, this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else
                        setContentView(R.layout.activity_login_no_internet);

                }
                passString = null;
            }
            loginString = null;
        }
    }

    private void launch(int select, String logString, String passString) {
        if (select == 0) {
            Intent intent = new Intent(this, WaitingRoomActivity.class);
            intent.putExtra("logString", logString);
            intent.putExtra("passString", passString);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }

    private void updateUI(int errorCode) {
        switch (errorCode) {
            case 0:
                oups.setText(getString(R.string.emptyEmail));
                break;
            case 1:
                oups.setText(getString(R.string.emptyPassword));
                break;
            case -1:
                oups.setText(getString(R.string.wrongCred));
                break;
            case -2:
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Tools.getDp(this, 300), Tools.getDp(this, 500));
                Bitmap bm = BitmapFactory.decodeFile(new File(this.getFilesDir(), "avatar.png").getAbsolutePath());
                logo.setImageBitmap(bm);
                logo.setLayoutParams(params);
                logo.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            default:
                oups.setText(getString(R.string.oups));

        }

        logging = false;
        loading.setVisibility(View.GONE);
        oups.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void processFinish(Integer output) {
        if (output == 0) {
            loginString = login.getText().toString();
            passString = password.getText().toString();
            launch(0, loginString, passString);
        } else {
            updateUI(output);
        }
    }
}