package barqsoft.footballscores.data;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.R;

/**
 * Created by Hari Nivas Kumar R P on 3/12/2016.
 */
public class MatchDataParser {

    private final String LOG_TAG = MatchDataParser.class.getSimpleName();

    public final String FIXTURES = "fixtures";
    public final String LINKS = "_links";
    public final String SOCCER_SEASON = "soccerseason";
    public final String SELF = "self";
    public final String HREF = "href";

    public final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/"; //to fetch leagueId
    public final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/"; //to fetch matchId
    public final String TEAMS_LINK = "http://api.football-data.org/alpha/teams/"; //to fetch teamId

    public final String MATCH_DATE = "date";
    public final String MATCH_DAY = "matchday";
    public final String HOME_TEAM_NAME = "homeTeamName";
    public final String AWAY_TEAM_NAME = "awayTeamName";
    public final String HOME_TEAM_ID = "homeTeam";
    public final String AWAY_TEAM_ID = "awayTeam";
    public final String HOME_GOALS = "goalsHomeTeam";
    public final String AWAY_GOALS = "goalsAwayTeam";
    public final String RESULT = "result";

    private Context mContext;
    private JSONArray matchesJSONArray;
    private MatchData mMatchData;
    private int matchCount;
    private boolean isRealData;

    public MatchDataParser(Context context, String jsonData){
        this.mContext = context;
        createJSONArrayWithRealMatches(jsonData);
        setMatchCount();
        checkForRealData();
    }

    private void createJSONArrayWithRealMatches(String jsonData){
        try {
            matchesJSONArray = new JSONObject(jsonData).getJSONArray(FIXTURES);
        }catch (JSONException e){
            Log.e(LOG_TAG, "createJSONArrayWithRealMatches : Exception - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void modifyJSONArrayWithFakeMatches(String fakeData){
        try {
            matchesJSONArray = new JSONObject(fakeData).getJSONArray(FIXTURES);
        }catch (JSONException e){
            Log.e(LOG_TAG, "modifyMatchesJSONArray : Exception - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkForRealData(){
        if (getMatchCount() == 0){
            setRealData(false);
        }else{
            setRealData(true);
        }
    }

    public void parse() {
        try {
            Vector<ContentValues> matchesDataVector = new Vector<ContentValues>(getMatchCount());

            for (int index = 0; index < getMatchCount(); index++) {
                mMatchData = new MatchData();
                populateMatchData(index);
                matchesDataVector.add(mMatchData.getMatchAsContentValues());
                mMatchData = null;
            }

            insertMatchDataToDB(matchesDataVector);
        }catch (JSONException jse){
            Log.e(LOG_TAG, "parse : JSONException - " + jse.getMessage());
            jse.printStackTrace();
        }catch (ParseException pe){
            Log.e(LOG_TAG, "parse : ParseException - " + pe.getMessage());
            pe.printStackTrace();
        }catch (NullPointerException npe) {
            Log.e(LOG_TAG, "parse : NullPointerException - " + npe.getMessage());
            npe.printStackTrace();
        } catch (Exception e){
            Log.e(LOG_TAG, "parse : Exception - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateMatchData(int matchIndex)
            throws JSONException, ParseException, NullPointerException {
        JSONObject matchJSONObject = matchesJSONArray.getJSONObject(matchIndex);

        parseMatchAndTeamIds(matchJSONObject.getJSONObject(LINKS), matchIndex);
        parseMatchDateTimeAndDay(matchJSONObject, matchIndex);
        parseTeamNames(matchJSONObject);
        parseMatchResults(matchJSONObject.getJSONObject(RESULT));
    }

    private void parseMatchAndTeamIds(JSONObject linksJSONObject,
                                      int matchIndex) throws JSONException{
            if (isRealData()) {
                mMatchData.setMatchId(linksJSONObject
                        .getJSONObject(SELF)
                        .getString(HREF)
                        .replace(MATCH_LINK, ""));

                mMatchData.setLeagueId(linksJSONObject
                        .getJSONObject(SOCCER_SEASON)
                        .getString(HREF)
                        .replace(SEASON_LINK, ""));

                mMatchData.setHomeTeamId(linksJSONObject
                        .getJSONObject(HOME_TEAM_ID)
                        .getString(HREF)
                        .replace(TEAMS_LINK, ""));

                mMatchData.setAwayTeamId(linksJSONObject
                        .getJSONObject(AWAY_TEAM_ID)
                        .getString(HREF)
                        .replace(TEAMS_LINK, ""));
            } else {
                //This if statement changes the match ID of the dummy data so that it all goes into the database
                mMatchData.setMatchId(String.valueOf(matchIndex));
                mMatchData.setLeagueId(String.valueOf(matchIndex));
                mMatchData.setHomeTeamId(String.valueOf(matchIndex));
                mMatchData.setAwayTeamId(String.valueOf(matchIndex));
            }
    }

    private void parseMatchDateTimeAndDay(JSONObject matchJSONObject,
                                          int matchIndex) throws JSONException, ParseException{
        parseToLocalMatchDateAndTime(matchJSONObject, matchIndex);
        mMatchData.setMatchDay(matchJSONObject.getString(MATCH_DAY));
    }

    private void parseToLocalMatchDateAndTime(JSONObject matchJSONObject,
                                              int matchIndex) throws JSONException, ParseException{
        String matchDate = matchJSONObject.getString(MATCH_DATE);
        String matchTime = null;

        if (isRealData()) {
            matchTime = matchDate.substring(matchDate.indexOf("T") + 1, matchDate.indexOf("Z"));
            matchDate = matchDate.substring(0, matchDate.indexOf("T"));
            //Log.d(LOG_TAG, "parseToLocalMatchDateAndTime : Before Actual " +
            //        "matchDate - " + matchDate + " & matchTime - " + matchTime);

            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
            simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsedDate = simpleDateFormat1.parse(matchDate + matchTime);

            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
            simpleDateFormat2.setTimeZone(TimeZone.getDefault());

            matchDate = simpleDateFormat2.format(parsedDate);
            matchTime = matchDate.substring(matchDate.indexOf(":") + 1);
            matchDate = matchDate.substring(0, matchDate.indexOf(":"));
        } else {
            //This if statement changes the dummy data's date to match our current date range.
            Date fragmentDate = new Date(System.currentTimeMillis() + ((matchIndex - 2) * 86400000));
            SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
            matchDate = simpleDateFormat3.format(fragmentDate);
            matchTime = "";
        }
        //Log.d(LOG_TAG, "parseToLocalMatchDateAndTime : After Local " +
        //        "matchDate - " + matchDate + " & matchTime - " + matchTime);

        mMatchData.setMatchDate(matchDate);
        mMatchData.setMatchTime(matchTime);
    }

    private void parseTeamNames(JSONObject matchJSONObject) throws JSONException{
        mMatchData.setHomeTeamName(matchJSONObject.getString(HOME_TEAM_NAME));
        mMatchData.setAwayTeamName(matchJSONObject.getString(AWAY_TEAM_NAME));
    }

    private void parseMatchResults(JSONObject resultsJSONObject) throws JSONException{
        mMatchData.setHomeTeamGoals(resultsJSONObject.getString(HOME_GOALS));
        mMatchData.setAwayTeamGoals(resultsJSONObject.getString(AWAY_GOALS));
    }

    private void insertMatchDataToDB(Vector<ContentValues> matchesDataVector){
        int insertedCount = 0;
        Log.d(LOG_TAG, "insertMatchDataToDB : Match data vector Size - " + matchesDataVector.size());
        if (matchesDataVector.size() > 0) {
            ContentValues[] matchDataCV = new ContentValues[matchesDataVector.size()];
            matchesDataVector.toArray(matchDataCV);
            insertedCount = mContext.getContentResolver()
                    .bulkInsert(DatabaseContract.ScoresTable.SCORES_BASE_CONTENT_URI, matchDataCV);
        }
        Log.v(LOG_TAG,"insertMatchDataToDB : Successfully Inserted - " + insertedCount);
    }

    private int getMatchCount() {
        return matchCount;
    }

    private void setMatchCount() {
        this.matchCount = matchesJSONArray.length();
    }

    private boolean isRealData() {
        return isRealData;
    }

    private void setRealData(boolean isRealData) {
        this.isRealData = isRealData;
        if (!this.isRealData) {
            /*
            if there is no data, call the function on dummy data
            this is expected behavior during the off season.
            */
            modifyJSONArrayWithFakeMatches(mContext.getString(R.string.dummy_data));
        }
    }
}