package com.gps.www.bike_pool;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Balaji on 3/12/2016.
 */
public class MainPage extends Fragment implements View.OnClickListener{
    Button take,offer,status;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.main_page,container,false);
        take=(Button)v.findViewById(R.id.take_ride);
        offer=(Button)v.findViewById(R.id.offer_ride);
        status=(Button)v.findViewById(R.id.status_btn);

        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);

        if((settings.getLong("t_start_time",-880727064)==-880727064l)&&(settings.getLong("o_start_time",-880727064)==-880727064l)) {
            status.setVisibility(View.INVISIBLE);
            //((MainActivity)getActivity()).remove_navmenu_item();
        }





        take.setOnClickListener(this);
        offer.setOnClickListener(this);
        status.setOnClickListener(this);



    return  v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity a=(MainActivity)getActivity();
        a.create_navpane();
        a.setbackFragment(null);


        if(getArguments()!=null&&getArguments().getBoolean("register"))
        Snackbar.make(getView(), "Registration Successful..", Snackbar.LENGTH_LONG).show();

        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
        //Snackbar.make(getView(),settings.getString("name",""),Snackbar.LENGTH_LONG).show();


    }


    @Override
    public void onClick(View v) {
        Fragment f=this;
        switch (v.getId())
        {
            case R.id.take_ride:
                f= new take_ride();
                break;
            case R.id.offer_ride:
                f= new Offer_Ride();
                break;
            case R.id.status_btn:
                f=new Status_Main();
                break;
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame,f);//.addToBackStack("main");
        fragmentTransaction.commit();
    }
}
