package app.fyp.besecure.PhoneModel;

/**
 * Created by WaqasAhmad on 1/2/2019.
 */

public class getLocationModel {

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public getLocationModel(double mLatitude, double mLongitude) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    double mLatitude;

    public getLocationModel() {
    }

    double mLongitude;
}
