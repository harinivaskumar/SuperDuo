package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.tasks.FootBallSyncTask;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class MatchFetchService extends IntentService {
    public static final String LOG_TAG = MatchFetchService.class.getSimpleName();

    private final int TOTAL_LEAGUES_COUNT = 12;

    private final String[] LEAGUES = { //Totally 12 LeagueIds for Season 2015 to 2106
            "394", "395", "396", "397", "398", "399",
            "400", "401", "402", "403", "404", "405"
    };
    private final String[] TIME_FRAMES = {
            "n2", //For next Two days from Today
            "p3"  //For previous Two days from Today and Today itself
    };

    public MatchFetchService() {
        super("MatchFetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        fetchAllTeamDetails();
        fetchAllMatchScoresData();
    }

    private void fetchAllTeamDetails(){
        int leaguesCount = 0;

        Uri leaguesCountUri = DatabaseContract.TeamsTable
                .buildLeaguesCount();

        Cursor cursor = getApplicationContext()
                .getContentResolver()
                .query(leaguesCountUri, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            leaguesCount = cursor.getInt(cursor.getColumnIndex(
                    DatabaseContract.TeamsTable.TOTAL_LEAGUES_COUNT));
            cursor.close();
            Log.v(LOG_TAG, "fetchAllTeamDetails : Total No of League's present - " + leaguesCount);
        }

        if (leaguesCount < TOTAL_LEAGUES_COUNT) {
            for (int leagueIdIndex = leaguesCount;
                 leagueIdIndex < TOTAL_LEAGUES_COUNT;
                 leagueIdIndex++) {
                fetchTeamDetails(LEAGUES[leagueIdIndex]);
            }
        }
    }

    private void fetchAllMatchScoresData(){
        for (String timeFrame : TIME_FRAMES) {
            fetchMatchScoresData(timeFrame);
        }
    }

    private void fetchTeamDetails(String leagueId){
        int leagueCount = 0;

        Uri leagueCountUri = DatabaseContract.TeamsTable
                .buildLeagueCountWithLeagueId(leagueId);

        Cursor cursor = getApplicationContext()
                .getContentResolver()
                .query(leagueCountUri, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            leagueCount = cursor.getInt(cursor.getColumnIndex(
                    DatabaseContract.TeamsTable.TOTAL_LEAGUES_COUNT));
            cursor.close();
            Log.v(LOG_TAG, "fetchTeamDetails :  No of LeagueId[" + leagueId +
                    "] present - " + leagueCount);
        }

        if (!Utilities.isNetworkAvailable(getApplicationContext())) {
            Log.e(LOG_TAG, "fetchTeamDetails : You are Offline!");
            return;
        }

        if (leagueCount == 0){
            new FootBallSyncTask(getApplicationContext(),
                    FootBallSyncTask.TEAM_FETCH_REQUEST,
                    leagueId).execute();
            Log.d(LOG_TAG, "fetchTeamDetails : TEAM_FETCH_REQUEST " + "Now initiated for League[" + leagueId + "]");
        }else{
            //Log.d(LOG_TAG, "fetchTeamDetails : TEAM_FETCH_REQUEST " + "Already Done/InProgress for League[" + leagueId + "]");
        }
    }

    private void fetchMatchScoresData(String timeFrame) {
        if (!Utilities.isNetworkAvailable(getApplicationContext())) {
            Log.e(LOG_TAG, "fetchMatchScoresData : You are Offline!");
            return;
        }

        new FootBallSyncTask(getApplicationContext(),
                FootBallSyncTask.SCORE_FETCH_REQUEST,
                timeFrame).execute();
        //Log.d(LOG_TAG, "fetchTeamDetails : SCORE_FETCH_REQUEST " + "initiated for TimeFrame[" + timeFrame + "]");
    }
}