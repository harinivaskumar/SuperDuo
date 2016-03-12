package barqsoft.footballscores.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.data.TeamData;

/**
 * Created by Hari Nivas Kumar R P on 3/12/2016.
 */
public class TeamDataParserTask extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = TeamDataParserTask.class.getSimpleName();

    private final String TEAMS = "teams";

    private final String LINKS = "_links";
    private final String TEAM_NAME = "name";
    private final String SHORT_NAME = "shortName";
    private final String CREST_URL = "crestUrl";

    private final String SELF = "self";
    private final String HREF = "href";
    private final String TEAMS_LINK = "http://api.football-data.org/alpha/teams/";

    private Context mContext;
    private JSONArray teamsJSONArray;
    private TeamData mTeamData;
    private int teamCount;
    private String leagueId;

    public TeamDataParserTask(Context context, String jsonData, String leagueId){
        this.mContext = context;
        createJSONArrayWithTeams(jsonData);
        setTeamCount();
        setLeagueId(leagueId);
    }

    private void createJSONArrayWithTeams(String jsonData){
        try {
            teamsJSONArray = new JSONObject(jsonData).getJSONArray(TEAMS);
        }catch (JSONException e){
            Log.e(LOG_TAG, "createJSONArrayWithTeams : Exception - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void parse(){
        try {
            Vector<ContentValues> teamsDataVector = new Vector<ContentValues>(getTeamCount());

            for (int teamIndex = 0; teamIndex < getTeamCount(); teamIndex++) {
                mTeamData = new TeamData();
                populateTeamData(teamIndex);
                teamsDataVector.add(mTeamData.getTeamAsContentValues());
                mTeamData = null;
            }

            insertTeamDataToDB(teamsDataVector);
        }catch (JSONException jse){
            Log.e(LOG_TAG, "parse : JSONException - " + jse.getMessage());
            jse.printStackTrace();
        }catch (Exception e){
            Log.e(LOG_TAG, "parse : Exception - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateTeamData(int teamIndex) throws JSONException{
        JSONObject teamJSONObject = teamsJSONArray.getJSONObject(teamIndex);

        parseTeamAndLeagueIds(teamJSONObject.getJSONObject(LINKS));
        parseTeamName(teamJSONObject);
        parseTeamCrestUrl(teamJSONObject);
    }

    private void parseTeamAndLeagueIds(JSONObject linksJSONObject) throws JSONException{
        mTeamData.setTeamId(linksJSONObject
                .getJSONObject(SELF)
                .getString(HREF)
                .replace(TEAMS_LINK, ""));
        mTeamData.setLeagueId(getLeagueId());
    }

    private void parseTeamName(JSONObject teamJSONObject) throws JSONException{
        mTeamData.setTeamName(teamJSONObject.getString(TEAM_NAME));
        mTeamData.setShortName(teamJSONObject.getString(SHORT_NAME));
    }

    private void parseTeamCrestUrl(JSONObject teamJSONObject)throws JSONException{
        mTeamData.setCrestUrl(teamJSONObject.getString(CREST_URL));
    }

    private void insertTeamDataToDB(Vector<ContentValues> teamsDataVector){
        int insertedCount = 0;
        //Log.d(LOG_TAG, "insertTeamDataToDB : Team data vector Size - " + teamsDataVector.size());
        if (teamsDataVector.size() > 0) {
            ContentValues[] teamDataCV = new ContentValues[teamsDataVector.size()];
            teamsDataVector.toArray(teamDataCV);
            insertedCount = mContext.getContentResolver()
                    .bulkInsert(DatabaseContract.TeamsTable.TEAMS_BASE_CONTENT_URI, teamDataCV);
        }
        //Log.v(LOG_TAG,"insertTeamDataToDB : Successfully Inserted - " + insertedCount);
    }

    private int getTeamCount() {
        return teamCount;
    }

    private void setTeamCount() {
        this.teamCount = teamsJSONArray.length();
    }

    private String getLeagueId() {
        return leagueId;
    }

    private void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    @Override
    protected Void doInBackground(Void... params) {
        this.parse();
        return null;
    }
}