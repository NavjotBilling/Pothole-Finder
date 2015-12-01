package com.example.start.finalproject2;

public class potHole {
    private int id, numSpotted;
    private double latitude, longitude;
    private String locationName;
    public potHole(int id, int numSpotted, double latitude, double longitude, String location){
        this.id = id;
        this.numSpotted = numSpotted;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = location;
    }
    public int getId(){
        return this.id;
    }
    public double getLatitude(){
        return this.latitude;
    }
    public double getLongitude(){
        return this.longitude;
    }
    public int getNumSpotted(){
        return this.numSpotted;
    }
    public String getLocationName(){
        return this.locationName;
    }

    /***********
     * The setter methods
     ***********/
    public void setId(int id){
        this.id = id;
    }
    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
    public void setNumSpotted(int numSpotted){
        this.numSpotted = numSpotted;
    }
    public void setLocationName(String locationName){
        this.locationName = locationName;
    }
}
