package com.jules.takemehomecountrytable.Fragments.Grade;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jules.takemehomecountrytable.Fragments.Map.MapFragment;
import com.jules.takemehomecountrytable.R;
import com.jules.takemehomecountrytable.Tools.Internet.HTMLSorter;
import com.jules.takemehomecountrytable.Tools.Tools;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class GradeFragment extends Fragment {

    private LinearLayout mainLayout;
    private LinkedList<TextView> lkLsModules = new LinkedList<>();
    private LinkedList<TextView> lkLsUE = new LinkedList<>();
    private LinkedList<TextView> lkLsGrades = new LinkedList<>();
    private HTMLSorter sorter;
    private String updateStr;
    private Map<String, TextView> mapModuleTxtView, mapUETxtView;


    public GradeFragment() {
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

        View view = inflater.inflate(R.layout.fragment_grade, container, false);

        try {
            String strHTML = Tools.getStringFromFile(getActivity(), "xml.txt");
            sorter = new HTMLSorter(strHTML);

            try {
                createEverything(view, sorter);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            getFailed(view);
        }

        //starting the Thread which get all coef on firebase
        new Thread() {
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestCoef(sorter.getSemester(), view);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    getDataCoefFailed(view);
                }
            }
        }.start();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
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

    private void affValues() {
        for (TextView txtUE : this.lkLsUE) {
            //txtUE.setText("oui");
            txtUE.setVisibility(View.VISIBLE);
        }
        for (TextView txtMod : this.lkLsModules) {
            txtMod.setVisibility(View.VISIBLE);
        }
        getView().findViewById(R.id.progress_bar_grade).setVisibility(View.GONE);
    }

    private void createEverything(View view, HTMLSorter sorter) throws Exception {

        this.mapUETxtView = new HashMap<>();
        this.mapModuleTxtView = new HashMap<>();

/**
 *      Arborescence du tableau d'UE
 *
 *      LL
 *      └──MCV
 *           └──LL
 *               ├──RL
 *               │   ├──TxtView (UE name)
 *               │   └──TxtView (UE value)
 *               └──MCV
 *                   └──LL
 *                       ├──RL
 *                       │   ├──TxtView (Module name)
 *                       │   └──TxtView (Module value)
 *                       └──LL
 *                           └──RL
 *                               ├──TxtView (Grade name)
 *                               └──TxtView (Grade value)
 */


        sorter.sortUE();

        TextView txtYear = view.findViewById(R.id.txtYear);
        txtYear.setText(sorter.getYear());

        mainLayout = view.findViewById(R.id.grade_layout);

        //Create all UE
        int numUE = 0;
        for (UE ue : sorter.getLsUE()) {
            numUE++;

            //MaterialCardView UE
            MaterialCardView matCardViewUE = new MaterialCardView(getActivity());
            LinearLayout.LayoutParams matCardViewParamUE = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            matCardViewParamUE.setMargins(Tools.getDp(getActivity(), 10), Tools.getDp(getActivity(), 16), Tools.getDp(getActivity(), 10), Tools.getDp(getActivity(), 16));
            matCardViewUE.setLayoutParams(matCardViewParamUE);
            //matCardViewUE.setBackgroundResource(R.drawable.border);
            matCardViewUE.setElevation(Tools.getDp(getActivity(), 10));

            mainLayout.addView(matCardViewUE);

            //creation of UE LinearLayout
            LinearLayout newLinLayUE = generateLinearLayoutUE(getActivity(), numUE, ue);

            //Listener for the infoBulle
            newLinLayUE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupUE(ue);
                }
            });

            matCardViewUE.addView(newLinLayUE);

            //Create all modules
            for (Module modules : ue.mod) {
                boolean goldModule = false;
                if (modules.getAverage() >= 20) {
                    goldModule = true;
                }

                //MaterialCardView Module
                MaterialCardView matCardViewModule = new MaterialCardView(getActivity());
                LinearLayout.LayoutParams matCardViewLinLayModule = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                matCardViewLinLayModule.setMargins(Tools.getDp(getActivity(), 8), Tools.getDp(getActivity(), 8), Tools.getDp(getActivity(), 8), Tools.getDp(getActivity(), 8));
                matCardViewModule.setLayoutParams(matCardViewLinLayModule);
                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(Tools.getDp(getActivity(), 8));
                shape.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                matCardViewModule.setBackground(shape);
                matCardViewModule.setElevation(Tools.getDp(getActivity(), 5));
                if (goldModule) {
                    //Gold background if the grade >= 20
                    matCardViewModule.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.gradient_animation));
                    AnimationDrawable animGold;
                    matCardViewModule.setBackgroundResource(R.drawable.gradient_animation_module);
                    animGold = (AnimationDrawable) matCardViewModule.getBackground();
                    animGold.setEnterFadeDuration(10);
                    animGold.setExitFadeDuration(2000);
                    animGold.start();
                }
                matCardViewModule.setRadius((float) Tools.getDp(getActivity(), 8));

                newLinLayUE.addView(matCardViewModule);

                //creation of LinearLayout Module
                LinearLayout newLinLayModule = generateLinearLayoutModule(getActivity(), modules);

                //Listener for the infoBulle
                newLinLayModule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupModule(modules, ue);
                    }
                });

                matCardViewModule.addView(newLinLayModule);


                //Create all grades
                int divider = 0;
                for (Grade grades : modules.grades) {
                    //if not first grade for the module, add a divider
                    if (divider > 0) {
                        View dividerGrade = new View(getActivity());
                        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
                        dividerParams.gravity = Gravity.CENTER_VERTICAL;
                        dividerParams.setMarginEnd(Tools.getDp(getActivity(), 8));
                        dividerParams.setMarginStart(Tools.getDp(getActivity(), 8));
                        dividerGrade.setLayoutParams(dividerParams);
                        dividerGrade.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
                        newLinLayModule.addView(dividerGrade);
                    }
                    divider++;

                    //creation of LinearLayout Grade
                    LinearLayout newLinLayGrade = this.generateLinearLayoutGrade(getActivity(), grades);

                    //Listener for the infoBulle
                    newLinLayGrade.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupGrade(grades, modules);
                        }
                    });

                    grades.setlLGrade(newLinLayGrade);

                    newLinLayModule.addView(newLinLayGrade);
                }
            }
        }


        //Text mentions
        TextView mentions = new TextView(getActivity());
        LinearLayout.LayoutParams TxtMentionsLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TxtMentionsLayoutParam.setMargins(Tools.getDp(getActivity(), 16), Tools.getDp(getActivity(), 16), Tools.getDp(getActivity(), 16), Tools.getDp(getActivity(), 16));
        mentions.setLayoutParams(TxtMentionsLayoutParam);
        mentions.setGravity(View.TEXT_ALIGNMENT_CENTER);
        mentions.setTextColor(Color.LTGRAY);
        mentions.setText("Les moyennes des modules et UE sont susceptibles de changer ou d'être imprécises si les coefficients venaient à changer, pour plus de renseignements, veuillez contacter vos professeurs.");
        mentions.setTextSize(10);

        mainLayout.addView(mentions);

    }

    private void lastUpdate(View view, String date) {

        mainLayout = view.findViewById(R.id.grade_layout);

        //Text last maj
        //if (getActivity() == null) {
            TextView mentions2 = new TextView(getActivity());
            LinearLayout.LayoutParams mentions2LayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mentions2LayoutParam.setMargins(Tools.getDp(getActivity(), 16), 0, Tools.getDp(getActivity(), 16), Tools.getDp(getActivity(), 16));
            mentions2.setLayoutParams(mentions2LayoutParam);
            mentions2.setGravity(View.TEXT_ALIGNMENT_CENTER);
            mentions2.setTextColor(Color.LTGRAY);

            String txtMentions2 = getString(R.string.lastUpdate) + " " + date;

            mentions2.setText(txtMentions2);
            mentions2.setTextSize(10);

            mainLayout.addView(mentions2);
        //}
    }

    private void getDataCoefFailed(View view) {

        mainLayout = view.findViewById(R.id.grade_layout);

        //Text last maj
        TextView mentions2 = new TextView(getActivity());
        LinearLayout.LayoutParams mentions2LayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mentions2LayoutParam.setMargins(Tools.getDp(getActivity(), 16), Tools.getDp(getActivity(), 16), Tools.getDp(getActivity(), 16), Tools.getDp(getActivity(), 16));
        mentions2.setLayoutParams(mentions2LayoutParam);
        mentions2.setGravity(View.TEXT_ALIGNMENT_CENTER);
        mentions2.setTextColor(Color.LTGRAY);

        String txtMentions2 = "Les coefficients n'ont pas pu être récupérés, veuillez vérifiez votre connexion Internet";

        mentions2.setText(txtMentions2);
        mentions2.setTextSize(10);

        mainLayout.addView(mentions2);
    }

    private void getFailed(View view) {
        mainLayout = view.findViewById(R.id.grade_layout);

        //Text mentions
        TextView txtError = new TextView(getActivity());
        LinearLayout.LayoutParams txtErrorLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtErrorLayoutParam.setMargins(Tools.getDp(getActivity(), 16), Tools.getDp(getActivity(), 16), Tools.getDp(getActivity(), 16), Tools.getDp(getActivity(), 16));
        txtError.setLayoutParams(txtErrorLayoutParam);
        txtError.setGravity(View.TEXT_ALIGNMENT_CENTER);
        txtError.setTextColor(Color.LTGRAY);
        txtError.setText("Erreur dans la récupération du fichier de notes, peut-être devriez-vous vérifier votre connexion Internet?");
        txtError.setTextSize(10);

        mainLayout.addView(txtError);
    }

    private LinearLayout generateLinearLayoutUE(Activity activity, int nb, UE ue) {
        //Linear Layout UE n2
        LinearLayout linLayUE = new LinearLayout(activity);
        linLayUE.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linLayUE.setOrientation(LinearLayout.VERTICAL);

        //RelativeLayout UE
        RelativeLayout relLayUE = new RelativeLayout(activity);
        relLayUE.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linLayUE.addView(relLayUE);

        //Text Name UE
        TextView txtViewUEName = new TextView(activity);
        RelativeLayout.LayoutParams txtViewUENameLayoutParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtViewUENameLayoutParam.setMargins(Tools.getDp(activity, 16), Tools.getDp(activity, 16), Tools.getDp(activity, 16), Tools.getDp(activity, 16));
        txtViewUENameLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        txtViewUEName.setLayoutParams(txtViewUENameLayoutParam);
        txtViewUEName.setGravity(View.TEXT_ALIGNMENT_CENTER);
        txtViewUEName.setTextColor(ContextCompat.getColor(getActivity(), R.color.darkGray));
        txtViewUEName.setTypeface(txtViewUEName.getTypeface(), Typeface.BOLD);
        txtViewUEName.setText("UE" + nb);
        txtViewUEName.setTextSize(30);

        relLayUE.addView(txtViewUEName);

        //Text Value UE
        TextView txtViewUEValue = new TextView(activity);
        RelativeLayout.LayoutParams txtViewUEValueLayoutParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtViewUEValueLayoutParam.setMargins(Tools.getDp(activity, 16), Tools.getDp(activity, 16), Tools.getDp(activity, 16), Tools.getDp(activity, 16));
        txtViewUEValueLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        txtViewUEValue.setLayoutParams(txtViewUEValueLayoutParam);
        txtViewUEValue.setGravity(View.TEXT_ALIGNMENT_CENTER);
        txtViewUEValue.setTextColor(ContextCompat.getColor(getActivity(), R.color.darkGray));
        txtViewUEValue.setTypeface(txtViewUEValue.getTypeface(), Typeface.BOLD);
        if (ue.getAverage() < 0) {
            txtViewUEValue.setText("");
        } else {
            String value = String.format("%,.3f", ue.getAverage());
            txtViewUEValue.setText(value);
        }
        txtViewUEValue.setTextSize(30);
        txtViewUEValue.setVisibility(View.INVISIBLE);

        this.mapUETxtView.put(ue.getRef(), txtViewUEValue);

        this.lkLsUE.add(txtViewUEValue);
        relLayUE.addView(txtViewUEValue);

        return linLayUE;
    }

    private LinearLayout generateLinearLayoutModule(Activity activity, Module module) {
        boolean goldModule = false;

        float value = module.getAverage();
        if (value >= 20) {
            goldModule = true;
        }

        //LL Module
        LinearLayout linLayModule = new LinearLayout(activity);
        LinearLayout.LayoutParams linLayModuleLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linLayModuleLayoutParams.setMargins(0, 0, 0, Tools.getDp(activity, 8));
        linLayModule.setLayoutParams(linLayModuleLayoutParams);
        linLayModule.setOrientation(LinearLayout.VERTICAL);

        //RelativeLayout
        RelativeLayout relLayModule = new RelativeLayout(activity);
        relLayModule.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        linLayModule.addView(relLayModule);

        //Text name Module
        TextView txtViewModuleName = new TextView(activity);
        RelativeLayout.LayoutParams txtViewModuleNameLayoutParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtViewModuleNameLayoutParam.setMargins(Tools.getDp(activity, 16), Tools.getDp(activity, 16), Tools.getDp(activity, 96), Tools.getDp(activity, 16));
        txtViewModuleNameLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        txtViewModuleName.setLayoutParams(txtViewModuleNameLayoutParam);
        txtViewModuleName.setGravity(View.TEXT_ALIGNMENT_CENTER);
        txtViewModuleName.setTextColor(Color.WHITE);
        txtViewModuleName.setText(module.getName());
        txtViewModuleName.setTextSize(20);
        if (goldModule) {
            txtViewModuleName.setTextColor(ContextCompat.getColor(activity, R.color.goldDark));
            txtViewModuleName.setTypeface(txtViewModuleName.getTypeface(), Typeface.BOLD);
        }

        relLayModule.addView(txtViewModuleName);


        //Text value Module
        TextView txtViewModuleValue = new TextView(activity);
        RelativeLayout.LayoutParams txtViewModuleValueLayoutParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtViewModuleValueLayoutParam.setMargins(Tools.getDp(activity, 16), Tools.getDp(activity, 16), Tools.getDp(activity, 16), Tools.getDp(activity, 16));
        txtViewModuleValueLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        txtViewModuleValue.setLayoutParams(txtViewModuleValueLayoutParam);
        txtViewModuleValue.setGravity(View.TEXT_ALIGNMENT_CENTER);
        txtViewModuleValue.setTextColor(Color.WHITE);
        if (value < 0) {
            txtViewModuleValue.setText("");
        } else {
            txtViewModuleValue.setText(String.format("%,.3f", value));
        }
        txtViewModuleValue.setTextSize(20);
        if (goldModule) {
            txtViewModuleValue.setTextColor(ContextCompat.getColor(activity, R.color.goldDark));
            txtViewModuleValue.setTypeface(txtViewModuleValue.getTypeface(), Typeface.BOLD);
        }
        txtViewModuleValue.setVisibility(View.INVISIBLE);

        this.mapModuleTxtView.put(module.getRef(), txtViewModuleValue);

        this.lkLsModules.add(txtViewModuleValue);
        relLayModule.addView(txtViewModuleValue);

        return linLayModule;
    }

    private LinearLayout generateLinearLayoutGrade(Activity activity, Grade grade) {

        float value = grade.getValue();


        //LL Grade
        LinearLayout linLayGrade = new LinearLayout(activity);
        boolean isGold = false;
        if (value >= 20.000)
            isGold = true;


        LinearLayout.LayoutParams linLayGradeLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linLayGradeLayoutParam.setMargins(Tools.getDp(activity, 8), 0, Tools.getDp(activity, 8), 0);
        linLayGrade.setOrientation(LinearLayout.VERTICAL);
        if (value == -2) {
            //Transparency but less than -1 if grade isn't shown
            linLayGrade.setBackgroundColor(ContextCompat.getColor(activity, R.color.unscatteredbg));
        } else if (value == -1) {
            //Transparency if grade isn't available
            linLayGrade.setBackgroundColor(ContextCompat.getColor(activity, R.color.nabg));
        } else if (isGold) {
            //Gold background if the grade = 20
            linLayGrade.setBackground(ContextCompat.getDrawable(activity, R.drawable.gradient_animation));
            AnimationDrawable animGold;
            linLayGrade.setBackgroundResource(R.drawable.gradient_animation);
            animGold = (AnimationDrawable) linLayGrade.getBackground();
            animGold.setEnterFadeDuration(10);
            animGold.setExitFadeDuration(2000);
            animGold.start();
        } else {
            linLayGrade.setBackgroundColor(Color.WHITE);
        }
        linLayGrade.setLayoutParams(linLayGradeLayoutParam);

        //RelativeLayout Grade
        RelativeLayout relLayGrade = new RelativeLayout(activity);
        relLayGrade.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        linLayGrade.addView(relLayGrade);


        //Text Value Grade
        TextView txtViewGradeValue = new TextView(activity);
        RelativeLayout.LayoutParams txtViewGradeValueLayoutParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtViewGradeValue.setGravity(View.TEXT_ALIGNMENT_CENTER);
        txtViewGradeValue.setTextColor(ContextCompat.getColor(activity, android.R.color.black));
        if (value < 0) {
            txtViewGradeValue.setTextColor(ContextCompat.getColor(activity, R.color.natxt));
            txtViewGradeValueLayoutParam.setMargins(Tools.getDp(activity, 16), Tools.getDp(activity, 8), Tools.getDp(activity, 16), Tools.getDp(activity, 8));
            if (value < 0) {
                txtViewGradeValue.setText(grade.getValueAlt());
            }
        } else {
            txtViewGradeValue.setText("" + value);
            txtViewGradeValueLayoutParam.setMargins(Tools.getDp(activity, 16), Tools.getDp(activity, 16), Tools.getDp(activity, 16), Tools.getDp(activity, 16));
        }
        if (isGold) {
            txtViewGradeValue.setTextColor(ContextCompat.getColor(activity, R.color.goldDark));
            txtViewGradeValue.setTypeface(txtViewGradeValue.getTypeface(), Typeface.BOLD);
        }
        txtViewGradeValue.setTextSize(15);
        txtViewGradeValueLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        txtViewGradeValue.setLayoutParams(txtViewGradeValueLayoutParam);

        this.lkLsGrades.add(txtViewGradeValue);
        relLayGrade.addView(txtViewGradeValue);

        //Text Name Grade
        TextView txtViewGradeName = new TextView(activity);
        RelativeLayout.LayoutParams txtViewGradeNameLayoutParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtViewGradeNameLayoutParam.setMargins(Tools.getDp(activity, 16), Tools.getDp(activity, 16), Tools.getDp(activity, 40), Tools.getDp(activity, 16));
        txtViewGradeName.setGravity(View.TEXT_ALIGNMENT_CENTER);
        txtViewGradeName.setTextColor(ContextCompat.getColor(activity, android.R.color.black));
        txtViewGradeName.setText(grade.getName());
        if (isGold) {
            txtViewGradeName.setTextColor(ContextCompat.getColor(activity, R.color.goldDark));
            txtViewGradeName.setTypeface(txtViewGradeName.getTypeface(), Typeface.BOLD);
        }
        if (value < 0) {
            txtViewGradeNameLayoutParam.setMargins(Tools.getDp(activity, 16), Tools.getDp(activity, 8), Tools.getDp(activity, 40), Tools.getDp(activity, 8));
            txtViewGradeName.setTextColor(ContextCompat.getColor(activity, R.color.blackTransparentMaisPasTrop));
        }
        txtViewGradeName.setTextSize(15);
        txtViewGradeName.setLayoutParams(txtViewGradeNameLayoutParam);

        relLayGrade.addView(txtViewGradeName);

        return linLayGrade;
    }

    private void popupUE(UE ue) {
        Dialog popup = new Dialog(getActivity());
        popup.setContentView(R.layout.popup_ue);
        TextView txtName = popup.findViewById(R.id.name);
        TextView txtRef = popup.findViewById(R.id.ref);
        TextView txtValue = popup.findViewById(R.id.value);

        txtName.setText(ue.getName());
        txtRef.setText(ue.getRef());
        String val;
        if (ue.getAverage() < 0) {
            txtValue.setTextSize(15);
            val = getString(R.string.noGrades);
        } else {
            val = String.format("%.3f", ue.getAverage());
        }
        txtValue.setText(val);


        LinearLayout lsModules = popup.findViewById(R.id.lsModules);
        for (Module module : ue.getAllModules()) {
            TextView newTxtViewModule = new TextView(getActivity());
            newTxtViewModule.setPadding(Tools.getDp(getActivity(), 4), Tools.getDp(getActivity(), 4), Tools.getDp(getActivity(), 4), Tools.getDp(getActivity(), 4));
            LinearLayout.LayoutParams newTxtViewModuleLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            newTxtViewModule.setLayoutParams(newTxtViewModuleLayoutParam);
            newTxtViewModule.setTextColor(Color.WHITE);
            newTxtViewModule.setText("\t- " + module.getName() + " (" + module.getRef() + ")");
            newTxtViewModule.setTextSize(10);
            lsModules.addView(newTxtViewModule);
        }


        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.show();
    }

    private void popupModule(Module module, UE ue) {
        Dialog popup = new Dialog(getActivity());
        popup.setContentView(R.layout.popup_module);
        TextView txtName = popup.findViewById(R.id.name);
        TextView txtUE = popup.findViewById(R.id.ue);
        TextView txtCoef = popup.findViewById(R.id.coef);
        TextView txtRef = popup.findViewById(R.id.ref);
        TextView txtValue = popup.findViewById(R.id.value);

        txtName.setText(module.getName());
        txtUE.setText(ue.getName() + " (" + ue.getRef() + ")");
        float coef = module.getCoef();
        if (coef <= 0) {
            txtCoef.setText(getString(R.string.unknown));
        } else {
            txtCoef.setText("" + coef);
        }
        txtRef.setText(module.getRef());
        String val;
        if (module.getAverage() < 0) {
            txtValue.setTextSize(15);
            val = getString(R.string.noGrades);
        } else {
            val = String.format("%.2f", module.getAverage()) + "/20";
        }
        txtValue.setText(val);

        LinearLayout lsGrade = popup.findViewById(R.id.lsGrades);
        for (Grade grade : module.getAllGrades()) {
            TextView newTxtViewGrade = new TextView(getActivity());
            newTxtViewGrade.setPadding(Tools.getDp(getActivity(), 4), Tools.getDp(getActivity(), 4), Tools.getDp(getActivity(), 4), Tools.getDp(getActivity(), 4));
            LinearLayout.LayoutParams newTxtViewGradeLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            newTxtViewGrade.setLayoutParams(newTxtViewGradeLayoutParam);
            newTxtViewGrade.setTextColor(Color.WHITE);
            newTxtViewGrade.setText("\t- " + grade.getName() + " (" + grade.getRef() + ")");
            newTxtViewGrade.setTextSize(10);
            lsGrade.addView(newTxtViewGrade);
        }


        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.show();
    }

    private void popupGrade(Grade grade, Module module) {
        Dialog popup = new Dialog(getActivity());
        popup.setContentView(R.layout.popup_grade);
        TextView txtName = popup.findViewById(R.id.name);
        TextView txtModule = popup.findViewById(R.id.module);
        TextView txtCoef = popup.findViewById(R.id.coef);
        TextView txtRef = popup.findViewById(R.id.ref);
        TextView txtValue = popup.findViewById(R.id.value);

        txtName.setText(grade.getName());
        txtModule.setText(module.getName() + " (" + module.getRef() + ")");
        float coef = grade.getCoef();
        if (coef <= 0) {
            txtCoef.setText(getString(R.string.unknown));
        } else {
            txtCoef.setText("" + coef);
        }
        txtRef.setText(grade.getRef());
        String val;
        if (grade.getValue() < 0) {
            if (grade.getValue() < -1) {
                val = getString(R.string.notShared);
            } else {
                val = grade.getValueAlt();
            }
        } else {
            val = grade.getValue() + "/20";
        }
        txtValue.setText(val);

        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.show();
    }

    private void requestCoef(String S, View view) {

        FirebaseApp.initializeApp(getActivity());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //code get from https://firebase.google.com/docs/firestore/query-data/get-data
        DocumentReference docRef = db.collection("coef").document(S);
        docRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //Everything is good
                                Map<String, Object> coefData;
                                //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                coefData = document.getData();
                                //Log.d("---------", coefData + "");
                                updateStr = coefData.get("lastUpdate") + "";
                                for (UE ue : sorter.getLsUE()) {
                                    for (Module module : ue.getAllModules()) {
                                        if (coefData.get(module.getRef()) == null || Float.parseFloat(coefData.get(module.getRef()) + "f") <= 0) {
                                            //if module coef not found, set to 1
                                            Log.d("setCoef", "valeur de " + module.getRef() + " non trouvée, set à -1");
                                            module.setCoef(-1);
                                        } else {
                                            //if module coef found, set
                                            Object newCoef = coefData.get(module.getRef());
                                            Log.d("setCoef", "set du module " + module.getRef() + " à " + newCoef);
                                            module.setCoef(Float.parseFloat(newCoef + "f"));
                                        }
                                        for (Grade grade : module.getAllGrades()) {
                                            if (coefData.get(grade.getRef()) == null || Float.parseFloat(coefData.get(grade.getRef()) + "f") <= 0) {
                                                //if grade coef not foud, set to 1/number of grade for the module
                                                float newCoef = -(1f / module.getAllGrades().size());
                                                Log.d("setCoef", "       valeur de " + grade.getRef() + " non trouvée, set à " + newCoef);
                                                grade.setCoef(newCoef);
                                            } else {
                                                //if grade coef found, set
                                                Object newCoef = coefData.get(grade.getRef());
                                                Log.d("setCoef", "       set de la note " + grade.getRef() + " à " + newCoef);
                                                grade.setCoef(Float.parseFloat(newCoef + "f"));
                                            }
                                        }
                                    }
                                }
                                calculAgainAverages();
                                affValues();
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                        lastUpdate(view, updateStr);
                    }
                });


    }

    private void calculAgainAverages() {
        for (UE ue : this.sorter.getLsUE()) {
            for (Module module : ue.getAllModules()) {
                if (module.getAverage() >= 0) {
                    module.calcAverageAgain();
                    String val = String.format("%.2f", module.getAverage()) + "";
                    Log.d("calcAverage", "val = " + val);
                    mapModuleTxtView.get(module.getRef()).setText(val);
                }

            }
            if (ue.getAverage() >= 0) {
                ue.calcAverageAgain();
                String val = String.format("%.3f", ue.getAverage()) + "";
                Log.d("calcAverage", "val = " + val);
                mapUETxtView.get(ue.getRef()).setText(val);
            }
        }
        this.sorter.setGeneralAverage(this.sorter.calculGeneraAverage());
        TextView genAvgTxt = getView().findViewById(R.id.genAvgTxt);
        genAvgTxt.setText(String.format("%.3f", this.sorter.getGeneralAverage()) + "");
    }
}
