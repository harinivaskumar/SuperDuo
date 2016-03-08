package barqsoft.footballscores;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class ViewPagerFragment extends Fragment {
    private final String LOG_TAG = ViewPagerFragment.class.getSimpleName();

    private static final int NUM_PAGES = 5;
    private final String DATE_FORMAT_1 = "yyyy-MM-dd";
    private final String DATE_FORMAT_2 = "EEEE";

    private ViewPager mPagerHandler;
    protected MainPageFragment[] mViewFragments = new MainPageFragment[NUM_PAGES];

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.viewpager_fragment, container, false);

        for (int index = 0; index < NUM_PAGES; index++) {
            Date date = new Date(System.currentTimeMillis() + ((index - 2) * 86400000));
//            Log.d(LOG_TAG, "TimeInMillis[" + index + "] - " + (System.currentTimeMillis() + ((index - 2) * 86400000)) +
//                    " and getTime() - " + date.getTime());
//            SimpleDateFormat dateFormatFull = new SimpleDateFormat(DATE_FORMAT_1);
//            Log.d(LOG_TAG, "onCreateView[" + index + "] : currentTimeInMilliSecond - " + System.currentTimeMillis() +
//                    " and extraValue - " + ((index - 2) * 86400000) +
//                    " Date - " + dateFormatFull.format(date));

            mViewFragments[index] = new MainPageFragment();
            //mViewFragments[index].setFragmentDateStr(dateFormatFull.format(date));
            mViewFragments[index].setFragmentDate(date);
        }

        setPagerHandler(rootView);
        return rootView;
    }

    public void setPagerHandler(View view){
        PagerAdapter myPagerAdapter = new PagerAdapter(getChildFragmentManager());

        mPagerHandler = (ViewPager) view.findViewById(R.id.pager);
        mPagerHandler.setAdapter(myPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.getCurrentFragmentId());
    }

    public ViewPager getPagerHandler(){
        return mPagerHandler;
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            return mViewFragments[index];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            Log.d(LOG_TAG, "getPageTitle[" + position + "]" +
                    " : currentTimeInMilliSecond - " + (System.currentTimeMillis() + ((position - 2) * 86400000)) +
                    " & getTime() - " +  mViewFragments[position].getFragmentDate().getTime());
            return getDayName(getActivity(), mViewFragments[position].getFragmentDate().getTime());
        }

        public String getDayName(Context context, long dateInMillis) {
            /*
             * Instead of the actual day name,
             * If the date is yesterday, return the localized version of "Yesterday",
             * If the date is today, return the localized version of "Today",
             * If the date is tomorrow, return the localized version of "Tomorrow",
             * Else If return the format as just the day of the week (e.g "Wednesday")
             */
            Time time = new Time();
            time.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), time.gmtoff);

            if (julianDay == currentJulianDay) {
                Log.d(LOG_TAG, context.getString(R.string.today));
                return context.getString(R.string.today);
            } else if (julianDay == currentJulianDay + 1) {
                Log.d(LOG_TAG, context.getString(R.string.tomorrow));
                return context.getString(R.string.tomorrow);
            } else if (julianDay == currentJulianDay - 1) {
                Log.d(LOG_TAG, context.getString(R.string.yesterday));
                return context.getString(R.string.yesterday);
            } else {
                SimpleDateFormat dateFormatDay = new SimpleDateFormat(DATE_FORMAT_2);
                Log.d(LOG_TAG, dateFormatDay.format(dateInMillis));
                return dateFormatDay.format(dateInMillis);
            }
        }
    }
}
