package com.gps.www.bike_pool;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class item {

    int id;
    String name;
    String mobile;
    LatLng[] path;
    Long start_time;
    Long end_time;
    LatLng start,dest;
    String marker_name;
    int points;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public LatLng[] getPath() {
        return path;
    }

    public void setPath(LatLng[] path) {
        this.path = path;
    }

    public Long getStart_time() {
        return start_time;
    }

    public String getStart_time_String() {
        Calendar calen= Calendar.getInstance();
        calen.setTimeInMillis(start_time);
        return String.format("%tI:%<tM", calen.getTime()) + " " + String.format("%tp", calen.getTime()).toUpperCase();

    }

    public void setStart_time(Long start_time) {
        this.start_time = start_time;
    }

    public Long getEnd_time() {
        return end_time;
    }

    public String getEnd_time_string() {
        Calendar calen= Calendar.getInstance();
        calen.setTimeInMillis(end_time);
        return String.format("%tI:%<tM", calen.getTime()) + " " + String.format("%tp", calen.getTime()).toUpperCase();

    }

    public void setEnd_time(Long end_time) {
        this.end_time = end_time;
    }

    public LatLng getStart() {
        return start;
    }

    public String getStart_String() {
        return ""+String.format("%.2f", start.latitude)+" , "+String.format("%.2f", start.longitude);

    }

    public void setStart(LatLng start) {
        this.start = start;
    }

    public LatLng getDest() {
        return dest;
    }

    public String getDest_String() {
        return ""+String.format("%.2f", dest.latitude)+" , "+String.format("%.2f", dest.longitude);

    }

    public void setDest(LatLng dest) {
        this.dest = dest;
    }

    public String getMarker_name() {
        return marker_name;
    }

    public void setMarker_name(String marker_name) {
        this.marker_name = marker_name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public item(int id, String name, String mobile, LatLng[] path, Long start_time, Long end_time, LatLng start, LatLng dest, String marker_name) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.path = path;
        this.start_time = start_time;
        this.end_time = end_time;
        this.start = start;
        this.dest=dest;
        this.marker_name = marker_name;
        this.points=0;


    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        item item = (item) o;
        if(item.id==this.id)
            return true;
        else
            return false;

    }

    @Override
    public int hashCode() {
        return this.mobile.hashCode();
    }
}
