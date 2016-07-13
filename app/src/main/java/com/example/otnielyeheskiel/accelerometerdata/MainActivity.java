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
    private MediaPlayer mpBump, mpNotsame;
    private int pcb=0;

    private long timestamp;
    private long pastTime;
    private long timeLocation;
    private long pTimeLocation = 0;
    private double max = 16.5, min = 0;

    private double alpha;
    private double beta;

    private double roll,pitch,magnitude,yaw;

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

    private float gravities[] = new float[3];
    private float accelerometers[] = new float[5];
    private float magnometers[] = new float[3];
    private TextView currentX, currentY, currentZ, status, lon, lat, tv_timestamp, tv_data, tv_speed, tv_maxmin, tv_std, tv_locationid;
    private TextView tv_jumlah_hole, tv_jumlah_bump, tv_bearing;
    private Button bumpBtn, orieantationBtn, autoBtn,speedBtn, setBtn;
    private EditText etMintresh, etMaxtresh, etPcb;

    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    protected LocationManager lm;
    protected Location location;
    private GeomagneticField geoField;
    private LocationListener locationListener;
    private boolean isGPS;
    private boolean isNetwork;
    private boolean isSent = false;
    private boolean isRealTime = false; //using volley jsonObject or jsonArray
    private boolean isReorientation = true;
    private boolean isAuto = false;
    private boolean isBump = false;
    private boolean isHole = false;
    private boolean isContinue = true;
    private int countData = 0;
    private float latitude, longitude;
    private int id_user = 2;
    private int last_id;
    private double speed_val = 1.5;
    private float bearing, declination;
    private Statistics c;
    final String URL2 = "http://128.199.232.180/api/alldata";
    final String URL3 = "http://128.199.232.180/api/id_block";
    final String URL_USER = "http://128.199.232.180/api/getuser";
    private int x = 1;//1 sent normal, 2 hole, 3 bump, 4 break
    public JSONArray dataAcc;
    private long t = 0;
    private long countHole = 0, countBump = 0;
    public LinkedList<JSONObject> q;

    private int count_bearing = 0;
    private float avg_bearing = 0;
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
            getUserId();
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

                bearing = location.getBearing();
                count_bearing++;
                if(count_bearing > 1){
                    if(Math.abs(avg_bearing-bearing) > 5){
                        avg_bearing = 0;
                        count_bearing = 1;
                    }
                }
                avg_bearing = (avg_bearing + bearing)/count_bearing;
                tv_bearing.setText(String.valueOf(avg_bearing));
                geoField = new GeomagneticField(
                        latitude,
                        longitude,
                        Double.valueOf(location.getAltitude()).floatValue(),
                        System.currentTimeMillis()
                );
                timeLocation = System.currentTimeMillis();
                tv_bearing.append(" " + String.valueOf(geoField.getDeclination()));
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
        etPcb = (EditText) findViewById(R.id.et_pcb);

        tv_timestamp = (TextView) findViewById(R.id.tv_timestamp);
        tv_std = (TextView) findViewById(R.id.tv_std);
        status = (TextView) findViewById(R.id.tv_status);
        tv_data = (TextView) findViewById(R.id.tv_data);
        tv_speed = (TextView) findViewById(R.id.speed);
        tv_maxmin = (TextView) findViewById(R.id.tv_maxmin);
        tv_locationid = (TextView) findViewById(R.id.tv_locationid);
        tv_jumlah_bump = (TextView) findViewById(R.id.tv_jumlah_bump);
        tv_bearing = (TextView) findViewById(R.id.tv_bearing);

        bumpBtn = (Button) findViewById(R.id.btn_bump);
        orieantationBtn = (Button) findViewById(R.id.btn_reorientation);
        autoBtn = (Button) findViewById(R.id.btn_automode);
        speedBtn = (Button) findViewById(R.id.btn_speed);
        setBtn = (Button) findViewById(R.id.btn_set);

        mpBump = MediaPlayer.create(getApplicationContext(), R.raw.bump);
        mpNotsame = MediaPlayer.create(getApplication(),R.raw.notsame);
    }

    public void initializeButton() {
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                max = Double.parseDouble(etMaxtresh.getText().toString());
                min = Double.parseDouble(etMintresh.getText().toString());
                pcb = Integer.parseInt(etPcb.getText().toString());
                tv_maxmin.setText("Max: " + etMaxtresh.getText().toString() + " Min:" + etMintresh.getText().toString()+",Pcb:"+etPcb.getText().toString());
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
                    try {
                        postArrayData();
                        getLastId();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dataAcc = new JSONArray();
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
                    bumpBtn.setVisibility(View.VISIBLE);
                } else {
                    isAuto = true;
                    autoBtn.setText("Manual");
                    status.setText("Auto");
                    bumpBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        speedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(speed_val == 1.5){
                    speed_val = 0;
                    status.setText("speed 0");
                }
                else {
                    speed_val = 1.5;
                    status.setText("Normal speed");
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
        if(isBump && ((timestamp - t) < 1500) && !isContinue){
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
    }

    public void setPostData(){
        for(int i=0;i<q.size();i++){
            try {
                float x = (float) q.get(i).getDouble("x");
                float y = (float) q.get(i).getDouble("y");
                float z = (float) q.get(i).getDouble("z");
                long time = q.get(i).getLong("waktu");
                addObject(x,y,z,time);
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
            float [] has = new float[] {0, 0, 0, 0};
            // Transposed matrix
            float[] Rt = new float[16];
            float[] matrix = new float[16];
            // TODO: http://developer.android.com/reference/android/hardware/SensorManager.html#getRotationMatrix(float[], float[], float[], float[])
            // says that R*gravity = [0 0 g] but the calculation only seems to work when we invert the Rotation matrix
            SensorManager.getRotationMatrix(Rotate, null, gravities, magnometers);
            Matrix.transposeM(Rt, 0, Rotate, 0);
            Matrix.multiplyMV(earthAcceleration, 0, Rt, 0, linearAcceleration, 0);
            if(avg_bearing!= 0 && declination !=0){
                Matrix.rotateM(matrix,0,avg_bearing-declination,0,0,1f);
                Matrix.multiplyMV(has, 0, matrix, 0, earthAcceleration, 0);
                axisX = has[0];
                axisY = has[1];
                axisZ = has[2];
            } else{
                axisX = earthAcceleration[0];
                axisY = earthAcceleration[1];
                axisZ = earthAcceleration[2];
            }

            axisX = earthAcceleration[0];
            axisY = earthAcceleration[1];
            axisZ = earthAcceleration[2];
            Log.d("sensor auto ",String.format("%.4f", axisX)+" " +String.format("%.4f", axisY)+" "+ String.format("%.4f", axisZ));

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
                q.removeFirst();
            }
            /*Event Detection*/
            if((axisZ > max || axisZ < min )
                    && isContinue
                    && speed >= speed_val
                    ){
                    isBump = true;
                    isContinue = false;
                    Log.d("flag","Bump "+ String.valueOf(isContinue));
                    mpBump.start();
                    t = timestamp;
                    countData = 0;
                    status.setText("BUMP ");
                    countBump++;
                    tv_jumlah_bump.setText(Long.toString(countBump));
                    tv_jumlah_bump.setTextColor(Color.RED);
                    try {
                        addLocation(3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setPostData();
            }
        }
        else if(!isAuto && event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            timestamp = System.currentTimeMillis();
            gravities[0] = LPF_ALPHA * gravities[0] + (1 - LPF_ALPHA) * aX;
            gravities[1] = LPF_ALPHA * gravities[1] + (1 - LPF_ALPHA) * aY;
            gravities[2] = LPF_ALPHA * gravities[2] + (1 - LPF_ALPHA) * aZ;

            linearAcceleration[0] = aX ;
            linearAcceleration[1] = aY ;
            linearAcceleration[2] = aZ ;

            float [] earthAcceleration = new float[] {0, 0, 0, 0};
            float [] has = new float[] {0, 0, 0, 0};
            // Transposed matrix
            float[] Rt = new float[16];
            float[] matrix = new float[16];

            Matrix.setIdentityM(matrix, 0);
            // TODO: http://developer.android.com/reference/android/hardware/SensorManager.html#getRotationMatrix(float[], float[], float[], float[])
            // says that R*gravity = [0 0 g] but the calculation only seems to work when we invert the Rotation matrix
            SensorManager.getRotationMatrix(Rotate, null, gravities, magnometers);
            Matrix.transposeM(Rt, 0, Rotate, 0);
            Matrix.multiplyMV(earthAcceleration, 0, Rt, 0, linearAcceleration, 0);
            if(bearing!= 0 && declination !=0){
                Matrix.rotateM(matrix,0,bearing-declination,0,0,1f);
                Matrix.multiplyMV(has, 0, matrix, 0, earthAcceleration, 0);
                axisX = has[0];
                axisY = has[1];
                axisZ = has[2];
            } else{
                axisX = earthAcceleration[0];
                axisY = earthAcceleration[1];
                axisZ = earthAcceleration[2];
            }

            Log.d("sensor Orie ",String.format("%.4f", axisX)+" " + String.format("%.4f", axisY)+" "+  String.format("%.4f", axisZ));

            if(isSent) {
                addObject(axisX,axisY,axisZ,timestamp);
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
                        Context context = getApplicationContext();

                        status.setText( response.toString()+" Berhasil!");
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
        obj.put("pcb",pcb);
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
