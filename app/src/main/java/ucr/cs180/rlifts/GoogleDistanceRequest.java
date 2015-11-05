package ucr.cs180.rlifts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.widget.Toast;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.lang.Object;
import java.util.ArrayList;

import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


public class GoogleDistanceRequest{
    public String passed_uid;
    public boolean status = false;
    public JSONObject response;
    public GoogleDistanceRequest(){

    }

    public boolean makeConnection(String start, String destination, String uid) {
        String distance = Distance_matrix + start + "&" + Destination + destination + "&" + Mode + "&" + Units + "&" + Api_Key;
        Dist = distance.replaceAll("\\s+", "");
        passed_uid = uid;
        Start = start;
        Dest = destination;
        new LongOperation().execute();
        if(status){
            return true;
        }
        else
        {
            return false;
        }

    }

    private String Dist;
    private String Start;
    private String Dest;
    private String Distance_matrix = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="; //link to calculate distance and travel time #google
    private String Api_Key = "key=AIzaSyACd20_Z99OGiI1aQaN5pN1eKtTtyLh8jM"; // used for the link
    private String Mode = "mode=driving";
    private String Units = "units=imperial";
    private String Destination = "destinations=";
    private final String USER_AGENT = "Mozilla/5.0";
    private String origin;
    private String destin;
    private String distance1;
    private String duration;
    private String delim = ":";

    //use these for your JSON Object to send to Create RIDES table
    private String db_pickup;
    private String db_destination;
    private String db_cost;
    private String db_distance;
    private String db_duration;


    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String distance = Distance_matrix + Start + "&" + Destination + Dest + "&" + Mode + "&" + Units + "&" + Api_Key;
            distance = distance.replaceAll("\\s+", "");
            try {

                URL url = new URL(distance);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConn.setDoInput(true);

                //urlConn.connect();
                InputStream in = new BufferedInputStream(urlConn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;

                while ((line = reader.readLine()) != null) {
                    //System.out.println("line: " + line);
                    JSONObject j = null;

                    try {
                        int destination_addresses = line.indexOf ("destination_addresses");
                        int origin_addresses = line.indexOf("origin_addresses");
                        int distance_index = line.indexOf("distance");
                        int text_index = line.indexOf("text");
                        if (origin_addresses == -1)
                        {
                            //error found
                        }
                        else
                        {
                            // System.out.println("Found OriginAdress at this index: " + origin_addresses + line );
                            origin = line;
                        }

                        if (destination_addresses == -1)
                        {
                            //System.out.println("distance index NOT FOUND");
                        }
                        else
                        {
                            // System.out.println("Found destinationAddy at this index: " + destination_addresses + line );
                            destin = line;

                        }

                        if (distance_index == -1)
                        {
                            // System.out.println("distance index NOT FOUND");
                        }
                        else
                        {
                            // System.out.println("Found distance at this index: " + distance_index + line );

                        }

                        if (text_index == -1)
                        {
                            // System.out.println("text NOT FOUND");
                        }
                        else
                        {
                            // System.out.println("Text index found: " + text_index + line);
                            if (line.contains("mins"))
                            {
                                duration = line;
                            }
                            else
                            {
                                distance1 = line;
                            }
                        }

                    } catch (Exception e) {
                        System.out.println(""); // so we can see python errors from server and still finish execution
                    }
                }
            } catch (IOException e) {e.printStackTrace();}
            //System.out.println(origin + " " + destin + " " + duration + " " + distance1 );
            //parsing tags values found in [1]
            String[] str_array = origin.split(delim);
            db_pickup = str_array[1];
            str_array = destin.split(delim);
            db_destination = str_array[1];
            str_array = duration.split(delim);
            db_duration = str_array[1];
            str_array = distance1.split(delim);
            db_distance = str_array[1];

            db_pickup = db_pickup.replace("[","");
            db_pickup = db_pickup.replace("]", "");
            db_pickup = db_pickup.replace("\"","");

            db_destination = db_destination.replace("[","");
            db_destination = db_destination.replace("]","");
            db_destination = db_destination.replace("\"","");

            db_distance = db_distance.replace("\"","");
            db_duration = db_duration.replace("\"","");

            System.out.println(db_pickup + " " + db_destination + " " + db_duration + " " + db_distance );

            //calculateRide(db_duration, db_distance);
            boolean result = db_addRide(db_pickup, db_destination, db_duration, db_distance);


            return "Executed";
        }

        protected boolean db_addRide (String pickup, String destination, String duration,String distance)
        {
            System.out.println(destination);
            try {
                NetworkRequest networkRequest = new NetworkRequest("http://45.55.29.36/");

                JSONObject data = new JSONObject();
                data.put("Rides", "");
                data.put("pickup", pickup);
                data.put("destination", destination);
                data.put("distance", distance);
                data.put("duration", duration );
                data.put("UID", passed_uid);
                //data.put("cost", );

                JSONArray cred = new JSONArray();
                cred.put(data);

                networkRequest.send("../cgi-bin/db-add.py", "POST", cred); // scripts should not be hard coded, create a structure and store all somewhere
                JSONArray response = networkRequest.getResponse();

                if (response != null) {
                    for (int i = 0; i < response.length(); i++) {
                        if (response.getJSONObject(i).get("status").equals("ok")) {
                            System.out.println("Successfully received confirmation from server for login existing user.");
                            status = true;
                            return true;
                        }
                    }
                }
            } catch (Exception e) { // for now all exceptions will return false
                System.out.println("Debug in background task:\n" + e.getMessage());
                return false;
            }
            return false;
        }

        public boolean tell_home_activity(){
            boolean result = db_addRide(db_pickup, db_destination, db_duration, db_distance);
            return result;
        }

        protected void calculateRide(String duration, String distance){
            //calculating
            //int miles = Integer.parseInt(distance);
            //System.out.println(miles);
        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}