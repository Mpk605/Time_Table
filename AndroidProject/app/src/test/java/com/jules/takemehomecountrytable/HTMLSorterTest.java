package com.jules.takemehomecountrytable;

import com.jules.takemehomecountrytable.Fragments.Grade.Grade;
import com.jules.takemehomecountrytable.Fragments.Grade.HTMLString;
import com.jules.takemehomecountrytable.Fragments.Grade.Module;
import com.jules.takemehomecountrytable.Fragments.Grade.UE;
import com.jules.takemehomecountrytable.Tools.Internet.HTMLSorter;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

class HTMLSorterTest {

    @Test
    void testCreateGrade() {
        String thisStr = HTMLString.strGrade;
        Document doc = Jsoup.parse(thisStr);

        Grade testGrade = HTMLSorter.createGrade(doc);

        assertEquals(testGrade.getRef(), "4R3202CE1");
        assertEquals(testGrade.getName(), "Soutenance");
        assertEquals(testGrade.getValue(), 11.0f);
        assertEquals(testGrade.getCoef(), 1f);
    }

    @Test
    void testCreateGradeNull() {
        Document doc = Jsoup.parse("String de test");

        Grade testGrade = HTMLSorter.createGrade(doc);

        assertTrue(testGrade == null);
    }

    @Test
    void testCreateModule() {
        String thisStr = HTMLString.strModule;
        Document doc = Jsoup.parse(thisStr);

        Module testModule = HTMLSorter.createModule(doc);

        assertEquals(testModule.getName(), "Probabilité et statistiques");
        assertEquals(testModule.getRef(), "4R3201");
        for (Object o : testModule.getAllGrades()) {
            assertTrue(o instanceof Grade);
        }
    }

    @Test
    void testCreateModuleNull() {
        Document doc = Jsoup.parse("String de test");

        Module testModule = HTMLSorter.createModule(doc);

        assertTrue(testModule == null);
    }


    @Test
    void testCreateUE() {
        String thisStr = HTMLString.strUE;
        Document doc = Jsoup.parse(thisStr);

        UE testUE = HTMLSorter.createUE(doc);

        assertEquals(testUE.getName(), "UE1-Informatique avancée");
        assertEquals(testUE.getRef(), "4RUE31");
        for (Object o : testUE.getAllModules()) {
            assertTrue(o instanceof Module);
        }
    }

    @Test
    void testCreateUENull() {
        Document doc = Jsoup.parse("String de test");

        UE testUE = HTMLSorter.createUE(doc);

        assertTrue(testUE == null);
    }
}
