package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
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

import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.service.MatchFetchService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainScreenFragment.class.getSimpleName();

    public static final int SCORES_LOADER = 0;

    private ScoresAdapter mAdapter;
    private String[] fragmentDate = new String[1];

    public MainScreenFragment() {
    }

    private void updateScores() {
        Intent matchFetchServiceIntent = new Intent(getActivity(), MatchFetchService.class);
        getActivity().startService(matchFetchServiceIntent);
    }

    public void setFragmentDate(String date) {
        fragmentDate[0] = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        updateScores();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
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
        return new CursorLoader(getActivity(),
                DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, fragmentDate, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.v(LOG_TAG, "onLoadFinished : Loader Finished!");
        int position = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            position++;
            cursor.moveToNext();
        }
        Log.v(LOG_TAG, "onLoadFinished : Loader query: " + String.valueOf(position));
        mAdapter.swapCursor(cursor);
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }
}
