package com.example.android.geo_loco;

import com.google.android.gms.maps.model.LatLng;

public class ClassRoom_Location {
    double Fence_Latitude;
    double Fence_Longitude;
    String ClassName;

    public ClassRoom_Location(double Fence_Latitude, double Fence_Longitude, String ClassName) {
        this.Fence_Latitude=Fence_Latitude;
        this.Fence_Longitude=Fence_Longitude;
        this.ClassName=ClassName;
    }
}
