package edu.sjsu.android.accelerometer;

import android.app.Activity;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.WindowManager;


public class MainActivity extends Activity {
    private static final String TAG = "edu.sjsu.android.accelerometer:MainActivity";
    private PowerManager.WakeLock mWakeLock;
    // The view
    private SimulationView mSimulationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PowerManager mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, TAG);
        mSimulationView = new SimulationView(this);

        // Set to the simulation view instead of layout file.
        setContentView(mSimulationView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // acquire wakelock
        mWakeLock.acquire();
        // start simulation to register the listener
        mSimulationView.startSimulation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Release wakelock
        mWakeLock.release();
        // stop simulation to unregister the listener
        mSimulationView.stopSimulation();
    }
}