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
import org.joda.time.DateTime;
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

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Unit;


public class GoogleDistanceRequest{
    public String passed_uid;
    public Boolean status = null;
    public JSONObject response;
    private HomeActivity.MySpinnerDialog waitOnGDR;
    private Object lock;
    private org.joda.time.DateTime leaveTime;
    public GoogleDistanceRequest(org.joda.time.DateTime leaveTime) {
        this.leaveTime = leaveTime;
    }

    public boolean makeConnection(String start, String destination, String uid) throws InterruptedException {
        passed_uid = uid;
        Start[0] = start;
        Dest[0] = destination;
        
        new LongOperation().execute();
        lock = new Object();
        synchronized (lock) {
            while (status == null) {
                lock.wait();
            }
        }
        System.out.println("The status flag is: " + status);
        return status;
    }

    private String[] Start = new String[1];
    private String[] Dest = new String[1];
    private String Api_Key = "AIzaSyACd20_Z99OGiI1aQaN5pN1eKtTtyLh8jM"; // used for the link
    private String Geo_Api_Key = "AIzaSyAw4-hFkaJFAcVQiz6-Muka5MtU7nU9FAI";

    private GeoApiContext distanceContext = new GeoApiContext().setApiKey(Api_Key);
    private GeoApiContext geoContext = new GeoApiContext().setApiKey(Geo_Api_Key);

    //use these for your JSON Object to send to Create RIDES table
    private String db_pickup;
    private String db_destination;
    private String db_distance;
    private String db_duration;
    private Double db_costFinal;


    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //Try catch to process a distance matrix request
            try {
                //Set the parameters for the request here
                DistanceMatrix matrix = DistanceMatrixApi.newRequest(distanceContext)
                        .origins(Start)
                        .destinations(Dest)
                        .units(Unit.IMPERIAL)
                        .await();
                //Pull the information from the matrix here, access via their respective arrays
                db_distance = matrix.rows[0].elements[0].distance.humanReadable;
                db_duration = matrix.rows[0].elements[0].duration.humanReadable;
                db_pickup = matrix.originAddresses[0];
                db_destination = matrix.destinationAddresses[0];
                db_costFinal = 10.0;
                System.out.println("Your pickup location is: " + db_pickup);
            } catch (Exception e) {
                status = false;
                System.out.println("Status is: " + status);
                e.printStackTrace();
                return e.toString();
            }


            //Arrays to hold coordinates for the start and endpoints
            double[] pickupCoordinates;
            double[] destinationCoordinates;
            //Call the googleCoordinateRequest function to calculate the lat/long values
            pickupCoordinates = googleCoordinateRequest(db_pickup);
            destinationCoordinates = googleCoordinateRequest(db_destination);

            if(pickupCoordinates.length == 0 || destinationCoordinates.length == 0)
            {
                status = false;
                return "Error";
            }

            boolean result = db_addRide(db_pickup, db_destination, db_duration, db_distance, db_costFinal, pickupCoordinates, destinationCoordinates);
            status = true;
            synchronized (lock) {
                lock.notify();
            }
            return "Executed";
        }

        //Request lat/long coordinates from google maps API
        protected double[] googleCoordinateRequest (String location)
        {
            double[] temp = new double[0];
            GeocodingResult[] results = new GeocodingResult[0];
            try {
                results = GeocodingApi.geocode(geoContext, location).await();
            } catch (Exception e) {
                e.printStackTrace();
                return temp;
            }

            status = true;

            //Create a double array to store the lat/long so we can return it to the calling function
            double[] coordinateArray = new double[2];
            coordinateArray[0] = results[0].geometry.location.lat;
            coordinateArray[1] = results[0].geometry.location.lng;
            return coordinateArray;
        }

        protected boolean db_addRide (String pickup, String destination, String duration,String distance, Double Cost, double[] pickupCoordinates, double[] destinationCoordinates)
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
                data.put("cost", Cost);
                data.put("UID", passed_uid);
                data.put("start_latitude", pickupCoordinates[0]);
                data.put("start_longitude", pickupCoordinates[1]);
                data.put("end_latitude", destinationCoordinates[0]);
                data.put("end_longitude", destinationCoordinates[1]);
                data.put("leave_time", leaveTime);
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
/*
        public boolean tell_home_activity(){
            boolean result = db_addRide(db_pickup, db_destination, db_duration, db_distance,db_costFinal);
            return result;
        }
*/
        /*
                public boolean tell_home_activity(){
                    boolean result = db_addRide(db_pickup, db_destination, db_duration, db_distance,db_costFinal);
                    return result;
                }
        */
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