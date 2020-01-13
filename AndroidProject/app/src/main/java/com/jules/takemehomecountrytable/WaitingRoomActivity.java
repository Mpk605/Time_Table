package com.jules.takemehomecountrytable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.jules.takemehomecountrytable.Tools.AsyncResponse;
import com.jules.takemehomecountrytable.Tools.Cipher.CipherTools;
import com.jules.takemehomecountrytable.Tools.Internet.FetchAvatar;
import com.jules.takemehomecountrytable.Tools.Internet.FetchGrades;
import com.jules.takemehomecountrytable.Tools.Internet.FetchICS;
import com.jules.takemehomecountrytable.Tools.Internet.FetchMap;
import com.jules.takemehomecountrytable.Tools.Internet.FirebaseHelper;

public class WaitingRoomActivity extends AppCompatActivity implements AsyncResponse {
    FetchICS fetchICS = new FetchICS();
    FetchGrades fetchGrades = new FetchGrades();
    FetchAvatar fetchAvatar = new FetchAvatar();
    FetchMap fetchMap = new FetchMap();

    private int moveOn = 0;
    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        String passString = getIntent().getStringExtra("passString");
        String logString = getIntent().getStringExtra("logString");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        FirebaseHelper firebaseHelper = FirebaseHelper.getHelperInstance(this);

        if (passString.equals(" ")) { // TODO update ics and grades every now and then
            String[] cred = CipherTools.decipherCredentials(prefs);

            if (cred != null) {
                Log.d("Cred", "is not null");

                firebaseHelper.authenticateFirebaseUser(prefs.getString("email", "empty"), cred[1]);

                fetchICS.execute(this, prefs.getString("email", "empty"), cred[0], cred[1]);
                fetchGrades.execute(this, cred[0], cred[1]);
                fetchMap.execute(this);

                logString = null;
                passString = null;
            }

            startActivity(new Intent(this, MainActivity.class));
        } else {
            CipherTools.cipherCredentials(prefs, new String[]{logString, passString});
            firebaseHelper.authenticateFirebaseUser(prefs.getString("email", "empty"), passString);

            fetchICS.delegate = this;
            fetchGrades.delegate = this;
            fetchAvatar.delegate = this;
            fetchMap.delegate = this;

            fetchICS.execute(this, prefs.getString("email", "empty"), logString, passString);
            fetchGrades.execute(this, logString, passString);
            fetchAvatar.execute(logString, passString, this);
            fetchMap.execute(this);

            logString = null;
            passString = null;

            setContentView(R.layout.activity_waiting_room);

            mMediaPlayer = MediaPlayer.create(this, R.raw.bossa);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void processFinish(Integer output) {
        moveOn += output;

        if (moveOn == 20) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
