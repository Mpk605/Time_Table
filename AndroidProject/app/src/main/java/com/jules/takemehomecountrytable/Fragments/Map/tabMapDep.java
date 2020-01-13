package com.jules.takemehomecountrytable.Fragments.Map;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.jules.takemehomecountrytable.R;
import com.jules.takemehomecountrytable.Tools.Tools;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * A simple {@link Fragment} subclass.
 */
public class tabMapDep extends Fragment implements AdapterView.OnItemSelectedListener {

    private LinearLayout mainLayout;
    private Spinner spinnerImg;
    private ImageView imgPlan;

    public tabMapDep() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_map_dep, container, false);
        spinnerImg = (Spinner) view.findViewById(R.id.spinnerMapDep);
        imgPlan = (ImageView) view.findViewById(R.id.plan);
        spinnerImg.setOnItemSelectedListener(this);
        return view;

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) spinnerImg.getItemAtPosition(position);
        switch (item){
            case "RDC" :
                imgPlan.setImageBitmap(BitmapFactory.decodeFile(new File(getContext().getFilesDir(), "map3.png").toString()));
                break;
            case "Étage 1" :
                imgPlan.setImageBitmap(BitmapFactory.decodeFile(new File(getContext().getFilesDir(), "map2.png").toString()));
                break;
            case "Étage 2":
                //imgPlan.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.map));
                imgPlan.setImageBitmap(BitmapFactory.decodeFile(new File(getContext().getFilesDir(), "map.png").toString()));
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        Log.d("papillon","NOON");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
/*        mainLayout = view.findViewById(R.id.map_layout);

        DrawRoomMap plan = null;
        try {
            plan = new DrawRoomMap(getActivity());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap result = Bitmap.createBitmap(110 * 11, 1500, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        plan.setLayoutParams(new LinearLayout.LayoutParams(Tools.getDp(getActivity(), canvas.getWidth()), Tools.getDp(getActivity(), canvas.getHeight())));

        mainLayout.addView(plan);*/
    }


    public interface OnFragmentInteractionListener {
    }
}
