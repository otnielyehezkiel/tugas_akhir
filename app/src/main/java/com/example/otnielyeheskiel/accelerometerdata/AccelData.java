package com.example.otnielyeheskiel.accelerometerdata;

/**
 * Created by Otniel Yeheskiel on 3/19/2016.
 */
public class AccelData {
    private long timestamp;
    private float z;
    private float lat;
    private float lon;
    private int id_user;

    public AccelData(long timestamp,float z, float lat, float lon, int id_user){
        this.timestamp = timestamp;
        this.z = z;
        this.lat = lat;
        this.lon = lon;
        this.id_user = id_user;
    }

    public long getTimestamp(){
        return timestamp;
    }
    public void setTimestamp(long timestamp){
        this.timestamp=timestamp;
    }
    public float getZ(){
        return z;
    }
    public void setZ(float z){
        this.z = z;
    }
    public float getLat(){
        return lat;
    }
    public void setLat(float lat){
        this.lat = lat;
    }

    public float getLon(){
        return lon;
    }
    public void setLon(float lon){
        this.lon = lon;
    }

    public int getId_user(){
        return id_user;
    }
    public void setId_user(int id_user){
        this.id_user = id_user;
    }

    public String toString(){
        return "z="+z+", lat="+lat+", lon="+lon+", timestamp"+timestamp+", id_user=" +id_user;
    }





}
