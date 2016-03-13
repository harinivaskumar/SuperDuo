package barqsoft.footballscores.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import barqsoft.footballscores.Utilities;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresProvider extends ContentProvider {

    private static final String LOG_TAG = ScoresProvider.class.getSimpleName();

    private static final int SCORES = 100;
    private static final int SCORES_WITH_DATE = 101;
    private static final int SCORE_WITH_MATCH_ID = 102;
    private static final int SCORE_WITH_DATE_RANGE = 103;

    private static final int TEAMS = 200;
    private static final int TEAM_CREST_WITH_LEAGUE_AND_TEAM_ID = 201;
    private static final int LEAGUES_COUNT = 202;
    private static final int LEAGUE_COUNT = 203;

    //Selection Strings
    private static final String sScoresMatchSelectionWithDate =
            DatabaseContract.ScoresTable.MATCH_DATE + " LIKE ?";
    private static final String sScoresMatchSelectionWithMatchId =
            DatabaseContract.ScoresTable.MATCH_ID + " = ?";
    private static final String sScoresMatchSelectionWithDateRange =
            DatabaseContract.ScoresTable.MATCH_DATE + " BETWEEN ? AND ?";
    private static final String sTeamCrestSelectionWithLeagueAndTeamId =
            DatabaseContract.TeamsTable.LEAGUE_ID + " = ? AND " +
            DatabaseContract.TeamsTable.TEAM_ID + " = ?" ;

    //Sort Order Strings
    private static final String sScoresMatchSortOrderTime =
            DatabaseContract.ScoresTable.MATCH_TIME + " ASC";
    private static final String sScoresMatchSortOrderDateAndTime =
            DatabaseContract.ScoresTable.MATCH_DATE + " ASC, " +
            DatabaseContract.ScoresTable.MATCH_TIME + " ASC";

    private static ScoresDBHelper mOpenHelper;
    private UriMatcher mUriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DatabaseContract.PATH_SCORES, SCORES);
        matcher.addURI(authority, DatabaseContract.PATH_SCORES + "/" +
                DatabaseContract.ScoresTable.SINGLE_DAY + "/*", SCORES_WITH_DATE);
        matcher.addURI(authority, DatabaseContract.PATH_SCORES + "/#", SCORE_WITH_MATCH_ID);
        matcher.addURI(authority, DatabaseContract.PATH_SCORES + "/" +
                DatabaseContract.ScoresTable.THREE_DAYS, SCORE_WITH_DATE_RANGE);

        matcher.addURI(authority, DatabaseContract.PATH_TEAMS, TEAMS);
        matcher.addURI(authority, DatabaseContract.PATH_TEAMS + "/*/#/#", TEAM_CREST_WITH_LEAGUE_AND_TEAM_ID);
        matcher.addURI(authority, DatabaseContract.PATH_TEAMS + "/*", LEAGUES_COUNT);
        matcher.addURI(authority, DatabaseContract.PATH_TEAMS + "/*/#", LEAGUE_COUNT);

        //Log.d(LOG_TAG, "buildUriMatcher : " + matcher.);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ScoresDBHelper(getContext());
        return true;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        final int matchUriId = mUriMatcher.match(uri);
        //Log.d(LOG_TAG, "getType : matchUriId - " + matchUriId + " for Uri - " + uri);
        switch (matchUriId) {
            case SCORES:
                return DatabaseContract.ScoresTable.CONTENT_TYPE_DIR;
            case SCORES_WITH_DATE:
                return DatabaseContract.ScoresTable.CONTENT_TYPE_DIR;
            case SCORE_WITH_MATCH_ID:
                return DatabaseContract.ScoresTable.CONTENT_TYPE_ITEM;
            case SCORE_WITH_DATE_RANGE:
                return DatabaseContract.ScoresTable.CONTENT_TYPE_DIR;
            case TEAMS:
                return DatabaseContract.TeamsTable.CONTENT_TYPE_DIR;
            case TEAM_CREST_WITH_LEAGUE_AND_TEAM_ID:
                return DatabaseContract.TeamsTable.CONTENT_TYPE_ITEM;
            case LEAGUES_COUNT:
                return DatabaseContract.TeamsTable.CONTENT_TYPE_ITEM;
            case LEAGUE_COUNT:
                return DatabaseContract.TeamsTable.CONTENT_TYPE_ITEM;
            default:
                throw new UnsupportedOperationException("getType : Unknown Uri - " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (mUriMatcher.match(uri)) {
            case SCORES:
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(DatabaseContract.ScoresTable.TABLE_NAME,
                                projection, null, null, null, null, sortOrder);
                break;
            case SCORES_WITH_DATE:
                selectionArgs = new String[] {
                        DatabaseContract.ScoresTable.getDateFromUriWithDate(uri)
                };
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(DatabaseContract.ScoresTable.TABLE_NAME,
                                projection,
                                sScoresMatchSelectionWithDate,
                                selectionArgs,
                                null,
                                null,
                                sScoresMatchSortOrderTime);
                break;
            case SCORE_WITH_MATCH_ID:
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(DatabaseContract.ScoresTable.TABLE_NAME,
                                projection,
                                sScoresMatchSelectionWithMatchId,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case SCORE_WITH_DATE_RANGE:
                selectionArgs = new String[] {
                        Utilities.getRequiredLocalDate(Utilities.DATE_TODAY),
                        Utilities.getRequiredLocalDate(Utilities.DATE_NEXT_DAY)
                };

/*                Log.d(LOG_TAG, "query : Scores with Date Range - " +
                        " PrevDate = " + Utilities.getRequiredLocalDate(Utilities.DATE_PREVIOUS_DAY) +
                        " NextDate = " + Utilities.getRequiredLocalDate(Utilities.DATE_NEXT_DAY));
                Log.d(LOG_TAG, "query : I have Uri as - " + uri);*/
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(DatabaseContract.ScoresTable.TABLE_NAME,
                                projection,
                                sScoresMatchSelectionWithDateRange,
                                selectionArgs,
                                null,
                                null,
                                sScoresMatchSortOrderDateAndTime);
                break;
            case TEAMS:
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(DatabaseContract.TeamsTable.TABLE_NAME,
                                projection, null, null, null, null, sortOrder);
                break;
            case TEAM_CREST_WITH_LEAGUE_AND_TEAM_ID:
                projection = new String[] {DatabaseContract.TeamsTable.CREST_URL};
                selectionArgs = new String[] {
                        DatabaseContract.TeamsTable.getLeagueIdFromCrestUri(uri),
                        DatabaseContract.TeamsTable.getTeamIdFromCrestUri(uri)
                };
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(DatabaseContract.TeamsTable.TABLE_NAME,
                                projection,
                                sTeamCrestSelectionWithLeagueAndTeamId,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case LEAGUES_COUNT:
                retCursor = mOpenHelper.getReadableDatabase()
                        .rawQuery("SELECT COUNT(DISTINCT " + DatabaseContract.TeamsTable.LEAGUE_ID +
                                " ) AS " + DatabaseContract.TeamsTable.TOTAL_LEAGUES_COUNT +
                                " FROM " + DatabaseContract.TeamsTable.TABLE_NAME,
                                null);
                break;
            case LEAGUE_COUNT:
                retCursor = mOpenHelper.getReadableDatabase()
                        .rawQuery("SELECT COUNT(DISTINCT " + DatabaseContract.TeamsTable.LEAGUE_ID +
                                " ) AS " + DatabaseContract.TeamsTable.LEAGUE_COUNT +
                                " FROM " + DatabaseContract.TeamsTable.TABLE_NAME +
                                " WHERE " + DatabaseContract.TeamsTable.LEAGUE_ID +
                                " = " + DatabaseContract.TeamsTable.getLeagueIdFromUri(uri),
                                null);
                break;
            default:
                throw new UnsupportedOperationException("query : Unknown Uri - " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri,@NonNull ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowCount = 0;
//        Log.v(LOG_TAG, "bulkInsert : Uri - " + getType(uri) +
//                " & UriId - " + String.valueOf(mUriMatcher.match(uri)));
        switch (mUriMatcher.match(uri)){
            case SCORES:
                //db.delete(DatabaseContract.ScoresTable.TABLE_NAME, null, null);
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(DatabaseContract.ScoresTable.TABLE_NAME,
                                null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            rowCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowCount;
            case TEAMS:
                //db.delete(DatabaseContract.TeamsTable.TABLE_NAME, null, null);
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(DatabaseContract.TeamsTable.TABLE_NAME,
                                null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            rowCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowCount;
            default:
                throw new UnsupportedOperationException("bulkInsert : Unknown Uri - " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    private String getScoresDateSelectionArgsFromUri(Uri uri){

        return null;
    }
}
