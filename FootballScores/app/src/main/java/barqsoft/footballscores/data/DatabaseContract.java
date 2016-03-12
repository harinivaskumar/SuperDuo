package barqsoft.footballscores.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class DatabaseContract {
    public static final String PATH_SCORES = "scores";
    public static final String PATH_TEAMS = "teams";

    //URI data
    public static final String CONTENT_AUTHORITY = "barqsoft.footballscores";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class ScoresTable implements BaseColumns {
        public static final String TABLE_NAME = "scores_table";

        public static final String MATCH_DATE = "match_date";
        public static final String MATCH_TIME = "match_time";
        public static final String LEAGUE_ID = "league_id";
        public static final String MATCH_ID = "match_id";
        public static final String MATCH_DAY = "match_day";
        public static final String HOME_TEAM_ID = "home_id";
        public static final String AWAY_TEAM_ID = "away_id";
        public static final String HOME_TEAM_NAME = "home_name";
        public static final String AWAY_TEAM_NAME = "away_name";
        public static final String HOME_TEAM_GOALS = "home_goals";
        public static final String AWAY_TEAM_GOALS = "away_goals";

        public static final Uri SCORES_BASE_CONTENT_URI = BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_SCORES).build();

        public static final String CONTENT_TYPE_DIR =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCORES;
        public static final String CONTENT_TYPE_ITEM =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCORES;

        public static Uri buildScoreWithDate(String date){//Dir with String
            return SCORES_BASE_CONTENT_URI.buildUpon()
                    .appendPath(date)
                    .build();
        }

        public static Uri buildScoreWithMatchId(int matchId){//Item with integer
            return ContentUris.withAppendedId(SCORES_BASE_CONTENT_URI, matchId);
        }

        public static String getDateFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static final class TeamsTable implements BaseColumns{
        public static final String TABLE_NAME = "teams_table";

        public static final String TEAM_ID = "team_id";
        public static final String NAME = "name";
        public static final String SHORT_NAME = "short_name";
        public static final String CREST_URL = "crest_url";
        public static final String LEAGUE_ID = "league_id";

        public static final Uri TEAMS_BASE_CONTENT_URI = BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_TEAMS).build();

        public static final String CONTENT_TYPE_DIR =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEAMS;
        public static final String CONTENT_TYPE_ITEM =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEAMS;

        public static Uri buildTeamCrestWithTeamId() { //Item
            return TEAMS_BASE_CONTENT_URI.buildUpon()
                    .appendPath("team-id")
                    .build();
        }

        public static String getTeamIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
}