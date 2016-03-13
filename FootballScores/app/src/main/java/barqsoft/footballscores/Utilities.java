package barqsoft.footballscores;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import barqsoft.footballscores.data.DatabaseContract;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities {

    public static final String[] TIME_FRAMES = {
            "n2", //For next Two days from Today
            "p3"  //For previous Two days from Today and Today itself
    };

    public static final int TOTAL_LEAGUES_COUNT = 12;

    public static final String[] LEAGUES = { //Totally 12 LeagueIds for Season 2015 to 2106
            "394", "395", "396", "397", "398", "399",
            "400", "401", "402", "403", "404", "405"
    };

    public static final int START_LEAGUE_ID = 394;
    public static final int END_LEAGUE_ID = 405;

    private static final String BUNDESLIGA_1 = " 1. Bundesliga 2015/16 "; // 394
    private static final String BUNDESLIGA_2 = " 2. Bundesliga 2015/16 "; // 395
    private static final String LIGUE_1 = " Ligue 1 2015/16 "; // 396
    private static final String LIGUE_2 = " Ligue 2 2015/16 "; // 397
    private static final String PREMIER_LEAGUE = " Premier League 2015/16 "; // 398
    private static final String PREMERA_DIVISION = " Primera Division 2015/16 "; // 399
    private static final String SEGUNDA_DIVISION = " Segunda Division 2015/16 "; // 400
    private static final String SERIA_A = " Serie A 2015/16 "; // 401
    private static final String PRIMEIRA_LIGA = " Primeira Liga 2015/16 "; // 402
    private static final String BUNDESLIGA_3 = " 3. Bundesliga 2015/16 "; // 403
    private static final String EREDIVISIE = " Eredivisie 2015/16 "; // 404
    private static final String CHAMPIONS_LEAGUE = " Champions League 2015/16 "; // 405
    private static final String UNKOWN_LEAGUE = "Unknown League. Please report!";

    private static final String ARSENAL_LONDON_FC = "Arsenal London FC";
    private static final String EVERTON_FC = "Everton FC";
    private static final String LEICESTER_CITY = "Leicester City";
    private static final String MANCHESTER_UNITED_FC= "Manchester United FC";
    private static final String STOKE_CITY_FC = "Stoke City FC";
    private static final String SUNDERLAN_FC = "Sunderland AFC";
    private static final String SWANSEA_CITY = "Swansea City";
    private static final String TOTTENHAM_HOTSPUR_FC = "Tottenham Hotspur FC";
    private static final String WEST_BROMWICH_ALBION = "West Bromwich Albion";
    private static final String WEST_HAM_UNITED_FC = "West Ham United FC";

    private static final int CHAMPIONS_LEAGUE_NUM = 362;

    public static final int DATE_PREVIOUS_DAY = -1;
    public static final int DATE_TODAY = 0;
    public static final int DATE_NEXT_DAY = 1;

    public static final int COL_MATCH_DATE = 1;
    public static final int COL_MATCH_TIME = 2;
    public static final int COL_LEAGUE_ID = 3;
    public static final int COL_MATCH_ID = 4;
    public static final int COL_MATCH_DAY = 5;
    public static final int COL_HOME_ID = 6;
    public static final int COL_AWAY_ID = 7;
    public static final int COL_HOME_NAME = 8;
    public static final int COL_AWAY_NAME = 9;
    public static final int COL_HOME_GOALS = 10;
    public static final int COL_AWAY_GOALS = 11;

    public static String getLeague(int leagueId) {
        //TODO change all these as String resources
        switch (leagueId) {
            case 394:   return BUNDESLIGA_1;
            case 395:   return BUNDESLIGA_2;
            case 396:   return LIGUE_1;
            case 397:   return LIGUE_2;
            case 398:   return PREMIER_LEAGUE;
            case 399:   return PREMERA_DIVISION;
            case 400:   return SEGUNDA_DIVISION;
            case 401:   return SERIA_A;
            case 402:   return PRIMEIRA_LIGA;
            case 403:   return BUNDESLIGA_3;
            case 404:   return EREDIVISIE;
            case 405:   return CHAMPIONS_LEAGUE;
            default:    return UNKOWN_LEAGUE;
        }
    }

    public static String getMatchDay(int matchDay, int leagueNum) {
        //TODO change all these as String resources
        if (leagueNum == CHAMPIONS_LEAGUE_NUM) {
            if (matchDay <= 6) {
                return "Group Stages, Match Day : 6";
            } else if (matchDay == 7 || matchDay == 8) {
                return "First Knockout Round";
            } else if (matchDay == 9 || matchDay == 10) {
                return "QuarterFinal";
            } else if (matchDay == 11 || matchDay == 12) {
                return "SemiFinal";
            } else {
                return "Final";
            }
        } else {
            return "Match day : " + String.valueOf(matchDay);
        }
    }

    public static int getTeamLogoByTeamName(String teamName) {
        if (teamName == null) {
            return R.drawable.ic_launcher;
        }
        /*
         * This is the set of icons that are currently in the app.
         * Feel free to find and add more as you go.
         */
        switch (teamName) {
            case ARSENAL_LONDON_FC:
                return R.drawable.arsenal;
            case EVERTON_FC:
                return R.drawable.everton_fc_logo1;
            case LEICESTER_CITY:
                return R.drawable.leicester_city_fc_hd_logo;
            case MANCHESTER_UNITED_FC:
                return R.drawable.manchester_united;
            case STOKE_CITY_FC:
                return R.drawable.stoke_city;
            case SUNDERLAN_FC:
                return R.drawable.sunderland;
            case SWANSEA_CITY:
                return R.drawable.swansea_city_afc;
            case TOTTENHAM_HOTSPUR_FC:
                return R.drawable.tottenham_hotspur;
            case WEST_BROMWICH_ALBION:
                return R.drawable.west_bromwich_albion_hd_logo;
            case WEST_HAM_UNITED_FC:
                return R.drawable.west_ham;
            default:
                return R.drawable.ic_launcher;
        }
    }

    public static String getScores(int homeGoals, int awayGoals) {
        if (homeGoals < 0 || awayGoals < 0) {
            //TODO change this to a String Resource
            return "Match yet to Start!";
        }
        return String.valueOf(homeGoals) + " - " + String.valueOf(awayGoals);
    }

    public static float getScoreTextSize(TextView score,
                                         int homeGoals, int awayGoals){
        if (homeGoals < 0 || awayGoals < 0) {
            //TODO change this to a float Resource
            return 20.0f;
        }
        return 22.0f;
    }

    public static int getScoreTextColor(int homeGoals, int awayGoals) {
        if (homeGoals < 0 || awayGoals < 0) {
            return Color.RED;
        }
        return Color.BLACK;
    }

    // Added from StackOverFlow
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        Log.e("Utilities", "No Internet Connection available.");
        return false;
    }

    public static String getTeamCrestURLStr(Context context,
                                            String teamId, String leagueId){
        String teamCrestUrl;

        Cursor cursor = context
                .getContentResolver()
                .query(DatabaseContract.TeamsTable.buildTeamCrestWithTeamId(teamId, leagueId),
                        null,
                        null,
                        null,
                        null);
        if (cursor != null && cursor.getCount() >= 1){
            cursor.moveToFirst();
            teamCrestUrl = cursor.getString(0);
            cursor.close();
            return teamCrestUrl;
        }else {
            //Log.e("Utilities", "getTeamCrestURLStr : Cursor Returned Empty!");
            if (cursor != null)
                cursor.close();
            return null;
        }
    }

    public static String getRequiredLocalDate (int whichDate){
        Date newDate = new Date(System.currentTimeMillis() + (whichDate * DateUtils.DAY_IN_MILLIS));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(newDate);
    }
}