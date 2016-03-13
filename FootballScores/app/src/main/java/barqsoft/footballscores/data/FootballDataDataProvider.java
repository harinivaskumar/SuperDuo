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
public class FootballDataDataProvider extends ContentProvider {

    private static final String LOG_TAG = FootballDataDataProvider.class.getSimpleName();

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
            FootballDataContract.ScoresTable.MATCH_DATE + " LIKE ?";
    private static final String sScoresMatchSelectionWithMatchId =
            FootballDataContract.ScoresTable.MATCH_ID + " = ?";
    private static final String sScoresMatchSelectionWithDateRange =
            FootballDataContract.ScoresTable.MATCH_DATE + " BETWEEN ? AND ?";
    private static final String sTeamCrestSelectionWithLeagueAndTeamId =
            FootballDataContract.TeamsTable.LEAGUE_ID + " = ? AND " +
            FootballDataContract.TeamsTable.TEAM_ID + " = ?" ;

    //Sort Order Strings
    private static final String sScoresMatchSortOrderTime =
            FootballDataContract.ScoresTable.MATCH_TIME + " ASC";
    private static final String sScoresMatchSortOrderDateAndTime =
            FootballDataContract.ScoresTable.MATCH_DATE + " ASC, " +
            FootballDataContract.ScoresTable.MATCH_TIME + " ASC";

    private static FootballDataDBHelper mOpenHelper;
    private UriMatcher mUriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FootballDataContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FootballDataContract.PATH_SCORES, SCORES);
        matcher.addURI(authority, FootballDataContract.PATH_SCORES + "/" +
                FootballDataContract.ScoresTable.SINGLE_DAY + "/*", SCORES_WITH_DATE);
        matcher.addURI(authority, FootballDataContract.PATH_SCORES + "/#", SCORE_WITH_MATCH_ID);
        matcher.addURI(authority, FootballDataContract.PATH_SCORES + "/" +
                FootballDataContract.ScoresTable.THREE_DAYS, SCORE_WITH_DATE_RANGE);

        matcher.addURI(authority, FootballDataContract.PATH_TEAMS, TEAMS);
        matcher.addURI(authority, FootballDataContract.PATH_TEAMS + "/*/#/#", TEAM_CREST_WITH_LEAGUE_AND_TEAM_ID);
        matcher.addURI(authority, FootballDataContract.PATH_TEAMS + "/*", LEAGUES_COUNT);
        matcher.addURI(authority, FootballDataContract.PATH_TEAMS + "/*/#", LEAGUE_COUNT);

        //Log.d(LOG_TAG, "buildUriMatcher : " + matcher.);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FootballDataDBHelper(getContext());
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
                return FootballDataContract.ScoresTable.CONTENT_TYPE_DIR;
            case SCORES_WITH_DATE:
                return FootballDataContract.ScoresTable.CONTENT_TYPE_DIR;
            case SCORE_WITH_MATCH_ID:
                return FootballDataContract.ScoresTable.CONTENT_TYPE_ITEM;
            case SCORE_WITH_DATE_RANGE:
                return FootballDataContract.ScoresTable.CONTENT_TYPE_DIR;
            case TEAMS:
                return FootballDataContract.TeamsTable.CONTENT_TYPE_DIR;
            case TEAM_CREST_WITH_LEAGUE_AND_TEAM_ID:
                return FootballDataContract.TeamsTable.CONTENT_TYPE_ITEM;
            case LEAGUES_COUNT:
                return FootballDataContract.TeamsTable.CONTENT_TYPE_ITEM;
            case LEAGUE_COUNT:
                return FootballDataContract.TeamsTable.CONTENT_TYPE_ITEM;
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
                        .query(FootballDataContract.ScoresTable.TABLE_NAME,
                                projection, null, null, null, null, sortOrder);
                break;
            case SCORES_WITH_DATE:
                selectionArgs = new String[] {
                        FootballDataContract.ScoresTable.getDateFromUriWithDate(uri)
                };
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(FootballDataContract.ScoresTable.TABLE_NAME,
                                projection,
                                sScoresMatchSelectionWithDate,
                                selectionArgs,
                                null,
                                null,
                                sScoresMatchSortOrderTime);
                break;
            case SCORE_WITH_MATCH_ID:
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(FootballDataContract.ScoresTable.TABLE_NAME,
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
                        .query(FootballDataContract.ScoresTable.TABLE_NAME,
                                projection,
                                sScoresMatchSelectionWithDateRange,
                                selectionArgs,
                                null,
                                null,
                                sScoresMatchSortOrderDateAndTime);
                break;
            case TEAMS:
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(FootballDataContract.TeamsTable.TABLE_NAME,
                                projection, null, null, null, null, sortOrder);
                break;
            case TEAM_CREST_WITH_LEAGUE_AND_TEAM_ID:
                projection = new String[] {FootballDataContract.TeamsTable.CREST_URL};
                selectionArgs = new String[] {
                        FootballDataContract.TeamsTable.getLeagueIdFromCrestUri(uri),
                        FootballDataContract.TeamsTable.getTeamIdFromCrestUri(uri)
                };
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(FootballDataContract.TeamsTable.TABLE_NAME,
                                projection,
                                sTeamCrestSelectionWithLeagueAndTeamId,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case LEAGUES_COUNT:
                retCursor = mOpenHelper.getReadableDatabase()
                        .rawQuery("SELECT COUNT(DISTINCT " + FootballDataContract.TeamsTable.LEAGUE_ID +
                                " ) AS " + FootballDataContract.TeamsTable.TOTAL_LEAGUES_COUNT +
                                " FROM " + FootballDataContract.TeamsTable.TABLE_NAME,
                                null);
                break;
            case LEAGUE_COUNT:
                retCursor = mOpenHelper.getReadableDatabase()
                        .rawQuery("SELECT COUNT(DISTINCT " + FootballDataContract.TeamsTable.LEAGUE_ID +
                                " ) AS " + FootballDataContract.TeamsTable.LEAGUE_COUNT +
                                " FROM " + FootballDataContract.TeamsTable.TABLE_NAME +
                                " WHERE " + FootballDataContract.TeamsTable.LEAGUE_ID +
                                " = " + FootballDataContract.TeamsTable.getLeagueIdFromUri(uri),
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
                //db.delete(FootballDataContract.ScoresTable.TABLE_NAME, null, null);
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(FootballDataContract.ScoresTable.TABLE_NAME,
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
                //db.delete(FootballDataContract.TeamsTable.TABLE_NAME, null, null);
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(FootballDataContract.TeamsTable.TABLE_NAME,
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
