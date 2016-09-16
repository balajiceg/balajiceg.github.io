package com.gps.www.bike_pool;

import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.List;


public class Get_Point_Map extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    static final LatLng HAMBURG = new LatLng(13.0,80.0);
    Marker start_pt;
    LatLng start,dest;
    GoogleMap map;
    ImageView marker;
    TouchableMapFragment mapFragment;


    FloatingActionButton floatbtn;
    Bundle b;
    Fragment frag;
    Polyline selected;
    Boolean point_selected;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity a=(MainActivity)getActivity();
        a.setbackFragment(frag);
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.get_point_map, null, false);



         mapFragment = (TouchableMapFragment)(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.get_pt_map);
        mapFragment.getMapAsync(this);
        floatbtn=(FloatingActionButton)v.findViewById(R.id.fab_getpt);
        floatbtn.setOnClickListener(this);
        marker=(ImageView)v.findViewById(R.id.marker);
        marker.setVisibility(View.INVISIBLE);
        return v;
    }


    @Override
    public void onMapReady(final GoogleMap map) {
        this.map=map;
        marker.setVisibility(View.VISIBLE);

        new MapStateListener(this.map, mapFragment, this.getActivity()) {
            @Override
            public void onMapTouched() {
                marker.setImageResource(R.drawable.map_marker2);

            }

            @Override
            public void onMapReleased() {
                // Map released
                marker.setImageResource(R.drawable.map_marker);

            }

            @Override
            public void onMapUnsettled() {
                // Map unsettled

            }

            @Override
            public void onMapSettled() {
                // Map settled

            }
        };


        map.setOnIndoorStateChangeListener(new GoogleMap.OnIndoorStateChangeListener() {
            @Override
            public void onIndoorBuildingFocused() {
                marker.setImageResource(R.drawable.map_marker1);
            }

            @Override
            public void onIndoorLevelActivated(IndoorBuilding indoorBuilding) {
                marker.setImageResource(R.drawable.map_marker);
            }
        });

        map.getUiSettings().setZoomControlsEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 12));
        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        point_selected=false;

    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab_getpt:
                btn_clicked();

        }
    }

    public void setFragment(Fragment v)
    {
        frag=v;

    }

    void btn_clicked()
    {
        if(getArguments().getString("class")==take_ride.class.getName())
        {

                Bundle b=new Bundle();
                switch (getArguments().getInt("position"))
                {
                    case 0:
                        b.putParcelable("start_point",map.getCameraPosition().target);
                        break;
                    case 1:
                        b.putParcelable("dest_point",map.getCameraPosition().target);
                        break;
                }
                FragmentManager fm=getActivity().getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                frag.setArguments(b);
                ft.replace(R.id.frame, frag);//.addToBackStack("getpoint");
                ft.commit();



        }
        else if(getArguments().getString("class")==Offer_Ride.class.getName())
        {
            if(point_selected&&getArguments().getBoolean("path"))
            {
                    set_and_goto();
            }
            else
            {
                point_selected=true;
                b=new Bundle();
                switch (getArguments().getInt("position")) {
                    case 0:
                        b.putParcelable("start_point",map.getCameraPosition().target);
                        if(getArguments().getBoolean("path"))
                            setPath(map.getCameraPosition().target,(LatLng)getArguments().getParcelable("dest_pt"));
                        break;
                    case 1:
                        b.putParcelable("dest_point", map.getCameraPosition().target);
                        if(getArguments().getBoolean("path"))
                            setPath(map.getCameraPosition().target,(LatLng)getArguments().getParcelable("start_pt"));
                        break;
                }

                if(getArguments().getBoolean("path")==false) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    frag.setArguments(b);
                    ft.replace(R.id.frame, frag);//.addToBackStack("getpoint");;
                    ft.commit();
                }
            }
        }

    }
    private void setPath(LatLng start,LatLng dest)
    {
        Log.v("-------------", "into set path");
     //   start_pt.remove();
      //  start_pt=null;
        marker.setVisibility(View.INVISIBLE);
        direction Dir=new direction(getContext(),map,start.latitude,start.longitude,dest.latitude,dest.longitude);
        Dir.execute(this);
    }

    public void set_line(Polyline p)
    {
        selected=p;
    }

    private void set_and_goto()
    {

            List larray=selected.getPoints();
            LatLng[] latLngs=new LatLng[larray.size()];
            for(int i=0;i<larray.size();i++)
            {
                latLngs[i]=(LatLng)larray.get(i);
            }
            b.putParcelableArray("polyline", latLngs);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            frag.setArguments(b);
            ft.replace(R.id.frame, frag);//.addToBackStack("getpoint");
            ft.commit();


    }
}
