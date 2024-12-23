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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    protected View mView;
    ListView list; ;
    ProgressDialog dl;
    ArrayList<String> data;
    SharedPreferences pref;
    Context mContext;
    String pid;
    String cid;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.contact_list_layout, container, false);
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

        list =(ListView)mView.findViewById(R.id.contact_list);
        new getlist().execute(cid,pid);


    }


    public class getlist extends AsyncTask<String, JSONObject,String>
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
                JSONObject json=api.getcontacts(params[0],params[1]);
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
//            dl.show();
            if(s.compareTo("no")==0)
            {
//                dl.dismiss();
                list.setAdapter(null);
                Snackbar.make(list,"There is No contacts available!",Snackbar.LENGTH_SHORT).show();
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
//                dl.dismiss();

            }
            else
            {
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
//                    dl.dismiss();
                }
                else {
//                    dl.dismiss();
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class Adapter extends ArrayAdapter<String>
    {

        Context con;
        ArrayList<String> dataset;
        public Adapter(Context context,ArrayList<String> data) {
            super(context, R.layout.contact_list,data);
            con=context;
            dataset=data;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v= LayoutInflater.from(con).inflate(R.layout.contact_list,null,true);

            TextView name= (TextView) v.findViewById(R.id.contact_name);
            TextView number = (TextView) v.findViewById(R.id.contact_number) ;

            String temp[]=dataset.get(position).split("\\*");
            name.setText(temp[0]);
            number.setText(temp[1]);

            return v;
        }
    }
}
