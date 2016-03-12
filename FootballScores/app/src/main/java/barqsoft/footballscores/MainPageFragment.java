package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.service.MatchFetchService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainPageFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainPageFragment.class.getSimpleName();

    public static final int SCORES_LOADER = 0;

    private ScoresAdapter mAdapter;
    private String[] mFragmentDateStr;

    public MainPageFragment() {
    }

    private void updateScores() {
        Intent matchFetchServiceIntent = new Intent(getActivity(), MatchFetchService.class);
        getActivity().startService(matchFetchServiceIntent);
        //Log.d(LOG_TAG, "updateScores : Started MatchFetch intent Service!");
    }

    public void setFragmentDateStr(String dateStr) {
        mFragmentDateStr = new String[1];
        mFragmentDateStr[0] = dateStr;
        //Log.d(LOG_TAG, "setFragmentDateStr(String) : DateStr - " + mFragmentDateStr[0]);
    }

    private String[] getFragmentDateStr(){
        return mFragmentDateStr;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        updateScores();

        View rootView = inflater.inflate(R.layout.mainpage_fragment, container, false);
        getLoaderManager().initLoader(SCORES_LOADER, null, this);

        final ListView scoreList = (ListView) rootView.findViewById(R.id.scores_list);

        mAdapter = new ScoresAdapter(getActivity(), null, 0);
        scoreList.setAdapter(mAdapter);

        mAdapter.setDetailedMatchId(MainActivity.getSelectedMatchId());

        scoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selectedView = (ViewHolder) view.getTag();

                mAdapter.setDetailedMatchId(selectedView.getMatchId());
                MainActivity.setSelectedMatchId(selectedView.getMatchId());

                mAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Log.v(LOG_TAG, "onCreateLoader : Loader Started!");
        Uri scoresForDateUri = DatabaseContract.ScoresTable
                .buildScoreWithDate(mFragmentDateStr[0]);
        //TODO Fill the projection array to retrieve only what is required.
        String[] projection = {
                DatabaseContract.ScoresTable.MATCH_TIME,
                DatabaseContract.ScoresTable.LEAGUE_ID,
                DatabaseContract.ScoresTable.MATCH_ID,
                DatabaseContract.ScoresTable.MATCH_DAY,
                DatabaseContract.ScoresTable.HOME_TEAM_NAME,
                DatabaseContract.ScoresTable.AWAY_TEAM_NAME,
                DatabaseContract.ScoresTable.HOME_TEAM_GOALS,
                DatabaseContract.ScoresTable.AWAY_TEAM_GOALS,
                DatabaseContract.TeamsTable.CREST_URL,
                DatabaseContract.TeamsTable.CREST_URL
        };
        //Not required now this is taken care in ContentProvider side
        //String[] selectionArgs = {mFragmentDateStr[0]};
/*        Log.d(LOG_TAG, "onCreateLoader : Encodedpath - " + scoresForDateUri.getEncodedPath() +
                " Encoded Query - " + scoresForDateUri.getEncodedQuery() +
                " Encoded UserInfo - " + scoresForDateUri.getEncodedUserInfo() +
                " Authority - " + scoresForDateUri.getAuthority() +
                " Host - " + scoresForDateUri.getHost() +
                " Last Patch Segment - " + scoresForDateUri.getLastPathSegment() +
                " Path - " + scoresForDateUri.getPath() +
                " Query - " + scoresForDateUri.getQuery());*/
        return new CursorLoader(getActivity(),
                scoresForDateUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //Log.v(LOG_TAG, "onLoadFinished : Loader Finished!");
/*        int position = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            position++;
            cursor.moveToNext();
        }
        Log.v(LOG_TAG, "onLoadFinished : Loader query: " + position);*/
        mAdapter.swapCursor(cursor);
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }
}