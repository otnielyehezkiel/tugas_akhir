package com.example.otnielyeheskiel.accelerometerdata;

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
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gravity;
    private Sensor magnometer;
    private MediaPlayer mpBump, mpHole, mpNotsame;

    private long timestamp;
    private long pastTime;
    private long timeLocation;
    private long pTimeLocation = 0;
    private double max = 16.5, min = 0;

    private double alpha;
    private double beta;

    private float axisX = 0;
    private float axisY = 0;
    private float axisZ = 0;

    private float aX = 0;
    private float aY = 0;
    private float aZ = 0;

    private float pastX = 0;
    private float pastY = 0;
    private float pastZ = 0;

    private int checkLocationSent, checkDataSent;

    private double speed;

    private double temp;

    private final float alfa = (float) 0.8;

    private float gravities[] = new float[3];
    private float accelerometers[] = new float[5];
    private float magnometers[] = new float[3];
    private TextView currentX, currentY, currentZ, status, lon, lat, tv_timestamp, tv_mode, tv_data, tv_speed, tv_maxmin, tv_std, tv_locationid;
    private TextView tv_jumlah_hole, tv_jumlah_bump, tv_bearing;
    private Button sentBtn, holeBtn, bumpBtn, orieantationBtn, modeBtn, sentarrayBtn, autoBtn, setBtn;
    private double std;
    private EditText etMintresh, etMaxtresh;

    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    protected LocationManager lm;
    protected Location location;
    private GeomagneticField geoField;
    private LocationListener locationListener;
    private boolean isGPS;
    private boolean isNetwork;
    private boolean isSent = false;
    private boolean isRealTime = true; //using volley jsonObject or jsonArray
    private boolean isReorientation = true;
    private boolean isAuto = false;
    private boolean isBump = false;
    private boolean isHole = false;
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
    final String URL4 = "http://128.199.235.115/api/location";
    final String URL_USER = "http://128.199.235.115/api/getuser";
    private double[] stdv = new double[10];
    private int x = 1;//1 sent normal, 2 hole, 3 bump, 4 break
    public JSONArray dataAcc;
    private long t = 0;
    private long countHole = 0, countBump = 0;
    public LinkedList<JSONObject> q;

    public static float LPF_ALPHA = 0.8f;
    private float[] linearAcceleration = new float[]{0, 0, 0, 0};
    private float[] Rotate = new float[16];

    private String android_id,device;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initializeViews();
        initializeButton();
        //Location Function
        locationFunc();
        q = new LinkedList<>();
        //sensor function
        sensorFunc();
        dataAcc = new JSONArray();

        android_id = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        device = android.os.Build.MODEL;
        Log.d("android_id",android_id+" "+device);
        try {
            getLastId();
            //getUserId();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void locationFunc() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
                tv_bearing.setText(String.valueOf(location.getBearing()));
                bearing = location.getBearing();
                geoField = new GeomagneticField(
                        latitude,
                        longitude,
                        Double.valueOf(location.getAltitude()).floatValue(),
                        System.currentTimeMillis()
                );
                timeLocation = System.currentTimeMillis();
                tv_bearing.append(" " + String.valueOf(geoField.getDeclination()));
                declination = geoField.getDeclination();
                if (location != null && location.hasSpeed()) {
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
            Log.d("location", e.getMessage());// lets the user know there is a problem with the gps           zz
        }
    }

    public void sensorFunc() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

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
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        lon = (TextView) findViewById(R.id.lon);
        lat = (TextView) findViewById(R.id.lat);

        etMaxtresh = (EditText) findViewById(R.id.et_maxtresh);
        etMintresh = (EditText) findViewById(R.id.et_mintresh);

        tv_timestamp = (TextView) findViewById(R.id.tv_timestamp);
        tv_std = (TextView) findViewById(R.id.tv_std);
        status = (TextView) findViewById(R.id.tv_status);
        tv_mode = (TextView) findViewById(R.id.tv_mode);
        tv_data = (TextView) findViewById(R.id.tv_data);
        tv_speed = (TextView) findViewById(R.id.speed);
        tv_maxmin = (TextView) findViewById(R.id.tv_maxmin);
        tv_locationid = (TextView) findViewById(R.id.tv_locationid);
        tv_jumlah_bump = (TextView) findViewById(R.id.tv_jumlah_bump);
        tv_jumlah_hole = (TextView) findViewById(R.id.tv_jumlah_hole);
        tv_bearing = (TextView) findViewById(R.id.tv_bearing);

        sentBtn = (Button) findViewById(R.id.btn_sent);
        holeBtn = (Button) findViewById(R.id.btn_hole);
        bumpBtn = (Button) findViewById(R.id.btn_bump);
        orieantationBtn = (Button) findViewById(R.id.btn_reorientation);
        autoBtn = (Button) findViewById(R.id.btn_automode);
        modeBtn = (Button) findViewById(R.id.btn_mode);
        setBtn = (Button) findViewById(R.id.btn_set);
        sentarrayBtn = (Button) findViewById(R.id.btn_sent_array);

        mpBump = MediaPlayer.create(getApplicationContext(), R.raw.bump);
        mpHole = MediaPlayer.create(getApplication(),R.raw.hole);
        mpNotsame = MediaPlayer.create(getApplication(),R.raw.notsame);

        sentarrayBtn.setVisibility(View.INVISIBLE);
        tv_mode.setText("ON");
    }

    public void initializeButton() {
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                max = Double.parseDouble(etMaxtresh.getText().toString());
                min = Double.parseDouble(etMintresh.getText().toString());
                tv_maxmin.setText("Max: " + etMaxtresh.getText().toString() + " Min:" + etMintresh.getText().toString());
            }
        });
        modeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRealTime) {
                    modeBtn.setText("ON");
                    tv_mode.setText("OFF");
                    sentarrayBtn.setVisibility(View.VISIBLE);
                    isRealTime = false;
                } else {
                    modeBtn.setText("OFF");
                    tv_mode.setText("ON");
                    isRealTime = true;
                    sentarrayBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
        sentarrayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRealTime) {
                    try {
                        postArrayData();
                        countData = 0;
                        dataAcc = new JSONArray();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        sentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isSent) {
                    isSent = true;
                    x = 1;
                    try {
                        addLocation(1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    status.setText("Normal");
                } else {
                    isSent = false;
                    status.setText("Not Sent");
                    last_id++;
                    tv_locationid.setText(Integer.toString(last_id));
                }
            }
        });
        holeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isSent) {
                    isSent = true;
                    x = 5;
                    status.setText("Hole");
                    try {
                        addLocation(5);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    isSent = false;
                    status.setText("Not Sent");
                    last_id++;
                    tv_locationid.setText(Integer.toString(last_id));
                }
            }
        });
        bumpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isSent) {
                    isSent = true;
                    x = 6;
                    status.setText("Bump");
                    try {
                        addLocation(6);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    isSent = false;
                    status.setText("Not Sent");
                    last_id++;
                    tv_locationid.setText(Integer.toString(last_id));
                }
            }
        });
        orieantationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isReorientation) {
                    isReorientation = false;
                    orieantationBtn.setText("REORIENTATION");
                    Context context = getApplicationContext();
                    CharSequence text = Integer.toString(last_id);
                    int duration = Toast.LENGTH_SHORT;
                    try {
                        getUserId();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                } else {
                    isReorientation = true;
                    orieantationBtn.setText("ORIENTATION");
                }
            }
        });
        autoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAuto) {
                    isAuto = false;
                    autoBtn.setText("AUTO");
                    status.setText("Manual");
                    holeBtn.setVisibility(View.VISIBLE);
                    sentBtn.setVisibility(View.VISIBLE);
                    bumpBtn.setVisibility(View.VISIBLE);
                } else {
                    isAuto = true;
                    autoBtn.setText("Manual");
                    status.setText("Auto");
                    holeBtn.setVisibility(View.INVISIBLE);
                    sentBtn.setVisibility(View.INVISIBLE);
                    bumpBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_UI);
        locationFunc();
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.removeUpdates(locationListener);
    }

    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void fillData(){
        if(isBump && ((timestamp - t) < 2000) && !isContinue){
            addObject(axisX,axisY,axisZ,timestamp);
        }
        else if(isBump){
            Log.d("flag","Bump_selesai");
            isBump = false;
            isContinue = true;
            status.setText("NORMAL");
            try {
                postArrayData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dataAcc = new JSONArray(); // clear data
            last_id++;
            tv_locationid.setText(Integer.toString(last_id));
            Log.d("flag","-----");
        }

        if(isHole && ((timestamp - t) < 2000) && !isContinue){
            addObject(axisX,axisY,axisZ,timestamp);
        }
        else if(isHole){
            Log.d("flag","Hole_selesai");
            isHole = false;
            isContinue = true;
            status.setText("NORMAL");
            try {
                postArrayData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dataAcc = new JSONArray(); // clear data
            last_id++;
            tv_locationid.setText(Integer.toString(last_id));
            Log.d("flag","-----");
        }
    }

    public void setPostData(){
        for(int i=0;i<q.size();i++){
            try {
                float x = (float) q.get(i).getDouble("x");
                float y = (float) q.get(i).getDouble("y");
                float z = (float) q.get(i).getDouble("z");
                long time = q.get(i).getLong("waktu");
                addObject(x,y,z,time);
                //Log.d("test= ",Float.toString(z)+ " ke-"+Integer.toString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("flag","setPostData");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()){
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

        if(isAuto && event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            timestamp = System.currentTimeMillis();
            gravities[0] = LPF_ALPHA * gravities[0] + (1 - LPF_ALPHA) * aX;
            gravities[1] = LPF_ALPHA * gravities[1] + (1 - LPF_ALPHA) * aY;
            gravities[2] = LPF_ALPHA * gravities[2] + (1 - LPF_ALPHA) * aZ;

            linearAcceleration[0] = aX ;
            linearAcceleration[1] = aY ;
            linearAcceleration[2] = aZ ;

            float [] earthAcceleration = new float[] {0, 0, 0, 0};

            // Transposed matrix
            float[] Rt = new float[16];

            // TODO: http://developer.android.com/reference/android/hardware/SensorManager.html#getRotationMatrix(float[], float[], float[], float[])
            // says that R*gravity = [0 0 g] but the calculation only seems to work when we invert the Rotation matrix
            SensorManager.getRotationMatrix(Rotate, null, gravities, magnometers);
            Matrix.transposeM(Rt, 0, Rotate, 0);
            Matrix.multiplyMV(earthAcceleration, 0, Rt, 0, linearAcceleration, 0);
            axisX = earthAcceleration[0];
            axisY = earthAcceleration[1];
            axisZ = earthAcceleration[2];
            Log.d("sensor auto ",String.format("%.4f", axisX)+" " +
                    String.format("%.4f", axisY)+" "+
                    String.format("%.4f", axisZ));

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
            if(q.size()==10){

                for(int i=0;i<q.size();i++){
                    try {
                        stdv[i]= q.get(i).getDouble("z");
                        //Log.d("test= ",Integer.toString(q.get(i).getInt("test")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //Log.d("test= ","------");
                c = new Statistics(stdv);
                std = c.getStdDev();
                Log.d("statistic std= ",String.format("%.4f", std));
                q.removeFirst();
            }
            /*Event Detection*/
            if((axisZ > max || axisZ < min ) && isContinue  && speed >1){
                if( (pastZ - axisZ) < 0 ){
                    isBump = true;
                    isContinue = false;
                    Log.d("flag","Bump "+ String.valueOf(isContinue));
                    mpBump.start();
                    t = timestamp;
                    countData = 0;
                    status.setText("BUMP ");
                    tv_std.setText(String.format("%.4f", std));
                    countBump++;
                    tv_jumlah_bump.setText(Long.toString(countBump));
                    tv_jumlah_bump.setTextColor(Color.RED);
                    tv_jumlah_hole.setTextColor(Color.BLACK);
                    try {
                        addLocation(3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setPostData();
                }
                else if((pastZ - axisZ) > 0) {
                    isHole = true;
                    isContinue = false;
                    Log.d("flag","Hole "+ String.valueOf(isContinue));
                    mpHole.start();
                    t = timestamp;
                    countData = 0;
                    status.setText("HOLE ");
                    tv_std.setText(String.format("%.4f", std));
                    countHole++;
                    tv_jumlah_hole.setText(Long.toString(countHole));
                    tv_jumlah_hole.setTextColor(Color.RED);
                    tv_jumlah_bump.setTextColor(Color.BLACK);
                    try {
                        addLocation(2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setPostData();
                }
            }
            //sent when detect event
            pastX = aX;
            pastY = aY;
            pastZ = aZ;
            pastTime = timestamp;
        }
        else if(!isAuto && event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            // reorientation
            if(isReorientation ){
                timestamp = System.currentTimeMillis();
                alpha= Math.atan2(aY,aZ);
                beta = Math.atan2(( -aX),(Math.hypot(aY,aZ)));
                axisX = (float) ((Math.cos(beta) * aX) + (Math.sin(beta) * Math.sin(alpha) * aY) +
                        (Math.cos(alpha) * Math.sin(beta) * aZ));
                axisY = (float) (Math.cos(alpha) * aY - Math.sin(alpha) * aZ);
                axisZ = (float) (-(Math.sin(beta) * aX) + (Math.cos(beta) * Math.sin(alpha) * aY) + Math.cos(beta) * Math.cos(alpha) * aZ);


                // Log.d("sensor Reor ",String.format("%.4f", axisX)+" " + String.format("%.4f", axisY)+" "+ String.format("%.4f", axisZ));
            }
            else{
                timestamp = System.currentTimeMillis();
                gravities[0] = LPF_ALPHA * gravities[0] + (1 - LPF_ALPHA) * aX;
                gravities[1] = LPF_ALPHA * gravities[1] + (1 - LPF_ALPHA) * aY;
                gravities[2] = LPF_ALPHA * gravities[2] + (1 - LPF_ALPHA) * aZ;

                linearAcceleration[0] = aX ;
                linearAcceleration[1] = aY ;
                linearAcceleration[2] = aZ ;

                float [] earthAcceleration = new float[] {0, 0, 0, 0};

                // Transposed matrix
                float[] Rt = new float[16];
                // TODO: http://developer.android.com/reference/android/hardware/SensorManager.html#getRotationMatrix(float[], float[], float[], float[])
                // says that R*gravity = [0 0 g] but the calculation only seems to work when we invert the Rotation matrix
                SensorManager.getRotationMatrix(Rotate, null, gravities, magnometers);
                Matrix.transposeM(Rt, 0, Rotate, 0);
                Matrix.multiplyMV(earthAcceleration, 0, Rt, 0, linearAcceleration, 0);
                /*if(bearing!= 0 && declination !=0){
                    Matrix.rotateM(earthAcceleration,0,bearing-declination,1f,1f,0);
                }*/
                axisX = earthAcceleration[0];
                axisY = earthAcceleration[1];
                axisZ = earthAcceleration[2];
                Log.d("sensor Orie ",String.format("%.4f", axisX)+" " + String.format("%.4f", axisY)+" "+  String.format("%.4f", axisZ));
            }
            pastX = aX;
            pastY = aY;
            pastZ = aZ;

            if(isSent) {
                if(isRealTime) {
                    try {
                        postAccelData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    addObject(axisX,axisY,axisZ,timestamp);
                }
            }
        }
        displayCurrentValues();
    }
    /*Method Post RealTime Data Accelerometer*/
    public void postAccelData() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("lat", Float.toString(latitude));
        obj.put("lon", Float.toString(longitude));
        obj.put("x", Float.toString(axisX));
        obj.put("y", Float.toString(axisY));
        obj.put("z", Float.toString(axisZ));
        obj.put("waktu", Long.toString(timestamp));
        obj.put("location_id",last_id+1);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,URL,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response postaccel", response.toString());
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
    /*Method Post Data JsonArray Accelerometer*/
    public void postArrayData() throws JSONException {
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(Request.Method.POST, URL2, dataAcc,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("response", response.toString());
                        Context context = getApplicationContext();
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
    public void getLastId() throws JSONException{
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL3, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // display response
                    Log.d("response", response.toString());
                    try {
                        last_id = response.getInt("id");
                        tv_locationid.setText(Integer.toString(last_id));
                        if(last_id == -1){
                            tv_locationid.setText("Tidak Sama");
                            mpNotsame.start();
                        }
                        Context context = getApplicationContext();
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
        tv_data.setText(Integer.toString(countData));
    }
    //display latitude, longitude
    public void displayLocation(){
        lat.setText(Float.toString(latitude));
        lon.setText(Float.toString(longitude));
    }

    void addObject(Float x, Float y, Float z, Long time){
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

    public void addLocation(int jenis_id) throws JSONException{
        JSONObject obj = new JSONObject();
        obj.put("lat", Float.toString(latitude));
        obj.put("lon", Float.toString(longitude));
        obj.put("jenis_id",jenis_id);
        obj.put("user_id", id_user);
        dataAcc.put(obj);
    }

    /*Method Get last_id*/
    public void getUserId() throws JSONException{
        JSONObject obj = new JSONObject();
        obj.put("nama", android_id);
        obj.put("device",device);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,URL_USER,obj,
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


}
