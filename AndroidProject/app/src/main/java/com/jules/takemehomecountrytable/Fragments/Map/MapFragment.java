package com.jules.takemehomecountrytable.Fragments.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.jules.takemehomecountrytable.R;
import com.jules.takemehomecountrytable.Tools.Tools;

import java.io.FileNotFoundException;

public class MapFragment extends Fragment{

    private LinearLayout mainLayout;

    private TabLayout tablayout;
    private ViewPager viewPager;
    private TabItem tabExam, tabDep;
    private PageAdapter pagerAdapter;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        // Inflate the layout for this fragment


        tablayout = (TabLayout) view.findViewById(R.id.tablayout);
        tabExam = (TabItem) view.findViewById(R.id.exam);
        tabDep = (TabItem) view.findViewById(R.id.dep);
        viewPager = view.findViewById(R.id.viewpager);

        pagerAdapter = new PageAdapter(getActivity().getSupportFragmentManager(), tablayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tablayout.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) {
                    pagerAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 1) {
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //TODO: FAIRE EN SORTE DE GET LA TAILLE DU PLAN PARCE QUE C'EST PAS BEAU( le 1100 et le 1500)
        super.onViewCreated(view, savedInstanceState);
        mainLayout = view.findViewById(R.id.map_layout);

        DrawRoomMap plan = null;
        try {
            plan = new DrawRoomMap(getActivity());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap result = Bitmap.createBitmap(110 * 11, 1500, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        plan.setLayoutParams(new LinearLayout.LayoutParams(Tools.getDp(getActivity(), canvas.getWidth()), Tools.getDp(getActivity(), canvas.getHeight())));

        //mainLayout.addView(plan);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
