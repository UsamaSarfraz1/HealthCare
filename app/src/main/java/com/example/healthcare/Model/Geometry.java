package com.example.healthcare.Model;



public class Geometry
{
    private Location location;

    private String location_type;

    private Viewport viewport;

    public void setLocation(Location location){
        this.location = location;
    }
    public Location getLocation(){
        return this.location;
    }
    public void setLocation_type(String location_type){
        this.location_type = location_type;
    }
    public String getLocation_type(){
        return this.location_type;
    }
    public void setViewport(Viewport viewport){
        this.viewport = viewport;
    }
    public Viewport getViewport(){
        return this.viewport;
    }
}

