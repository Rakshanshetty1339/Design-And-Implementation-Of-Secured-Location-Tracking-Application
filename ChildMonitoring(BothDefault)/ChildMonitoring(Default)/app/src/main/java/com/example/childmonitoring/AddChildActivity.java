package com.example.childmonitoring;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;


public class AddChildActivity extends AppCompatActivity {

    protected EditText Name,Age,UserName,Password;
    protected Button Add;
    protected RadioButton RadioMale,RadioFemale;
    protected RadioGroup GenderSelection;
    protected RelativeLayout relativeLayout;
    ProgressDialog dl;
    protected String pid;
    SharedPreferences pref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_child_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        pref=getSharedPreferences("childmonitor",Context.MODE_PRIVATE);
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


    protected void init() {

        dl = new ProgressDialog(this);
        dl.setMessage("Loading...");

        Name = (EditText) findViewById(R.id.Name);
        Age = (EditText) findViewById(R.id.age);
        UserName = (EditText) findViewById(R.id.userName);
        Password = (EditText) findViewById(R.id.password);
        Add = (Button) findViewById(R.id.add_chid);
        relativeLayout = (RelativeLayout) findViewById(R.id.add_child_layout);

        GenderSelection = (RadioGroup) findViewById(R.id.radio_gender);
        RadioMale = (RadioButton) findViewById(R.id.radio_male);
        RadioFemale = (RadioButton) findViewById(R.id.radio_female);


        Add.setOnClickListener(
                new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View v) {

                        if (checkCriteria()) {

                            //checkCriteria()
                            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                            String namePattern = "^[a-zA-Z ]+$";

                            if (GenderSelection.getCheckedRadioButtonId() <= 0) {
                                RadioButton lastRadioBtn = (RadioButton) findViewById(R.id.radio_female);
                                Snackbar.make(relativeLayout, "Select a Gender", Snackbar.LENGTH_LONG).show();
                            } else if (Name.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, "Name is required", Snackbar.LENGTH_LONG).show();
                            } else if (!((Name.getText().toString()).trim()).matches(namePattern)) {
                                Snackbar.make(relativeLayout, "Use letters only in Name", Snackbar.LENGTH_LONG).show();
                            } else if (Age.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, "Age is required", Snackbar.LENGTH_LONG).show();
                            } else if (UserName.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, "User Name is required", Snackbar.LENGTH_LONG).show();
                            } else if (Password.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, "Password is required", Snackbar.LENGTH_LONG).show();
                            } else {

                                int genderRadioButtonID = GenderSelection.getCheckedRadioButtonId();
                                RadioButton GenderRadioBtn = (RadioButton) findViewById(genderRadioButtonID);

                                //pid left [comming from shared prefrence
                                //    public JSONObject addchild(String name,String age,String gen,String cid,String pass,String pid)
                                new addChildtask().execute(Name.getText().toString(), Age.getText().toString(), GenderRadioBtn.getText().toString(),
                                        UserName.getText().toString(), Password.getText().toString(), pref.getString("pid",""));
                            }
                        } else {
                            new AlertDialog.Builder(AddChildActivity.this)
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
        if((Name.getText().toString()).equals("")) {
            b = false;
        }
        else if((Age.getText().toString()).equals("")) {
            b = false;
        }
        return b;
    }

    public class addChildtask extends AsyncTask<String,JSONObject,String>
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
                JSONObject json=api.addchild(params[0],params[1],params[2],params[3],params[4],params[5]);
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
            if(s.compareTo("true")==0)
            {
                Snackbar.make(relativeLayout, "Child Added successfully", Snackbar.LENGTH_LONG).show();
                Name.setText(" ");
                Age.setText(" ");
                UserName.setText(" ");
                Password.setText(" ");

            }else if(s.compareTo("already")==1){
                Name.setText(" ");
                Age.setText(" ");
                UserName.setText(" ");
                Password.setText(" ");
                Snackbar.make(relativeLayout, "Enter child credential is already added", Snackbar.LENGTH_LONG).show();
            }
            else {
                if(s.contains("Unable to resolve host"))
                {
                    AlertDialog.Builder ad=new AlertDialog.Builder(AddChildActivity.this);
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
                    Toast.makeText(AddChildActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
