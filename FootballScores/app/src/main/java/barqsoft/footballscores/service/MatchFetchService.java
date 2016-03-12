package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class MatchFetchService extends IntentService {
    public static final String LOG_TAG = MatchFetchService.class.getSimpleName();

    private final String[] LEAGUES = { //Totally 12 LeagueIds
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
        for (String leagueId : LEAGUES){
            fetchTeamDetails(leagueId);
        }
    }

    private void fetchAllMatchScoresData(){
        for (String timeFrame : TIME_FRAMES) {
            fetchMatchScoresData(timeFrame);
        }
    }

    private void fetchTeamDetails(String leagueId){
        new FootBallSyncTask(getApplicationContext(),
                FootBallSyncTask.TEAM_FETCH_REQUEST,
                leagueId).execute();
        Log.d(LOG_TAG, "fetchTeamDetails : TEAM_FETCH_REQUEST " +
                "initiated for League[" + leagueId + "]");
    }

    private void fetchMatchScoresData(String timeFrame) {
        new FootBallSyncTask(getApplicationContext(),
                FootBallSyncTask.SCORE_FETCH_REQUEST,
                timeFrame).execute();
        Log.d(LOG_TAG, "fetchTeamDetails : SCORE_FETCH_REQUEST " +
                "initiated for TimeFrame[" + timeFrame + "]");
    }
}