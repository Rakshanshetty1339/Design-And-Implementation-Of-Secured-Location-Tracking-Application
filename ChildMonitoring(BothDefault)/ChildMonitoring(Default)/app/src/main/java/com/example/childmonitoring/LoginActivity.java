package com.example.childmonitoring;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences pref;
    protected EditText MobileNumber,Password;
    protected Button SignIn,SignUp;
    protected RelativeLayout relativeLayout;
    ProgressDialog dl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref=getSharedPreferences("childmonitor", Context.MODE_PRIVATE);
        getSupportActionBar().hide();

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

        dl = new ProgressDialog(this);
        dl.setMessage("Loading...");

        MobileNumber =findViewById(R.id.loginUserName);
        Password = (EditText) findViewById(R.id.loginPassword);
        SignIn = (Button) findViewById(R.id.loginButton)  ;
        SignUp  =(Button) findViewById(R.id.signUp)  ;

        relativeLayout = (RelativeLayout) findViewById(R.id.activity_login);



        SignUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                        startActivity(intent);
                    }
                }
        );


        SignIn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (checkCriteria()) {
                            if (MobileNumber.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, "Mobile Number is required", Snackbar.LENGTH_LONG).show();
                            } else if (Password.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, "Password is required", Snackbar.LENGTH_LONG).show();
                            }
                            else {
                                new logintask().execute(MobileNumber.getText().toString(),Password.getText().toString());

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
        if((MobileNumber.getText().toString()).equals("")) {
            b = false;
        }
        return b;
    }


    public class logintask extends AsyncTask<String, JSONObject,String>
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
                JSONObject json=api.login(params[0],params[1]);
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
            if(s.compareTo("true")==0)
            {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("pid", ((EditText) findViewById(R.id.loginUserName)).getText().toString());
                editor.apply();
                editor.commit();

                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            } else if(s.compareTo("false")==0){
                Snackbar.make(relativeLayout, "Invalid Credential", Snackbar.LENGTH_LONG).show();
                MobileNumber.setText("");
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


}