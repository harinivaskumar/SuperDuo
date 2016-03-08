package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter {

    private final String LOG_TAG = ScoresAdapter.class.getSimpleName();

    private static final int COL_DATE = 1;
    private static final int COL_MATCH_TIME = 2;
    private static final int COL_HOME = 3;
    private static final int COL_AWAY = 4;
    private static final int COL_LEAGUE = 5;
    private static final int COL_HOME_GOALS = 6;
    private static final int COL_AWAY_GOALS = 7;
    private static final int COL_ID = 8;
    private static final int COL_MATCH_DAY = 9;

    private double mDetailedMatchId = 0;

    private String FOOTBALL_SCORES_HASHTAG = "#FootballScores";
    private ViewHolder mViewHolder;

    public ScoresAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View newView = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(newView);
        newView.setTag(viewHolder);
//        Log.v(LOG_TAG, "new View inflated");
        return newView;
    }

    @Override
    public void bindView(View oldView, Context context, Cursor cursor) {
        mViewHolder = (ViewHolder) oldView.getTag();
        populateViewHolderWithInformation(cursor);

        //Log.v(LOG_TAG, "DetailedMatchId - " + String.valueOf(mDetailedMatchId));

        ViewGroup containerVG = (ViewGroup) oldView.findViewById(R.id.details_fragment_container);

        if (mViewHolder.getMatchId() == getDetailedMatchId()) {
            addNewViewToViewGroup(containerVG, context, cursor);
        } else {
            containerVG.removeAllViews();
        }
    }

    public double getDetailedMatchId() {
        return mDetailedMatchId;
    }

    public void setDetailedMatchId(double mDetailedMatchId) {
        this.mDetailedMatchId = mDetailedMatchId;
    }

    private void populateViewHolderWithInformation(Cursor cursor) {
        mViewHolder.homeCrest.setImageResource(
                Utilities.getTeamLogoByTeamName(cursor.getString(COL_HOME)));
        mViewHolder.homeName.setText(cursor.getString(COL_HOME));

        mViewHolder.awayCrest.setImageResource(
                Utilities.getTeamLogoByTeamName(cursor.getString(COL_AWAY)));
        mViewHolder.awayName.setText(cursor.getString(COL_AWAY));

        mViewHolder.date.setText(cursor.getString(COL_MATCH_TIME));
        mViewHolder.score.setText(Utilities.getScores(
                cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));

        mViewHolder.setMatchId(cursor.getDouble(COL_ID));
/*
        Log.v(LOG_TAG, "Home Name - " + mViewHolder.homeName.getText() +
                " Vs. Away Name - " + mViewHolder.awayName.getText() +
                " & Match Id " + String.valueOf(mViewHolder.getMatchId()));
*/
    }

    private void addNewViewToViewGroup(ViewGroup containerVG, final Context context, Cursor cursor){
        LayoutInflater systemService = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newView = systemService.inflate(R.layout.matchdetail_fragment, null);

        containerVG.addView(newView, 0,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        TextView leagueTextView = (TextView) newView.findViewById(R.id.league_textview);
        leagueTextView.setText(Utilities.getLeague(cursor.getInt(COL_LEAGUE)));

        TextView matchDayTextView = (TextView) newView.findViewById(R.id.matchday_textview);
        matchDayTextView.setText(Utilities.getMatchDay(
                cursor.getInt(COL_MATCH_DAY), cursor.getInt(COL_LEAGUE)));

        Button shareButton = (Button) newView.findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(createShareIntent(
                        mViewHolder.homeName.getText() + " " +
                                mViewHolder.score.getText() + " " +
                                mViewHolder.awayName.getText() + " "));
            }
        });
    }

    private Intent createShareIntent(String shareString) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareString + FOOTBALL_SCORES_HASHTAG);

        return shareIntent;
    }
}