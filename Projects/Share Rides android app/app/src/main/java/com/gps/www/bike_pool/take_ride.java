package com.gps.www.bike_pool;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Balaji on 3/16/2016.
 */
public class take_ride extends Fragment implements OnMapReadyCallback,View.OnClickListener,Serializable{
    EditText start_et,dest_et;
    TextView textclock,textclock1;
    String str="";
    LatLng start,dest;
    Calendar time,time1;
    int i,j;
    FloatingActionButton floatbtn;
    Marker start_marker,dest_marker;
    GoogleMap map;
    LatLngBounds.Builder b;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        map.getUiSettings().setAllGesturesEnabled(false);
        run();

        if(i==0) {
             b = new LatLngBounds.Builder();
            i++;
        }


        if(dest!=null)
        {
            if(dest_marker!=null)dest_marker.remove();
            dest_marker=map.addMarker(new MarkerOptions().position(dest).title("destination Point"));
            b.include(dest);
        }
        if(start!=null)
        {
            if(start_marker!=null)start_marker.remove();
            start_marker=map.addMarker(new MarkerOptions().position(start).title("start Point"));
            b.include(start);
        }

        //Change the padding as per needed
        try {
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(b.build(),70 );
            map.moveCamera(cu);
        }catch (Exception e){e.printStackTrace();}
    }


    @Override
    public void onResume() {
        super.onResume();

       /* Bundle b=getArguments()
        if(b!=null) {

            LatLng l = (LatLng) getArguments().getParcelable("l");
            Snackbar.make(getView(), "Pls select a point..." + l.latitude, Snackbar.LENGTH_SHORT).show();
            Log.v("asasas----------",""+l.latitude);
        }
        else
            Log.v("--------","null");

        Log.v("--------------","reesume");*/
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(j==0)
        intialize_saved_details();
        j++;
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.takrid_data_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.main,menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.delete_icon:
                SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.remove("t_start_time");
                editor.remove("t_end_time");
                editor.remove("t_start");
                editor.remove("t_dest");
                editor.commit();
                new take_ride_clr_asyc(this).execute();

        }
        return super.onOptionsItemSelected(item);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.take_ride,container,false);

        start_et=(EditText)v.findViewById(R.id.start_pt);
        dest_et=(EditText)v.findViewById(R.id.destination);
        textclock=(TextView)v.findViewById(R.id.textClock);
        textclock1=(TextView)v.findViewById(R.id.textClock1);
        floatbtn=(FloatingActionButton)v.findViewById(R.id.fab_tak_rid);
        floatbtn.setOnClickListener(this);
        textclock.setOnClickListener(this);
        textclock1.setOnClickListener(this);

        start_et.setOnClickListener(this);
        dest_et.setOnClickListener(this);

        return v;}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        MainActivity a=(MainActivity)getActivity();
        a.setbackFragment(new MainPage());


        if(time==null)
        {
            time1=Calendar.getInstance();
            time=Calendar.getInstance();
        }

        textclock.setText(String.format("%tI:%<tM", time.getTime()) + " " + String.format("%tp", time.getTime()).toUpperCase());
        textclock1.setText(String.format("%tI:%<tM", time1.getTime()) + " " + String.format("%tp", time1.getTime()).toUpperCase());

//        i++;
//        Snackbar.make(getView(),""+i,Snackbar.LENGTH_SHORT).show();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab_tak_rid:
                if(start==null) {
                    Snackbar.make(getView(), "Select a Start point...", Snackbar.LENGTH_SHORT).show();
                    break;
                }
                if(dest==null){
                    Snackbar.make(getView(), "Select a Destination point...", Snackbar.LENGTH_SHORT).show();
                    break;
                }
                if(start!=null&&dest!=null){
                    //Snackbar.make(getView(), "Ok", Snackbar.LENGTH_SHORT).show();
                    //intialize saved details
                    SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putLong("t_start_time",time.getTimeInMillis());
                    editor.putLong("t_end_time",time1.getTimeInMillis());
                    editor.putString("t_start","POINT("+start.longitude+" "+start.latitude+")");
                    editor.putString("t_dest","POINT("+dest.longitude+" "+dest.latitude+")");
                    editor.commit();

                    new take_ride_asyc(this).execute();
                    break;
                }

            case R.id.start_pt:
                FragmentManager fm=getActivity().getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                Get_Point_Map f=new Get_Point_Map();
                f.setFragment(this);
                Bundle b=new Bundle();
                b.putString("class", this.getClass().getName());
                b.putInt("position", 0);
                f.setArguments(b);
                ft.replace(R.id.frame, f);//.addToBackStack("take_ride");;
                ft.commit();
                break;
            case R.id.destination:
                FragmentManager fm1=getActivity().getSupportFragmentManager();
                FragmentTransaction ft1=fm1.beginTransaction();
                Get_Point_Map f1=new Get_Point_Map();
                f1.setFragment(this);
                Bundle b1=new Bundle();
                b1.putString("class", this.getClass().getName());
                b1.putInt("position", 1);
                f1.setArguments(b1);
                ft1.replace(R.id.frame, f1);//.addToBackStack("take_ride");;
                ft1.commit();
                break;
            case R.id.textClock:
                TimePickDialog temp=new TimePickDialog();
                temp.setTextClock(textclock, time);
                DialogFragment newFragment =temp;
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                break;
            case R.id.textClock1:
                TimePickDialog temp1=new TimePickDialog();
                temp1.setTextClock(textclock1, time1);
                DialogFragment newFragment1 =temp1;
                newFragment1.show(getActivity().getSupportFragmentManager(), "timePicker");
                break;



        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void run()
    {

        Bundle bun=getArguments();

        if(bun!=null)
        {

           final LatLng position=(LatLng)bun.getParcelable("start_point");

           if(position!=null) {
               start=position;
              start_et.post(new Runnable() {
                   @Override
                   public void run() {
                       start_et.setText(String.format("%.2f", position.latitude)+ "," +String.format("%.2f", position.longitude));
                   }
               });


           }
            else
           {
               final LatLng position1=(LatLng)bun.getParcelable("dest_point");
               if(position1!=null) {
                   dest=position1;
                   dest_et.post(new Runnable() {
                       @Override
                       public void run() {
                           dest_et.setText(String.format("%.2f", position1.latitude)+ "," +String.format("%.2f", position1.longitude));
                       }
                   });

               }
           }
        }
    }
    void goto_status()
    {
        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("take_ride_set",true);
        editor.commit();

        Fragment f=new Status_Take();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame,f);//.addToBackStack("offer_ride");
        fragmentTransaction.commit();
    }

    void intialize_saved_details()
    {
        Log.v(getClass().getName(),"intializing saved details");
        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
        if(settings.getLong("t_start_time",-880727064)!=-880727064)
        {
            //time setting
            time= Calendar.getInstance();
            time.setTimeInMillis(settings.getLong("t_start_time",-1));
            //Log.v("time:",settings.getLong("t_start_time",-1)+"");

            time1= Calendar.getInstance();
            time1.setTimeInMillis(settings.getLong("t_end_time",-1));

            //lat long points
            String temp_lat=settings.getString("t_start",null);
            temp_lat=temp_lat.substring(6,temp_lat.length()-1);
            String[] temp_arr=temp_lat.split(" ");
            start=new LatLng(Double.parseDouble(temp_arr[1]),Double.parseDouble(temp_arr[0]));
            start_et.setText(String.format("%.2f", start.latitude)+ "," +String.format("%.2f", start.longitude));


            temp_lat=settings.getString("t_dest",null);
            temp_lat=temp_lat.substring(6,temp_lat.length()-1);
            temp_arr=temp_lat.split(" ");
            dest=new LatLng(Double.parseDouble(temp_arr[1]),Double.parseDouble(temp_arr[0]));
            dest_et.setText(String.format("%.2f", dest.latitude)+ "," +String.format("%.2f", dest.longitude));


        }
    }


}

class take_ride_asyc extends AsyncTask<Void,Void,Void>
{
    ProgressDialog progressDialog;
    take_ride or;

    public take_ride_asyc(take_ride x)
    {
        or=x;
    }
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        progressDialog = new ProgressDialog(or.getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }


    @Override
    protected Void doInBackground(Void... params) {
        SharedPreferences settings = or.getActivity().getSharedPreferences("UserInfo", 0);

        String s="";
        s+="req=2";
        s+="&start_lat="+or.start.latitude;
        s+="&start_lng="+or.start.longitude;
        s+="&dest_lat="+or.dest.latitude;
        s+="&dest_lng="+or.dest.longitude;
        s+="&start_time="+or.time.getTimeInMillis();
        s+="&end_time="+or.time1.getTimeInMillis();
        s+="&id="+settings.getInt("id", -8);
        Log.v("------------", "" + s);
        Log.v("------------", "" + or.time.getTime());

        String result=http_request.post_and_get(s,or.getResources().getString(R.string.php_url));

        Log.v("------------", "" + result);
        return  null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.hide();
        or.goto_status();
    }
}

class take_ride_clr_asyc extends AsyncTask<Void,Void,String>
{
    ProgressDialog progressDialog;
    take_ride or;

    public take_ride_clr_asyc(take_ride x)
    {
        or=x;
    }
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        progressDialog = new ProgressDialog(or.getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }


    @Override
    protected String doInBackground(Void... params) {
        SharedPreferences settings = or.getActivity().getSharedPreferences("UserInfo", 0);

        String s="req=9&id="+settings.getInt("id",-8);
        Log.v("take_ride_clrasyc_snd:", "" +s);
        String result=http_request.post_and_get(s, or.getResources().getString(R.string.php_url));

        Log.v("take_ride_clrasyc_res:", "" + result);
        return result;
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);

        if(aVoid=="error_caught"||!(aVoid.trim().equals("{\"error\":0,\"error_msg\":\"none\",\"result\":\"update_success\"}"))) Toast.makeText(or.getContext(),"Try after some time...",Toast.LENGTH_SHORT).show();
        else Toast.makeText(or.getContext(),"Delete success...",Toast.LENGTH_SHORT).show();

        progressDialog.hide();

        or.getActivity().onBackPressed();
    }
}


