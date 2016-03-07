package barqsoft.footballscores;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities {
    private static final int BUNDESLIGA = 351;
    private static final int PREMIER_LEGAUE = 354;
    private static final int SERIE_A = 357;
    private static final int PRIMERA_DIVISION = 358;
    private static final int CHAMPIONS_LEAGUE = 362;

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

    public static String getLeague(int league_num) {
        switch (league_num) {
            case BUNDESLIGA:
                return "Bundesliga";
            case PREMIER_LEGAUE:
                return "Premier League";
            case SERIE_A:
                return "Seria A";
            case PRIMERA_DIVISION:
                return "Primera Division";
            case CHAMPIONS_LEAGUE:
                return "UEFA Champions League";
            default:
                return "Unknown League. Please report!";
        }
    }

    public static String getMatchDay(int matchDay, int leagueNum) {
        if (leagueNum == CHAMPIONS_LEAGUE) {
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

    public static String getScores(int homeGoals, int awayGoals) {
        if (homeGoals < 0 || awayGoals < 0) {
            return " - ";
        } else {
            return String.valueOf(homeGoals) + " - " + String.valueOf(awayGoals);
        }
    }

    public static int getTeamLogoByTeamName(String teamName) {
        if (teamName == null) {
            return R.drawable.no_icon;
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
                return R.drawable.no_icon;
        }
    }
}