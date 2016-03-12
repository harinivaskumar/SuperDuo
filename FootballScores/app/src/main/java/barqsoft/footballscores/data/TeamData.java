package barqsoft.footballscores.data;

import android.content.ContentValues;

/**
 * Created by Hari Nivas Kumar R P on 3/12/2016.
 */
public class TeamData {
    private ContentValues teamAsContentValues;

    private String teamId, leagueId;
    private String teamName, shortName;
    private String crestUrl;

    TeamData(){
        setTeamAsContentValues();
    }

    public ContentValues getTeamAsContentValues() {
        return teamAsContentValues;
    }

    public void setTeamAsContentValues() {
        teamAsContentValues = new ContentValues();;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
        teamAsContentValues.put(DatabaseContract.TeamsTable.TEAM_ID, getTeamId());
    }

    public String getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
        teamAsContentValues.put(DatabaseContract.TeamsTable.LEAGUE_ID, getLeagueId());
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
        teamAsContentValues.put(DatabaseContract.TeamsTable.NAME, getTeamName());
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
        teamAsContentValues.put(DatabaseContract.TeamsTable.SHORT_NAME, getShortName());
    }

    public String getCrestUrl() {
        return crestUrl;
    }

    public void setCrestUrl(String crestUrl) {
        this.crestUrl = crestUrl;
        teamAsContentValues.put(DatabaseContract.TeamsTable.CREST_URL, getCrestUrl());
    }
}