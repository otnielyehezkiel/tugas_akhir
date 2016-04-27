package com.example.otnielyeheskiel.accelerometerdata;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.example.otnielyeheskiel.accelerometerdata.Statistics;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Handler;

public class MainActivity extends Activity implements SensorEventListener {

    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gravity;
    private Sensor magnometer;
    private long timestamp;
    private long pastTime;
    private long timeLocation;
    private long pTimeLocation=0;
    private double max=16.5,min=0;

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

    private double speed;

    private double temp;

    private final float alfa = (float) 0.8;

    private float gravities[]=new float[3];
    private float accelerometers[]=new float[5];
    private float magnometers[]=new float[3];
    private float Rotation[] =new float[16];
    private float earth_acc[] =new float[3];
    private TextView currentX, currentY, currentZ, status, lon,lat,tv_timestamp,tv_mode,tv_data,tv_speed,tv_maxmin,tv_std,tv_locationid;
    private Button sentBtn, holeBtn, bumpBtn, breakBtn ,orieantationBtn, modeBtn, sentarrayBtn, autoBtn,setBtn;
    private double std;
    private EditText etMintresh, etMaxtresh;

    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    protected LocationManager lm;
    protected Location location;
    private boolean isGPS;
    private boolean isNetwork;
    private boolean isSent=false;
    private boolean isRealTime = true; //using volley jsonObject or jsonArray
    private boolean isReorientation = true;
    private boolean isAuto = false;
    private boolean isBump = false;
    private boolean isHole = false;
    private boolean isContinue = true;
    private int countData = 0;
    private float latitude, longitude;
    private int id_user=1;
    private int last_id;
    private Statistics c;
    final String URL = "http://128.199.235.115/api/accelerometer";
    final String URL2 = "http://128.199.235.115/api/array";
    final String URL3 = "http://128.199.235.115/api/id_block";
    final String URL4 = "http://128.199.235.115/api/location";

    private int x = 1;//1 sent normal, 2 hole, 3 bump, 4 break
    public JSONArray dataAcc;
    private long t=0;
    public LinkedList<JSONObject> q;

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
        try {
            getLastId();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void locationFunc(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_REQUEST_COARSE_LOCATION);
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                latitude = (float) location.getLatitude();
                longitude =(float)  location.getLongitude();
                timeLocation = System.currentTimeMillis();
                if(location!=null && location.hasSpeed()){
                    speed = location.getSpeed()*3.6;
                    tv_speed.setText(String.format("%.3f", speed));
                }
                displayLocation();
                Log.d("location", "lat: "+ latitude + " lon: "+ longitude+ " speed: "+String.format("%.3f", speed));
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
            if(isGPS){
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locationListener );
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = (float) location.getLatitude();
                    longitude = (float)  location.getLongitude();
                    Log.d("location gps","lat:" + latitude +", lon: " +longitude+", speed:"+speed);
                    displayLocation();
                }
            }
            else if(isNetwork){
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    latitude = (float) location.getLatitude();
                    longitude = (float)  location.getLongitude();
                    Log.d("location net","lat:" + latitude +", lon: " +longitude);
                    displayLocation();
                }
            }
        } catch (Exception e) {
            Log.d("location",e.getMessage());// lets the user know there is a problem with the gps           zz
        }
    }

    public void sensorFunc(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success!
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            Log.d("sensor","accelerometer success");
        } else {
            Log.d("sensor", "accelerometer failed");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            // success!
            gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_UI);
            Log.d("sensor","gravity success");
        } else {

            Log.d("sensor", "gravity failed");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            // success!
            magnometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_UI);
            Log.d("sensor","magnometer success");
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
        sentBtn = (Button) findViewById(R.id.btn_sent);
        holeBtn = (Button) findViewById(R.id.btn_hole);
        bumpBtn = (Button) findViewById(R.id.btn_bump);
        breakBtn = (Button) findViewById(R.id.btn_break);
        orieantationBtn = (Button) findViewById(R.id.btn_reorientation);
        autoBtn = (Button) findViewById(R.id.btn_automode);
        modeBtn = (Button) findViewById(R.id.btn_mode);
        setBtn = (Button) findViewById(R.id.btn_set);
        sentarrayBtn = (Button) findViewById(R.id.btn_sent_array);
        sentarrayBtn.setVisibility(View.INVISIBLE);
        tv_mode.setText("ON");
    }

    public void initializeButton(){
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                max = Double.parseDouble(etMaxtresh.getText().toString());
                min = Double.parseDouble(etMintresh.getText().toString());
                tv_maxmin.setText("Max: "+ etMaxtresh.getText().toString()+", Min:"+etMintresh.getText().toString());
            }
        });
        modeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRealTime){
                    modeBtn.setText("ON");
                    tv_mode.setText("OFF");
                    sentarrayBtn.setVisibility(View.VISIBLE);
                    isRealTime = false;
                }
                else {
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
                if(!isRealTime){
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
                if(!isSent){
                    isSent = true;
                    x=1;
                    try {
                        postLocation(1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    status.setText("Normal");
                }
                else {
                    isSent = false;
                    status.setText("Not Sent");
                    last_id++;
                    tv_locationid.setText(Integer.toString(last_id));
                }
            }
        });
        holeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!isSent){
                    isSent = true;
                    x=5;
                    status.setText("Hole");
                    try {
                        postLocation(5);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    isSent = false;
                    status.setText("Not Sent");
                    last_id++;
                    tv_locationid.setText(Integer.toString(last_id));
                }
            }
        });
        bumpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!isSent){
                    isSent = true;
                    x=6;
                    status.setText("Bump");
                    try {
                        postLocation(6);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    isSent = false;
                    status.setText("Not Sent");
                    last_id++;
                    tv_locationid.setText(Integer.toString(last_id));
                }
            }
        });
        breakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSent){
                    isSent = true;
                    x=4;
                    status.setText("Break");
                    try {
                        postLocation(4);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
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
                if(isReorientation){
                    isReorientation = false;
                    orieantationBtn.setText("REORIENTATION");
                    Context context = getApplicationContext();
                    CharSequence text = Integer.toString(last_id);
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }
                else {
                    isReorientation = true;
                    orieantationBtn.setText("ORIENTATION");
                }
            }
        });
        autoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAuto){
                    isAuto = false;
                    autoBtn.setText("AUTO");
                    status.setText("Manual");
                    holeBtn.setVisibility(View.VISIBLE);
                    sentBtn.setVisibility(View.VISIBLE);
                    bumpBtn.setVisibility(View.VISIBLE);
                    breakBtn.setVisibility(View.VISIBLE);
                }
                else{
                    isAuto = true;
                    autoBtn.setText("Manual");
                    status.setText("Auto");
                    holeBtn.setVisibility(View.INVISIBLE);
                    sentBtn.setVisibility(View.INVISIBLE);
                    bumpBtn.setVisibility(View.INVISIBLE);
                    breakBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

    }
    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void fillData(){
        if(isBump && ((timestamp - t) < 2000) && !isContinue){
            addObject(axisZ,timestamp);
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
            addObject(axisZ,timestamp);
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
                float z = (float) q.get(i).getDouble("z");
                long time = q.get(i).getLong("waktu");
                addObject(z,time);
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
                //accelerometers = event.values.clone();
                accelerometers[0] = event.values[0];
                accelerometers[1] = event.values[1];
                accelerometers[2] = event.values[2];
                accelerometers[3] = 0;
                //accelerometers[3] = 0;
                //Log.d("sensor",accelerometers[0]+" " +accelerometers[1]+" "+ accelerometers[2]);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnometers = event.values.clone();
                break;
            case Sensor.TYPE_GRAVITY:
                gravities = event.values.clone();
                break;

        }

        timestamp = System.currentTimeMillis();
        Log.d("timestamp",Long.toString(timestamp));

        aX = accelerometers[0];
        aY = accelerometers[1];
        aZ = accelerometers[2];

        if(isAuto){
            alpha= Math.atan2(aY,aZ);
            beta = Math.atan2(( -aX),(Math.hypot(aY,aZ)));
            axisX = (float) ((Math.cos(beta) * aX) + (Math.sin(beta) * Math.sin(alpha) * aY) +
                    (Math.cos(alpha) * Math.sin(beta) * aZ));
            axisY = (float) (Math.cos(alpha) * aY - Math.sin(alpha) * aZ);
            axisZ = (float) (-(Math.sin(beta) * aX) + (Math.cos(beta) * Math.sin(alpha) * aY) + Math.cos(beta) * Math.cos(alpha) * aZ);
            aX = axisX;
            aY = axisY;
            aZ = axisZ;
            Log.d("sensor auto ",String.format("%.4f", axisX)+" " +
                    String.format("%.4f", axisY)+" "+
                    String.format("%.4f", axisZ));

            fillData();
            JSONObject obj = new JSONObject();
            try {
                obj.put("z", Float.toString(axisZ));
                obj.put("waktu", Long.toString(timestamp));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            q.addLast(obj);
            if(q.size()==10){
                double[] stdv = new double[10];
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
            if((axisZ > max || axisZ < min ) && isContinue && speed != 0){
                if( (pastZ - axisZ) < 0 ){
                    isBump = true;
                    isContinue = false;
                    Log.d("flag","Bump "+ String.valueOf(isContinue));
                    t = timestamp;
                    countData = 0;
                    status.setText("BUMP ");
                    tv_std.setText(String.format("%.4f", std));
                    try {
                        postLocation(3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setPostData();
                }
                else if((pastZ - axisZ) > 0) {
                    isHole = true;
                    isContinue = false;
                    Log.d("flag","Hole "+ String.valueOf(isContinue));
                    t = timestamp;
                    countData = 0;
                    status.setText("HOLE ");
                    tv_std.setText(String.format("%.4f", std));
                    try {
                        postLocation(2);
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
        else{
            // reorientation
            if(isReorientation){

                alpha= Math.atan2(aY,aZ);
                beta = Math.atan2(( -aX),(Math.hypot(aY,aZ)));
                axisX = (float) ((Math.cos(beta) * aX) + (Math.sin(beta) * Math.sin(alpha) * aY) +
                        (Math.cos(alpha) * Math.sin(beta) * aZ));
                axisY = (float) (Math.cos(alpha) * aY - Math.sin(alpha) * aZ);
                axisZ = (float) (-(Math.sin(beta) * aX) + (Math.cos(beta) * Math.sin(alpha) * aY) + Math.cos(beta) * Math.cos(alpha) * aZ);


                // Log.d("sensor Reor ",String.format("%.4f", axisX)+" " + String.format("%.4f", axisY)+" "+ String.format("%.4f", axisZ));
            }
            else{
                float[] R = new float[9];
                float[] I = new float[9];
                SensorManager.getRotationMatrix(R, I, gravities, magnometers);
                axisX = R[0] * aX + R[1] * aY + R[2] * aZ;
                axisY = R[3] * aX + R[4] * aY + R[5] * aZ;
                axisZ = R[6] * aX + R[7] * aY + R[8] * aZ;

                Log.d("sensor Orie ",String.format("%.4f", axisX)+" " +
                        String.format("%.4f", axisY)+" "+
                        String.format("%.4f", axisZ));
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
                    addObject(axisZ,timestamp);
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
    /*Method Post Location*/
    public void postLocation(int jenis_id) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("lat", Float.toString(latitude));
        obj.put("lon", Float.toString(longitude));
        obj.put("jenis_id",jenis_id);
        obj.put("user_id", id_user);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,URL4,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Context context = getApplicationContext();
                        CharSequence text = response.toString();
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        Log.d("response", response.toString());
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
    /*Method Get last_id*/
    public void getLastId() throws JSONException{
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL3, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // display response
                    Log.d("response", response.toString());
                    try {
                        last_id = response.getInt("max");
                        tv_locationid.setText(Integer.toString(last_id));
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

    void addObject(Float z, Long time){
        try {
            JSONObject obj = new JSONObject();
            obj.put("lat", Float.toString(latitude));
            obj.put("lon", Float.toString(longitude));
            obj.put("z", Float.toString(z));
            obj.put("waktu", Long.toString(time));
            obj.put("location_id",last_id+1);
            dataAcc.put(obj);
            countData++;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
