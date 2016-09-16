package com.gps.www.bike_pool;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Balaji on 3/5/2016.
 */
public class direction extends AsyncTask<Get_Point_Map, Void, String> {
     Context mActivity;
    GoogleMap mMap;
    private ProgressDialog progressDialog;
    String url;
    double sourcelat, sourcelog, destlat, destlog;
    Polyline selected;
    Get_Point_Map get_pt;


    direction(Context m,GoogleMap gm, double sourcelat, double sourcelog, double destlat, double destlog) {
        mActivity=m;
        mMap = gm;
        this.sourcelat = sourcelat;
        this.sourcelog = sourcelog;
        this.destlat = destlat;
        this.destlog = destlog;
    }

    public String makeURL(double sourcelat, double sourcelog, double destlat, double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyDISyaqmurEEr-X7dRc6YkDOew8dH2u_yk");
        return urlString.toString();
    }

    public String makeURL()
    {
        return makeURL(sourcelat,sourcelog,destlat,destlog);
    }

    public Polyline getLine()
    {
        return selected;
    }

    public void drawPath(String result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            Random rnd = new Random();
            for(int i=0;i<routeArray.length();i++)
            {
                JSONObject routes = routeArray.getJSONObject(i);
                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");
                List<LatLng> list = decodePoly(encodedString);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(5)
                                .color(Color.argb(120, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))//Google maps blue color
                                .geodesic(true)
                );
                line.setClickable(true);

                mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                    @Override
                    public void onPolylineClick(Polyline polyline) {
                        if(selected!=null)
                        {
                            selected.setWidth(5);
                            selected.setZIndex(0);
                        }
                            selected=polyline;
                            get_pt.set_line(selected);
                            polyline.setZIndex(999);
                            polyline.setWidth(10);
                    }
                });

            }

           /*
           for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
                .width(2)
                .color(Color.BLUE).geodesic(true));
            }
           */
        } catch (JSONException e) {

        }
    }


    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    void display(String str) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
        dialog.setMessage(str);
        dialog.show();
    }


    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage("Fetching route, Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Get_Point_Map... params) {
        get_pt=params[0];
        JSONParser jParser = new JSONParser();
        String json = jParser.getJSONFromUrl(makeURL(sourcelat, sourcelog, destlat, destlog));
        return json;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mMap.setOnMapClickListener(null);
        progressDialog.hide();
        Snackbar.make(get_pt.getView(),"Select your route.....",Snackbar.LENGTH_LONG).show();
       // display(result);
        if(result!=null){
            drawPath(result);
        }

    }

}


class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    // constructor
    public JSONParser() {
    }

    String getJSONFromUrl(String url) {

        // Making HTTP request
        try {
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            URL urll=new URL(url);
            connection = (HttpURLConnection) urll.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            is=connection.getInputStream();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        //} catch (ClientProtocolException e) {
        } catch (Exception e) {
            e.printStackTrace();}
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            json = sb.toString();
            is.close();
        } catch (Exception e) {
            Log.v("Buffer Error", "Error converting result " + e.toString());
        }
        return json;

    }



    }







