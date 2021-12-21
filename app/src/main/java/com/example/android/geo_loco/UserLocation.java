package com.example.android.geo_loco;

public class UserLocation {
    private String UserLatitude;
    private String UserLongitude;

   private ReadWriteUserDetail userDetail;

    public UserLocation() {

    }

    public UserLocation(String userLatitude, String userLongitude, ReadWriteUserDetail userDetail) {
        this.UserLatitude=userLatitude;
        this.UserLongitude=userLongitude;
        this.userDetail=userDetail;
    }

    public String getUserLatitude() {
        return UserLatitude;
    }

    public void setUserLatitude(String userLatitude) {
        UserLatitude = userLatitude;
    }

    public String getUserLongitude() {
        return UserLongitude;
    }

    public void setUserLongitude(String userLongitude) {
        UserLongitude = userLongitude;
    }

    public ReadWriteUserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(ReadWriteUserDetail userDetail) {
        this.userDetail = userDetail;
    }
}
