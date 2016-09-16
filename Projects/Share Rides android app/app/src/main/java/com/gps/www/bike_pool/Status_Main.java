package com.gps.www.bike_pool;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Idiot1 on 3/27/2016.
 */
public class Status_Main extends Fragment implements View.OnClickListener{
    Button take,offer;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity a=( MainActivity)getActivity();
        Fragment f=new MainPage();
        f.setArguments(new Bundle());
        a.setbackFragment(f);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.status_main, container, false);
        take=(Button)v.findViewById(R.id.take_ride_sta);
        offer=(Button)v.findViewById(R.id.offer_ride_sta);


        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
        if((settings.getLong("t_start_time",-880727064)==-880727064))
            take.setVisibility(View.INVISIBLE);
        if((settings.getLong("o_start_time",-880727064)==-880727064))
            offer.setVisibility(View.INVISIBLE);

        take.setOnClickListener(this);
        offer.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        Fragment f=null;
        Bundle b=new Bundle();
        switch (v.getId())
        {
            case R.id.take_ride_sta:
                f=new Status_Take();
                break;
            case R.id.offer_ride_sta:
                f=new Status_Offer();
                break;

        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, f);//.addToBackStack("status_main");;
        fragmentTransaction.commit();
    }
}
