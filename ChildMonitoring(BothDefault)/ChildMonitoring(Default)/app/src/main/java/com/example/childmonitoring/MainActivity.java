package com.example.childmonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    ListView list;
    ArrayAdapter<CharSequence> adapter;
    CoordinatorLayout coordinatorLayout;
    Cursor res;
    //    String[] childName;
    ProgressDialog dl;
    TextView ChildName;
    ArrayList<String> data;
    Context mContext;
    SharedPreferences pref;
    String str;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences("childmonitor", Context.MODE_PRIVATE);
        str = pref.getString("pid", "");

        init();
//        showListDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = getSharedPreferences("childmonitor", Context.MODE_PRIVATE);
        new getlist().execute(pref.getString("pid", "0"));
    }


    protected void init() {


        dl = new ProgressDialog(this);
        dl.setMessage("Loading...");

        list = (ListView) findViewById(R.id.myListView);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_activity_layout);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.addFloatButton);

        floatingActionButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, AddChildActivity.class);
                        startActivity(intent);
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = new MenuInflater(MainActivity.this);
        mi.inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout) {

            SharedPreferences pref = getSharedPreferences("childmonitor", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();
            finish();

            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    public void showListDetails() {
        if (res.getCount() == 0) {
            list.setAdapter(null);
            Snackbar.make(coordinatorLayout, "Products Not Available.", Snackbar.LENGTH_LONG).show();
        } else {
//            childName = new String[res.getCount()];

            int i = 0;
            while (res.moveToNext()) {
//                childName[i] = res.getString(2);
                i++;
            }
//            DataAdapter adapt=new DataAdapter(MainActivity.this,childName);
//            list.setAdapter(adapt);
        }
    }


    public class getlist extends AsyncTask<String, JSONObject, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String a = "back";
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.showchild(params[0]);
                JSONPARSE jp = new JSONPARSE();
                a = jp.parse(json);
            } catch (Exception e) {
                a = e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dl.show();
            if (s.compareTo("no") == 0) {
                dl.dismiss();
                list.setAdapter(null);
                Snackbar.make(list, "There is No data available!", Snackbar.LENGTH_SHORT).show();
//                ChildName.setVisibility(View.GONE);
            } else if (s.contains("*")) {
                int tot = 0;
                String temp[] = s.split("\\#");
                data = new ArrayList<String>();
                for (int i = 0; i < temp.length; i++) {
                    data.add(temp[i]);
                }

                Adapter adapt = new Adapter(MainActivity.this, data);
                list.setAdapter(adapt);

//                String t = "<font color='#E3746D'>Total Intake: </font>" + "<font color='#262932'>" + tot + " cal</font>";
//                ChildName.setText(Html.fromHtml(t));
//                ChildName.setVisibility(View.VISIBLE);
                dl.dismiss();

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
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
                } else {
                    dl.dismiss();
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class Adapter extends ArrayAdapter<String> {

        Context con;
        ArrayList<String> dataset;

        public Adapter(Context context, ArrayList<String> data) {
            super(context, R.layout.list_row_layout, data);
            con = context;
            dataset = data;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(con).inflate(R.layout.list_row_layout, null, true);

            TextView name = (TextView) v.findViewById(R.id.child_name);
            ImageView remove_name = (ImageView) v.findViewById(R.id.remove_name);

            final String temp[] = dataset.get(position).split("\\*");

            remove_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(con).setTitle("Remove")
                            .setMessage("Are you sure to remove ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new removeChild().execute(temp[1], str);
                                }

                            })
                            .setNegativeButton(android.R.string.no, null).show();


                }
            });

            name.setText(temp[0]);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("cid", temp[1]);
                    intent.putExtra("ChildName", temp[0]);
                    startActivity(intent);
                }
            });
            return v;
        }
    }


    public class removeChild extends AsyncTask<String, JSONObject, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String a = "back";
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.removechild(params[0], params[1]);
                JSONPARSE jp = new JSONPARSE();
                a = jp.parse(json);
            } catch (Exception e) {
                a = e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dl.show();
            if (s.compareTo("true") == 0) {
                Snackbar.make(coordinatorLayout, "Child deleted Sucessfully ", Snackbar.LENGTH_LONG).show();

                new getlist().execute(pref.getString("pid", "0"));


            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
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
                } else {
                    dl.dismiss();
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
