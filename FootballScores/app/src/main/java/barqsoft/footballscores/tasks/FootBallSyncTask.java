package barqsoft.footballscores.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import barqsoft.footballscores.BuildConfig;
import barqsoft.footballscores.Utilities;

/**
 * Created by Hari Nivas Kumar R P on 3/12/2016.
 */
public class FootBallSyncTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FootBallSyncTask.class.getSimpleName();

    private final String TEAMS_BASE_URL = "http://api.football-data.org/alpha/soccerseasons/"; //Team Base URL
    private final String TEAMS_QUERY_PARAM = "/teams";

    private final String SCORES_BASE_URL = "http://api.football-data.org/alpha/fixtures"; //Scores Base URL
    private final String TIME_FRAME_QUERY_PARAM = "timeFrame"; //Time Frame parameter to determine days

    public static final int TEAM_FETCH_REQUEST = 1;
    public static final int SCORE_FETCH_REQUEST = 2;

    private int requestType;
    private String leagueId, timeFrame, jsonDataStr;
    private Context mContext;

    public FootBallSyncTask(Context context, int requestType,
                            String leagueIdOrTimeFrame){
        mContext = context;
        setRequestType(requestType);
        if (isRequestTeamFetchRequestType()) {
            setLeagueId(leagueIdOrTimeFrame);
        }else{
            setTimeFrame(leagueIdOrTimeFrame);
        }
    }

    private boolean isRequestTeamFetchRequestType(){
        return getRequestType() == TEAM_FETCH_REQUEST;
    }

    private int getRequestType() {
        return requestType;
    }

    private void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    private String getLeagueId() {
        return leagueId;
    }

    private void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    private String getTimeFrame() {
        return timeFrame;
    }

    private void setTimeFrame(String timeFrame) {
        this.timeFrame = timeFrame;
    }

    private String getJsonDataStr() {
        return jsonDataStr;
    }

    private void setJsonDataStr(String jsonDataStr) {
        this.jsonDataStr = jsonDataStr;
    }

    private URL getFetchURL() throws MalformedURLException{
        Uri fetchUri = null;
        URL fetchUrl = null;
        if (isRequestTeamFetchRequestType()) {
            fetchUri = Uri.parse(TEAMS_BASE_URL + getLeagueId() + TEAMS_QUERY_PARAM);
            fetchUrl = new URL(fetchUri.toString());
            //Log.d(LOG_TAG, "getFetchURL : [" + getLeagueId() + "] URL is - " + fetchUrl);
        }else {
            fetchUri = Uri.parse(SCORES_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(TIME_FRAME_QUERY_PARAM, getTimeFrame())
                    .build();
            fetchUrl = new URL(fetchUri.toString());
            //Log.d(LOG_TAG, "getFetchURL : [" + getTimeFrame() + "] URL is - " + fetchUrl);
        }
        return fetchUrl;
    }

    private void parseJsonData(){
        if (isRequestTeamFetchRequestType()){
            new TeamDataParserTask(mContext, getJsonDataStr(), getLeagueId()).execute();
        }else{
            new MatchDataParserTask(mContext, getJsonDataStr()).execute();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        try {
            if (!Utilities.isNetworkAvailable(mContext)){
                Log.e(LOG_TAG, "You are Offline!");
                return null;
            }

            //Opening Connection
            httpURLConnection = (HttpURLConnection) getFetchURL().openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.addRequestProperty("X-Auth-Token", BuildConfig.FOOTBALL_API_KEY);
            httpURLConnection.connect();

            // Read the input stream into a String
            if (!Utilities.isNetworkAvailable(mContext)){
                Log.e(LOG_TAG, "You are Offline!");
                return null;
            }
            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream == null) {
                Log.e(LOG_TAG, "doInBackground : InputStream is NULL");
                return null;
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
                Log.e(LOG_TAG, "doInBackground : String Buffer Length is Zero");
                return null;
            }
            setJsonDataStr(stringBuffer.toString());
            //Log.d(LOG_TAG, "doInBackground : JSON String " + jsonDataStr);
        } catch (Exception e) {
            Log.e(LOG_TAG, "doInBackground : Exception - " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ioe) {
                    Log.e(LOG_TAG, "doInBackground : IOException - " + ioe.getMessage());
                    ioe.printStackTrace();
                }
            }
        }
        parseJsonData();
        return null;
    }
}