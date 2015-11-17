package ucr.cs180.rlifts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class DriverRegistration extends AppCompatActivity {

    private EditText mcarbrandview;
    private EditText mcarmodelview;
    private EditText mcaryearview;
    private EditText mcarcolorview;
    private EditText mlicenseplateview;
    private EditText mdriverslicenseview;

    private registerDriver mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        showAlert();

        TextView carbrandview = (TextView) findViewById(R.id.car_brand);
        TextView carmodelview = (TextView) findViewById(R.id.car_model);
        TextView caryearview = (TextView) findViewById(R.id.car_year);
        TextView carcolorview = (TextView) findViewById(R.id.car_color);
        TextView licenseplateview = (TextView) findViewById(R.id.license_plate);
        TextView driverslicenseview = (TextView) findViewById(R.id.drivers_license);

        mcarbrandview = ((EditText) findViewById(R.id.car_brand));
        mcarmodelview = ((EditText) findViewById(R.id.car_model));
        mcaryearview = ((EditText) findViewById(R.id.car_year));
        mcarcolorview = ((EditText) findViewById(R.id.car_color));
        mlicenseplateview = ((EditText) findViewById(R.id.license_plate));
        mdriverslicenseview = ((EditText) findViewById(R.id.drivers_license));



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button button = (Button) findViewById(R.id.register_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptDriverRegister();
            }
        });
    }

    private void attemptDriverRegister(){
        if(mAuthTask != null) {
            return;
        }

        mcarcolorview.setError(null);
        mcarmodelview.setError(null);
        mcarbrandview.setError(null);
        mcaryearview.setError(null);
        mdriverslicenseview.setError(null);
        mlicenseplateview.setError(null);

        String car_color = mcarcolorview.getText().toString();
        String car_model = mcarmodelview.getText().toString();
        String car_brand = mcarbrandview.getText().toString();
        String car_year = mcaryearview.getText().toString();
        String drivers_license = mdriverslicenseview.getText().toString();
        String license_plate = mlicenseplateview.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(car_color))
        {
            mcarcolorview.setError("This field is required");
            focusView = mcarcolorview;
            cancel = true;
        }
        if(TextUtils.isEmpty(car_model))
        {
            mcarmodelview.setError("This field is required");
            focusView = mcarmodelview;
            cancel = true;
        }
        if(TextUtils.isEmpty(car_year))
        {
            mcaryearview.setError("This field is required");
            focusView = mcaryearview;
            cancel = true;
        }
        if(TextUtils.isEmpty(drivers_license))
        {
            mdriverslicenseview.setError("This field is required");
            focusView = mdriverslicenseview;
            cancel = true;
        }
        if(TextUtils.isEmpty(license_plate))
        {
            mlicenseplateview.setError("This field is required");
            focusView = mlicenseplateview;
            cancel = true;
        }
        if(cancel)
        {
            focusView.requestFocus();
        }
        else {
            mAuthTask = new registerDriver(car_brand, car_model, car_year, car_color, drivers_license, license_plate);
            mAuthTask.execute((Void) null);
            Toast.makeText(getApplicationContext(),
                    "Driver registration complete!", Toast.LENGTH_LONG).show();
        }
    }

    public void showAlert ()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You are not a registered driver, please register to continue").create();
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) // yes buton
            {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) // no button
            {
                dialog.dismiss();
            }
        });
        builder.setTitle("NOTIFICATION");
        builder.show();
    }

    public class registerDriver extends AsyncTask<Void, Void, Boolean> {

        private final String mcar_brand;
        private final String mcar_model;
        private final String mcar_year;
        private final String mcar_color;
        private final String mdrivers_license;
        private final String mlicense_plate;

        private RegisterActivity mActivity;

        registerDriver(String car_brand, String car_model, String car_year, String car_color, String drivers_license, String license_plate){
            mcar_brand = car_brand;
            mcar_model = car_model;
            mcar_year = car_year;
            mcar_color = car_color;
            mdrivers_license = drivers_license;
            mlicense_plate = license_plate;
        }

        protected Boolean doInBackground(Void... params) {
            try {
                NetworkRequest networkRequest = new NetworkRequest("http://45.55.29.36/");

                JSONObject data = new JSONObject();


                JSONArray cred = new JSONArray();
                cred.put(data);
                System.out.println(cred);

                networkRequest.send("../cgi-bin/db-add.py", "POST", cred);
                JSONArray response = networkRequest.getResponse();

                if (response != null) {
                    for (int i = 0; i < response.length(); i++) {
                        System.out.println("doing check: ");
                        if (response.getJSONObject(i).get("status").equals("ok")) {
                            System.out.println("Successfully received confirmation from server for register user.");
                            return true;
                        }
                        else {

                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("Debug in RegisterNewUser in background: \n" + e.getMessage());
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

        }

    }

}
