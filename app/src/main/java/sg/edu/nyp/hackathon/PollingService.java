package sg.edu.nyp.hackathon;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.razer.android.nabuopensdk.NabuOpenSDK;
import com.razer.android.nabuopensdk.interfaces.Hi5Listener;
import com.razer.android.nabuopensdk.models.Hi5Data;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import sg.edu.nyp.backend.userApi.UserApi;
import sg.edu.nyp.backend.userApi.model.User;

/**
 * Created by admin on 18/7/15.
 */
public class PollingService extends IntentService {
    static NabuOpenSDK nabuOpenSDK = null;
    String nabuAPPID;
    String userID;
    private long time = 0;
    public PollingService() {
        super("PollingService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        return START_STICKY;
    }

    public boolean isOnline(){
        Context context = getBaseContext();
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;

    }
    boolean running = true;
    @Override
    public void onHandleIntent(Intent intent) {
        setupAPIs();

        Toast.makeText(getApplicationContext(),"Service running", Toast.LENGTH_SHORT).show();
        LoginUtils.getInstance(getApplicationContext()).loginFromDevice();

        if(LoginUtils.getInstance(getApplicationContext()).isLoggedIn()){
            //If not logged, setup something here?

            userID = LoginUtils.getInstance(getApplicationContext()).getUser().getRazerID();
        }

        System.out.println("Intent Running!");
        nabuOpenSDK = nabuOpenSDK.getInstance(this);
        nabuAPPID = getResources().getString(R.string.NABU_APP_ID);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);

        LocationManager mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);

        String provider = mLocationManager.getBestProvider(criteria, true);
        mLocationManager.requestLocationUpdates(provider, 30 * 1000, 100, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    Toast.makeText(getApplicationContext(),"Location change", Toast.LENGTH_SHORT).show();
                    new UpdateLocation(location.getLatitude(), location.getLongitude()).execute(userID).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
        while(running){

            if(isOnline()){

            nabuOpenSDK.getHi5Data(PollingService.this, c.getTimeInMillis(), System.currentTimeMillis(), new Hi5Listener() {
                @Override
                public void onReceiveData(Hi5Data[] hi5Datas) {
                    for (Hi5Data hi5 : hi5Datas) {
                        //Cannot handshake twice in a minute
                        //TODO : remove when in demo!!!!!
                        if(Math.abs(time - System.currentTimeMillis()) > (60 * 1000)){

                        try {
                            new GivePoints().execute(userID, hi5.userID).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                            time = System.currentTimeMillis();
                        }
                    }
                }

                @Override
                public void onReceiveFailed(String s) {
                    System.out.println(s);
                }
            });
            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            }
        }
    }


    private UserApi api = null;
    public void setupAPIs(){
        api = ApisProvider.getUserApi();
    }
    private class UpdateLocation extends  AsyncTask<String, Void, Void>{

        private double lat;
        private double lng;

        public UpdateLocation(double lat, double lng){
            super();
            this.lat = lat;
            this.lng = lng;
        }
        @Override
        protected Void doInBackground(String... razerIDs) {
            try {
                api.updateLocation(razerIDs[0], lat, lng).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private class GivePoints extends AsyncTask<String,Void, User> {
        @Override
        protected User doInBackground(String... user) {
            try {
                User newUser = api.addPoint(user[0], user[1]).execute();
                System.out.println("Insert data nw");
                return newUser;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
            //return null;
        }
    }
    private class MyBinder extends Binder {

    }
}
