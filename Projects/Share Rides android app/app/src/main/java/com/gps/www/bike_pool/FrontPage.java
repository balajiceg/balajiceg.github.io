package com.gps.www.bike_pool;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.*;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;



public class FrontPage extends Fragment {


    private ProgressBar p_bar;



    public FrontPage() {
        // Required empty public constructor
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

       Log.v("network avail",":"+isNetworkAvailable());
        if(isNetworkAvailable())
        {
            SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
            if(!settings.contains("mobile"))
            {
                TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Activity.TELEPHONY_SERVICE);
                String number = tm.getLine1Number();
                Toast.makeText(getContext(),number,Toast.LENGTH_LONG).show();
                number=number.replaceAll("\\D+", "");
                if(number.length()>10)
                    number=number.substring(number.length()-10);

                new Front_Page_asyc(this,number).execute(p_bar);
            }
            else
            {
                new chk_number_exists(this,settings.getString("mobile","")).execute(p_bar);
            }
//            new CountDownTimer(200,500){
//                @Override
//                public void onTick(long millisUntilFinished){}
//
//                @Override
//                public void onFinish(){
//                    //set the new Content of your activity
//                    goto_main_page();
//                }
//            }.start();
        }
        else
        {
            new CountDownTimer(3000,1000){
                boolean first=true;
                @Override
                public void onTick(long millisUntilFinished){
                    if(!first)
                    Toast.makeText(getContext(),"Network Error",Toast.LENGTH_SHORT).show();
                    first=false;
                    Log.v("asas",""+millisUntilFinished);
                }



                @Override
                public void onFinish(){
                    //set the new Content of your activity

                    getActivity().finish();
                }
            }.start();

        }





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v=inflater.inflate(R.layout.front_page, container, false);
        p_bar=(ProgressBar)v.findViewById(R.id.progressBar);

        p_bar.setVisibility(View.VISIBLE);

               return v;
    }

    void goto_register_page()
    {
        p_bar.setVisibility(View.INVISIBLE);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        RegisterPage f= new RegisterPage();

        fragmentTransaction.replace(R.id.frame,f);
        fragmentTransaction.commit();
    }
    void goto_main_page()
    {

        p_bar.setVisibility(View.INVISIBLE);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment f= new MainPage();
        Bundle b=new Bundle();
        b.putString("mobile","idiot");
        f.setArguments(b);
        fragmentTransaction.replace(R.id.frame,f);
        fragmentTransaction.commit();
    }
    void goto_main_page(int id,String name,String mobile)
    {
        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("id",id);
        editor.putString("name", name);
        editor.putString("mobile",mobile);
        editor.commit();
        goto_main_page();
    }



    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }






}




class Front_Page_asyc extends AsyncTask<ProgressBar,Void,String> {
    String mob;
    FrontPage frontpage;
    ProgressBar p;
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //p.setVisibility(View.INVISIBLE);

        JSONObject jObject;
        try {
            jObject = new JSONObject(s);
            if(jObject.getInt("error")==0)
            {
                frontpage.goto_main_page(jObject.getInt("id"),jObject.getString("name"),mob);
            }
            else if(jObject.getInt("error")==2)
            {
                frontpage.goto_register_page();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public Front_Page_asyc(FrontPage f,String s)
    {
        mob=s;
        frontpage=f;
    }

    @Override
    protected String doInBackground(ProgressBar... params) {
        p=params[0];
        Log.v("front page asyncputdata", "" + mob);
        String s=http_request.post_and_get("req=0&mobile="+mob,frontpage.getResources().getString(R.string.php_url));

        Log.v("front page async rply", "" + s);
        return s;
    }
}

class chk_number_exists extends AsyncTask<ProgressBar,Void,String> {
    String mob;
    FrontPage frontpage;
    ProgressBar p;
    @Override
    protected void onPostExecute(String s) {


        final JSONObject jObject;
        try {
            jObject = new JSONObject(s);
            if(jObject.getInt("error")==7)
            {
                final boolean allow=jObject.getInt("allow")==1;
                final AlertDialog.Builder dialog = new AlertDialog.Builder(frontpage.getContext());
                dialog.setMessage(jObject.getString("message"));
                dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                       if(allow)
                       {
                           paramDialogInterface.dismiss();


                           try {
                               if(jObject.getInt("error")==2)
                               {
                                   frontpage.goto_register_page();
                               }
                               else if(jObject.getInt("result")==1)
                               {
                                   JSONObject user=jObject.getJSONObject("user");
                                   SharedPreferences settings = frontpage.getActivity().getSharedPreferences("UserInfo", 0);
                                   SharedPreferences.Editor editor = settings.edit();
                                   editor.putInt("id", user.getInt("id"));
                                   editor.putInt("points",user.getInt("points"));
                                   editor.putString("name",user.getString("name"));

                                   if(user.getString("t_start_time")!="null") {
                                       editor.putLong("t_start_time", user.getLong("t_start_time"));
                                       editor.putLong("t_end_time", user.getLong("t_end_time"));
                                       editor.putString("t_start", user.getString("t_start"));
                                       editor.putString("t_dest", user.getString("t_dest"));
                                   }


                                   if(user.getString("o_start_time")!="null") {
                                       editor.putLong("o_start_time", user.getLong("o_start_time"));
                                       editor.putString("o_start", user.getString("o_start"));
                                       editor.putString("o_dest", user.getString("o_dest"));
                                       editor.putString("o_path_wgs", user.getString("o_path_wgs"));
                                   }
                                   editor.commit();

                                   frontpage.goto_main_page();
                               }
                           } catch (JSONException e) {
                               e.printStackTrace();
                           }
                       }


                       else frontpage.getActivity().finish();
                       }
                });
                dialog.show();

            }
            else if(jObject.getInt("error")==2)
            {
                frontpage.goto_register_page();
            }
            else if(jObject.getInt("result")==1)
            {
                JSONObject user=jObject.getJSONObject("user");
                SharedPreferences settings = frontpage.getActivity().getSharedPreferences("UserInfo", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("id", user.getInt("id"));
                editor.putInt("points",user.getInt("points"));
                editor.putString("name",user.getString("name"));

                if(user.getString("t_start_time")!="null") {
                    editor.putLong("t_start_time", user.getLong("t_start_time"));
                    editor.putLong("t_end_time", user.getLong("t_end_time"));
                    editor.putString("t_start", user.getString("t_start"));
                    editor.putString("t_dest", user.getString("t_dest"));
                }


                if(user.getString("o_start_time")!="null") {
                    editor.putLong("o_start_time", user.getLong("o_start_time"));
                    editor.putString("o_start", user.getString("o_start"));
                    editor.putString("o_dest", user.getString("o_dest"));
                    editor.putString("o_path_wgs", user.getString("o_path_wgs"));
                }
                editor.commit();

                frontpage.goto_main_page();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public chk_number_exists(FrontPage f,String s)
    {
        mob=s;
        frontpage=f;
    }

    @Override
    protected String doInBackground(ProgressBar... params) {
        PackageInfo pInfo = null;
        try {
            pInfo = frontpage.getContext().getPackageManager().getPackageInfo(frontpage.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        int verCode = pInfo.versionCode;
        Log.v(this.getClass().getName(),"version:"+version+" code:"+verCode);

        p=params[0];

        Log.v(this.getClass().getName(),"number sent for confirmation-"+mob);
        String s=http_request.post_and_get("req=8&mobile="+mob+"&vercode="+verCode,frontpage.getResources().getString(R.string.php_url));
        Log.v("num confirmation msg:", "" +s);
        return s;
    }
}
