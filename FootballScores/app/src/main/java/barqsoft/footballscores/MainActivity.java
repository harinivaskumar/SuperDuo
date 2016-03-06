package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private final String SELECTED_MATCH_ID = "Selected Match ID";
    private final String CURRENT_FRAGMENT_ID = "Current Fragment ID";
    private final String VISIBLE_FRAGMENT = "Visible Fragment";

    private PagerFragment mVisibleFragment;
    //By Default : 'Today' Fragment is shown to the user
    private static int currentFragmentId = 2;
    private static double selectedMatchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null){
            mVisibleFragment = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mVisibleFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent start_about = new Intent(this, AboutActivity.class);
            startActivity(start_about);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outInstanceState) {
        setCurrentFragmentId(mVisibleFragment.mPagerHandler.getCurrentItem());

        outInstanceState.putInt(CURRENT_FRAGMENT_ID, getCurrentFragmentId());
        outInstanceState.putDouble(SELECTED_MATCH_ID, getSelectedMatchId());
        getSupportFragmentManager().
                putFragment(outInstanceState, VISIBLE_FRAGMENT, mVisibleFragment);
        super.onSaveInstanceState(outInstanceState);

        Log.v(LOG_TAG, "onSaveInstanceState : Stored  - " +
                "Fragment Id: " + getCurrentFragmentId() + " | " +
                " and Selected Match Id: " + getSelectedMatchId());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        setCurrentFragmentId(savedInstanceState.getInt(CURRENT_FRAGMENT_ID));
        setSelectedMatchId(savedInstanceState.getDouble(SELECTED_MATCH_ID));
        mVisibleFragment = (PagerFragment) getSupportFragmentManager().
                getFragment(savedInstanceState, VISIBLE_FRAGMENT);
        super.onRestoreInstanceState(savedInstanceState);

        Log.v(LOG_TAG, "onRestoreInstanceState : Restored  - " +
                "Fragment Id: " + getCurrentFragmentId() +
                " and Selected Match Id: " + getSelectedMatchId());
    }

    public static void setCurrentFragmentId(int fragmentId){
        currentFragmentId = fragmentId;
    }

    public static int getCurrentFragmentId(){
        return currentFragmentId;
    }

    public static void setSelectedMatchId(Double matchId){
        selectedMatchId = matchId;
    }

    public static Double getSelectedMatchId(){
        return selectedMatchId;
    }
}
