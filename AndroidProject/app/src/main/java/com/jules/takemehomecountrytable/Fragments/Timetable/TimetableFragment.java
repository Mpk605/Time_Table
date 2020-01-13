package com.jules.takemehomecountrytable.Fragments.Timetable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.jules.takemehomecountrytable.PeriodsDatabase.DAO.PeriodDao;
import com.jules.takemehomecountrytable.PeriodsDatabase.DAO.TutorDao;
import com.jules.takemehomecountrytable.PeriodsDatabase.Entities.Period;
import com.jules.takemehomecountrytable.PeriodsDatabase.Entities.Teacher;
import com.jules.takemehomecountrytable.PeriodsDatabase.PeriodsDatabase;
import com.jules.takemehomecountrytable.R;
import com.jules.takemehomecountrytable.TimeTableDetailsActivity;
import com.jules.takemehomecountrytable.Tools.BuildUI;
import com.jules.takemehomecountrytable.Tools.Tools;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class TimetableFragment extends Fragment {
    private int dayID;

    private int lastHour;

    public TimetableFragment() {
        // Required empty public constructor
    }

    private List<Period> buildPeriodMap(List<Period> periods) {
        for (int i = 0; i < periods.size(); i++) {
            int[] start = getHourAndMinuteFromString(periods.get(i).getStartHour());
            int[] end = getHourAndMinuteFromString(periods.get(i).getEndHour());

            periods.get(i).setPosition(getHourPosition(start[0], start[1]));
            if (lastHour < end[0])
                lastHour = end[0];
        }

        return periods;
    }

    private List<Period> getClasses() {
        PeriodsDatabase periodsDatabase = PeriodsDatabase.getInstance(getActivity());

        PeriodDao periodDao = periodsDatabase.getPeriodDao();

        Calendar calendarInstance = Calendar.getInstance();

        calendarInstance.add(Calendar.DAY_OF_MONTH, -((Tools.MAX_VALUE / 2) - dayID));

        int month = calendarInstance.get(Calendar.MONTH) + 1;
        int day = calendarInstance.get(Calendar.DAY_OF_MONTH);

        String startDate = calendarInstance.get(Calendar.YEAR) + "" + String.format("%02d", month) + "" + String.format("%02d", day) + "T000000Z";
        String endDate = calendarInstance.get(Calendar.YEAR) + "" + String.format("%02d", month) + "" + String.format("%02d", (day + 1)) + "T000000Z";

        List<Period> periods = periodDao.getDaysPeriods(startDate, endDate);

        return buildPeriodMap(periods);
    }

    static TimetableFragment newInstance(Bundle data) {
        TimetableFragment fragment = new TimetableFragment();
        Bundle dayBundle = new Bundle();
        dayBundle.putBundle("data", data);
        fragment.setArguments(dayBundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        Bundle data = getArguments();
        dayID = data.getBundle("data").getInt("day");

        Log.d("Integer", Tools.MAX_VALUE + "");
        Log.d("DAYID", dayID + "");

        Calendar today = Calendar.getInstance();

        today.add(Calendar.DAY_OF_MONTH, -((Tools.MAX_VALUE / 2) - dayID));

        Log.d("Picker", today.get(Calendar.DAY_OF_MONTH) + "");
        Log.d("Picker", today.getTimeInMillis() + "");

        List<Period> classesList = getClasses();

        if (classesList.isEmpty()) {
            view = inflater.inflate(R.layout.fragment_timetable_noclass, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_timetable, container, false);

            RelativeLayout mainLayout = view.findViewById(R.id.time_layout);

            Calendar dayCalendar = Calendar.getInstance();
            dayCalendar.set(Calendar.HOUR_OF_DAY, 8);
            dayCalendar.set(Calendar.MINUTE, 0);

            for (int i = 0; i < classesList.size(); i++) {
                final int tmp_i = i;

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                TutorDao tutorDao = PeriodsDatabase.getInstance(getContext()).getTutorDao();
                PeriodDao periodDao = PeriodsDatabase.getInstance(getContext()).getPeriodDao();

                int[] start = getHourAndMinuteFromString(classesList.get(i).getStartHour());
                int[] end = getHourAndMinuteFromString(classesList.get(i).getEndHour());

                /* Event MaterialCardView */
                MaterialCardView classContainer = BuildUI.buildMaterialCardView(getActivity(),
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        Tools.getDp(getActivity(), (int) ((8 + (64 * getHourPosition(end[0], end[1]))) - (16 + (64 * getHourPosition(start[0], start[1]))))),
                        16,
                        new int[]{60, (int) (16 + (classesList.get(i).getPosition() * 64)), 16, 0},
                        classesList.get(i).getColor());

                String tmp_room = String.valueOf(classesList.get(i).getRoom());

                if (tmp_room.length() <= 3 && tmp_room.length() != 0)
                    tmp_room = String.format("IN%03d", Integer.parseInt(classesList.get(i).getRoom()));

                final String room = tmp_room;

                if (!classesList.get(tmp_i).getTitle().isEmpty()) {
                    classContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent details = new Intent(v.getContext(), TimeTableDetailsActivity.class);
                            details.putExtra("title", classesList.get(tmp_i).getTitle());
                            details.putExtra("room", room);
                            details.putExtra("group", "S3E");
                            details.putExtra("color", classesList.get(tmp_i).getColor());
                            // prefs.getString("room", "empty")

                            details.putExtra("hour", getHourFromLongFormat(classesList.get(tmp_i).getStartHour()) + " - " + getHourFromLongFormat(classesList.get(tmp_i).getEndHour()));

                            ArrayList<String> teachers = new ArrayList<>();
                            for (Teacher teacher : tutorDao.getTeachersForPeriod(periodDao.getId(
                                    classesList.get(tmp_i).getStartHour(),
                                    classesList.get(tmp_i).getEndHour(),
                                    classesList.get(tmp_i).getRoom()
                            ))) {
                                teachers.add(teacher.getFullName());
                            }

                            String[] teachersArray = teachers.toArray(new String[0]);

                            details.putExtra("teachers", teachersArray);
                            startActivity(details);
                        }
                    });
                }

                mainLayout.addView(classContainer);

                LinearLayout classLayout = BuildUI.buildLinearLayout(getActivity(),
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.VERTICAL);

                classContainer.addView(classLayout);

                TextView roomTextView = BuildUI.buildTextView(getActivity(),
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        room + "  |  " + getHourFromLongFormat(classesList.get(i).getStartHour()) + " — " + getHourFromLongFormat(classesList.get(i).getEndHour()),
                        15,
                        new int[]{16, 8, 0, 0},
                        classesList.get(i).getColor());

                classLayout.addView(roomTextView);

                TextView moduleTextView = BuildUI.buildTextView(getActivity(),
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        classesList.get(i).getTitle(),
                        20,
                        new int[]{16, 0, 16, 8},
                        classesList.get(i).getColor());
                moduleTextView.setLines(2 * (int) (getHourPosition(end[0], end[1]) - getHourPosition(start[0], start[1]))); // Unit = 2

                classLayout.addView(moduleTextView);

                dayCalendar.add(Calendar.HOUR, 1);
                dayCalendar.add(Calendar.MINUTE, 30);
            }

            Calendar dividerCalendar = Calendar.getInstance();
            dividerCalendar.set(Calendar.HOUR_OF_DAY, 8);
            dividerCalendar.set(Calendar.MINUTE, 0);

            for (int i = 8; i < lastHour - 1; i++) {
                addHourDivider(mainLayout, dividerCalendar);

                dividerCalendar.add(Calendar.HOUR_OF_DAY, 1);
                dividerCalendar.add(Calendar.MINUTE, 30);
            }
        }

        int dialogTheme = resolveOrThrow(getContext(), R.attr.materialCalendarTheme);

        RelativeLayout detailsMonth = view.findViewById(R.id.details_month);
        detailsMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<?> builder =
                        setupDateSelectorBuilder(today);

                builder.setTheme(dialogTheme);

                builder.setTitleText("Selectionnez une date");

                try {
                    MaterialDatePicker<?> picker = builder.build();
                    picker.addOnPositiveButtonClickListener(
                            selection -> {
                                ViewPager mViewPager = getActivity().findViewById(R.id.container);

                                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                                calendar.setTimeInMillis(Long.valueOf(picker.getSelection().toString()));

                                DateTimeZone zone = DateTimeZone.getDefault();

                                DateTime now = new DateTime(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), 0, 0, 0, zone);
                                DateTime then = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0, zone);

                                mViewPager.setCurrentItem(dayID + Days.daysBetween(now, then).getDays());
                            });
                    picker.addOnNegativeButtonClickListener(
                            dialog -> {
                                Log.d("Picker", "User Cancel");
                            });
                    picker.addOnCancelListener(
                            dialog -> {
                                Log.d("Picker", "Cancel");
                            });
                    picker.show(getFragmentManager(), picker.toString());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        });

        TextView dayTextView;
        TextView dayDateTextView;
        dayDateTextView = view.findViewById(R.id.date_day_text);
        dayDateTextView.setText(
                String.format("%02d", today.get(Calendar.DAY_OF_MONTH)) + '/' +
                        String.format("%02d", today.get(Calendar.MONTH) + 1)
        );
        dayTextView = view.findViewById(R.id.day_text);

        switch (today.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                dayTextView.setText(getActivity().getResources().getString(R.string.monday));
                break;
            case Calendar.TUESDAY:
                dayTextView.setText(getActivity().getResources().getString(R.string.tuesday));
                break;
            case Calendar.WEDNESDAY:
                dayTextView.setText(getActivity().getResources().getString(R.string.wednesday));
                break;
            case Calendar.THURSDAY:
                dayTextView.setText(getActivity().getResources().getString(R.string.thursday));
                break;
            case Calendar.FRIDAY:
                dayTextView.setText(getActivity().getResources().getString(R.string.friday));
                break;
            case Calendar.SATURDAY:
                dayTextView.setText(getActivity().getResources().getString(R.string.saturday));
                break;
            case Calendar.SUNDAY:
                dayTextView.setText(getActivity().getResources().getString(R.string.sunday));
                break;
        }

        return view;
    }

    private MaterialDatePicker.Builder<?> setupDateSelectorBuilder(Calendar calendar) {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setSelection(calendar.getTimeInMillis());
        return builder;
    }

    private static int resolveOrThrow(Context context, @AttrRes int attributeResId) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attributeResId, typedValue, true)) {
            return typedValue.data;
        }
        throw new IllegalArgumentException(context.getResources().getResourceName(attributeResId));
    }

    private void addHourDivider(RelativeLayout mainLayout, Calendar dayCalendar) {
        // Hour divider
        LinearLayout dividerLayout = BuildUI.buildLinearLayout(getActivity(),
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dividerLayout.getLayoutParams();
        layoutParams.setMargins(0, Tools.getDp(getActivity(), (int) (2 + (getHourPosition(dayCalendar.get(Calendar.HOUR_OF_DAY), dayCalendar.get(Calendar.MINUTE)) * 64))), 0, 0);

        mainLayout.addView(dividerLayout);

        TextView hourTextView = BuildUI.buildTextView(getActivity(),
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                getHour(dayCalendar),
                15,
                new int[]{16, 0, 0, 0},
                R.color.white);

        dividerLayout.addView(hourTextView);

        View divider = BuildUI.buildDivider(getActivity(),
                new int[]{8, 0, 8, 0});

        dividerLayout.addView(divider);
    }

    public String getHourFromLongFormat(String longDate) {
        String hourString = longDate.split("T")[1];
        int hour = Integer.parseInt(hourString.substring(0, 2)) + 1;

        return String.format("%02d", hour) + ':' + hourString.substring(2, 4);
    }

    public int[] getHourAndMinuteFromString(String longDate) {
        String hourString = longDate.split("T")[1];
        int hour = Integer.parseInt(hourString.substring(0, 2)) + 1;

        return new int[]{hour, Integer.parseInt(hourString.substring(2, 4))};
    }

    public String getHour(Calendar calendar) {
        return String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ':' +
                String.format("%02d", calendar.get(Calendar.MINUTE));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public float getHourPosition(int hour, int minutes) {
        return (hour - 8) + ((float) minutes / 60.0f);
    }
}


// TODO try a relative layout with hour depending margin