package com.jules.takemehomecountrytable.Tools.Internet;

import android.content.Context;
import android.os.AsyncTask;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jules.takemehomecountrytable.Tools.AsyncResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;

public class FetchGrades extends AsyncTask<Object, Void, Integer> {
    /*
     *  Si tu lis ces mots et que le site visunotes a ete mis a jour
     *  tu peux remplacer ce code et juste faire une requete post vers visunotes avec
     *  {"identifiant": "xxx0000x", "pass": "******"}
     *  */

    @Override
    protected Integer doInBackground(Object... params) {
        try {
            java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

            // HtmlUnit
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setRedirectEnabled(true);

            HtmlPage page = webClient.getPage("https://notes.info.iut-tlse3.fr/visuNotes.php");

            HtmlForm form = (HtmlForm) page.getElementById("login");

            HtmlButton button = (HtmlButton) page.getElementsByTagName("button").get(0);

            HtmlInput id = form.getInputByName("identifiant");
            HtmlInput pass = form.getInputByName("pass");

            id.type((String) params[1]);
            pass.type((String) params[2]);

            button.click();

            webClient.waitForBackgroundJavaScript(30000);

            HtmlPage page2 = (HtmlPage) page.getEnclosingWindow().getEnclosedPage();

//            Log.d("AUTH", page2.asXml());

            String pageString = page2.asXml();

            Document doc = Jsoup.parse(pageString);

            if (doc.selectFirst("h1") == null)
                return 2;
            else {
                File xml = new File(((Context) params[0]).getFilesDir(), "xml.txt");

                FileOutputStream fout = new FileOutputStream(xml);
                ObjectOutputStream oos = new ObjectOutputStream(fout);

                oos.writeObject(pageString);

                oos.close();

                return 5;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 5;
    }

    public AsyncResponse delegate = null;

    @Override
    protected void onPostExecute(Integer result) {
        if (delegate != null)
            delegate.processFinish(result);
    }
}
