package com.example.childmonitoring;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    protected EditText name,phoneNumber,emailID,password,confirmPassword;
    protected Button register;
    protected RelativeLayout relativeLayout;
    ProgressDialog dl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        init();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    protected void init(){

        dl = new ProgressDialog(this);
        dl.setMessage("Loading...");

        name = (EditText) findViewById(R.id.userName);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        emailID = (EditText) findViewById(R.id.emailID);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        relativeLayout =(RelativeLayout)findViewById(R.id.activity_registration) ;
        register = (Button) findViewById(R.id.registerButton);

        register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        isEditNull(name, phoneNumber, emailID, password, confirmPassword);

                        if (checkCriteria()) {

                            String match = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                            if (name.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, "Name is required", Snackbar.LENGTH_LONG).show();
                            } else if (phoneNumber.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, "Password is required", Snackbar.LENGTH_LONG).show();
                            }else if(phoneNumber.getText().toString().length() != 10){
                                Snackbar.make(relativeLayout, "Invalid Number,Must Be 10 Digits", Snackbar.LENGTH_LONG).show();
                            }else if(emailID.getText().toString().equals("")){
                                Snackbar.make(relativeLayout, "Email Id is required", Snackbar.LENGTH_LONG).show();
                            }else if(!emailID.getText().toString().matches(match)){
                                Snackbar.make(relativeLayout, "Please Fallow Email Standards", Snackbar.LENGTH_LONG).show();
                            }else if(password.getText().toString().equals("")){
                                Snackbar.make(relativeLayout, "Password is required", Snackbar.LENGTH_LONG).show();
                            }else if(confirmPassword.getText().toString().equals("")){
                                Snackbar.make(relativeLayout, " Confirm Password is required", Snackbar.LENGTH_LONG).show();

                            } else if (password.getText().toString().compareTo(confirmPassword.getText().toString())!=0){
                                Snackbar.make(relativeLayout, "Password Does Not Match", Snackbar.LENGTH_LONG).show();
                            }
                            else {

//                                public JSONObject register(String name,String mobile,String email,String pass) throws Exception {

                                new registertask().execute(name.getText().toString(),phoneNumber.getText().toString(),emailID.getText().toString(), password.getText().toString());
                            }
                        } else {
                            new AlertDialog.Builder(RegisterActivity.this)
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
        if((name.getText().toString()).equals("")) {
            b = false;
        }
        return b;
    }




    public class registertask extends AsyncTask<String, JSONObject,String>
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
                JSONObject json=api.register(params[0],params[1],params[2],params[3]);
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
            dl.show();
            if(s.compareTo("true")==0)
            {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

            }else if(s.compareTo("already")==1){
                phoneNumber.setText(" ");
                Snackbar.make(relativeLayout, "Enter credential is already registred", Snackbar.LENGTH_LONG).show();
            }
            else {
                if(s.contains("Unable to resolve host"))
                {
                    AlertDialog.Builder ad=new AlertDialog.Builder(RegisterActivity.this);
                    ad.setTitle("Unable to Connect!");
                    ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                    ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    ad.show();
                    dl.dismiss();
                }
                else {
                    dl.dismiss();
                    Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
