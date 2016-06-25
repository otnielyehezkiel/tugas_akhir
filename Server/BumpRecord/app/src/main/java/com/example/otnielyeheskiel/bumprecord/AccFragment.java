package com.example.otnielyeheskiel.bumprecord;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.opengl.Matrix;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccFragment extends Fragment implements SensorEventListener, FragmentLifecycle {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnometer;
    private MediaPlayer mpBump;

    private long timestamp;
    private double max = 17, min = 3;

    private float axisX = 0;
    private float axisY = 0;
    private float axisZ = 0;

    private float aX = 0;
    private float aY = 0;
    private float aZ = 0;


    private double speed;


    private float gravities[] = new float[3];
    private float accelerometers[] = new float[5];
    private float magnometers[] = new float[3];
    private TextView currentX, currentY, currentZ, tv_status, lon, lat, tv_timestamp,tv_speed,tv_jumlah_bump;

    private static final int MY_PERMISSIONS_REQUEST = 1;
    protected LocationManager lm;
    protected Location location;
    private GeomagneticField geoField;
    private LocationListener locationListener;
    private boolean isGPS;
    private boolean isNetwork;
    private boolean isSent = false;
    private boolean isBump = false;
    private boolean isContinue = true;
    private int countData = 0;
    private float latitude, longitude;
    private int id_user = 2;
    private int last_id;
    private float bearing, declination;
    private Statistics c;
    final String URL = "http://128.199.235.115/api/accelerometer";
    final String URL2 = "http://128.199.235.115/api/alldata";
    final String URL3 = "http://128.199.235.115/api/id_block";
    final String URL_USER = "http://128.199.235.115/api/getuser";
    private int x = 1;//1 sent normal, 2 hole, 3 bump, 4 break
    public JSONArray dataAcc;
    private long t = 0;
    private long countBump = 0;
    public LinkedList<JSONObject> q;

    private int count_bearing = 0;
    private float avg_bearing = 0;
    public static float LPF_ALPHA = 0.8f;
    private float[] linearAcceleration = new float[]{0, 0, 0, 0};
    private float[] Rotate = new float[16];

    private String android_id, device;

    public AccFragment() {
        // Required empty public constructor
    }

    protected FragmentActivity activity;
    protected View view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onActivityCreated(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_blank, container, false);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initializeViews();
        //Location Function
        locationFunc();
        q = new LinkedList<>();
        //sensor function
        sensorFunc();
        dataAcc = new JSONArray();

        android_id = Settings.Secure.getString(activity.getContentResolver(),Settings.Secure.ANDROID_ID);
        device = android.os.Build.MODEL;
        Log.d("android_id",android_id+" "+device);
        try {
            getLastId();
            getUserId();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    public void locationFunc() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);

            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }
        lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
                bearing = location.getBearing();
                count_bearing++;
                if (count_bearing > 1) {
                    if (Math.abs(avg_bearing - bearing) > 5) {
                        avg_bearing = 0;
                        count_bearing = 1;
                    }
                }
                avg_bearing = (avg_bearing + bearing) / count_bearing;

                geoField = new GeomagneticField(
                        latitude,
                        longitude,
                        Double.valueOf(location.getAltitude()).floatValue(),
                        System.currentTimeMillis()
                );
                declination = geoField.getDeclination();
                if (location.hasSpeed()) {
                    speed = location.getSpeed() * 3.6;
                    tv_speed.setText(String.format("%.3f", speed));
                }
                displayLocation();
                Log.d("location", "lat: " + latitude + " lon: " + longitude + " speed: " + String.format("%.3f", speed));
            }

            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d("location", "Status changed: " + s);
            }

            public void onProviderEnabled(String s) {
                Log.d("location", "PROVIDER DISABLED: " + s);
            }

            public void onProviderDisabled(String s) {
                Log.d("location", "PROVIDER DISABLED: " + s);
            }
        };


        try {
            this.isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isGPS) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = (float) location.getLatitude();
                    longitude = (float) location.getLongitude();
                    Log.d("location gps", "lat:" + latitude + ", lon: " + longitude + ", speed:" + speed);
                    displayLocation();
                }
            } else if (isNetwork) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    latitude = (float) location.getLatitude();
                    longitude = (float) location.getLongitude();
                    Log.d("location net", "lat:" + latitude + ", lon: " + longitude);
                    displayLocation();
                }
            }
        } catch (Exception e) {
            Log.d("location", e.getMessage());
        }
    }

    public void sensorFunc() {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success!
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            Log.d("sensor", "accelerometer success");
        } else {
            Log.d("sensor", "accelerometer failed");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            // success!
            magnometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_UI);
            Log.d("sensor", "magnometer success");
        } else {
            Log.d("sensor", "magnometer failed");
        }
    }

    public void initializeViews() {
        currentX = (TextView) view.findViewById(R.id.currentX);
        currentY = (TextView) view.findViewById(R.id.currentY);
        currentZ = (TextView) view.findViewById(R.id.currentZ);

        lon = (TextView) view.findViewById(R.id.lon);
        lat = (TextView) view.findViewById(R.id.lat);

        tv_timestamp = (TextView) view.findViewById(R.id.tv_timestamp);
        tv_status = (TextView) view.findViewById(R.id.tv_status);
        tv_speed = (TextView) view.findViewById(R.id.speed);
        tv_jumlah_bump = (TextView) view.findViewById(R.id.tv_jumlah_bump);

        mpBump = MediaPlayer.create(activity.getApplicationContext(), R.raw.bump);
    }

    public void fillData() {
        if (isBump && ((timestamp - t) < 1500) && !isContinue) {
            tv_status.setText("Bump");
            addObject(axisX, axisY, axisZ, timestamp);
        } else if (isBump) {
            Log.d("flag", "Bump_selesai");
            isBump = false;
            isContinue = true;
            tv_status.setText("Normal");
            try {
                postArrayData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dataAcc = new JSONArray(); // clear data
            last_id++;
        }
    }

    public void setPostData() {
        for (int i = 0; i < q.size(); i++) {
            try {
                float x = (float) q.get(i).getDouble("x");
                float y = (float) q.get(i).getDouble("y");
                float z = (float) q.get(i).getDouble("z");
                long time = q.get(i).getLong("waktu");
                addObject(x, y, z, time);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("flag", "setPostData");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometers = event.values.clone();
                aX = accelerometers[0];
                aY = accelerometers[1];
                aZ = accelerometers[2];
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, magnometers, 0, event.values.length);
                break;

        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            timestamp = System.currentTimeMillis();
            gravities[0] = LPF_ALPHA * gravities[0] + (1 - LPF_ALPHA) * aX;
            gravities[1] = LPF_ALPHA * gravities[1] + (1 - LPF_ALPHA) * aY;
            gravities[2] = LPF_ALPHA * gravities[2] + (1 - LPF_ALPHA) * aZ;

            linearAcceleration[0] = aX;
            linearAcceleration[1] = aY;
            linearAcceleration[2] = aZ;

            float[] earthAcceleration = new float[]{0, 0, 0, 0};
            float[] has = new float[]{0, 0, 0, 0};
            // Transposed matrix
            float[] Rt = new float[16];
            float[] matrix = new float[16];
            // TODO: http://developer.android.com/reference/android/hardware/SensorManager.html#getRotationMatrix(float[], float[], float[], float[])
            // says that R*gravity = [0 0 g] but the calculation only seems to work when we invert the Rotation matrix
            SensorManager.getRotationMatrix(Rotate, null, gravities, magnometers);
            Matrix.transposeM(Rt, 0, Rotate, 0);
            Matrix.multiplyMV(earthAcceleration, 0, Rt, 0, linearAcceleration, 0);
            if (avg_bearing != 0 && declination != 0) {
                Matrix.rotateM(matrix, 0, avg_bearing - declination, 0, 0, 1f);
                Matrix.multiplyMV(has, 0, matrix, 0, earthAcceleration, 0);
                axisX = has[0];
                axisY = has[1];
                axisZ = has[2];
            } else {
                axisX = earthAcceleration[0];
                axisY = earthAcceleration[1];
                axisZ = earthAcceleration[2];
            }

            axisX = earthAcceleration[0];
            axisY = earthAcceleration[1];
            axisZ = earthAcceleration[2];

            fillData();
            JSONObject obj = new JSONObject();
            try {
                obj.put("x", Float.toString(axisX));
                obj.put("y", Float.toString(axisY));
                obj.put("z", Float.toString(axisZ));
                obj.put("waktu", Long.toString(timestamp));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            q.addLast(obj);
            if (q.size() == 10) {
                q.removeFirst();
            }
            /*Event Detection with Z-Threshold*/
            if ((axisZ > max || axisZ < min)
                    && isContinue
                    && speed > 1
                    ) {
                isBump = true;
                isContinue = false;
                Log.d("flag", "Bump " + String.valueOf(isContinue));
                mpBump.start();
                t = timestamp;
                countData = 0;
                tv_status.setText("BUMP");
                countBump++;
                tv_jumlah_bump.setText(Long.toString(countBump));
                try {
                    addLocation(3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setPostData();
            }
        }
        displayCurrentValues();
    }

    /*Method Post Data JsonArray Accelerometer*/
    public void postArrayData() throws JSONException {
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(Request.Method.POST, URL2, dataAcc,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("response", response.toString());
                        Context context = activity.getApplicationContext();
                        CharSequence text = response.toString();
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("volley: ", error.toString());
                    }
                });
        ApplicationController.getInstance().addToRequestQueue(jsArrRequest);
    }

    /*Method Get last_id*/
    public void getLastId() throws JSONException {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL3, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("response", response.toString());
                        try {
                            last_id = response.getInt("id");
                            Context context = activity;
                            CharSequence text = response.toString();
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("volley:", error.toString());
                    }
                });
        ApplicationController.getInstance().addToRequestQueue(getRequest);
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(String.format("%.5f", axisX));
        currentY.setText(String.format("%.5f", axisY));
        currentZ.setText(String.format("%.5f", axisZ));
        tv_timestamp.setText(Long.toString(timestamp));
    }

    //display latitude, longitude
    public void displayLocation() {
        lat.setText(Float.toString(latitude));
        lon.setText(Float.toString(longitude));
    }

    void addObject(Float x, Float y, Float z, Long time) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("lat", Float.toString(latitude));
            obj.put("lon", Float.toString(longitude));
            obj.put("x", Float.toString(x));
            obj.put("y", Float.toString(y));
            obj.put("z", Float.toString(z));
            obj.put("waktu", Long.toString(time));
            dataAcc.put(obj);
            countData++;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addLocation(int jenis_id) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("lat", Float.toString(latitude));
        obj.put("lon", Float.toString(longitude));
        obj.put("jenis_id", jenis_id);
        obj.put("user_id", id_user);
        dataAcc.put(obj);
    }

    /*Method Get last_id*/
    public void getUserId() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("nama", android_id);
        obj.put("device", device);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, URL_USER, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            id_user = response.getInt("ID");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("getuserid", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("volley: ", error.toString());
                    }
                });
        ApplicationController.getInstance().addToRequestQueue(jsObjRequest);
    }

    @Override
    public void onPauseFragment() {
        Log.i("fragment", "onPauseFragment() Acc");
    }

    @Override
    public void onResumeFragment() {
        Log.i("fragment", "onResumeFragment() Acc");
    }

    //onResume() register the accelerometer for listening the events
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_UI);
        locationFunc();
        Log.d("Fragment","Fragment Acc resume");
    }

    //onPause() unregister the accelerometer for stop listening the events
    public void onPause() {
        sensorManager.unregisterListener(this);
        lm.removeUpdates(locationListener);
        super.onPause();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d("Fragment","Fragment Acc pause");
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}