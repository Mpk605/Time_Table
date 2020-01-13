package com.jules.takemehomecountrytable.Fragments.Grade;

import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jules.takemehomecountrytable.R;
import com.jules.takemehomecountrytable.Tools.Internet.HTMLSorter;

public class Grade {

    private float value, coef;
    private String name, ref, valueAlt;
    private TextView txtName, txtValue;
    private LinearLayout lLGrade;

    public Grade(String value, String name, String ref, float coef) {

        this.valueAlt = "";
        try {
            this.value = Float.valueOf(value);
        } catch (NumberFormatException nfe) {
            if (HTMLSorter.isSomewhere("non diffusée", value)) {
                this.value = -2;
                this.valueAlt = "non diffusée";
            } else if (HTMLSorter.isSomewhere("n/a", value)) {
                this.value = -1;
                this.valueAlt = "n/a";
            } else {
                this.value = -1;
                this.valueAlt = value;
            }
        }

        this.name = name;
        this.ref = ref;
        this.coef = 1;
        this.coef = coef;
    }

    public float getValue() {
        return this.value;
    }
    public float getCoef() {
        return this.coef;
    }
    public String getName() {
        return this.name;
    }
    public String getRef() {
        return this.ref;
    }
    public String getValueAlt() {
        return this.valueAlt;
    }

    public void setCoef(float coef) {
        this.coef = coef;
    }
    public void setlLGrade(LinearLayout lLGrade) {
        this.lLGrade = lLGrade;
    }

    public String toString() {
        return this.name + " - " + this.value;
    }
}