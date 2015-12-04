package ucr.cs180.rlifts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.location.Location;
import android.support.v4.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import javax.xml.transform.Result;


import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProfileFragment.OnFragmentInteractionListener,
        RiderFragment.OnFragmentInteractionListener, DriverFragment.OnFragmentInteractionListener,
        Tutorial.OnFragmentInteractionListener, PaymentListFragment.OnFragmentInteractionListener {

    private EditText StartView;
    private EditText DestinationView;
    private EditText DepartureView;
    private TimePicker timePicker1;
    private DatePicker datePicker1;
    private org.joda.time.DateTime leaveTime;
    private static String uid;
    private static String rider_id;
    private JSONArray send_over;

    private static Boolean flag = false;
    private static boolean run_showalert = true;
    private static Boolean rider_flag = false;
    private static String m_id;
    private static String global_status;
    private JSONArray profileData;
    private JSONArray picture;
    private static Integer driver_flag;
    final Handler ha = new Handler();
    FloatingActionButton fab;
    private static MySpinnerDialog myInstance;
    private final Object lock = new Object();




    Runnable messageRunnable = new Runnable() {
        @Override
        public void run() {
            if(driver_flag != null && driver_flag == 1){
                new Get_Driver_Message().execute();
            }


            if (flag == true && driver_flag != null && driver_flag == 1) {
                showAlert();
                flag = false;
            }
            if(rider_flag == true) {
                showRiderMessage();
                rider_flag = false;
            }

            ha.postDelayed(this,1000);
        }
    };
    Handler mHandler;

    public String randomString() {
        int length = 10;
        char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
            result[i] = CHARSET_AZ_09[randomCharIndex];
        }
        return new String(result);
    }

    public void postRideOnClick(View view){
        StartView = (EditText) findViewById(R.id.start);
        DestinationView = (EditText) findViewById(R.id.destination);
        DepartureView = (EditText) findViewById(R.id.leaveTime);
        timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
        datePicker1 = (DatePicker) findViewById(R.id.datePicker1);
        int hour = timePicker1.getCurrentHour();
        int min = timePicker1.getCurrentMinute();
        int day = datePicker1.getDayOfMonth();
        int month = datePicker1.getMonth();
        int year = datePicker1.getYear();
        org.joda.time.DateTime date = new org.joda.time.DateTime(year, month, day, hour, min);
        System.out.println("date: " + date);
        leaveTime = date;

        System.out.println("hour: " + hour + "\nminute: " + min + "\nday: " + day + "\nmonth: " + month + "\nyear: " + year);

        String start = StartView.getText().toString();
        String dest = DestinationView.getText().toString();
        GoogleDistanceRequest gdr = new GoogleDistanceRequest(leaveTime);

        boolean flag = false;
        try {
            flag = gdr.makeConnection(start, dest, uid);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (flag) {
            //driver_flag = 1;
            Toast.makeText(getApplicationContext(),
                    "Ride posted!", Toast.LENGTH_LONG).show();
            showAlertforDriver();
            StartView.setText("");
            DestinationView.setText("");
            DepartureView.setText("");

        } else {
            Toast.makeText(getApplicationContext(),"Invalid Address!", Toast.LENGTH_LONG).show();
        }


        if(driver_flag == 0 && flag == true)
        {
            Intent intent = new Intent(this, DriverRegistration.class);
            Bundle bundle = new Bundle();
            bundle.putString("UID", uid);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        //drawer.openDrawer(Gravity.LEFT);
        toggle.syncState();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("global_uid");
            uid = value;
            System.out.println("LALA " + uid);
        }

        new Get_Rides().execute();
        ha.postDelayed(messageRunnable, 1000);


        new getProfileInformation().execute();
        Fragment fragment = null;
        fragment = Tutorial.newInstance("string1", "string2");
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    public void showAlertforDriver ()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your ride has been posted. Please wait for someone to select your ride").create();
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) // yes buton
            {
                dialog.dismiss();
            }
        });
        builder.setTitle ("NOTIFICATION");
        builder.show();
    }
    boolean ride_taken = true;
    boolean start_intent = false;
    public void showAlert ()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("YOUR ROUTE HAS BEEN SELECTED, DO YOU WANT TO PICK UP THIS PASSENGER?").create(); // maybe send basic profile information with it?
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) // yes buton
            {
                start_intent = true;
                new send_message().execute();
                synchronized (lock) {
                    lock.notify();
                }
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) // no button
            {
                dialog.dismiss();
                ride_taken = false;
            }
        });
        builder.setTitle("NOTIFICATION");
        builder.show();
        /*if(ride_taken) {
            synchronized (lock) {
                while (rider_flag  == null || flag == null) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }*/
            if(start_intent) {
                System.out.println("Inside start intent");
                Intent intent = new Intent(this, DropOffRider.class);
                startActivity(intent);
            }
        }

    public void showRiderMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your driver has accepted your ride. Here is your ride completion code: " + randomString() + ". Please give this to your driver upon ride completion").create();
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) // yes buton
            {
                dialog.dismiss();
            }
        });
        builder.setTitle("NOTIFICATION");
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, setting.class);
            Bundle bundle = new Bundle();
            try {

                bundle.putString("name", profileData.getJSONObject(0).getString("uname"));
                bundle.putString("lastname", profileData.getJSONObject(0).getString("uname"));
                bundle.putString("phonenumber", profileData.getJSONObject(0).getString("phone_num"));
                bundle.putString("birthday", profileData.getJSONObject(0).getString("birthday"));
                bundle.putString("email", profileData.getJSONObject(0).getString("email"));
                bundle.putString("first_name", profileData.getJSONObject(0).getString("first_name"));
                bundle.putString("last_name", profileData.getJSONObject(0).getString("last_name"));

                bundle.putString("UID", uid);
            }
            catch (JSONException e) { return false;}
            intent.putExtras(bundle);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        RiderFragment riderMapFragment = new RiderFragment(); // All fragments should be initialized this way
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (id == R.id.nav_profile) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,GravityCompat.END);
            System.out.println("handling the profile view!");
            fab.setVisibility(View.INVISIBLE);

            // TODO: get the photo from local file and pass it through as encoded string
            fragment = ProfileFragment.newInstance("string1", "string2", profileData, picture);
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            getSupportActionBar().setTitle("Profile");

        } else if (id == R.id.nav_rider) {
            System.out.println("handling the rider view!");
            fab.setVisibility(View.VISIBLE);

            //fragment = RiderFragment.newInstance("string1", "string2", send_over, uid);
            riderMapFragment = riderMapFragment.newInstance(riderMapFragment,"string1", "string2", send_over, uid);
            // Insert the fragment by replacing any existing fragment
            new Get_Rides().execute();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, riderMapFragment).commit();
            new Get_Rides().execute();
            getSupportActionBar().setTitle("Rider");


        } else if (id == R.id.nav_driver) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,GravityCompat.END);
            System.out.println("handling the driver view!");
            new valid_driver().execute();
            synchronized (lock) {
                while (driver_flag == null) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            myInstance = new MySpinnerDialog();
            myInstance.show(getSupportFragmentManager(), "Waiting on valid_driver");

            if(driver_flag != 1){
                Intent intent = new Intent(this, DriverRegistration.class);
                Bundle bundle = new Bundle();
                bundle.putString("UID", uid);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            fab.setVisibility(View.INVISIBLE);

            fragment = DriverFragment.newInstance("string1", "string2");
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            getSupportActionBar().setTitle("Driver");


        } else if(id == R.id.logout){
            Intent intent = new Intent(this, LoginActivity.class);
            Toast.makeText(getApplicationContext(),
                    "Logout Successful", Toast.LENGTH_LONG).show();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ha.removeCallbacks(messageRunnable);
            //driver_flag = null;
            startActivity(intent);
            FragmentManager fm = getFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }

            finish();

        } else if(id == R.id.nav_payment) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,GravityCompat.END);
            System.out.println("handling payment fragment view!");
            fab.setVisibility(View.INVISIBLE);

            fragment = PaymentListFragment.newInstance("string1", "string2");
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            getSupportActionBar().setTitle("Add Tokens");
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteractionR(Uri uri) {

    }

    @Override
    public void onFragmentInteractionP(Uri uri) {

    }

    @Override
    public void onFragmentInteractionD(Uri uri) {

    }
    @Override
    public void onFragmentInteractionF(Uri uri) {
    }

    @Override
    public void onFragmentInteraction(String id) {
        System.out.println(id);

        // start the paypal activity here
        //TODO: send over the string id with the intent, that way you know how many tokens they selected.

        Intent intent = new Intent(HomeActivity.this, PaypalActivity.class);
        intent.putExtra("EXTRA_MENU_ID", id);
        intent.putExtra("EXTRA_UID", uid);
        startActivity(intent);
    }


    private class Get_Driver_Message extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            flag = false;
            try {
                NetworkRequest networkRequest = new NetworkRequest("http://45.55.29.36/");

                JSONObject data = new JSONObject();
                data.put("Messages", "Messages");
                data.put("queryType", "inboxCheck");
                data.put("data", uid);


                JSONArray cred = new JSONArray();
                cred.put(data);

                networkRequest.send("../cgi-bin/db-select.py", "POST", cred); // scripts should not be hard coded, create a structure and store all somewhere
                JSONArray response = networkRequest.getResponse();
                //parsing response
                String message_id = "";
                String status="";
                String message_to="";
                JSONArray rideList = response.getJSONObject(0).getJSONArray("messages");
                for(int i = 0; i < rideList.length(); i++){
                    JSONObject ride = rideList.getJSONObject(i);
                    message_id = ride.getString("MID");
                    status = ride.getString("status");
                    message_to = ride.getString("type");
                    rider_id = ride.getString("sentBy");
                    m_id = message_id;
                    global_status = status;
                }

                JSONObject otherdata = new JSONObject();
                otherdata.put("Messages", "Messages");
                otherdata.put("queryType", "setMessageStatus");
                otherdata.put("MID", m_id);


                JSONArray othercred = new JSONArray();
                othercred.put(otherdata);

                networkRequest.send("../cgi-bin/db-select.py", "POST", othercred); // scripts should not be hard coded, create a structure and store all somewhere
                JSONArray otherresponse = networkRequest.getResponse();

                for (int i = 0; i < response.length(); i++) {
                    if (response.getJSONObject(i).get("status").equals("ok")) {
                        System.out.println("Successfully received confirmation from server for getting messages.");
                        //return true;
                        System.out.println("MESSAGE TO: " + message_to);
                        if(message_to.equals("driver")){
                            System.out.println("Inside here");
                            flag = true;
                        }
                        if(message_to.equals("rider")){
                            System.out.println("Setting rider flag");
                            rider_flag = true;
                        }
                    }
                }

                //System.out.println("MESSAGES : " + response);

            } catch (Exception e) { // for now all exceptions will return false
                System.out.println("Debug in background task:\n" + e.getMessage());
                //return false;
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
        @Override
        protected void onCancelled(){
            finish();
        }

    }

    private class getProfileInformation extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                NetworkRequest networkRequest = new NetworkRequest("http://45.55.29.36/");


                // request for profile data
                JSONObject data = new JSONObject();

                data.put("Users", "Users");
                data.put("queryType", "profileData");
                data.put("data", uid);

                JSONArray cred = new JSONArray();
                cred.put(data);

                networkRequest.send("../cgi-bin/db-select.py", "POST", cred); // scripts should not be hard coded, create a structure and store all somewhere
                JSONArray response = networkRequest.getResponse();
                profileData = response;
                //System.out.println(response);
                System.out.flush();
                System.out.println("Attempting to get response here: " + response);
                if (response != null) {
                    for (int i = 0; i < response.length(); i++) {
                        if (response.getJSONObject(i).get("status").equals("ok")) {
                            System.out.println("Successfully received confirmation from server for getting profile data.");
                            //return true;
                        }
                    }
                }

                // request for photo
                networkRequest.response = new JSONArray();
                String email = response.getJSONObject(0).getString("email");
                data = new JSONObject();
                data.put("Users", "Users");
                data.put("queryType", "null");
                data.put("photo", email);
                cred = new JSONArray();
                cred.put(data);
                networkRequest.send("../cgi-bin/db-select.py", "POST", cred);
                System.out.println("Testing picture request");
                System.out.println(networkRequest.response);
                picture = networkRequest.response;

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

    private class Get_Rides extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                NetworkRequest networkRequest = new NetworkRequest("http://45.55.29.36/");

                JSONObject data = new JSONObject();
                data.put("Rides", "Rides");
                data.put("queryType", "allRides");
                data.put("data", uid);


                JSONArray cred = new JSONArray();
                cred.put(data);

                networkRequest.send("../cgi-bin/db-select.py", "POST", cred); // scripts should not be hard coded, create a structure and store all somewhere
                JSONArray response = networkRequest.getResponse();
                send_over = response;
                System.out.println("Response in get_rides home activity async task: " + response);
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

    public class valid_driver extends AsyncTask<String,Void,Boolean> {
        int driver_status;
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                NetworkRequest networkRequest = new NetworkRequest("http://45.55.29.36/");

                JSONObject data = new JSONObject();
                data.put("Driver_Check","Driver_Check");
                data.put("UID", uid);

                JSONArray cred = new JSONArray();
                cred.put(data);

                networkRequest.send("../cgi-bin/jverify.py", "POST", cred); // scripts should not be hard coded, create a structure and store all somewhere
                JSONArray response = networkRequest.getResponse();

                System.out.println("Response in valid_driver: " + response);
                if (response != null) {
                    for (int i = 0; i < response.length(); i++) {
                        if (response.getJSONObject(i).get("status").equals("ok")) {
                            System.out.println("Successfully received confirmation from server for driver status.");
                            //return true;
                        }
                        else{
                            System.out.println("WE HAVE AN ERROR IN VALID DRIVER");
                        }
                    }
                }

                driver_status = response.getJSONObject(0).getInt("driverStatus");



            } catch (Exception e) { // for now all exceptions will return false
                System.out.println("Debug in background task:\n" + e.getMessage());
                return false;
            }
            driver_flag = driver_status;
            synchronized (lock) {
                lock.notify();
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            myInstance.dismiss();
        }

    }

    public static class MySpinnerDialog extends DialogFragment {

        public MySpinnerDialog() {
            // use empty constructors. If something is needed use onCreate's
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {

            Dialog _dialog = new ProgressDialog(getActivity());
            this.setStyle(STYLE_NO_TITLE, getTheme()); // You can use styles or inflate a view
            _dialog.setCancelable(false);

            return _dialog;
        }
    }
    private class send_message extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                NetworkRequest networkRequest = new NetworkRequest("http://45.55.29.36/");

                JSONObject data = new JSONObject();
                data.put("Messages", "Messages");
                data.put("sentBy", uid);
                data.put("sentTo", rider_id);
                data.put("mtext", "My message");
                data.put("status", 0);
                data.put("type", "rider");
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





