package barqsoft.footballscores.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Binder;
import android.util.TypedValue;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.data.DatabaseContract;

/**
 * Created by Hari Nivas Kumar R P on 3/13/2016.
 */
public class FootballScoresWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = FootballScoresWidgetDataProvider.class.getSimpleName();

    private Cursor mMatchScoresCursor;
    private Context context;
    private Intent intent;

    public FootballScoresWidgetDataProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        if (mMatchScoresCursor != null){
            mMatchScoresCursor.close();
        }

        final long identityToken = Binder.clearCallingIdentity();

        mMatchScoresCursor = context.getContentResolver().query(
                DatabaseContract.ScoresTable.buildScoreWithDateRange(),
                null, null, null, null);

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mMatchScoresCursor != null){
            mMatchScoresCursor.close();
            mMatchScoresCursor = null;
        }
    }

    @Override
    public int getCount() {
        return ( mMatchScoresCursor == null ? 0 : mMatchScoresCursor.getCount());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                mMatchScoresCursor == null ||
                !mMatchScoresCursor.moveToPosition(position)) {
            return null;
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.scores_list_item);

        int homeTeamGoals = mMatchScoresCursor.getInt(Utilities.COL_HOME_GOALS);
        int awayTeamGoals = mMatchScoresCursor.getInt(Utilities.COL_AWAY_GOALS);
        String matchScore = Utilities.getScores(homeTeamGoals, awayTeamGoals);

        String matchTime = mMatchScoresCursor.getString(Utilities.COL_MATCH_TIME);
        String homeTeamName = mMatchScoresCursor.getString(Utilities.COL_HOME_NAME);
        String awayTeamName = mMatchScoresCursor.getString(Utilities.COL_AWAY_NAME);

        //Log.d(LOG_TAG, "homeTeamName - " + homeTeamName + " & awayTeamName - " + awayTeamName);
        //Log.d(LOG_TAG, "Match Score - " + matchScore + " & matchTime - " + matchTime);

        remoteViews.setTextViewText(R.id.time_textview, matchTime);
        remoteViews.setTextColor(R.id.time_textview, Color.BLACK);

        remoteViews.setTextViewText(R.id.score_textview, matchScore);
        remoteViews.setTextViewTextSize(R.id.score_textview,
                TypedValue.COMPLEX_UNIT_SP,
                Utilities.getScoreTextSize(null, homeTeamGoals, awayTeamGoals));
        remoteViews.setTextColor(R.id.score_textview,
                Utilities.getScoreTextColor(homeTeamGoals, awayTeamGoals));

        remoteViews.setTextViewText(R.id.home_name, homeTeamName);
        remoteViews.setTextColor(R.id.home_name, Color.BLACK);
        remoteViews.setImageViewResource(R.id.home_crest, R.drawable.ic_launcher);

        remoteViews.setTextViewText(R.id.away_name, awayTeamName);
        remoteViews.setTextColor(R.id.away_name, Color.BLACK);
        remoteViews.setImageViewResource(R.id.away_crest, R.drawable.ic_launcher);

        final Intent fillInIntent = new Intent();
        remoteViews.setOnClickFillInIntent(R.id.scores_list_item, fillInIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(context.getPackageName(), R.layout.scores_list_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (mMatchScoresCursor.moveToPosition(position))
            return mMatchScoresCursor.getLong(0);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}