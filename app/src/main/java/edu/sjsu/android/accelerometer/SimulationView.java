package edu.sjsu.android.accelerometer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.*;
import android.graphics.Canvas;
import android.hardware.*;
import android.view.*;

public class SimulationView extends View implements SensorEventListener {
    // Define SensorManager, Sensor, and Display
    private final SensorManager mSensorManager;
    private final Sensor mSensor;
    private final Display mDisplay;

    // Define variables for the graphical user interface
    private Bitmap mField;
    private final Bitmap mBasket;
    private final Bitmap mBitMAP;
    private static final int BALL_SIZE = 100;
    private static final int BASKET_SIZE = 200;

    private float mXOrigin;
    private float mYOrigin;
    private float mHorizontalBound;
    private float mVerticalBound;

    private final Particle mBall = new Particle();

    // Define variables for sensor data
    private float mSensorX, mSensorY, mSensorZ;
    private long mSensorTimeStamp;

    public SimulationView(Context context) {
        super(context);

        // Initialize images from drawable
        Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        mBitMAP = Bitmap.createScaledBitmap(ball, BALL_SIZE, BALL_SIZE, true);
        Bitmap basket = BitmapFactory.decodeResource(getResources(), R.drawable.basket);
        mBasket = Bitmap.createScaledBitmap(basket, BASKET_SIZE, BASKET_SIZE, true);
        Options opts = new Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        mField = BitmapFactory.decodeResource(getResources(), R.drawable.field, opts);

        // Initialize display
        mDisplay = context.getDisplay();

        // Initialize mSensorManager and mSensor
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Set parameters for position to fit the screen
        mXOrigin = w * 0.5f;
        mYOrigin = h * 0.5f;
        mHorizontalBound = (w - BALL_SIZE) * 0.5f;
        mVerticalBound = (h - BALL_SIZE) * 0.5f;
        // Draw mField according to screen size so the background will fit the screen
        mField = Bitmap.createScaledBitmap(mField, w, h, true);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // for some device e.g. tablets the natural orientation of the device could be landscape mode.
        // So its important to put logic to interpret the sensor data properly.
        if (mDisplay.getRotation() == Surface.ROTATION_0) {
            mSensorX = event.values[0];
            mSensorY = event.values[1];
        } else if (mDisplay.getRotation() == Surface.ROTATION_90) {
            mSensorX = -event.values[1];
            mSensorY = event.values[0];
        }

        mSensorZ = event.values[2];
        mSensorTimeStamp = event.timestamp;
    }

    /**
     * Register the listener when app runs
     */
    public void startSimulation() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Unregister the listener when app paused/stopped
     */
    public void stopSimulation() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mField, 0, 0, null);
        canvas.drawBitmap(mBasket, mXOrigin - BASKET_SIZE / 2, mYOrigin - BASKET_SIZE, null);

        mBall.updatePosition(mSensorX, mSensorY, mSensorZ, mSensorTimeStamp);
        mBall.resolveCollisionWithBounds(mHorizontalBound, mVerticalBound);

        canvas.drawBitmap(mBitMAP,
                (mXOrigin - BALL_SIZE / 2) + mBall.mPosX,
                (mYOrigin - BALL_SIZE / 2) - mBall.mPosY, null);

        invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}