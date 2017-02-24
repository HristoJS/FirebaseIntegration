package com.evilcorp.firebaseintegration.services;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private Geocoder mGeocoder;
    private static final int MAX_RESULTS = 50;
    public GeoService() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mGeocoder = new Geocoder(this, Locale.getDefault());
        return mBinder;
    }

    public void findAddress(final String query,final ResultListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                List<Address> addresses = null;
                if(Geocoder.isPresent()) {
                    try {
                        addresses = mGeocoder.getFromLocationName(query, MAX_RESULTS);
                    } catch (IOException e) {
                        e.printStackTrace();
                        listener.fail("IOException");
                    }
                }
                else listener.fail("No Geocoder");

                if(addresses!=null && addresses.size()>0)
                    listener.success(addresses);
                else listener.fail("No Addresses");
            }
        }).start();
    }

    public class LocalBinder extends Binder {
        public GeoService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GeoService.this;
        }
    }

    public interface ResultListener{
        void success(List<Address> addresses);
        void fail(String reason);
    }
}
