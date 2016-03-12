package barqsoft.footballscores.data;

import android.content.ContentValues;

/**
 * Created by Hari Nivas Kumar R P on 3/12/2016.
 */
public class MatchData {
    private ContentValues matchAsContentValues;

    private String matchDate, matchTime, matchDay;
    private String leagueId, matchId;
    private String homeTeamName, awayTeamName;
    private String homeTeamId, awayTeamId;
    private String homeTeamGoals, awayTeamGoals;

    public MatchData(){
        setMatchAsContentValues();
    }

    public ContentValues getMatchAsContentValues() {
        return matchAsContentValues;
    }

    public void setMatchAsContentValues() {
        matchAsContentValues = new ContentValues();;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
        matchAsContentValues.put(DatabaseContract.ScoresTable.MATCH_DATE, getMatchDate());
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
        matchAsContentValues.put(DatabaseContract.ScoresTable.MATCH_TIME, getMatchTime());
    }

    public String getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
        matchAsContentValues.put(DatabaseContract.ScoresTable.LEAGUE_ID, getLeagueId());
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
        matchAsContentValues.put(DatabaseContract.ScoresTable.MATCH_ID, getMatchId());
    }

    public String getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(String matchDay) {
        this.matchDay = matchDay;
        matchAsContentValues.put(DatabaseContract.ScoresTable.MATCH_DAY, getMatchDay());
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
        matchAsContentValues.put(DatabaseContract.ScoresTable.HOME_TEAM_NAME, getHomeTeamName());
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
        matchAsContentValues.put(DatabaseContract.ScoresTable.AWAY_TEAM_NAME, getAwayTeamName());
    }

    public String getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(String homeTeamId) {
        this.homeTeamId = homeTeamId;
        matchAsContentValues.put(DatabaseContract.ScoresTable.HOME_TEAM_ID, getHomeTeamId());
    }

    public String getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(String awayTeamId) {
        this.awayTeamId = awayTeamId;
        matchAsContentValues.put(DatabaseContract.ScoresTable.AWAY_TEAM_ID, getAwayTeamId());
    }

    public String getHomeTeamGoals() {
        return homeTeamGoals;
    }

    public void setHomeTeamGoals(String homeTeamGoals) {
        this.homeTeamGoals = homeTeamGoals;
        matchAsContentValues.put(DatabaseContract.ScoresTable.HOME_TEAM_GOALS, getHomeTeamGoals());
    }

    public String getAwayTeamGoals() {
        return awayTeamGoals;
    }

    public void setAwayTeamGoals(String awayTeamGoals) {
        this.awayTeamGoals = awayTeamGoals;
        matchAsContentValues.put(DatabaseContract.ScoresTable.AWAY_TEAM_GOALS, getAwayTeamGoals());
    }
}