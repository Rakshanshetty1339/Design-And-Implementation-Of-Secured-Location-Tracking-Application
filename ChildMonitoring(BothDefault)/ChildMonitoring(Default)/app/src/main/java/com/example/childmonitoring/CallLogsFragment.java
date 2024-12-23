package com.example.childmonitoring;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CallLogsFragment extends Fragment {

    protected View mView;
    ListView list;
    ProgressDialog dl;
    ArrayList<String> data;
    SharedPreferences pref;
    Context mContext;
    String pid;
    String cid;
    ProgressDialog mDialog;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.call_logs_layout, container, false);
        mDialog = new ProgressDialog(getActivity());
        mDialog.setTitle("Loading Data");
//        mDialog.setCancelable(false);
        pref= getActivity().getSharedPreferences("childmonitor",Context.MODE_PRIVATE);
        pid = pref.getString("pid","");

        Intent intent = getActivity().getIntent();
        cid = intent.getStringExtra("cid");
        return mView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }


    private void init(){

        list =(ListView)mView.findViewById(R.id.call_log_list);
        new getlist().execute(cid,pid);


    }


    public class getlist extends AsyncTask<String, JSONObject,String>
    {

        @Override
        protected void onPreExecute() {
            mDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.getcalllogs(params[0],params[1]);
                JSONPARSE jp=new JSONPARSE();
                a=jp.parse(json);
            } catch (Exception e) {
                a=e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            mDialog.dismiss();
            super.onPostExecute(s);
            if(s.compareTo("no")==0)
            {
                list.setAdapter(null);
                Snackbar.make(list,"There is No Call Logs available!",Snackbar.LENGTH_SHORT).show();
                mDialog.dismiss();

//                ChildName.setVisibility(View.GONE);
            }
            else if(s.contains("*"))
            {
                int tot=0;
                String temp[]=s.split("\\#");
                data=new ArrayList<String>();
                for(int i=0;i<temp.length;i++)
                {
                    data.add(temp[i]);
                }

                Adapter adapt=new Adapter(getActivity(),data);
                list.setAdapter(adapt);
                mDialog.dismiss();

//                dl.dismiss();

            }
            else
            {
                mDialog.dismiss();
                if(s.contains("Unable to resolve host"))
                {
                    AlertDialog.Builder ad=new AlertDialog.Builder(getActivity());
                    ad.setTitle("Unable to Connect!");
                    ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                    ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    ad.show();
                }
                else {
                    mDialog.dismiss();
                }
            }
        }
    }

    public class Adapter extends ArrayAdapter<String>
    {

        Context con;
        ArrayList<String> dataset;
        public Adapter(Context context,ArrayList<String> data) {
            super(context, R.layout.calllog_list,data);
            con=context;
            dataset=data;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v= LayoutInflater.from(con).inflate(R.layout.calllog_list,null,true);

            TextView contact_number =(TextView) v.findViewById(R.id.contact_number);
            TextView call_duration =(TextView) v.findViewById(R.id.call_duration);
            TextView date =(TextView) v.findViewById(R.id.date);
            TextView time =(TextView)v.findViewById(R.id.time) ;

            ImageView call_type =(ImageView) v.findViewById(R.id.call_type);

            String temp[]=dataset.get(position).split("\\*");
            contact_number.setText(temp[0]);
            if(temp[1].compareTo("Incoming")==0){

                call_type.setImageResource(R.drawable.incoming_call);
            }else if(temp[1].compareTo("Outgoing")==0){
                call_type.setImageResource(R.drawable.outgoing_call);
            }else {
                call_type.setImageResource(R.drawable.missed_call);

            }
//            call_type.setImageResource(temp[1]);
            date.setText(temp[2]);
            time.setText(temp[3]);

            String duration = temp[4];
            if (duration.compareTo("0")==0){
                call_duration.setText(duration);

            }else {

                double ans=Double.parseDouble(duration)/60;
                DecimalFormat df=new DecimalFormat("#.00");
                call_duration.setText(df.format(ans) +" min");
            }

            return v;
        }
    }
}
