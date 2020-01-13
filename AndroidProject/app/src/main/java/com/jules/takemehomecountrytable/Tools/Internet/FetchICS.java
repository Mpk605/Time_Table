package com.jules.takemehomecountrytable.Tools.Internet;

import android.content.Context;
import android.os.AsyncTask;

import androidx.preference.PreferenceManager;

import com.jules.takemehomecountrytable.PeriodsDatabase.DAO.PeriodDao;
import com.jules.takemehomecountrytable.PeriodsDatabase.DAO.TeacherDao;
import com.jules.takemehomecountrytable.PeriodsDatabase.DAO.TutorDao;
import com.jules.takemehomecountrytable.PeriodsDatabase.Entities.Teacher;
import com.jules.takemehomecountrytable.PeriodsDatabase.Entities.Tutor;
import com.jules.takemehomecountrytable.PeriodsDatabase.PeriodsDatabase;
import com.jules.takemehomecountrytable.R;
import com.jules.takemehomecountrytable.Tools.AsyncResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.util.DateTimeComponents;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FetchICS extends AsyncTask<Object, Void, Integer> {
    private OkHttpClient client = new OkHttpClient();

    private String POST(String emailAddress) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("__EVENTTARGET", "")
                .add("__EVENTARGUMENT", "")
                .add("__LASTFOCUS", "")
                .add("__VIEWSTATE", "/wEPDwULLTEwMDUyNjYzMjgPZBYCZg9kFgICAw9kFggCAw8PFgIeBFRleHQFFUlVVCAnQScgUGF1bCBTYWJhdGllcmRkAgUPDxYCHwAFFlVuaXZlcnNpdMOpIFRvdWxvdXNlIDNkZAIHDw8WAh8ABQkyMDE4LzIwMTlkZAIJD2QWAgIDDw8WAh8ABSNQbGVhc2UgY29udGFjdCB1cyBpZiB5b3UgbmVlZCBoZWxwLmRkZPswtdUDyI3IwR5+M5B7cQFVFWUvnu8CzNc8tUfY4u0A")
                .add("__VIEWSTATEGENERATOR", "69BE51F3")
                .add("__EVENTVALIDATION", "/wEdAAYTUGNGICPNmxuJATyqZ/CSQ4OC5ej/xbGFAHPZpv1H8Zcr9ta/ztGV1JRD1BcigtX9li3g/yRIWkNwOwYzDKaHKyMEtceVT7MJRiMsjII5S/qrHqe2gOdKR4QAVi7QLXQvx6rrTqiQ3RTDfxHQ4JdqfcoNHZEyexN1pEMB6yLGgA==")
                .add("ctl00$MainContentPH$languageDropDown", "en")
                .add("ctl00$MainContentPH$EMAIL_TB", emailAddress)
                .add("ctl00$MainContentPH$emailSubmitButton", "Submit")
                .build();
        Request request = new Request.Builder().url("https://edt.iut-tlse3.fr/Icalendar/")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36")
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                .header("accept-encoding", "gzip, deflate, br")
                .header("accept-language", "en-US,en;q=0.9")
                .header("sec-fetch-mode", "navigate")
                .header("sec-fetch-site", "same-origin")
                .header("sec-fetch-user", "?1")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().toString();
        }
    }

    private String getICSLink(Object... params) {
        try {
            Properties props = new Properties();

            String host = "imaps-etu.iut-tlse3.fr";

            // Connect to the server
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect(host, (String) params[2], (String) params[3]);

            params = null;

            // Open the inbox folder
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            while (true) {
                Message[] messages = inbox.getMessages();

                String regex = "\\(?\\b(https://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
                Pattern p = Pattern.compile(regex);

                for (Message m : messages) {
                    if (((InternetAddress) m.getFrom()[0]).getAddress().equals("cri.celcat@iut-tlse3.fr")) {
                        Matcher matcher = p.matcher(m.getContent().toString());
                        if (matcher.find()) {
                            m.setFlag(Flags.Flag.DELETED, true);
                            inbox.expunge();
                            inbox.close(true);
                            store.close();
                            return matcher.group();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected Integer doInBackground(Object... params) {
        try {
            URL url;
            if (PreferenceManager.getDefaultSharedPreferences((Context) params[0]).getString("ICSLink", "empty").equals("empty")) {
                POST((String) params[1]);
                String link = getICSLink(params);
                PreferenceManager.getDefaultSharedPreferences((Context) params[0]).edit().putString("ICSLink", link).apply();
                url = new URL(link);
            } else {
                url = new URL(PreferenceManager.getDefaultSharedPreferences((Context) params[0]).getString("ICSLink", "empty"));
            }
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();

            InputStream is = c.getInputStream();

            PeriodsDatabase periodsDatabase = PeriodsDatabase.getInstance((Context) params[0]);

            PeriodDao periodDao = periodsDatabase.getPeriodDao();
            TeacherDao teacherDao = periodsDatabase.getTeacherDao();
            TutorDao tutorDao = periodsDatabase.getTutorDao();

            try {
                List<ICalendar> icals = Biweekly.parse(is).all();
                DateTimeComponents eventStartDate, eventEndDate;

                int i = 0;

                for (ICalendar iCalendar : icals) {
//                    Log.d("SQL", iCalendar.getEvents().size() + "events");
                    for (VEvent event : iCalendar.getEvents()) {
                        eventStartDate = event.getDateStart().getValue().getRawComponents();
                        eventEndDate = event.getDateEnd().getValue().getRawComponents();

                        String[] summaries = event.getSummary().getValue().split("; ");
                        int color;

                        switch (summaries[0]) {
                            case "TP":
                                color = R.color.tp;
                                break;
                            case "TD":
                                color = R.color.td;
                                break;
                            case "CM":
                                color = R.color.cm;
                                break;
                            case "Examen":
                                color = R.color.exam;
                                break;
                            default:
                                color = R.color.lightGray;
                                break;
                        }

                        try {
                            periodDao.insert(new com.jules.takemehomecountrytable.PeriodsDatabase.Entities.Period(
                                    summaries[1],
                                    event.getLocation().getValue(),
                                    summaries[0],
                                    eventStartDate.toString(),
                                    eventEndDate.toString(),
                                    i,
                                    color));

                            String[] description = event.getDescription().getValue().split("\n");
                            for (String s : description) {
                                if (s.contains("Tuteur")) {
                                    for (String teacher : s.split(": ")[1].split("; ")) {
                                        String[] teacherInHalf = teacher.split(" ");
                                        String firstName, lastName;
                                        if (teacherInHalf.length == 2) {
                                            firstName = teacherInHalf[1];
                                            lastName = teacherInHalf[0];
                                        } else {
                                            firstName = "Jesus";
                                            lastName = "Christ";
                                        }

                                        try {
                                            tutorDao.insert(new Tutor(
                                                    i,
                                                    lastName
                                            ));
                                        } catch (Exception e) {
                                            // This is supposed to happen from time to time
                                        }

                                        try {
                                            teacherDao.insert(new Teacher(firstName, lastName));

                                        } catch (Exception e) {
                                            // This is supposed to happen from time to time
                                        }
                                    }
                                }
                            }

                            i++;
                        } catch (Exception e) {
                            // This is supposed to happen from time to time
                        }
                    }
                }

            } catch (Exception e) {
                // This is supposed to happen from time to time
            }

            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
