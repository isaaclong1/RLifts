package ucr.cs180.rlifts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RiderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RiderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RiderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static String sendby_id;
    private OnFragmentInteractionListener mListener;
    public static int driver_id;
    public static String requester; // jai said this

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
    //private static JSONArray received_json;

    /*public static RiderFragment newInstance() {
        Bundle args = new Bundle();
        RiderFragment fragment = new RiderFragment();
        fragment.setArguments(args);
        return fragment;
    } */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_rider, container, false);
        ListView listView = (ListView) mainView.findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, rides_list));
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked perform some action...
                String choice = ((TextView) view).getText().toString();
                int place = choice.indexOf("Driver's ID");
                int temp = Integer.parseInt(choice.substring(place + 15, choice.length()));
                driver_id = temp;
                new send_message().execute();
            }
            // When clicked perform some action...
        });
        return mainView;
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