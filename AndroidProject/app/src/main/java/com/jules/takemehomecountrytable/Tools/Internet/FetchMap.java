package com.jules.takemehomecountrytable.Tools.Internet;

import android.content.Context;
import android.os.AsyncTask;

import com.jules.takemehomecountrytable.Tools.AsyncResponse;

public class FetchMap extends AsyncTask<Object, Void, Integer> {

    @Override
    protected Integer doInBackground(Object... params) {
        FirebaseHelper firebaseHelper = FirebaseHelper.getHelperInstance((Context) params[0]);
        firebaseHelper.downloadFileFromStorage((Context) params[0], "map3.png");
        firebaseHelper.downloadFileFromStorage((Context) params[0], "map2.png");
        firebaseHelper.downloadFileFromStorage((Context) params[0], "map.png");

        params = null;


        return 5;
    }

    public AsyncResponse delegate = null;

    @Override
    protected void onPostExecute(Integer result) {
        if (delegate != null)
            delegate.processFinish(result);
    }
}
