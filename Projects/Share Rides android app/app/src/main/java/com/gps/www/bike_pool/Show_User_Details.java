package com.gps.www.bike_pool;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Idiot1 on 4/9/2016.
 */
public class Show_User_Details extends Fragment implements OnMapReadyCallback, View.OnClickListener {

 item user;
    TextView mobile,name,time,end_time;
    FloatingActionButton floatbtn;

    public void setUser(item user) {
        this.user = user;
    }

/*
    @Override
    public void onResume() {
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.show_data_map);
        mapFragment.getMapAsync(this);

    }
*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.show_user_data,null,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mobile=(TextView)view.findViewById(R.id.phone_udi);
        time=(TextView)view.findViewById(R.id.starttime_udi);
        end_time=(TextView)view.findViewById(R.id.endtime_udi);
        end_time.setVisibility(View.INVISIBLE);
        name=(TextView)view.findViewById(R.id.name_udi);
        floatbtn=(FloatingActionButton)view.findViewById(R.id.fab_getpt);
        floatbtn.setOnClickListener(this);

        mobile.setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.show_data_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.phone_udi:
                openWhatsappContact(user.mobile);
                break;
            case R.id.fab_getpt:
                navigate_to_track();
                break;

        }

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Fragment f=new Status_Take();

        Calendar calen= Calendar.getInstance();
        calen.setTimeInMillis(user.start_time);
        time.setText(String.format("%tI:%<tM", calen.getTime()) + " " + String.format("%tp", calen.getTime()).toUpperCase());
        if(user.end_time!=null)
        {
            f=new Status_Offer();
            end_time.setVisibility(View.VISIBLE);
            calen.setTimeInMillis(user.end_time);
            end_time.setText(String.format("%tI:%<tM", calen.getTime()) + " " + String.format("%tp", calen.getTime()).toUpperCase());

        }
        mobile.setText(user.mobile);
        name.setText(user.name);




            MainActivity a=(MainActivity)getActivity();
            a.setbackFragment(f);


    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        Random rnd = new Random();


        Polyline line = mMap.addPolyline(new PolylineOptions()
                .addAll(new ArrayList<LatLng>(Arrays.asList(user.path)))
                .width(5)
                .color(Color.argb(120, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))//Google maps blue color
                .geodesic(true));

        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (LatLng m : user.path) {
            b.include(m);
        }
        LatLngBounds bounds = b.build();
//Change the padding as per needed
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 5);
        mMap.animateCamera(cu);

       Marker start=mMap.addMarker(new MarkerOptions().position(user.start).title(user.marker_name+"start Point"));
      Marker dest=mMap.addMarker(new MarkerOptions().position(user.dest).title(user.marker_name+" dest Point"));
       // Toast.makeText(getContext(),""+user.dest.latitude+":"+user.dest.longitude,Toast.LENGTH_SHORT).show();




    }

    void openWhatsappContact(String number) {

        PackageManager pm=getActivity().getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            Uri uri = Uri.parse("smsto:" + number);
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            i.setPackage("com.whatsapp");
            startActivity(Intent.createChooser(i, ""));
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }


    }

    void navigate_to_track()
    {
        FragmentManager fm=getActivity().getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        Tracking f=new Tracking();
        f.setUser(user);
        ft.replace(R.id.frame, f);
        ft.commit();

    }


}
