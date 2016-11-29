package qut.wearable_remake;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.ViewSwitcher;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;

import com.microsoft.band.BandClient;

import qut.wearable_remake.band.ConnectAsync;
import qut.wearable_remake.band.ProjectClient;
import qut.wearable_remake.band.Setup;
import qut.wearable_remake.graphs.AccLineGraph;
import qut.wearable_remake.graphs.DailyMovesBullet;
import qut.wearable_remake.graphs.HourlyMovesBar;
import qut.wearable_remake.sensors.SensorInterface;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SpecialEventListener {
    private static final long GRAPH_REFRESH_TIME = 1000;

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private View progressClock;
    private Switch liveGraphingSwitch;
    private Switch sendHapticsSwitch;
    private ViewSwitcher chartSwitcher;

    private ProjectClient projectClient;
    private AccLineGraph accLineGraph;
    private HourlyMovesBar hourlyMovesBar;
    private DailyMovesBullet dailyMovesBullet;

    private long lastRefreshed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ConnectAsync(MainActivity.this, this).execute();

        progressClock = findViewById(R.id.progressClock);
        chartSwitcher = (ViewSwitcher) findViewById(R.id.chartSwitcher);

        LayoutInflater ltInflater = getLayoutInflater();
        View setting = ltInflater.inflate(R.layout.fragment_settings, null, false);

        final EditText moveGoalEditTxt = (EditText) setting.findViewById(R.id.moveGoalEditTxt);

        ((WearableApplication) this.getApplication())
                .setMoveGoal(Integer.parseInt(moveGoalEditTxt.getText().toString()));

        Button moveGoalBtn = (Button) setting.findViewById(R.id.moveGoalBtn);
        moveGoalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WearableApplication) getApplication())
                        .setMoveGoal(Integer.parseInt(moveGoalEditTxt.getText().toString()));
                dailyMovesBullet.updateMoveGoalLine();
            }
        });

        Switch recordDataSwitch = (Switch) setting.findViewById(R.id.recordDataSwitch);
        recordDataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BandClient bandClient = projectClient.getBandClient();
                SensorInterface[] sensors = projectClient.getSensors();
                if (isChecked) {
                    for (SensorInterface sensor : sensors) {
                        sensor.registerListener(bandClient);
                    }
                } else {
                    for (SensorInterface sensor : sensors) {
                        sensor.unregisterListener(bandClient);
                    }
                }
            }
        });

        liveGraphingSwitch = (Switch) setting.findViewById(R.id.liveGraphSwitch);
        sendHapticsSwitch = (Switch) setting.findViewById(R.id.sendHapticsSwitch);

        //testing uuid save function
        /*
        TextView uuid = (TextView) findViewById(R.id.uuid_text);

        String uuid_str = "default";
        try {
            uuid_str = HelperMethods.getDataFromFile("app_id", MainActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        uuid.setText(uuid_str);
        //end testing */

        Button removeTileBtn = (Button) setting.findViewById(R.id.removeTileBtn);
        removeTileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectClient.removeTile();
            }
        });


        // Setup nav drawer
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[1];
        drawerItem[0] = new ObjectDrawerItem(R.drawable.ic_action_settings, "Settings");

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.listview_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    /**
     * Called after the device has connected to the Band.
     * Checks to see if a previous setup exists on the device and/or Band.  If a previous setup is
     * found, the existing data is loaded otherwise a fresh setup is undertaken.
     *
     * @param bandClient The Band client returned from the ConnectAsync task
     */
    @Override
    public void onConnectDone(BandClient bandClient) {
        if (bandClient != null) {
            projectClient = new ProjectClient(bandClient, this);
            if (HelperMethods.isInstalled(this, "acc_data")
                    && HelperMethods.isInstalled(this, "move_count")
                    && HelperMethods.isInstalled(this, "app_id")
                    && HelperMethods.isInstalled(this, "please_fail")) { // Intentional fail until load data works
                // loads previous files
                List<UUID> uuids = HelperMethods.getUUID(MainActivity.this);
                UUID app_uuid = uuids.get(0);
                UUID app_pageid = uuids.get(1);
                projectClient.setTileId(app_uuid);
                projectClient.setPageId(app_pageid);

                initGraphs();
                projectClient.sendDialog("Device status", "Connected to existing data.");
            } else {
                new Setup(MainActivity.this, projectClient, this).execute();
            }
        }
    } // end onConnectDone()

    /**
     * Called after the app has been fully setup or fully loaded.
     * Initialises all save-dependent page data, such as graphs.
     */
    @Override
    public void onSetupDone() {
        initGraphs();
        //findViewById(R.id.recordDataSwitch).setEnabled(true);
    } // end onSetupDone

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getFragmentManager().popBackStack();
            }
        }
    }

    /**
     * Called when the moveCount variable has been updated.
     * Updates page values and graphs relating to the movement count.
     */
    @Override
    public void onMoveCountChanged() {
        final int moveCount = ((WearableApplication) this.getApplication()).getTotalMovesToday() + 1;
        ((WearableApplication) this.getApplication()).setTotalMovesToday(moveCount);

        if (sendHapticsSwitch.isChecked()) {
            projectClient.sendHaptic();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hourlyMovesBar.incrementDataSet(HelperMethods.getCurrentDate());
                if (liveGraphingSwitch.isChecked()) {
                    projectClient.setMovePageData(moveCount);
                    hourlyMovesBar.updateDisplay();
                    dailyMovesBullet.updateDataSet();
                }
                progressClock.invalidate();
            }
        });
    } // end onMoveCountChanged()

    /**
     * Called when the accelerometer data has changed.
     * Updates page values and graphs relating to accelerometer data.
     *
     * @param timestamp The timestamp that the accelerometer data was taken at.
     * @param accData The accelerometer data.
     */
    @Override
    public void onAccChanged(final long timestamp, final float accData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                accLineGraph.addToDataSet(accData);
                if (timestamp > lastRefreshed + GRAPH_REFRESH_TIME && liveGraphingSwitch.isChecked()) {
                    lastRefreshed = timestamp;
                    accLineGraph.updateDisplay();
                }
            }
        });
    } // end onAccChanged()

    /**
     * Initialise all graphs.
     */
    private void initGraphs() {
        LineChart accLineView = (LineChart) findViewById(R.id.accLineView);
        accLineGraph = new AccLineGraph(accLineView, this);
        accLineView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    chartSwitcher.showNext();
                    return true;
                }
                return false;
            }
        });

        BarChart hourlyBarView = (BarChart) findViewById(R.id.hourlyBarView);
        hourlyMovesBar = new HourlyMovesBar(hourlyBarView, this);
        hourlyBarView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    chartSwitcher.showPrevious();
                    return true;
                }
                return false;
            }
        });

        HorizontalBarChart dailyBulletView = (HorizontalBarChart) findViewById(R.id.dailyBulletView);
        dailyMovesBullet = new DailyMovesBullet(dailyBulletView, this);
    } // end initGraphs


    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {

        Fragment fragment = null;

        switch (position) {

            default:
                fragment = new SettingsFragment();
                break;
        }

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            //getActionBar().setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

}