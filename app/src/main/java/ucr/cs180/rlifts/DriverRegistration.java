package ucr.cs180.rlifts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Driver;

public class DriverRegistration extends AppCompatActivity {

    private EditText mcarbrandview;
    private EditText mcarmodelview;
    private EditText mcaryearview;
    private EditText mcarcolorview;
    private EditText mlicenseplateview;
    private EditText mdriverslicenseview;
    private EditText mstateview;
    private HomeActivity.MySpinnerDialog waitDialog;

    public boolean driver_status;

    private registerDriver mAuthTask = null;

    private DriverRegistration mActivity;

    private static String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);

        showAlert();

        TextView carbrandview = (TextView) findViewById(R.id.car_brand);
        TextView carmodelview = (TextView) findViewById(R.id.car_model);
        TextView caryearview = (TextView) findViewById(R.id.car_year);
        TextView carcolorview = (TextView) findViewById(R.id.car_color);
        TextView licenseplateview = (TextView) findViewById(R.id.license_plate);
        TextView driverslicenseview = (TextView) findViewById(R.id.drivers_license);
        TextView stateview = (TextView) findViewById(R.id.state);

        mcarbrandview = ((EditText) findViewById(R.id.car_brand));
        mcarmodelview = ((EditText) findViewById(R.id.car_model));
        mcaryearview = ((EditText) findViewById(R.id.car_year));
        mcarcolorview = ((EditText) findViewById(R.id.car_color));
        mlicenseplateview = ((EditText) findViewById(R.id.license_plate));
        mdriverslicenseview = ((EditText) findViewById(R.id.drivers_license));
        mstateview = ((EditText) findViewById(R.id.state));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("UID");
            uid = value;
        }


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
        mstateview.setError(null);

        String car_color = mcarcolorview.getText().toString();
        String car_model = mcarmodelview.getText().toString();
        String car_brand = mcarbrandview.getText().toString();
        String car_year = mcaryearview.getText().toString();
        String drivers_license = mdriverslicenseview.getText().toString();
        String license_plate = mlicenseplateview.getText().toString();
        String drive_state = mstateview.getText().toString();

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
        if(TextUtils.isEmpty(drive_state))
        {
            mstateview.setError("This field is required");
            focusView = mstateview;
            cancel = true;
        }
        if(cancel)
        {
            focusView.requestFocus();
        }
        else {
            mAuthTask = new registerDriver(car_brand, car_model, car_year, car_color, drivers_license, license_plate, drive_state, DriverRegistration.this);
            mAuthTask.execute((Void) null);
            waitDialog = new HomeActivity.MySpinnerDialog();
            String tag = "Waiting on registerDriver";
            waitDialog.show(getSupportFragmentManager(), tag);
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
        builder.setTitle("NOTIFICATION");
        builder.show();
    }

    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    ImageView ivImage;
    private String encoded_photo;
    private String image_path;

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverRegistration.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        //keeping photo locally
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //encoding photo and saving into a variable
        byte[] b = bytes.toByteArray();
        String encodedimage = Base64.encodeToString(b, Base64.DEFAULT);
        encoded_photo = encodedimage;

        ivImage = (ImageView) findViewById(R.id.imageButton);
        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);
        image_path = selectedImagePath;

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byte[] b = bytes.toByteArray();
        String encodedimage = Base64.encodeToString(b, Base64.DEFAULT);
        encoded_photo = encodedimage;

        ivImage = (ImageView) findViewById(R.id.imageButton);
        ivImage.setImageBitmap(bm);
    }

    public class registerDriver extends AsyncTask<Void, Void, Boolean> {

        private final String mcar_brand;
        private final String mcar_model;
        private final String mcar_year;
        private final String mcar_color;
        private final String mdrivers_license;
        private final String mlicense_plate;
        private final String mstate;



        registerDriver(String car_brand, String car_model, String car_year, String car_color, String drivers_license, String license_plate, String drive_state, DriverRegistration activity){
            mcar_brand = car_brand;
            mcar_model = car_model;
            mcar_year = car_year;
            mcar_color = car_color;
            mdrivers_license = drivers_license;
            mlicense_plate = license_plate;
            mstate = drive_state;
            mActivity = activity;
        }

        protected Boolean doInBackground(Void... params) {
            try {
                NetworkRequest networkRequest = new NetworkRequest("http://45.55.29.36/");

                JSONObject data = new JSONObject();
                data.put("Drivers", "");
                data.put("UID", uid);
                data.put("make", mcar_brand);
                data.put("model", mcar_model);
                data.put("year", mcar_year);
                data.put("color", mcar_color);
                data.put("drivers_license", mdrivers_license);
                data.put("license_plate", mlicense_plate);
                data.put("state", mstate);


                JSONArray cred = new JSONArray();
                cred.put(data);

                networkRequest.send("../cgi-bin/jadd.py", "POST", cred);
                JSONArray response = networkRequest.getResponse();

                if (response != null) {
                    for (int i = 0; i < response.length(); i++) {
                        System.out.println("doing check: ");
                        if (response.getJSONObject(i).get("status").equals("ok")) {
                            System.out.println("Successfully received confirmation from server for register driver.");
                            driver_status = true;
                            //return true;
                        }
                        else {
                            //we have an error
                            driver_status = false;

                        }
                    }
                }
                if(driver_status) {
                    JSONObject data2 = new JSONObject();
                    data2.put("DriverModeOn", "");
                    data2.put("UID", uid);

                    JSONArray cred2 = new JSONArray();
                    cred2.put(data2);
                    System.out.println("IN HERE YO" + uid);
                    networkRequest.send("../cgi-bin/jadd.py", "POST", cred2);
                    JSONArray response2 = networkRequest.getResponse();

                    if (response2 != null) {
                        for (int i = 0; i < response2.length(); i++) {
                            System.out.println("doing check: ");
                            if (response2.getJSONObject(i).get("status").equals("ok")) {
                                System.out.println("Successfully received confirmation from server for changing driver flag mode.");
                                return true;
                            } else {
                                // we have an error
                                driver_status = false;
                            }
                        }
                    }
                }


            } catch (Exception e) {
                System.out.println("Debug in RegisterDriver in background: \n" + e.getMessage());
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if(driver_status) {
                Toast.makeText(getApplicationContext(), "Driver registration complete!", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(),
                        "This car has already been registered! Try again.", Toast.LENGTH_LONG).show();
            }
            waitDialog.dismiss();
            finish();
        }
        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

}
