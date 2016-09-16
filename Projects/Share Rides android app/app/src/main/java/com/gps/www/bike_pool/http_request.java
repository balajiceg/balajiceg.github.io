package com.gps.www.bike_pool;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Balaji on 3/11/2016.
 */
public class http_request {

    public static String post_and_get(String snd_txt,String URL)
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;
        //String parameters = "lat="+mUsername+"&lng="+mPassword+"&id="+id;

        try
        {
            //url = new URL("http://balaji.apps19.com/bike/putdata.php");
            url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");

            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(snd_txt);
            request.flush();
            request.close();
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            // Response from server after login process will be stored in response variable.
            response = sb.toString();
            // You can perform UI operations here
            isr.close();
            reader.close();

        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.v("Http Request","Error");
            response="error_caught";
        }
        finally {
            return response;
        }

    }
}
