package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import barqsoft.footballscores.BuildConfig;
import barqsoft.footballscores.data.MatchDataParser;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class MatchFetchService extends IntentService {
    public static final String LOG_TAG = MatchFetchService.class.getSimpleName();

    private final String[] LEAGUES = {"394", "395", "396", "397", "398", "399", "400", "401", "402", "403", "404", "405"};

    public MatchFetchService() {
        super("MatchFetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        fetchDataForTimeFrame("n2");
        fetchDataForTimeFrame("p3");
    }

    private void fetchDataForTimeFrame(String timeFrame) {
        final String BASE_URL = "http://api.football-data.org/alpha/fixtures"; //Base URL
        final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days

        Uri fetchUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter(QUERY_TIME_FRAME, timeFrame)
                .build();

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        String jsonDataStr = null;
        try {
            URL fetchUrl = new URL(fetchUri.toString());
            Log.d(LOG_TAG, "fetchDataForTimeFrame : [" + timeFrame + "] URL is - " + fetchUrl);

            //Opening Connection
            httpURLConnection = (HttpURLConnection) fetchUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.addRequestProperty("X-Auth-Token", BuildConfig.FOOTBALL_API_KEY);
            httpURLConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream == null) {
                Log.e(LOG_TAG, "fetchDataForTimeFrame : InputStream is NULL");
                return;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String lineString;
            while ((lineString = bufferedReader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // stringBuffer for debugging.
                stringBuffer.append(lineString + "\n");
            }

            if (stringBuffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                Log.e(LOG_TAG, "fetchDataForTimeFrame : String Buffer Length is Zero");
                return;
            }
            jsonDataStr = stringBuffer.toString();
            //Log.d(LOG_TAG, "fetchDataForTimeFrame : JSON String " + jsonDataStr);
        } catch (Exception e) {
            Log.e(LOG_TAG, "fetchDataForTimeFrame : Exception - " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ioe) {
                    Log.e(LOG_TAG, "fetchDataForTimeFrame : IOException - " + ioe.getMessage());
                }
            }
        }

        new MatchDataParser(getApplicationContext(), jsonDataStr).parse();
    }
}