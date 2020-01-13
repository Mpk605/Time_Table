package com.jules.takemehomecountrytable.Fragments.Grade;

import android.util.Log;

import com.jules.takemehomecountrytable.Tools.Internet.HTMLSorter;

import java.util.LinkedList;

public class Module {

    public LinkedList<Grade> grades = new LinkedList<>();
    private float coef;
    private float average;
    private String name;
    private String ref;

    public Module(String name, String ref) {
        this.name = name;
        this.ref = ref;
        this.coef = 1;
    }

    public float getCoef() {
        return this.coef;
    }
    public float getAverage() {
        return this.average;
    }
    public String getName() {
        return this.name;
    }
    public LinkedList<Grade> getAllGrades() {
        return grades;
    }
    public String getRef() {
        return this.ref;
    }

    public void setCoef(float coef) {
        this.coef = coef;
    }

    public void addGrade(Grade grade) {
        this.grades.add(grade);
        this.average = this.calcAverage();
    }

    public float calcAverage() {
        float total = 0;
        float coef = 0;
        for (Grade g : grades) {
            if (g.getValue() >= 0) {
                if (g.getCoef() <= 0) {
                    coef += -g.getCoef();
                    total += -g.getCoef() * g.getValue();
                } else {
                    coef += g.getCoef();
                    Log.d("average", "coef = " + g.getCoef());
                    total += g.getCoef() * g.getValue();
                }
            }
        }
        Log.d("debugUE", total+"/"+coef);
        if (coef == 0)
            return -1;
        return total/coef;
    }

    public void calcAverageAgain() {
        this.average = this.calcAverage();
    }

    public String toString() {
        String toStr = "" + this.name;
        for (Grade grade : grades) {
            toStr += "\n\t" + grade;
        }
        return toStr;
    }
}
