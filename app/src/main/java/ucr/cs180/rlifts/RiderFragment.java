package ucr.cs180.rlifts;

//import android.app.Fragment;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;





public class RiderFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static String sendby_id;
    private OnFragmentInteractionListener mListener;
    public static int driver_id;
    public static String requester; // jai said this
    public static GoogleMap myMap;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RiderFragment.
     */
    public static String[] RIDES = {"TEMP", "temp", "temp", "temp"};
    public static List<String> rides_list = new ArrayList<String>();
    public static List<String[]> originDestination = new ArrayList<String[]>();
    public GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyAw4-hFkaJFAcVQiz6-Muka5MtU7nU9FAI")
            .setQueryRateLimit(3)
            .setConnectTimeout(1, TimeUnit.SECONDS)
            .setReadTimeout(1, TimeUnit.SECONDS)
            .setWriteTimeout(1, TimeUnit.SECONDS);

    public static DirectionsRoute[] routes;

    private class RouteGenerator extends AsyncTask<GoogleMap, PolylineOptions, String>{

        @Override
        protected String doInBackground(GoogleMap... params){
            for(int i = 0; i < originDestination.size(); i++) {
                int R = (int) (Math.random() * 256);
                int G = (int) (Math.random() * 256);
                int B = (int) (Math.random() * 256);

                String uidVal = originDestination.get(i)[0];
                String originRoute = originDestination.get(i)[1];
                String destinationRoute = originDestination.get(i)[2];
                try {
                    //need to put some or all of this on the background thread
                    routes = DirectionsApi.newRequest(context)
                            .origin(originRoute)
                            .destination(destinationRoute)
                            .await();
                    //Pull the information from the response here, access via their respective arrays

                    for (int j = 0; j < routes[0].legs.length; j++) {
                        for (int k = 0; k < routes[0].legs[j].steps.length; k++) {
                            List<LatLng> coords = PolyUtil.decode(routes[0].legs[j].steps[k].polyline.getEncodedPath());
                            System.out.println(coords);

                            Color lineColor = new Color();
                            PolylineOptions routeOptions = new PolylineOptions()
                                    .addAll(coords)
                                    .width(10)
                                    .color(lineColor.rgb(R, G, B));
                            publishProgress(routeOptions);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Directions API Screwed up: ");
                    e.printStackTrace();
                }



            }
            return "LOL";
        }

        @Override
        protected  void onProgressUpdate(PolylineOptions... lineOps){
            Polyline polyline = myMap.addPolyline(lineOps[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_rider, container, false);
        //super.onCreateView(savedInstanceState);

        MapFragment mapFragment = (MapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return mainView;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng irvine = new LatLng(33.9665302, -117.3521857);
        map.addMarker(new MarkerOptions().position(irvine).title("Marker in Irvine (Hardcoded)"));
        map.moveCamera(CameraUpdateFactory.newLatLng(irvine));

        myMap = map;

        new RouteGenerator().execute();



    }

    // TODO: Rename and change types and number of parameters
    public static RiderFragment newInstance(String param1, String param2, JSONArray response, String uid) {
        RiderFragment fragment = new RiderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        //received_json = response;

        parse_json_for_displaying(response, uid);
        requester = uid;
        return fragment;
    }

    public static void parse_json_for_displaying(JSONArray response, String driver_uid) {
        try {
            ArrayList <String> list = new ArrayList<String>();

            String distance = "";
            String pickup = "";
            String destination = "";
            String cost  = "";
            String duration = "";
            String ride_string = "";
            String uid = "";
            String mpickup = "Pick Up:";
            String mdestin = " \nDestination:";
            String mdist = "\nDistance: ";
            String mdur = " \nDuration: ";
            String mcost = "\nEstimated Cost: $";
            String mdriverID = "\nYour Driver's ID #: ";
            double sLat = 0.0;
            double sLong = 0.0;
            double eLat = 0.0;
            double eLong = 0.0;

            String newline = "\n";

            //THIS HOW YOU ACCESS THE OBJECT INFORMATION
            JSONArray rideList = response.getJSONObject(0).getJSONArray("data");
            for (int i = 0; i < rideList.length(); ++i)
            {
                //THIS HOW YOU ACCESS THE OBJECT INFORMATION
                JSONObject ride = rideList.getJSONObject(i);
                pickup = ride.getString("pickup");
                distance = ride.getString("distance");
                destination = ride.getString("destination");
                duration = ride.getString("duration");
                uid = ride.getString("UID");
                cost = ride.getString("cost");
                String[] startFin = new String[3];
                startFin[0] = uid;
                startFin[1] = pickup;
                startFin[2] = destination;
                originDestination.add(startFin);

                sendby_id = uid;
                System.out.println("UID here: " + uid);

                ride_string = mpickup+ pickup + newline + mdestin + destination + newline + mdist + distance + newline + mcost + cost + newline + mdur + duration + newline + mdriverID + uid;

                rides_list.add(i,ride_string);

                /*rides_list.add(i,pickup);
                rides_list.add(i,destination);
                rides_list.add(i,distance);*/

            }
        }catch (Exception e) {System.out.println(e);}

    }

    public RiderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionR(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteractionR(Uri uri);
    }

    private class send_message extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                NetworkRequest networkRequest = new NetworkRequest("http://45.55.29.36/");

                JSONObject data = new JSONObject();
                data.put("Messages", "Messages");
                data.put("sentBy", requester);
                data.put("sentTo", driver_id);
                data.put("mtext", "My message");
                data.put("status", 0);
                //data.put("notification_check", 0);

                JSONArray cred = new JSONArray();
                cred.put(data);

                networkRequest.send("../cgi-bin/db-add.py", "POST", cred); // scripts should not be hard coded, create a structure and store all somewhere
                JSONArray response = networkRequest.getResponse();
                if (response != null) {
                    for (int i = 0; i < response.length(); i++) {
                        if (response.getJSONObject(i).get("status").equals("ok")) {
                            System.out.println("Successfully received confirmation from server for getting rides.");
                            //return true;
                        }
                    }
                }

            } catch (Exception e) { // for now all exceptions will return false
                System.out.println("Debug in background task:\n" + e.getMessage());
                //return false;
            }
            //return false;
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}