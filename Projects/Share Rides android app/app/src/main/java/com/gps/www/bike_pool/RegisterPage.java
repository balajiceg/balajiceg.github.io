package com.gps.www.bike_pool;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Balaji on 3/12/2016.
 */
public class RegisterPage extends Fragment {

    FloatingActionButton btn;
    EditText et_phone;
    EditText et_name;
    ProgressBar progress;


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

        View v=inflater.inflate(R.layout.register_page,container,false);

        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn=(FloatingActionButton)getView().findViewById(R.id.reg_done);
        et_name=(EditText)getView().findViewById(R.id.reg_name);
        et_phone=(EditText)getView().findViewById(R.id.reg_phone);
        progress=(ProgressBar)getView().findViewById(R.id.regprogress);
        progress.setVisibility(View.INVISIBLE);
        final RegisterPage reg=this;

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number=et_phone.getText().toString();
                number=number.replaceAll("\\D+","");
                if(number.length()>10)
                    number=number.substring(number.length()-10);


                if(et_name.getText()!=null&&number.length()==10)
                new Register_asyc(reg,progress,et_name.getText().toString(),number).execute(et_name.getText().toString(),et_phone.getText().toString());

                else
                    Toast.makeText(getContext(),"Enter valid name and mobile number..",Toast.LENGTH_LONG).show();
            }

        });



    }

    void goto_main_page()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment f= new MainPage();
        Bundle b=new Bundle();
        b.putBoolean("register",true);
        f.setArguments(b);
        fragmentTransaction.replace(R.id.frame,f);
        fragmentTransaction.commit();
    }
    void goto_main_page(int id,String name,String mobile,int points)
    {
        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("id", id);
        editor.putInt("points", points);
        editor.putString("name", name);
        editor.putString("mobile", mobile);
        //setting everything else null
       /* editor.putString("t_start_time", null);
        editor.putString("t_end_time", null);
        editor.putString("t_start", null);
        editor.putString("t_dest", null);
        editor.putString("o_start_time", null);
        editor.putString("o_start", null);
        editor.putString("o_dest", null);
        editor.putString("o_path_wgs", null);*/

        editor.commit();
        Log.v("---------------",settings.toString());

        goto_main_page();
    }
}



class Register_asyc extends AsyncTask<String,Void,String>
{
    RegisterPage regpage;
    ProgressBar p;
    String name;
    String mobile;
    int id;
    int points;

    public Register_asyc(RegisterPage r,ProgressBar pr,String name,String mobile)
    {
        regpage=r;
        p=pr;
        this.name=name;
        this.mobile=mobile;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        p.setVisibility(View.INVISIBLE);
        JSONObject jObject;
        try {
            jObject = new JSONObject(s);
            if(jObject.getInt("error")==0)
            {
               id= jObject.getInt("id");
               points= jObject.getInt("points");

                regpage.goto_main_page(id,name,mobile,points);

            }
            else if(jObject.getInt("error")==3)
            {
                Toast.makeText(regpage.getContext(),"Sorry!!Mobile number already exists...",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        p.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... str) {
        String s=http_request.post_and_get("req=1&name="+str[0]+"&mobile="+str[1],regpage.getResources().getString(R.string.php_url));
        Log.v("------------", "" + s);
        return s;
    }
}
