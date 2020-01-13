package com.jules.takemehomecountrytable.Fragments.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jules.takemehomecountrytable.Fragments.Grade.Grade;
import com.jules.takemehomecountrytable.Fragments.Grade.Module;
import com.jules.takemehomecountrytable.Fragments.Grade.UE;
import com.jules.takemehomecountrytable.R;
import com.jules.takemehomecountrytable.Tools.Tools;

import java.io.FileNotFoundException;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ExamRoomFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private LinearLayout mainLayout;

    public ExamRoomFragment() {
        // Required empty public constructor
    }

    public static ExamRoomFragment newInstance() {
        ExamRoomFragment fragment = new ExamRoomFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDataFirebase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        /**
         View view = inflater.inflate(R.layout.fragment_exam_room, container, false);
         FirebaseFirestore db = FirebaseFirestore.getInstance();
         db.collection("examRoom")
         .get()
         .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
        for (QueryDocumentSnapshot document : task.getResult()) {
        Log.d(TAG, document.getId() + " => " + document.getData());
        }
        } else {
        Log.w(TAG, "Error getting documents.", task.getException());
        }
        }
        });
         return view;
         */
        return getView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //TODO: FAIRE EN SORTE DE GET LA TAILLE DU PLAN PARCE QUE C'EST PAS BEAU( le 1100 et le 1500)
        super.onViewCreated(view, savedInstanceState);
        mainLayout = view.findViewById(R.id.exam_room_layout);

        DrawRoomMap plan = null;
        try {
            plan = new DrawRoomMap(getActivity());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap result = Bitmap.createBitmap(1100, 1500, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        plan.setLayoutParams(new LinearLayout.LayoutParams(Tools.getDp(getActivity(), canvas.getWidth()), Tools.getDp(getActivity(), canvas.getHeight())));

        mainLayout.addView(plan);


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getDataFirebase() {
        //starting the Thread which get all coef on firebase
        new Thread() {
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestPlaces("rangueil_cril", getView());
                            //TODO ICI L'AFFICHAGE
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    //getDataCoefFailed(view);
                }
            }
        }.start();
    }

    private void requestPlaces(String doc, View view) {
        Log.d("debug -----", "requestPlaces");
        FirebaseApp.initializeApp(getActivity());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //code get from https://firebase.google.com/docs/firestore/query-data/get-data
        DocumentReference docRef = db.collection("examRoom").document(doc);
        docRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //Everything is good
                                Map<String, Object> arraysPlaces;
                                arraysPlaces = document.getData();

                                Log.d("debug-----", "" + arraysPlaces);

                                //TODO A FAIRE ICI avec les arraysPlaces

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


    }

    private void createPlaces(Map<String, int[]> mapRow, View view, Activity activity) {

        //create horizontal division hDiv
        LinearLayout hDiv = new LinearLayout(activity);
        LinearLayout.LayoutParams hDivLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hDivLayoutParam.setMargins(0, 0, 0, 0);
        hDiv.setOrientation(LinearLayout.HORIZONTAL);
        //add it to the scroll horizontal layout

        String[] rows = {"leftRow", "centerRow", "rightRow"};

        for (String row : rows) {

            //create vertical division vDiv
            LinearLayout vDiv = new LinearLayout(activity);
            LinearLayout.LayoutParams vDivLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            vDivLayoutParam.setMargins(0, 0, 0, 0);
            vDiv.setOrientation(LinearLayout.VERTICAL);
            //add it to hDiv
            hDiv.addView(vDiv);

            int[] lsPlaces = mapRow.get(row);
            for (int i = 0; i < lsPlaces.length; i += 4) {
                hDiv.addView(newPlace(lsPlaces[i] + "", activity));
                hDiv.addView(newPlace(lsPlaces[i + 1] + "", activity));
                hDiv.addView(newPlace(lsPlaces[i + 2] + "", activity));
                hDiv.addView(newPlace(lsPlaces[i + 3] + "", activity));
            }

            if (row != rows[rows.length]) {
                //create empty layout to separate
                LinearLayout separate = new LinearLayout(activity);
                LinearLayout.LayoutParams separateLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                separateLayoutParam.setMargins(Tools.getDp(activity, 8), 0, Tools.getDp(activity, 8), 0);
                separate.setOrientation(LinearLayout.VERTICAL);
                //add it to hDiv
                hDiv.addView(vDiv);
            }
        }
    }

    private TextView newPlace(String number, Activity activity) {
        TextView txtPlace = new TextView(activity);
        RelativeLayout.LayoutParams txtPlaceLayoutParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtPlaceLayoutParam.setMargins(Tools.getDp(activity, 1), Tools.getDp(activity, 1), Tools.getDp(activity, 1), Tools.getDp(activity, 1));
        txtPlace.setGravity(View.TEXT_ALIGNMENT_CENTER);
        txtPlace.setTextColor(ContextCompat.getColor(activity, android.R.color.black));
        txtPlace.setText(number);
        txtPlace.setTypeface(txtPlace.getTypeface(), Typeface.BOLD);
        txtPlace.setTextSize(15);
        txtPlace.setLayoutParams(txtPlaceLayoutParam);

        return txtPlace;
    }
}
