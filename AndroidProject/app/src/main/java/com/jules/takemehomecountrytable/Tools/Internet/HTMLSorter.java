package com.jules.takemehomecountrytable.Tools.Internet;

import com.jules.takemehomecountrytable.Fragments.Grade.Grade;
import com.jules.takemehomecountrytable.Fragments.Grade.Module;
import com.jules.takemehomecountrytable.Fragments.Grade.UE;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;

public class HTMLSorter {

    private LinkedList<String> lsUEStr = new LinkedList<>();
    private LinkedList<UE> lsUE = new LinkedList<>();
    private String HTML, year, semester;
    private float generalAverage;

    public HTMLSorter(String HTML) {
        this.HTML = HTML;
    }

    public String getYear() {
        return this.year;
    }
    public LinkedList<UE> getLsUE() {
        return this.lsUE;
    }
    public String getSemester() {
        return this.semester;
    }
    public float getGeneralAverage() {
        return this.generalAverage;
    }

    public void setGeneralAverage(float generalAverage) {
        this.generalAverage = generalAverage;
    }

    public static boolean is(String balise, int point, String str) {
        for (int i = 0; i < balise.length(); i++) {
            if (i + point < str.length()) {
                if (balise.toCharArray()[i] != str.toCharArray()[i + point]) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }
    public static boolean isSomewhere(String balise, String str) {
        int i = 0;
        while (i < str.length()) {
            if (is(balise, i, str)) {
                return true;
            }
            i++;
        }
        return false;
    }

    public static String removeTag(String str, String balise) {
        return str.replaceAll(balise, "");
    }
    public static String removeTag(String str, String[] arBalise) {
        String strReturn = str;
        for (String balise : arBalise) {
            strReturn = strReturn.replaceAll(balise, "");
        }
        return strReturn;
    }

    public void sortUE() {
        Document doc = Jsoup.parse(this.HTML);

        this.year = doc.select("body > h2").first().text();

        //get the current semester
        String semester = "S";
        int j;
        for (j = 0; j < this.year.length() && !is("Semestre", j, this.year); j++) {
        }
        semester += this.year.toCharArray()[j + "Semestre S".length()];
        this.semester = semester;

        Elements allUE = doc.select("body > ul");
        for (Element e : allUE) {
            UE newUE = createUE(Jsoup.parse(e.toString()));
            lsUE.add(newUE);
        }

        this.generalAverage = calculGeneraAverage();
    }

    public static Grade createGrade(Document strGrade) {
        Elements gradeElements = strGrade.select("td");

        if (gradeElements.first() == null)
            return null;

        //get name
        String name = (gradeElements.get(0).text()).replaceAll("&nbsp;", "");

        //get ref
        String ref = "";
        for (int j = 0; j < name.length() && !is(" -", j, name); j++) {
            ref += name.toCharArray()[j];
        }
        name = name.replaceAll(ref + " - ", "");

        //get value
        String value = (gradeElements.get(1).text()).replaceAll("&nbsp;", "");

        return new Grade(value, name, ref, 1);
    }
    public static Module createModule(Document strModule) {

        if (strModule.select("ul > b").first() == null)
            return null;

        //get name
        String name = strModule.select("ul > b").first().text();

        //get ref
        String ref = "";
        for (int j = 0; j < name.length() && !is(" -", j, name); j++) {
            ref += name.toCharArray()[j];
        }
        name = name.replaceAll(ref + " - ", "");

        Module newModule = new Module(name, ref);

        for (Element e : strModule.select("ul > ul")) {
            Grade newGrade = createGrade(Jsoup.parse(e.toString()));
            if (newGrade != null)
                newModule.addGrade(newGrade);
        }

        return newModule;
    }
    public static UE createUE(Document strUE) {

        if (strUE.select("ul > b").first() == null)
            return null;

        //get name
        String name = strUE.select("ul > b").first().text();

        //get ref
        String ref = "";
        for (int j = 0; j < name.length() && !is(" -", j, name); j++) {
            ref += name.toCharArray()[j];
        }
        name = name.replaceAll(ref + " - ", "");

        UE newUE = new UE(name, ref);

        for (Element e : strUE.select("ul > ul")) {
            Module newModule = createModule(Jsoup.parse(e.toString()));
            if (newModule != null)
                newUE.addModule(newModule);
        }

        return newUE;
    }

    public float calculGeneraAverage() {
        float total = 0;
        float coef = 0;
        for (UE ue: this.lsUE) {
            for (Module mod: ue.getAllModules()) {
                if (mod.getAverage() >= 0) {
                    if (mod.getCoef() >= 0) {
                        coef += mod.getCoef();
                        total += mod.getAverage() * mod.getCoef();
                    } else {
                        coef += 1;
                        total += mod.getAverage() * 1;
                    }
                }
            }
        }
        return total/coef;
    }


}