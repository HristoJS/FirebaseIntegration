package com.evilcorp.firebaseintegration.data.local;

/**
 * Created by hristo.stoyanov on 01-Feb-17.
 */

@Deprecated
public class Address {

    public String[] addressLines;
    public String feature;
    public String admin;
    public String subadmin;
    public String locality;
    public String thoroughfare;
    public String postalCode;
    public String countryCode;
    public String countryName;
    public boolean hasLatitude;
    public float latitude;
    public boolean hasLongitude;
    public float longitude;

    public String[] getAddressLines() {
        return addressLines;
    }

    public void setAddressLines(String[] addressLines) {
        this.addressLines = addressLines;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getSubadmin() {
        return subadmin;
    }

    public void setSubadmin(String subadmin) {
        this.subadmin = subadmin;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getThoroughfare() {
        return thoroughfare;
    }

    public void setThoroughfare(String thoroughfare) {
        this.thoroughfare = thoroughfare;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isHasLatitude() {
        return hasLatitude;
    }

    public void setHasLatitude(boolean hasLatitude) {
        this.hasLatitude = hasLatitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public boolean isHasLongitude() {
        return hasLongitude;
    }

    public void setHasLongitude(boolean hasLongitude) {
        this.hasLongitude = hasLongitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
