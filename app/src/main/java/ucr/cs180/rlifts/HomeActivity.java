package ucr.cs180.rlifts;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProfileFragment.OnFragmentInteractionListener,
        RiderFragment.OnFragmentInteractionListener, DriverFragment.OnFragmentInteractionListener,
        Tutorial.OnFragmentInteractionListener, PaymentListFragment.OnFragmentInteractionListener {

    private EditText StartView;
    private EditText DestinationView;
    private EditText DepartureView;
    private static String uid;
    private JSONArray send_over;
    private static boolean flag = false;
    private static boolean run_showalert = true;
    private static String m_id;
    private static String global_status;
    private JSONArray profileData;
    private JSONArray picture;
    private static int driver_flag;
    final Handler ha = new Handler();
    Runnable messageRunnable = new Runnable() {
        @Override
        public void run() {
            if(driver_flag == 1){
                new Get_Driver_Message().execute();
            }

            if (flag == true && driver_flag == 1) {
                showAlert();
                flag = false;
            }

            ha.postDelayed(this,1000);
        }
    };
    Handler mHandler;

    public void post_ride_click(View view) throws IOException {
        StartView = (EditText) findViewById(R.id.start);
        DestinationView = (EditText) findViewById(R.id.destination);
        DepartureView = (EditText) findViewById(R.id.leaveTime);
        String start = StartView.getText().toString();
        String dest = DestinationView.getText().toString();
        GoogleDistanceRequest gdr = new GoogleDistanceRequest();

        boolean flag = gdr.makeConnection(start, dest, uid);
        if (flag) {
            Toast.makeText(getApplicationContext(),
                    "Ride posted!", Toast.LENGTH_LONG).show();
            showAlertforDriver();
            StartView.setText("");
            DestinationView.setText("");
            DepartureView.setText("");

        } else {
            Toast.makeText(getApplicationContext(),"Invalid Address!", Toast.LENGTH_LONG).show();
        }

        new valid_driver().execute();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {}

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

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        //drawer.openDrawer(Gravity.LEFT);
        toggle.syncState();

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
        builder.setMessage("Please wait for someone to select your ride").create();
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) // yes buton
            {
                dialog.dismiss();
            }
        });
        builder.setTitle ("NOTIFICATION");
        builder.show();
    }

    public void showAlert ()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("YOUR ROUTE HAS BEEN SELECTED, DO YOU WANT TO TAKE THIS RIDE?").create();
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) // yes buton
            {
                //run_showalert = false;
                //new set_alert().execute();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) // no button
            {
                //run_showalert = false;
                //new set_alert().execute();
                dialog.dismiss();
            }
        });
        builder.setTitle ("NOTIFICATION");
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
        RiderFragment dontKnowWhatThisIsFor = null;

        if (id == R.id.nav_profile) {
            System.out.println("handling the profile view!");
            // TODO: get the photo from local file and pass it through as encoded string
            fragment = ProfileFragment.newInstance("string1", "string2", profileData, picture);
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            getSupportActionBar().setTitle("Profile");

        } else if (id == R.id.nav_rider) {
            System.out.println("handling the rider view!");
            //fragment = RiderFragment.newInstance("string1", "string2", send_over, uid);
            dontKnowWhatThisIsFor = RiderFragment.newInstance("string1", "string2", send_over, uid);
            // Insert the fragment by replacing any existing fragment
            new Get_Rides().execute();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, dontKnowWhatThisIsFor).commit();
            new Get_Rides().execute();
            getSupportActionBar().setTitle("Rider");


        } else if (id == R.id.nav_driver) {
            System.out.println("handling the driver view!");
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
            startActivity(intent);

        } else if(id == R.id.nav_payment) {
            System.out.println("handling payment fragment view!");
            fragment = PaymentListFragment.newInstance("string1", "string2");
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            getSupportActionBar().setTitle("Add Tokens");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                JSONArray rideList = response.getJSONObject(0).getJSONArray("messages");
                for(int i = 0; i < rideList.length(); i++){
                    JSONObject ride = rideList.getJSONObject(i);
                    message_id = ride.getString("MID");
                    status = ride.getString("status");
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
                        System.out.println("Successfully received confirmation from server for getting rides.");
                        //return true;
                        System.out.println("OVER HERE");
                        flag = true;
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

    private class valid_driver extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
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

                int driver_status = response.getJSONObject(0).getInt("driverStatus");
                driver_flag = driver_status;


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

    }
}



