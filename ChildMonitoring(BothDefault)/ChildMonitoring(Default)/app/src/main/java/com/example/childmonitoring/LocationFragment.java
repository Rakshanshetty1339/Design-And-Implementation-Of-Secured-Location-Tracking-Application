package com.example.childmonitoring;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationFragment extends Fragment implements OnMapReadyCallback {

    protected View mView;
    SharedPreferences pref;
    Context mContext;
    String pid;
    String cid;
    Fragment MapFragment;
    Double Lat, Lng;
    private GoogleMap mMap;
    String[] time, Loc;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_maps, container, false);
        pref = getActivity().getSharedPreferences("childmonitor", Context.MODE_PRIVATE);
        pid = pref.getString("pid", "");
        Intent intent = getActivity().getIntent();
        cid = intent.getStringExtra("cid");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {

        SimpleDateFormat st = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        new getLocation().execute(cid, pid, st.format(date.getTime()));


    }


    public class getLocation extends AsyncTask<String, JSONObject, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String a = "back";
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.getlocation(params[0], params[1], params[2]);
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
//            Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
            if (s.compareTo("no") == 0) {
                Snackbar.make(getView(), "There is No Location available!", Snackbar.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(),"There is No data available!", Toast.LENGTH_SHORT).show();
            } else if (s.contains("*")) {
                String temp[] = s.split("\\#");
                time = new String[temp.length];
                Loc = new String[temp.length];

                for (int i = 0; i < temp.length; i++) {

                    String temp1[] = temp[i].split("\\*");
                    Loc[i] = temp1[0];
                    time[i] = temp1[1];
                }

                for (int j = 0; j < Loc.length; j++) {

                    String Location[] = Loc[j].split(",");
                    LatLng ll = new LatLng(Double.parseDouble(Location[0]), Double.parseDouble(Location[1]));

                    final MarkerOptions mo = new MarkerOptions();
                    mo.position(ll);
                    mo.title(time[j]);
                    mo.draggable(false);
                    mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mMap.addMarker(mo);


                    if (Loc.length > 1) {

                        if (j >= 1) {

                            String Location1[] = Loc[j - 1].split(",");
                            LatLng ll1 = new LatLng(Double.parseDouble(Location1[0]), Double.parseDouble(Location1[1]));
                            PolylineOptions po = new PolylineOptions();
                            po.add(ll, ll1);
                            po.color(Color.BLUE);
                            po.width(5);
                            mMap.addPolyline(po);

                        }
                    }

                    if (j == 0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 12));

                    }
                }


            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                    ad.setTitle("Unable to Connect!");
                    ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                    ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    ad.show();
                } else {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        LatLng sydney = new LatLng(-34, 151);
//
    }

}
