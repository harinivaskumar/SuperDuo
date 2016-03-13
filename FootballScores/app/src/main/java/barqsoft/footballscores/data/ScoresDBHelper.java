package barqsoft.footballscores.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresDBHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = ScoresDBHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "Scores.db";
    private static final int DATABASE_VERSION = 1;

    final String CREATE_SCHEMA_SCORES_TABLE = "CREATE TABLE " + DatabaseContract.ScoresTable.TABLE_NAME + " ("
            + DatabaseContract.ScoresTable._ID + " INTEGER PRIMARY KEY,"
            + DatabaseContract.ScoresTable.MATCH_DATE + " TEXT NOT NULL,"
            + DatabaseContract.ScoresTable.MATCH_TIME + " INTEGER NOT NULL,"
            + DatabaseContract.ScoresTable.LEAGUE_ID + " INTEGER NOT NULL,"
            + DatabaseContract.ScoresTable.MATCH_ID + " INTEGER NOT NULL,"
            + DatabaseContract.ScoresTable.MATCH_DAY + " INTEGER NOT NULL,"
            + DatabaseContract.ScoresTable.HOME_TEAM_ID + " INTEGER NOT NULL,"
            + DatabaseContract.ScoresTable.AWAY_TEAM_ID + " INTEGER NOT NULL,"
            + DatabaseContract.ScoresTable.HOME_TEAM_NAME + " TEXT NOT NULL,"
            + DatabaseContract.ScoresTable.AWAY_TEAM_NAME + " TEXT NOT NULL,"
            + DatabaseContract.ScoresTable.HOME_TEAM_GOALS + " TEXT NOT NULL,"
            + DatabaseContract.ScoresTable.AWAY_TEAM_GOALS + " TEXT NOT NULL,"
            + " UNIQUE (" + DatabaseContract.ScoresTable.MATCH_ID + ") ON CONFLICT REPLACE"
            + " );";

    final String DELETE_SCHEMA_SCORES_TABLE = "DROP TABLE IF EXISTS " + DatabaseContract.ScoresTable.TABLE_NAME;

    final String CREATE_SCHEMA_TEAMS_TABLE = "CREATE TABLE " + DatabaseContract.TeamsTable.TABLE_NAME + " ("
            + DatabaseContract.TeamsTable._ID + " INTEGER PRIMARY KEY,"
            + DatabaseContract.TeamsTable.TEAM_ID + " INTEGER NOT NULL,"
            + DatabaseContract.TeamsTable.NAME + " TEXT NOT NULL,"
            + DatabaseContract.TeamsTable.SHORT_NAME + " TEXT NOT NULL,"
            + DatabaseContract.TeamsTable.CREST_URL + " TEXT NOT NULL,"
            + DatabaseContract.TeamsTable.LEAGUE_ID + " INTEGER NOT NULL,"
            + " UNIQUE (" + DatabaseContract.TeamsTable.TEAM_ID + ","
            + DatabaseContract.TeamsTable.LEAGUE_ID + ") ON CONFLICT IGNORE"
            + " );";

    final String DELETE_SCHEMA_TEAMS_TABLE = "DROP TABLE IF EXISTS " + DatabaseContract.TeamsTable.TABLE_NAME;

    public ScoresDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SCHEMA_SCORES_TABLE);
        db.execSQL(CREATE_SCHEMA_TEAMS_TABLE);

        Log.d(LOG_TAG, "onCreate : Scores & Teams table Created! version - " + DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_SCHEMA_SCORES_TABLE);
        db.execSQL(DELETE_SCHEMA_TEAMS_TABLE);

        Log.d(LOG_TAG, "onUpgrade : Scores & Teams table Deleted!" +
                " oldVersion - " + oldVersion + " & newVersion - " + newVersion);
    }
}
