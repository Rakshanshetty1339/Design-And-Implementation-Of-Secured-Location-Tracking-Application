package com.child.calculator;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Nevon Dell on 4/3/2017.
 */

public class LoginActivity extends AppCompatActivity {

    SharedPreferences pref;
    protected EditText UserName,Password;
    protected Button SignIn;
    protected RelativeLayout relativeLayout;
    double Lat=0,Lng=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref=getSharedPreferences("childmonitor",Context.MODE_PRIVATE);

        stopService(new Intent(LoginActivity.this,BackgroundService.class));
        startService(new Intent(LoginActivity.this,BackgroundService.class));


        Boolean ans=weHavePermission();
        if(!ans)
        {
            requestforPermissionFirst();
        }

        String str=pref.getString("pid","");
        if(str.compareTo("")!=0)
        {
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            setContentView(R.layout.login_activity);
            init();
        }


    }

   protected void init(){

     UserName =(EditText) findViewById(R.id.loginUserName);
     Password = (EditText) findViewById(R.id.loginPassword);
     SignIn = (Button) findViewById(R.id.loginButton)  ;

       relativeLayout = (RelativeLayout) findViewById(R.id.activity_login);

       SignIn.setOnClickListener(
               new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       if (checkCriteria()) {
                             if (UserName.getText().toString().equals("")) {
                               Snackbar.make(relativeLayout, "User Name is required", Snackbar.LENGTH_LONG).show();
                           } else if (Password.getText().toString().equals("")) {
                               Snackbar.make(relativeLayout, "Password is required", Snackbar.LENGTH_LONG).show();
                           }
                           else {

                                 Boolean ans=weHavePermission();
                                 if(!ans)
                                 {
                                     requestforPermissionFirst();
                                 }
                                 else {
                                     GPS_Tracker gps_tracker = new GPS_Tracker(LoginActivity.this,LoginActivity.this);
                                     if (gps_tracker.canGetLocation())
                                     {
                                         Lat = gps_tracker.getLatitude();
                                         Lng = gps_tracker.getLongitude();

                                         if (Lat!=0 && Lng!=0){

                                             new logintask().execute(UserName.getText().toString(), Password.getText().toString());


                                         }else {
                                             Toast.makeText(LoginActivity.this, "Determining Your cordinates,Click on Sign In Again", Toast.LENGTH_SHORT).show();

                                         }
                                     }
                                     else {
                                         Toast.makeText(LoginActivity.this, "Enable Your GPS(Location)", Toast.LENGTH_SHORT).show();
                                     }
                                 }
                             }
                       } else {
                           new AlertDialog.Builder(LoginActivity.this)
                                   .setMessage("All fields are mandatary. Please enter all details")
                                   .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           dialog.dismiss();
                                       }
                                   })
                                   .show();
                       }
                   }
               }
       );


   }

    protected boolean checkCriteria() {
        boolean b = true;
        if((UserName.getText().toString()).equals("")) {
            b = false;
        }
        return b;
    }


    public class logintask extends AsyncTask<String,JSONObject,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.childlogin(params[0],params[1]);
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
//            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
//            dl.show();
            if(s.length()==10)
            {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("pid",s);
                editor.putString("cid",((EditText) findViewById(R.id.loginUserName)).getText().toString());
                editor.apply();
                editor.commit();

                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            } else if(s.compareTo("false")==0){
                Snackbar.make(relativeLayout, "Invalid Credential", Snackbar.LENGTH_LONG).show();
                UserName.setText("");
                Password.setText("");
            }else
                {
                    if(s.contains("Unable to resolve host"))
                    {
                        AlertDialog.Builder ad=new AlertDialog.Builder(LoginActivity.this);
                        ad.setTitle("Unable to Connect!");
                        ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                        ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        ad.show();
//                        dl.dismiss();
                    }
                    else {
//                        dl.dismiss();
                        Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }


     //Android Runtime Permission
    private boolean weHavePermission()
    {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestforPermissionFirst()
    {
        if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))|| (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALL_LOG)))
        {
            requestForResultContactsPermission();
        }
        else
        {
            requestForResultContactsPermission();
        }
    }

    private void requestForResultContactsPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_CONTACTS,Manifest.permission.READ_SMS,Manifest.permission.READ_CALL_LOG}, 111);
    }




}



