package com.gps.www.bike_pool;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.ramotion.foldingcell.FoldingCell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by Idiot1 on 3/27/2016.
 */
public class Status_Take extends Fragment{
    ListView lv;



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainActivity a=(MainActivity)getActivity();
        a.setbackFragment(new Status_Main());

       // SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
       // Toast.makeText(getContext(),""+settings.getInt("id",-8),Toast.LENGTH_LONG).show();
        new Status_Take_asyc(this).execute();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.status_take,container,false);
        lv=(ListView)v.findViewById(R.id.listView_tak);

        return v;
    }


    void inflate(String json_result)
    {
        Log.v("---------------",json_result);
       // Toast.makeText(getContext(),json_result,Toast.LENGTH_LONG).show();
        JSONObject jObject;
        try {
            jObject = new JSONObject(json_result);
            if(jObject.getInt("error")==0)
            {
                LatLng start,dest;

                String temp_lat=jObject.getString("start");
                temp_lat=temp_lat.substring(6,temp_lat.length()-1);
                String[] temp_arr=temp_lat.split(" ");
                start=new LatLng(Double.parseDouble(temp_arr[1]),Double.parseDouble(temp_arr[0]));

                temp_lat=jObject.getString("dest");
                temp_lat=temp_lat.substring(6,temp_lat.length()-1);
                temp_arr=temp_lat.split(" ");
                dest=new LatLng(Double.parseDouble(temp_arr[1]),Double.parseDouble(temp_arr[0]));

               JSONArray user_list=jObject.getJSONArray("result");
                ArrayList<item> users = new ArrayList<item>();
                for(int i=0;i<user_list.length();i++)
                {

                    JSONObject user=user_list.getJSONObject(i);
                    String path=user.getString("ASTEXT(O_PATH_WGS)");
                    path=path.substring(11,path.length()-1);
                    String[] temp=path.split(",");
                    LatLng[] points=new LatLng[temp.length];
                    for(int j=0;j<temp.length;j++)
                    {
                        String[] x=temp[j].split(" ");
                        points[j]=new LatLng(Double.parseDouble(x[1]),Double.parseDouble(x[0]));

                    }

                    item it=new item(user.getInt("ID"),user.getString("NAME"),user.getString("MOBILE"),points,
                            user.getLong("O_START_TIME"),null,start,dest,"Your");

                    users.add(it);
                }

                final FoldingCellListAdapter adapter = new FoldingCellListAdapter(this, users);
                lv.setAdapter(adapter);

                //lv.setRecyclerListener(FoldingCellListAdapter.mRecycleListener);


                // set on click event listener to list view
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                        // toggle clicked cell state
                        Log.v(getClass().toString(),"clicked");
                        ((FoldingCell) view).toggle(false);
                        // register in adapter that state for selected cell is toggled
                        adapter.registerToggle(pos);
                    }
                });

            }
            else if(jObject.getInt("error")==6)
            {
                Toast.makeText(getContext(),"Sorry!!No Matching routes...",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }





    }
}




class Status_Take_asyc extends AsyncTask<Void,Void,String>
{
    ProgressDialog progressDialog;
    Status_Take or;

    public Status_Take_asyc(Status_Take x)
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

        String s="";
        s+="req=4";
        s+="&id="+settings.getInt("id", -8);
        s+="&dis="+settings.getFloat("buffer_distance",or.getResources().getInteger(R.integer.default_buff_distance));
        s+="&tzone="+ TimeZone.getDefault().getRawOffset();
        Log.v("------------", "" + s);


        String result=http_request.post_and_get(s,or.getResources().getString(R.string.php_url));


        return  result;
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        Log.v("------------", aVoid.toString());
        Log.v("------------", "data got");
        //progressDialog.setMessage(aVoid);
        progressDialog.hide();
        or.inflate(aVoid);
    }
}


