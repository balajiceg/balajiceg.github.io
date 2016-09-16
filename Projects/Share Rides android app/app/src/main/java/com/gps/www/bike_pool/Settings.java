package com.gps.www.bike_pool;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Idiot1 on 3/27/2016.
 */
public class Settings extends Fragment implements View.OnClickListener{
    EditText buff_dis;
    FloatingActionButton fab;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity a=( MainActivity)getActivity();
        if(a.getbackFragment()==null)
        {
            Fragment f=new MainPage();
            f.setArguments(new Bundle());
            a.setbackFragment(f);
        }

        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
       float dis=settings.getFloat("buffer_distance",-880727064);
        if(dis==-880727064)
        {
            SharedPreferences.Editor editor = settings.edit();
            dis=this.getResources().getInteger(R.integer.default_buff_distance);
            editor.putFloat("buffer_distance",dis);
            editor.commit();
        }
        buff_dis.setText(dis+"");



    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buff_dis=(EditText)view.findViewById(R.id.settings_buff_dis);
        fab=(FloatingActionButton) view.findViewById(R.id.settings_fab_icon);
        fab.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.settings,container,false);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.settings_fab_icon:
                if(buff_dis.getText().toString()=="")
                {
                    Snackbar.make(getView(),"Sorry!! Invalid Entery",Snackbar.LENGTH_SHORT).show();
                    break;
                }
                else
                {
                    float dis=Float.parseFloat(buff_dis.getText().toString());
                    if(dis<100||dis>1000)
                    {
                        Snackbar.make(getView(),"Distance must between 100 to 1000",Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    else
                    {
                        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putFloat("buffer_distance",dis);
                        editor.commit();
                        getActivity().onBackPressed();
                    }
                }


        }
    }
}
