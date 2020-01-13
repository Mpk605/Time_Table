package com.jules.takemehomecountrytable.Tools.Internet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jules.takemehomecountrytable.Tools.AsyncResponse;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Authentication extends AsyncTask<Object, Void, Integer> {
    String cookie;

    private HttpResponse<String> POST(Object... params) throws UnirestException {
        Unirest.setTimeouts(1000, 5000);
        HttpResponse<String> base = Unirest.get("http://iut.ups-tlse.fr")
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; Pixel 3a Build/QP1A.191105.003; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/79.0.3945.79 Mobile Safari/537.36 GSA/10.88.11.21.arm64")
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                .header("accept-encoding", "deflate, br")
                .header("accept-language", "en-US,en;q=0.9,fr-FR;q=0.8,fr;q=0.7")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("DNT", "1")
                .header("charset", "utf-8")
                .header("Cache-Control", "max-age=0")
                .header("sec-fetch-mode", "navigate")
                .header("sec-fetch-site", "same-origin")
                .header("sec-fetch-user", "?1")
                .header("Upgrade-Insecure-Requests", "1")
                .asString();

        Log.d("HTPP", base.getHeaders().toString());

        cookie = base.getHeaders().get("set-cookie");

        return Unirest.post("https://auth.iut-tlse3.fr/")
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; Pixel 3a Build/QP1A.191105.003; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/79.0.3945.79 Mobile Safari/537.36 GSA/10.88.11.21.arm64")
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                .header("accept-encoding", "deflate, br")
                .header("accept-language", "en-US,en;q=0.9,fr-FR;q=0.8,fr;q=0.7")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("DNT", "1")
                .header("charset", "utf-8")
                .header("Cache-Control", "max-age=0")
                .header("sec-fetch-mode", "navigate")
                .header("sec-fetch-site", "same-origin")
                .header("sec-fetch-user", "?1")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Cookie", cookie)
                .field("url", "aHR0cHM6Ly93d3cuaXV0LXRsc2UzLmZyL3BvaW50dmlydHVlbC8=")
                .field("timezone", "1")
                .field("user", (String) params[0])
                .field("password", (String) params[1])
                .asString();
    }

    @Override
    protected Integer doInBackground(Object... params) {
        try {
            HttpResponse<String> result = POST(params);

            Document doc = Jsoup.parse(result.getBody());

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences((Context) params[2]);

            Log.d("HTTP", cookie);

            prefs.edit().putString("email", doc.getElementsByClass("iut_style-menu-bar-perscard").get(0).child(0).child(1).child(1).html()).apply();

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public AsyncResponse delegate = null;

    @Override
    protected void onPostExecute(Integer result) {
        if (delegate != null)
            delegate.processFinish(result);
    }
}
