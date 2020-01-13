package com.jules.takemehomecountrytable.Fragments.Grade;

import android.util.Log;

import com.jules.takemehomecountrytable.Tools.Internet.HTMLSorter;

import java.util.LinkedList;
import java.util.Map;

public class UE {

    public LinkedList<Module> mod = new LinkedList<>();
    private float average;
    private String name;
    private String ref;

    public UE(String name, String ref) {
        this.name = name;
        this.ref = ref;
    }

    public String getName() {
        return this.name;
    }
    public float getAverage() {
        return average;
    }
    public String getRef() {
        return this.ref;
    }

    public void addModule(Module mod) {
        this.mod.add(mod);
        this.average = this.calcAverage();
    }

    public float calcAverage() {
        float average = 0;
        float coef = 0;
        for (Module m : mod) {
            if (m.getAverage() >= 0) {
                if (m.getCoef() <= 0) {
                    coef += -m.getCoef();
                    average += -m.getCoef() * m.getAverage();
                } else {
                    coef += m.getCoef();
                    average += m.getCoef() * m.getAverage();
                }
            }
        }
        if (coef == 0)
            return -1;
        return average/coef;
    }

    public void calcAverageAgain() {
        this.average = this.calcAverage();
    }

    public LinkedList<Module> getAllModules() {
        LinkedList<Module> list = new LinkedList<>();
        for (Module module : this.mod) {
            list.add(module);
        }
        return list;
    }

    public void setAllCoef(Map<String, Object> mCoef) {
        Log.d("---- debug", "MAP == "+mCoef);

        for (Module modules: mod){
            for (Grade grade: modules.getAllGrades()) {
                //set all coef on each grade
                String laref = grade.getRef();
                try {
                    Object coef = mCoef.get(laref);
                    Log.d("---- debug", "ref: " + laref + " - coef : " + coef);
                    grade.setCoef(Float.parseFloat(coef+""));
                } catch (Exception e ) {
                    Log.d("---- debug", "la référence |" + laref + "| n'existe pas");
                }
            }
        }
    }

    @Override
    public String toString() {
        String str = "\n---------- " + this.name + " ----------\n";
        for(Module module : mod) {
            str += module;
        }
        return str;
    }
}


