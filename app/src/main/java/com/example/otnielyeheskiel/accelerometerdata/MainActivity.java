package com.example.otnielyeheskiel.accelerometerdata;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;

import java.security.Timestamp;

public class MainActivity extends Activity implements SensorEventListener {

    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private long timestamp;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private double alpha;
    private double beta;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float aX = 0;
    private float aY = 0;
    private float aZ = 0;

    private float accelerometervalues[] = new float[3];
    private float orientationvalues[] = new float[3];
    private float geomagneticmatrix[] = new float[3];

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ;

    public Vibrator v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success!
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);

        } else {
            // fail!
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            // success!
            magnetometer  = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(this, magnetometer , SensorManager.SENSOR_DELAY_UI);

        } else {
            // fail!
        }


    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch(event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                accelerometervalues = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagneticmatrix = event.values.clone();
                break;
        }

        timestamp = System.currentTimeMillis();
        lastX = deltaX;
        lastY = deltaY;
        lastZ = deltaZ;


        if (geomagneticmatrix != null && accelerometervalues != null) {
            float[] R = new float[16];
            float[] I = new float[16];
            SensorManager.getRotationMatrix(R, I, accelerometervalues, geomagneticmatrix);
            SensorManager.getOrientation(R, orientationvalues);
            deltaX =(float) (accelerometervalues[0]*(Math.cos(orientationvalues[2])*Math.cos(orientationvalues[0])+Math.sin(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.sin(orientationvalues[0])) + accelerometervalues[1]*(Math.cos(orientationvalues[1])*Math.sin(orientationvalues[0])) + accelerometervalues[2]*(-Math.sin(orientationvalues[2])*Math.cos(orientationvalues[0])+Math.cos(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.sin(orientationvalues[0])));
            deltaY = (float) (accelerometervalues[0]*(-Math.cos(orientationvalues[2])*Math.sin(orientationvalues[0])+Math.sin(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.cos(orientationvalues[0])) + accelerometervalues[1]*(Math.cos(orientationvalues[1])*Math.cos(orientationvalues[0])) + accelerometervalues[2]*(Math.sin(orientationvalues[2])*Math.sin(orientationvalues[0])+ Math.cos(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.cos(orientationvalues[0])));
            deltaZ = (float) (accelerometervalues[0]*(Math.sin(orientationvalues[2])*Math.cos(orientationvalues[1])) + accelerometervalues[1]*(-Math.sin(orientationvalues[1])) + accelerometervalues[2]*(Math.cos(orientationvalues[2])*Math.cos(orientationvalues[1])));
            //What should I do here to transform the components of accelerometervalues into real world acceleration components??
        }

        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();


        deltaX = Math.abs(lastX-deltaX);
        deltaY = Math.abs(lastY-deltaY);
        deltaZ = Math.abs(lastZ-deltaZ);

        // get the x,y,z values of the accelerometer
       /* aX = event.values[0];
        aY = event.values[1];
        aZ = event.values[2];

        alpha= Math.atan(aY/aZ);
        beta = Math.atan((-aX)/(Math.hypot(aY,aZ)));
        // reorientation
        deltaX =(float) ((Math.cos(beta)*aX) + (Math.sin(beta)*Math.sin(alpha)*aY) +
                (Math.cos(alpha)*Math.sin(beta)*aZ));
        deltaY = (float)(Math.cos(alpha)*aY-Math.sin(alpha)*aZ);
        deltaZ = (float) (-(Math.sin(beta)*aX)+(Math.cos(beta)*Math.sin(alpha)*aY)+Math.cos(beta)*Math.cos(alpha)*aZ);

        deltaX = Math.abs(lastX-deltaX);
        deltaY = Math.abs(lastY-deltaY);
        deltaZ = Math.abs(lastZ-deltaZ);*/
        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if (deltaZ < 2)
            deltaZ = 0;

    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));

    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }
}
