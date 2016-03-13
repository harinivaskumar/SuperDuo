package barqsoft.footballscores.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class FootballDataDBHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = FootballDataDBHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "Scores.db";
    private static final int DATABASE_VERSION = 1;

    final String CREATE_SCHEMA_SCORES_TABLE = "CREATE TABLE " + FootballDataContract.ScoresTable.TABLE_NAME + " ("
            + FootballDataContract.ScoresTable._ID + " INTEGER PRIMARY KEY,"
            + FootballDataContract.ScoresTable.MATCH_DATE + " TEXT NOT NULL,"
            + FootballDataContract.ScoresTable.MATCH_TIME + " INTEGER NOT NULL,"
            + FootballDataContract.ScoresTable.LEAGUE_ID + " INTEGER NOT NULL,"
            + FootballDataContract.ScoresTable.MATCH_ID + " INTEGER NOT NULL,"
            + FootballDataContract.ScoresTable.MATCH_DAY + " INTEGER NOT NULL,"
            + FootballDataContract.ScoresTable.HOME_TEAM_ID + " INTEGER NOT NULL,"
            + FootballDataContract.ScoresTable.AWAY_TEAM_ID + " INTEGER NOT NULL,"
            + FootballDataContract.ScoresTable.HOME_TEAM_NAME + " TEXT NOT NULL,"
            + FootballDataContract.ScoresTable.AWAY_TEAM_NAME + " TEXT NOT NULL,"
            + FootballDataContract.ScoresTable.HOME_TEAM_GOALS + " TEXT NOT NULL,"
            + FootballDataContract.ScoresTable.AWAY_TEAM_GOALS + " TEXT NOT NULL,"
            + " UNIQUE (" + FootballDataContract.ScoresTable.MATCH_ID + ") ON CONFLICT REPLACE"
            + " );";

    final String DELETE_SCHEMA_SCORES_TABLE = "DROP TABLE IF EXISTS " + FootballDataContract.ScoresTable.TABLE_NAME;

    final String CREATE_SCHEMA_TEAMS_TABLE = "CREATE TABLE " + FootballDataContract.TeamsTable.TABLE_NAME + " ("
            + FootballDataContract.TeamsTable._ID + " INTEGER PRIMARY KEY,"
            + FootballDataContract.TeamsTable.TEAM_ID + " INTEGER NOT NULL,"
            + FootballDataContract.TeamsTable.NAME + " TEXT NOT NULL,"
            + FootballDataContract.TeamsTable.SHORT_NAME + " TEXT NOT NULL,"
            + FootballDataContract.TeamsTable.CREST_URL + " TEXT NOT NULL,"
            + FootballDataContract.TeamsTable.LEAGUE_ID + " INTEGER NOT NULL,"
            + " UNIQUE (" + FootballDataContract.TeamsTable.TEAM_ID + ","
            + FootballDataContract.TeamsTable.LEAGUE_ID + ") ON CONFLICT IGNORE"
            + " );";

    final String DELETE_SCHEMA_TEAMS_TABLE = "DROP TABLE IF EXISTS " + FootballDataContract.TeamsTable.TABLE_NAME;

    public FootballDataDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SCHEMA_SCORES_TABLE);
        db.execSQL(CREATE_SCHEMA_TEAMS_TABLE);

        //Log.d(LOG_TAG, "onCreate : Scores & Teams table Created! version - " + DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_SCHEMA_SCORES_TABLE);
        db.execSQL(DELETE_SCHEMA_TEAMS_TABLE);

        //Log.d(LOG_TAG, "onUpgrade : Scores & Teams table Deleted!" +
        //        " oldVersion - " + oldVersion + " & newVersion - " + newVersion);
    }
}
