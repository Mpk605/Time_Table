package com.jules.takemehomecountrytable.Fragments.Map;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jules.takemehomecountrytable.R;
import com.jules.takemehomecountrytable.Tools.Internet.HTMLSorter;
import com.jules.takemehomecountrytable.Tools.Tools;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class tabMapExam extends Fragment {

    private HashMap<String, CardView> mapPlaces;
    private String hilightedPlace;
    private HashMap<String, Integer> row;
    private int nbRow;

    public tabMapExam() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //starting the Thread which get all places on firebase
        new Thread() {
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestPlaces("rangueil_cril", getView());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    getDataCoefFailed(getView());
                }
            }
        }.start();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_map_exam, container, false);
    }


    private void requestPlaces(String doc, View view) {
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


                                createPlaces(arraysPlaces, view, getActivity());
                                lastUpdate(view, arraysPlaces.get("lastUpdate")+"");

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


    }

    private void createPlaces(Map<String, Object> mapRow, View view, Activity activity) {

        LinearLayout mainLayout = view.findViewById(R.id.mainLayoutMapExam);

        this.mapPlaces = new HashMap<>();
        this.row = new HashMap<>();

        String[] rows = {"leftRow", "centerRow", "rightRow"};
        this.nbRow = 0;
        for (String row : rows) {
            //create vertical division vDiv
            LinearLayout vDiv = new LinearLayout(activity);
            LinearLayout.LayoutParams vDivLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            vDivLayoutParam.setMargins(0, 0, 0, 0);
            vDiv.setOrientation(LinearLayout.VERTICAL);
            //add it to hDiv
            mainLayout.addView(vDiv);

            //placing tables number
            ArrayList<Integer> lsPlaces = (ArrayList) mapRow.get(row);
            for (int i = 0; i < lsPlaces.size(); i += 4) {
                //create horizontal division hDiv
                LinearLayout hDiv = new LinearLayout(activity);
                LinearLayout.LayoutParams hDivLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                hDivLayoutParam.setMargins(0, 0, 0, 0);
                hDiv.setOrientation(LinearLayout.HORIZONTAL);
                vDiv.addView(hDiv);

                hDiv.addView(newPlace(lsPlaces.get(i) + "", activity));
                this.row.put(lsPlaces.get(i) + "", this.nbRow);
                hDiv.addView(newPlace(lsPlaces.get(i + 1) + "", activity));
                this.row.put(lsPlaces.get(i+1) + "", this.nbRow);
                hDiv.addView(newPlace(lsPlaces.get(i + 2) + "", activity));
                this.row.put(lsPlaces.get(i+2) + "", this.nbRow);
                hDiv.addView(newPlace(lsPlaces.get(i + 3) + "", activity));
                this.row.put(lsPlaces.get(i+3) + "", this.nbRow);
            }

            //separate rows
            if (row != rows[rows.length - 1]) {
            View dividerGrade = new View(activity);
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
            dividerParams.gravity = Gravity.CENTER_VERTICAL;
            dividerParams.setMargins(Tools.getDp(getActivity(), 30), 0, Tools.getDp(getActivity(), 30), 0);
            dividerGrade.setLayoutParams(dividerParams);
            dividerGrade.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
            mainLayout.addView(dividerGrade);
            this.nbRow ++;

            }
        }

        this.hilightedPlace = null;

        getView().findViewById(R.id.progress_bar_exam).setVisibility(View.GONE);

        initSearchBar(view);
    }

    private CardView newPlace(String number, Activity activity) {

        CardView place = new CardView(activity);
        LinearLayout.LayoutParams paramPlace = new LinearLayout.LayoutParams(175, 100);
        place.setLayoutParams(paramPlace);

        if (!HTMLSorter.isSomewhere("null", number + "")) {

            place.setBackgroundResource(R.drawable.border_exam_map);

            //fill the card with text
            TextView txtPlace = new TextView(activity);
            RelativeLayout.LayoutParams txtPlaceLayoutParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            txtPlaceLayoutParam.setMargins(Tools.getDp(activity, 16), Tools.getDp(activity, 8), Tools.getDp(activity, 16), Tools.getDp(activity, 8));
            txtPlace.setGravity(View.TEXT_ALIGNMENT_CENTER);
            txtPlace.setTextColor(ContextCompat.getColor(activity, android.R.color.black));
            txtPlace.setText(number);
            txtPlace.setTypeface(txtPlace.getTypeface(), Typeface.BOLD);
            txtPlace.setTextSize(15);
            txtPlace.setLayoutParams(txtPlaceLayoutParam);
            txtPlace.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            place.addView(txtPlace);

            this.mapPlaces.put(""+number, place);

            //Listener for the hilighting
            place.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hilightPlace(number, false);
                }
            });
        }

        return place;
    }

    private void lastUpdate(View view, String date) {
//TODO A REFAIRE
        //TextView mention = view.findViewById(R.id.mention);

        //Text last maj
        //mention.setText("Les numéros de place sont susceptibles de changer pour différentes raisons, elles on été mises à jours pour la dernière fois dans la base de données le " + date);

    }

    private void getDataCoefFailed(View view) {
        // TODO A REFAIRE
/**
        TextView mentions2 = view.findViewById(R.id.mention);

        //Text last maj
        LinearLayout.LayoutParams mentions2LayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mentions2LayoutParam.setMargins(Tools.getDp(getActivity(), 16), Tools.getDp(getActivity(), 16), Tools.getDp(getActivity(), 16), Tools.getDp(getActivity(), 16));
        mentions2.setLayoutParams(mentions2LayoutParam);
        mentions2.setGravity(View.TEXT_ALIGNMENT_CENTER);
        mentions2.setTextColor(Color.LTGRAY);

        String txtMentions2 = "Le plan de la salle exam n'a pas pu être récupéré, veuillez vérifiez votre connexion Internet";

        mentions2.setText(txtMentions2);
        mentions2.setTextSize(10);*/
    }

    public interface OnFragmentInteractionListener {
    }

    public void initSearchBar(View view) {

        SearchView simpleSearchView = (SearchView) view.findViewById(R.id.searchPlaceExam); // inititate a search view
        CharSequence query = simpleSearchView.getQuery(); // get the query string currently in the text field

        // perform set on query text listener event
        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
// do something on text submit
                Log.d("search","looking for " + query);
                hilightPlace(query, true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
// do something when text changes
                return false;
            }
        });

        simpleSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleSearchView.setIconified(false);
            }
        });
    }

    public void hilightPlace(String num, boolean forceSwipe) {
        HorizontalScrollView hsv = getView().findViewById(R.id.hScrollExam);

        if (this.hilightedPlace != null) {
            //remove old place
            this.mapPlaces.get(this.hilightedPlace).setBackgroundResource(R.drawable.border_exam_map);
            TextView txt = (TextView) this.mapPlaces.get(this.hilightedPlace).getChildAt(0);
            txt.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        }

        if (this.mapPlaces.containsKey(num)) {
            this.mapPlaces.get(num).setBackgroundResource(R.drawable.border_exam_map_hilight);
            TextView txt = (TextView) this.mapPlaces.get(num).getChildAt(0);
            txt.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            this.hilightedPlace = num;
            if (forceSwipe)
                hsv.scrollTo(667 * this.row.get(num), 0);
        } else {
            this.hilightedPlace = null;
        }
    }
}
