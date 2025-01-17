package na.knowyourreps;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*
 * Thanks so much to http://developer.android.com/training/animation/screen-slide.html
 * for help in understanding and implementing fragments. Used/took lots of this code for my project,
 * both inspiration and my actual code
 */

public class ScreenSlidePagerActivity extends FragmentActivity {

    private static int NUM_PAGES = 3;    // For Now :D
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private HashMap<Integer, String> nameHash;
    private HashMap<Integer, String> partyHash;
    private HashMap<Integer, String> queryIdHash;
    private HashMap<Integer, String> endOfTermHash;
    private HashMap<Integer, String> seatHash;
    private HashMap<Integer, String> bioGuideIdHash;

    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set up Hashes
        nameHash = new HashMap<>();
        partyHash = new HashMap<>();
        queryIdHash = new HashMap<>();
        endOfTermHash = new HashMap<>();
        seatHash = new HashMap<>();
        bioGuideIdHash = new HashMap<>();

        // Grab representatives information that was sent over from the phone
        String repsInfo = getIntent().getExtras().getString("REP_INFORMATION");
        repsStringParser(repsInfo);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide_pager);

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {
                Intent intent = new Intent(getBaseContext(), VoteViewActivity.class);
                //you need to add this flag since you're starting a new activity from a service
                intent.putExtra("randomCounty", "true");
                intent.putExtra("county", "RANDOM_GENERATION");
                Log.d("T", "about to start watch VoteViewActivity because a shaking has occured :D");
                Toast.makeText(ScreenSlidePagerActivity.this, "Shake!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        // Instantiate a ViewPager and a PagerAdapter
        mPager = (ViewPager) findViewById(R.id.viewpager_reps);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    private void repsStringParser(String s) {
        String splitString[] = s.split("__");
        NUM_PAGES = Integer.parseInt(splitString[0]);
        List<String> iterateThroughRepInfo = Arrays.asList(splitString);
        int parityAndPositionNumber = 0;
        int locationNumber = 0;
        for (String elem:iterateThroughRepInfo.subList(1, 6*NUM_PAGES+1)) {
            if (parityAndPositionNumber % 6 == 0) {
                nameHash.put(locationNumber, elem);
            } else if (parityAndPositionNumber %6 == 1) {
                partyHash.put(locationNumber, elem);
            } else if (parityAndPositionNumber %6 == 2) {
                endOfTermHash.put(locationNumber, elem);
            } else if (parityAndPositionNumber %6 == 3) {
                seatHash.put(locationNumber, elem);
            } else if (parityAndPositionNumber %6 == 4) {
                bioGuideIdHash.put(locationNumber, elem);
            } else if (parityAndPositionNumber %6 == 5) {
                queryIdHash.put(locationNumber, elem);
            }
            parityAndPositionNumber += 1;
            if (parityAndPositionNumber % 6 == 0) {
                locationNumber += 1;
            }
        }
    }

    /**
     * A simple pager adapter that represents NUM_PAGES ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle currentViewBundle = new Bundle();
            currentViewBundle.putString("name", nameHash.get(position));
            currentViewBundle.putString("party", partyHash.get(position));
            currentViewBundle.putString("position", Integer.toString(position));
            currentViewBundle.putString("endOfTerm", endOfTermHash.get(position));
            currentViewBundle.putString("seat", seatHash.get(position));
            currentViewBundle.putString("query", queryIdHash.get(position));
            Fragment currentPageFragment = new ScreenSlidePageFragment();
            currentPageFragment.setArguments(currentViewBundle);
            return currentPageFragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public void openPhoneDetailedView(View view) {
        Intent intent = new Intent(this, WatchToPhoneService.class);
        Button clickedButton = (Button) view.findViewById(R.id.goToPhoneDetailedViewButton);
        String openDetailedInfoStr = "";
        String buttonNumberStr = Integer.toString((int) clickedButton.getTag());
        openDetailedInfoStr += buttonNumberStr;     // Send position over first
        openDetailedInfoStr += "__";
        openDetailedInfoStr += nameHash.get(clickedButton.getTag());
        openDetailedInfoStr += "__";
        openDetailedInfoStr += partyHash.get(clickedButton.getTag());
        openDetailedInfoStr += "__";
        openDetailedInfoStr += endOfTermHash.get(clickedButton.getTag());
        openDetailedInfoStr += "__";
        openDetailedInfoStr += seatHash.get(clickedButton.getTag());
        openDetailedInfoStr += "__";
        openDetailedInfoStr += bioGuideIdHash.get(clickedButton.getTag());
        openDetailedInfoStr += "__";
        openDetailedInfoStr += queryIdHash.get(clickedButton.getTag());
        intent.putExtra("watch_to_phone_detailed", openDetailedInfoStr);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }
}

