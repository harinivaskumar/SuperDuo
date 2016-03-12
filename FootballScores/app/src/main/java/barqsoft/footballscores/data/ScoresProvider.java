package barqsoft.footballscores.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresProvider extends ContentProvider {

    private static final String LOG_TAG = ScoresProvider.class.getSimpleName();

    private static final int SCORES = 100;
    private static final int SCORES_WITH_DATE = 101;
    private static final int SCORE_WITH_MATCH_ID = 102;

    private static final int TEAMS = 200;
    private static final int TEAM_CREST_WITH_TEAM_ID = 201;
    private static final int LEAGUES_COUNT = 202;
    private static final int LEAGUE_COUNT = 203;

    private static final String sScoresDateSelection =
            DatabaseContract.ScoresTable.MATCH_DATE + " LIKE ?";
    private static final String sScoresMatchSelection =
            DatabaseContract.ScoresTable.MATCH_ID + " = ?";
    private static final String sTeamCrestSelectionWithTeamId =
            DatabaseContract.TeamsTable.LEAGUE_ID + " = ?" ;

    private static final String sMatchDateSortOrder =
            DatabaseContract.ScoresTable.MATCH_TIME + " ASC";

    private static ScoresDBHelper mOpenHelper;
    private UriMatcher mUriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DatabaseContract.PATH_SCORES, SCORES);
        matcher.addURI(authority, DatabaseContract.PATH_SCORES + "/*", SCORES_WITH_DATE);
        matcher.addURI(authority, DatabaseContract.PATH_SCORES + "/#", SCORE_WITH_MATCH_ID);

        matcher.addURI(authority, DatabaseContract.PATH_TEAMS, TEAMS);
        matcher.addURI(authority, DatabaseContract.PATH_TEAMS + "/#", TEAM_CREST_WITH_TEAM_ID);
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
        //Log.d(LOG_TAG, "getType : matchUriId - " + matchUriId);
        switch (matchUriId) {
            case SCORES:
                return DatabaseContract.ScoresTable.CONTENT_TYPE_DIR;
            case SCORES_WITH_DATE:
                return DatabaseContract.ScoresTable.CONTENT_TYPE_DIR;
            case SCORE_WITH_MATCH_ID:
                return DatabaseContract.ScoresTable.CONTENT_TYPE_ITEM;
            case TEAMS:
                return DatabaseContract.TeamsTable.CONTENT_TYPE_DIR;
            case TEAM_CREST_WITH_TEAM_ID:
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
                selectionArgs = new String[] {DatabaseContract.ScoresTable.getDateFromUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(DatabaseContract.ScoresTable.TABLE_NAME,
                                projection,
                                sScoresDateSelection,
                                selectionArgs,
                                null,
                                null,
                                sMatchDateSortOrder);
                break;
            case SCORE_WITH_MATCH_ID:
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(DatabaseContract.ScoresTable.TABLE_NAME,
                                projection,
                                sScoresMatchSelection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case TEAMS:
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(DatabaseContract.TeamsTable.TABLE_NAME,
                                projection, null, null, null, null, sortOrder);
                break;
            case TEAM_CREST_WITH_TEAM_ID:
                projection = new String[] {DatabaseContract.TeamsTable.CREST_URL};
                selectionArgs = new String[] {DatabaseContract.TeamsTable.getTeamIdFromUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(DatabaseContract.TeamsTable.TABLE_NAME,
                                projection,
                                sTeamCrestSelectionWithTeamId,
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
