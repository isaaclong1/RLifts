package ucr.cs180.rlifts;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.Random;

public class DropOffRider extends AppCompatActivity {

    private static String uid;
    private static String rider_id;
    private static String random;
    private static EditText prompt;
    String drivers_response;
    public void onClick(View view){
        Toast.makeText(getApplicationContext(),
                "Ride complete. Tokens have been transferred to your account.", Toast.LENGTH_LONG).show();
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_off_rider);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ride completion");
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); */


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            uid = extras.getString("UID");
            rider_id = extras.getString("RID");
            random = extras.getString("Random");

        }
        //boolean result;
        prompt = (EditText) findViewById(R.id.prompt_string);
        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("PROMPT1 :" + prompt +" Random1:"  + random);
                boolean result = check_string(prompt.getText().toString());
                if(result) {
                    Toast.makeText(getApplicationContext(),
                            "Ride complete. Tokens have been transferred to your account.", Toast.LENGTH_LONG).show();
                    new updateDriverTokens().execute();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Codes don't match", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public boolean check_string(String prompt){
        System.out.println("PROMPT :" + prompt +" Random:"  + random);
        if(prompt.equals(random)){
            return true;
        }
        else{ return false;}
    }


    private class updateDriverTokens extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                NetworkRequest networkRequest = new NetworkRequest("http://45.55.29.36/");

                JSONObject data = new JSONObject();
                data.put("Users", "Users");
                data.put("queryType", "addTokens");
                data.put("UID", uid);
                data.put("token", 10.0);

                JSONArray cred = new JSONArray();
                cred.put(data);
                System.out.println(data);

                networkRequest.send("../cgi-bin/db-modify.py", "POST", cred); // scripts should not be hard coded, create a structure and store all somewhere
                JSONArray response = networkRequest.getResponse();
                System.out.println("Response in updateTokens home activity async task: " + response);
                if (response != null) {
                    for (int i = 0; i < response.length(); i++) {
                        if (response.getJSONObject(i).get("status").equals("ok")) {
                            System.out.println("Successfully received confirmation from server for token update.");
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
