package com.jules.takemehomecountrytable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jules.takemehomecountrytable.Tools.BuildUI;

public class TimeTableDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_details);

        try {
            String[] teachers = getIntent().getStringArrayExtra("teachers");
            TextView timeView = findViewById(R.id.details_time);
            timeView.setText(getIntent().getStringExtra("hour"));

            TextView roomView = findViewById(R.id.details_room);
            roomView.setText(getIntent().getStringExtra("room"));

            TextView groupView = findViewById(R.id.details_groups);
            groupView.setText(getIntent().getStringExtra("group"));

            TextView titleView = findViewById(R.id.details_title);
            titleView.setText(getIntent().getStringExtra("title"));
            titleView.setBackgroundColor(ContextCompat.getColor(this, getIntent().getIntExtra("color", 0)));

            Log.d("BS", getIntent().getIntExtra("color", 0) + "");

            LinearLayout tutors = findViewById(R.id.details_tutors);

            for (String s : teachers) {
                TextView tutor = BuildUI.buildTextView(this,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        s,
                        15,
                        new int[]{64, 0, 0, 0},
                        R.color.lightGray);

                tutors.addView(tutor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
