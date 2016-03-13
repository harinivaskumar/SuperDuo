package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import barqsoft.footballscores.data.FootballDataContract;
import barqsoft.footballscores.service.FootballDataFetchService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainPageFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainPageFragment.class.getSimpleName();

    public static final int SCORES_LOADER = 0;

    private static Toast notifyAsToast;
    private ScoresAdapter mAdapter;
    private String[] mFragmentDateStr;

    public MainPageFragment() {
        mFragmentDateStr = new String[1];
        mFragmentDateStr[0] = Utilities.getRequiredLocalDate(Utilities.DATE_TODAY);
    }

    private void updateScores() {
        if (!Utilities.isNetworkAvailable(getActivity().getApplicationContext())) {
            Log.e(LOG_TAG, "updateScores : You are Offline!");
            if (notifyAsToast == null) {
                notifyAsToast = Toast.makeText(getActivity().getApplicationContext(),
                        R.string.you_are_offline, Toast.LENGTH_LONG);
                notifyAsToast.show();
            }
            return;
        }

        Intent matchFetchServiceIntent = new Intent(getActivity(), FootballDataFetchService.class);
        getActivity().startService(matchFetchServiceIntent);
        //Log.d(LOG_TAG, "updateScores : Started MatchFetch intent Service!");
    }

    public void setFragmentDateStr(String dateStr) {
        mFragmentDateStr[0] = dateStr;
        //Log.d(LOG_TAG, "setFragmentDateStr(String) : DateStr - " + mFragmentDateStr[0]);
    }

    private String getFragmentDateStr(int position){
        return mFragmentDateStr[0];
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
        Uri scoresForDateUri = FootballDataContract.ScoresTable
                .buildScoreWithDate(getFragmentDateStr(0));
        return new CursorLoader(getActivity(),
                scoresForDateUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
/*      Log.v(LOG_TAG, "onLoadFinished : Loader Finished!");
        int position = 0;
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