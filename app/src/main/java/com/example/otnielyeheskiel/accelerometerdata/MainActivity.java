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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements SensorEventListener {

    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gravity;
    private long timestamp;


    private double alpha;
    private double beta;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float aX = 0;
    private float aY = 0;
    private float aZ = 0;

    private float pastX = 0;
    private float pastY = 0;
    private float pastZ = 0;

    private double temp;

    private final float alfa = (float) 0.8;

    private float gravities[]=new float[3];
    private float accelerometers[]=new float[3];

    private TextView currentX, currentY, currentZ, status, lon,lat,tv_timestamp,tv_mode,tv_data;
    private Button sentBtn,holeBtn,bumpBtn,breakBtn,orieantationBtn,modeBtn,sentarrayBtn;

    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    protected LocationManager lm;
    protected Location location;
    private boolean isGPS;
    private boolean isNetwork;
    private boolean isSent=false;
    private boolean isRealTime = true; //using volley jsonObject or jsonArray
    private boolean isReorientation = true;
    private int countData = 0;
    private float latitude, longitude;

    final String URL = "http://128.199.235.115/api/accelerometer";
    final String URL2 = "http://128.199.235.115/api/array";

    private int x = 1;//1 sent normal, 2 hole, 3 bump, 4 break
    public JSONArray dataAcc;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        initializeButton();
        //Location Function
        locationFunc();

        //sensor function
        sensorFunc();
        dataAcc = new JSONArray();


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
                displayLocation();
                Log.d("location", "lat: "+ latitude + "lon: "+ longitude);
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
                    Log.d("location gps","lat:" + latitude +", lon: " +longitude);
                    displayLocation();
                }
            }

            if(isNetwork){
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
            Log.d("sensor","accelerometer failed");
        }
    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        lon = (TextView) findViewById(R.id.lon);
        lat = (TextView) findViewById(R.id.lat);


        tv_timestamp = (TextView) findViewById(R.id.tv_timestamp);
        status = (TextView) findViewById(R.id.tv_status);
        tv_mode = (TextView) findViewById(R.id.tv_mode);
        tv_data = (TextView) findViewById(R.id.tv_data);

        sentBtn = (Button) findViewById(R.id.btn_sent);
        holeBtn = (Button) findViewById(R.id.btn_hole);
        bumpBtn = (Button) findViewById(R.id.btn_bump);
        breakBtn = (Button) findViewById(R.id.btn_break);
        orieantationBtn = (Button) findViewById(R.id.btn_reorientation);
        modeBtn = (Button) findViewById(R.id.btn_mode);
        sentarrayBtn = (Button) findViewById(R.id.btn_sent_array);
        sentarrayBtn.setVisibility(View.INVISIBLE);
        tv_mode.setText("ON");
    }
    // Initialize Button funtion
    public void initializeButton(){
        modeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRealTime){
                    modeBtn.setText("OFF");
                    tv_mode.setText("OFF");
                    sentarrayBtn.setVisibility(View.VISIBLE);
                    isRealTime = false;
                }
                else {
                    modeBtn.setText("ON");
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
                        dataAcc = new JSONArray(); //clear JSON array
                        countData = 0;
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
                    status.setText("Normal");
                }
                else {
                    isSent = false;
                    status.setText("Not Sent");
                }
            }
        });
        holeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!isSent){
                    isSent = true;
                    x=2;
                    status.setText("Hole");
                }
                else {
                    isSent = false;
                    status.setText("Not Sent");
                }
            }
        });
        bumpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!isSent){
                    isSent = true;
                    x=3;
                    status.setText("Bump");
                }
                else {
                    isSent = false;
                    status.setText("Not Sent");
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
                }
                else {
                    isSent = false;
                    status.setText("Not Sent");
                }

            }
        });
        orieantationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isReorientation){
                    isReorientation = false;
                    orieantationBtn.setText("REORIENTATION");
                }
                else {
                    isReorientation = true;
                    orieantationBtn.setText("ORIENTATION");
                }
            }
        });

    }
    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                accelerometers = event.values.clone();
                //Log.d("sensor",accelerometers[0]+" " +accelerometers[1]+" "+ accelerometers[2]);
                break;
        }
        timestamp = System.currentTimeMillis();
        Log.d("timestamp",Long.toString(timestamp));
        // get the x,y,z values of the accelerometer
        gravities[0] = alfa * gravities[0] + (1 - alfa) * accelerometers[0];
        gravities[1] = alfa * gravities[1] + (1 - alfa) * accelerometers[1];
        gravities[2] = alfa * gravities[2] + (1 - alfa) * accelerometers[2];
        aX = accelerometers[0];
        aY = accelerometers[1];
        aZ = accelerometers[2];

        alpha= Math.atan2(aY,aZ);
        beta = Math.atan2(( -aX),(Math.hypot(aY,aZ)));
        // reorientation
        if(isReorientation) {
            deltaX = (float) ((Math.cos(beta) * aX) + (Math.sin(beta) * Math.sin(alpha) * aY) +
                    (Math.cos(alpha) * Math.sin(beta) * aZ));
            deltaY = (float) (Math.cos(alpha) * aY - Math.sin(alpha) * aZ);
            deltaZ = (float) (-(Math.sin(beta) * aX) + (Math.cos(beta) * Math.sin(alpha) * aY) + Math.cos(beta) * Math.cos(alpha) * aZ);
            aX = deltaX;
            aY = deltaY;
            aZ = deltaZ;
            //deltaX -= pastX;
//            deltaY -= pastY;
//            deltaZ -= pastZ;
            /*Filter */
            if((deltaX >= -1) && (deltaX <=1)) deltaX = 0;
            if((deltaY >= -1) && (deltaY <=1)) deltaY = 0;
            if((deltaZ >= -1) && (deltaZ <=1)) deltaZ = 0;
            Log.d("sensor Reor ",String.format("%.4f", deltaX)+" " +
                                String.format("%.4f", deltaY)+" "+
                                String.format("%.4f", deltaZ));
        }
        else{
            /*deltaX = aX - pastX;
            deltaY = aY - pastY;
            deltaZ = aZ - pastZ;*/
            deltaX = aX;
            deltaY = aY;
            deltaZ = aZ;
            /*Filter */
            if((deltaX >= -1) && (deltaX <=1)) deltaX = 0;
            if((deltaY >= -1) && (deltaY <=1)) deltaY = 0;
            if((deltaZ >= -1) && (deltaZ <=1)) deltaZ = 0;
            Log.d("sensor Orie ",String.format("%.4f", deltaX)+" " +
                    String.format("%.4f", deltaY)+" "+
                    String.format("%.4f", deltaZ));
        }
        pastX = aX;
        pastY = aY;
        pastZ = aZ;

        //AccelData data = new AccelData(timestamp,deltaZ,latitude,longitude,1);
        //dataAcc.add(data);

        if(isSent) {
            if(isRealTime) {
                try {
                    postAccelData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("lat", Float.toString(latitude));
                    obj.put("lon", Float.toString(longitude));
                    obj.put("z", Float.toString(deltaZ));
                    obj.put("waktu", Long.toString(timestamp));
                    obj.put("id_user", "2");
                    obj.put("jenis_id",Integer.toString(x));
                    dataAcc.put(obj);
                    countData++;
                } catch (JSONException e) {
                    e.printStackTrace();
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
        obj.put("z", Float.toString(deltaZ));
        obj.put("waktu", Long.toString(timestamp));
        obj.put("id_user", "2");
        obj.put("jenis_id",Integer.toString(x));

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,URL,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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
        ApplicationController.getInstance().addToRequestQueue(jsObjRequest);
    }
    /*Method Post Data JsonArray Accelerometer*/
    public void postArrayData() throws JSONException{
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(Request.Method.POST,URL2,dataAcc,
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

   /* public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }*/

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(String.format("%.4f", deltaX));
        currentY.setText(String.format("%.4f", deltaY));
        currentZ.setText(String.format("%.4f", deltaZ));
        tv_timestamp.setText(Long.toString(timestamp));
        tv_data.setText(Integer.toString(countData));
    }

    // display the max x,y,z accelerometer values
/*
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaZ < deltaYMax) {
            deltaYMax = deltaZ;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }
*/

    //display latitude, longitude
    public void displayLocation(){
        lat.setText(Float.toString(latitude));
        lon.setText(Float.toString(longitude));
    }

  /*  private void makeJsonObjReq() {

        final String URL = "http://128.199.235.115/api/accelerometer";
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lat", Float.toString(latitude));
        params.put("lon", Float.toString(longitude));
        params.put("z", Float.toString(deltaZ));
        params.put("waktu", Long.toString(timestamp));
        params.put("id_user", "1");

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        // add the request object to the queue to be executed

        ApplicationController.getInstance().addToRequestQueue(req);
    }
*/
/*    public void postData(){
        mPostDataResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.POST,"http://128.199.235.115/api/accelerometer", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mPostDataResponse.requestCompleted();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mPostDataResponse.requestEndedWithError(error);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("lat", Float.toString(latitude));
                params.put("lon", Float.toString(longitude));
                params.put("z", Float.toString(deltaZ));
                params.put("waktu", Long.toString(timestamp));
                params.put("id_user", "1");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }

    public interface PostDataResponseListener {
        public void requestStarted();
        public void requestCompleted();
        public void requestEndedWithError(VolleyError error);
    }*/

   /* public void postJsonData(){
        Map<String,String> params = new HashMap<String, String>();
        params.put("lat",'\"'+ Float.toString(latitude)+'\"');
        params.put("lon", '\"'+Float.toString(longitude)+'\"');
        params.put("z", '\"'+Float.toString(deltaZ)+'\"');
        params.put("waktu",'\"'+ Long.toString(timestamp)+'\"');
        params.put("id_user", '\"'+"1"+'\"');

        if(!params.isEmpty()){
            for(String name: params.keySet()){
                String key = name.toString();
            String value = params.get(name).toString();
                Log.d("params",key +" "+value);
            }
        }
        if(!params.isEmpty()) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, URL, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String x = response.toString();
                            Log.d("response", x);
                            System.out.println(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("volley: ", error.toString());
                        }
                    });

            requestQueue.add(jsObjRequest);
        }
    }*/

    /*private void loadAPI(){

        Map<String,String> params = new HashMap<String, String>();
        params.put("lat", Float.toString(latitude));
        params.put("lon", Float.toString(longitude));
        params.put("z", Float.toString(deltaZ));
        params.put("waktu", Long.toString(timestamp));
        params.put("id_user", "1");

        // Request with API parameters
        GsonRequest<AccelData> myReq = new GsonRequest<AccelData>(
                com.android.volley.Request.Method.POST,
                URL,
                AccelData.class,
                params,
                createMyReqSuccessListener(),
                createMyReqErrorListener());


        ApplicationController.getInstance().addToRequestQueue(myReq, "json");
    }

    private Response.Listener<AccelData> createMyReqSuccessListener() {
        return new Response.Listener<AccelData>() {
            @Override
            public void onResponse(AccelData response) {
                try {
                    Log.d("Json Response", response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("volley: ", error.toString());
            }
        };
    }*/


}
