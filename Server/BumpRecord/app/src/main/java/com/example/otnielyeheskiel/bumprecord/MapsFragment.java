package com.example.otnielyeheskiel.bumprecord;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class MapsFragment extends Fragment implements OnMapReadyCallback, FragmentLifecycle,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    protected FragmentActivity activity;
    protected View view;
    final String URL = "http://128.199.235.115/api/data";
    private List<LatLng> centroid;
    private LatLng currentLocation;

    private static final int MY_PERMISSIONS_REQUEST = 1;
    private LocationManager locationManager;
    protected Location location;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap gMap;
    private SupportMapFragment map;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_maps, container, false);
        centroid = new ArrayList<>();

        map = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        map.getMapAsync(this);

        try {
            getCentroid();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }


    @Override
    public void onMapReady(GoogleMap map) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);

            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }
        map.setMyLocationEnabled(true);

        gMap = map;
        if(currentLocation != null) {
            Log.d("map","currentLocation get");
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        } else map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-7.2793623, 112.7972274), 15));

        Log.d("map","Masuk");

    }

    public void getCentroid() throws JSONException {
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d("response", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject hasil = response.getJSONObject(i);
                                LatLng cen = new LatLng(hasil.getDouble("clat"), hasil.getDouble("clon"));
                                centroid.add(cen);
                            }
                            Log.d("centroid", String.valueOf(centroid.size()));

                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_MAGENTA);
                            for(int i=0; i<centroid.size();i++){
                                gMap.addMarker(new MarkerOptions()
                                            .position(centroid.get(i))
                                            .snippet(String.format("%.6f", centroid.get(i).latitude)+", "+
                                                    String.format("%.6f", centroid.get(i).longitude))
                                            .title("Bump")
                                            .icon(bitmapDescriptor)
                                );
                            }

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


    @Override
    public void onConnected(Bundle connectionHint) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        Log.d("maps", String.valueOf(currentLocation));

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onPauseFragment() {
        Log.i("fragment", "onPauseFragment() Maps");
    }

    @Override
    public void onResumeFragment() {
        Log.i("fragment", "onResumeFragment() Maps");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
