package com.gps.www.bike_pool;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by Idiot1 on 3/27/2016.
 */
public class Tracking extends Fragment implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    private static final String TAG = "Tracking...";
    private static final long INTERVAL = 1000 * 3;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    boolean get_data= true;
    Location mCurrentLocation;
    boolean gps_enabled;
    boolean network_enabled;
    int marker_count = 0;
    Marker marker1, marker2;
    boolean usr_loc_ok=true;
    Marker mark;
    Snackbar snack;
    Track_getdata tem;

    item user;
    int id;

    public void setUser(item user) {
        this.user = user;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        snack=Snackbar.make(getView(),"",Snackbar.LENGTH_INDEFINITE);
       snack.setText("").show();
        if(tem==null)
        {tem=new Track_getdata(this);
       startMyTask(tem);}


        Fragment f=new Status_Take();
        if(user.end_time!=null) f=new Status_Offer();
        MainActivity a=(MainActivity)getActivity();
        a.setbackFragment(f);


        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
        id=settings.getInt("id", -1);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tracking, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.track_map);
        mapFragment.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        mMap = Map;
        Map.getUiSettings().setZoomControlsEnabled(true);
        Random rnd = new Random();

        Polyline line = Map.addPolyline(new PolylineOptions()
                .addAll(new ArrayList<LatLng>(Arrays.asList(user.path)))
                .width(5)
                .color(Color.argb(120, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))//Google maps blue color
                .geodesic(true));

        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (LatLng m : user.path) {
            b.include(m);
        }
        LatLngBounds bounds = b.build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 5);
        Map.animateCamera(cu);
        //////////////////////////////////////////////////


        check_gps_enabled();


        if(usr_loc_ok)
        {
            if (!isGooglePlayServicesAvailable()) {
                Toast.makeText(getContext(), "Please install google play services", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;}

            Map.setMyLocationEnabled(true);
            Map.getUiSettings().setMyLocationButtonEnabled(false);
            if (!mGoogleApiClient.isConnected()) {
                Log.d(TAG, "Location service started...");
                mGoogleApiClient.connect();
            }
            updateUI();
        }



        
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
        get_data=false;
        snack.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (usr_loc_ok&&mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
        if(usr_loc_ok&&!mGoogleApiClient.isConnected()) {
            chk_location_inner();
            if (mMap != null && (gps_enabled || network_enabled)) {
                if (!mGoogleApiClient.isConnected()) {
                    Log.d(TAG, "Location service started...");
                    mGoogleApiClient.connect();
                }
                updateUI();
            }
        }
        if(tem==null)
        {
            tem=new Track_getdata(this);
            startMyTask(tem);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected())
            stopLocationUpdates();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        updateUI();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());

    }


/////////////////////////check google play services

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0).show();
            return false;
        }
    }
/////////////////////////.check google play services


    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");

        if (null != mCurrentLocation) {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());

//            Snackbar.make(getView(), "Latitude: " + lat + "\n" +
//                    "Longitude: " + lng + "\n" +
//                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
//                    "Provider: " + mCurrentLocation.getProvider(), Snackbar.LENGTH_LONG).show();

            String s="";
            s+="req=6";
            s+="&lat="+lat;
            s+="&lng="+lng;
            s+="&id="+id;
            new Track_putdata().execute(s);


        } else {
            Log.v(TAG + "-----------", "location is null ...............");

        }
    }


    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    /////////////////////////////chk location enabled
    protected void check_gps_enabled() {


        chk_location_inner();


        if (!gps_enabled && !network_enabled) {
            Log.v(TAG,"Gps not enabled...");
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().getApplicationContext().startActivity(myIntent);
                    paramDialogInterface.dismiss();

                }
            });
            dialog.setNegativeButton(this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    chk_location_inner();
                    if(!gps_enabled && !network_enabled) {
                        mMap.setMyLocationEnabled(false);
                        usr_loc_ok=false;
                    }

                }
            });
            dialog.show();
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
            return;
        }
        else if(gps_enabled)
            mMap.setMyLocationEnabled(true);

    }

    void chk_location_inner() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = false;
        network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /////////////////////////////....chk location enabled

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    check_gps_enabled();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    void set_user_point(String s)
    {

        try {

           // if(s.toCharArray()[0]=='{')
           // {
            if(!s.equals("error_caught"))
                 Toast.makeText(getContext(),s,Toast.LENGTH_SHORT).show();
            else
            {
                snack.setText("OfflineE");
                if(mark!= null) mark.remove();
            }

                JSONObject jObject = new JSONObject(s);

                if(jObject.getInt("error")==0)
                {
                    snack.setText("Online").show();
                    s=jObject.getString("location");
                    String temp_lat=s.substring(6, s.length() - 1);
                    String[] temp_arr=temp_lat.split(" ");
                    LatLng loc=new LatLng(Double.parseDouble(temp_arr[1]),Double.parseDouble(temp_arr[0]));

                    if(mark==null) {
                        mark = mMap.addMarker(new MarkerOptions()
                                .position(loc)
                                .title(""));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mark.getPosition(), 15));

                    }
                    else
                    {
                        mark.setPosition(loc);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(mark.getPosition()));

                    }
                }
                if(jObject.getInt("error")==2)
                {
                    if(mark!=null)
                        mark.remove();
                    snack.setText("Offline");
                }

           // }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    class Track_putdata extends AsyncTask<String,Void,String>
    {



        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... params) {

            String s=params[0];
            Log.v("------------", "" + s);


            String result=http_request.post_and_get(s, getResources().getString(R.string.php_url));

            Log.v("------------", "" + result);
            return  result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.v("------------", "" + result);
        }
    }

    class Track_getdata extends AsyncTask<Void,String,String>
    {
        Tracking track;

        public Track_getdata(Tracking track) {
            this.track = track;
        }


        @Override
        protected String doInBackground(Void... params) {

            String s="";
            s+="req=7";
            s+="&id="+user.id;
            String result="{error:2}";
            while(get_data==true) {
                Log.v("------------", "" + s);
                result = http_request.post_and_get(s, getResources().getString(R.string.php_url));
                Log.v("------------", "" + result);
                publishProgress(result);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            return result;
        }

        @Override
        protected void onProgressUpdate(String... str) {
            super.onProgressUpdate(str);
          track.set_user_point(str[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    void startMyTask(AsyncTask asyncTask) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);
        else
            asyncTask.execute();
    }



}


