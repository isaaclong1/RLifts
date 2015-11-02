package ucr.cs180.rlifts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Intent;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.plus.Plus;

/**
 * A login screen that offers login via email/password.
 */

//written by Terry and Don.
public class RegisterActivity extends AppCompatActivity {
    public void onclick(View view) {
        Intent intent = new Intent(this, RiderOrDriverActivity.class);
        startActivity(intent);
    }
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    ImageView ivImage;

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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

        //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        //byte[] b = bytes.toByteArray();
        //String encodedimage = Base64.encodeToString(b, Base64.DEFAULT);
        //encoded_photo = encodedimage;

        ivImage = (ImageView) findViewById(R.id.imageButton);
        ivImage.setImageBitmap(bm);
    }
    private registerUser mAuthTask = null;

    private EditText mPasswordView;
    private EditText mBirthdayView;
    private EditText mPhoneNumView;
    private EditText mConfirm_PWView;
    private EditText mUsernameView;
    private EditText mFnameView;
    private EditText mLnameView;
    private EditText mEmailView;
    private String encoded_photo;
    private String image_path;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);

        TextView fnameview = (TextView) findViewById(R.id.firstname);
        TextView lnameview = (TextView) findViewById(R.id.lastname);
        TextView phoneview = (TextView) findViewById(R.id.Phone_num);
        TextView birthdayview = (TextView) findViewById(R.id.Birthday);
        TextView emailview = (TextView) findViewById(R.id.email);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("username");
            System.out.println(value);
        }
        Intent getIntent = getIntent();
        Bundle extrasBundle = getIntent.getExtras();
        try {


            boolean hasName = extrasBundle.containsKey("username");
            boolean hasBirthday = extrasBundle.containsKey("birthday");
            boolean hasEmail = extrasBundle.containsKey("email");
            if(hasName){
                String uname_from_intent = extrasBundle.getString("username"); //username is their first and last name
                String delim = " ";
                String[] tokens = uname_from_intent.toString().split(delim);

                fnameview.setText(tokens[0]);
                lnameview.setText(tokens[1]);
            }
            if(hasEmail){
                String email_from_intent = extrasBundle.getString("email");
                emailview.setText(email_from_intent);
            }
            if(hasBirthday){
                String birthday_from_intent = extrasBundle.getString("birthday");
                birthdayview.setText(birthday_from_intent);
            }
        } catch(NullPointerException n) {}

        mFnameView = ((EditText) findViewById(R.id.firstname));
        mLnameView = ((EditText) findViewById(R.id.lastname));
        mUsernameView = ((EditText) findViewById(R.id.Username));
        mBirthdayView= ((EditText) findViewById(R.id.Birthday));
        mPhoneNumView = ((EditText) findViewById(R.id.Phone_num));
        mPasswordView= ((EditText) findViewById(R.id.reg_password));
        mConfirm_PWView= ((EditText) findViewById(R.id.reg_confirm_password));
        mEmailView = ((EditText) findViewById(R.id.email));

        Button button = (Button) findViewById(R.id.register_button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        }); // can't press more than once.

        ImageButton pic_button = (ImageButton) findViewById(R.id.imageButton);
        pic_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void attemptRegister(){
        if(mAuthTask != null) {
            return;
        }

        mPasswordView.setError(null);
        mBirthdayView.setError(null);
        mPhoneNumView.setError(null);
        mConfirm_PWView.setError(null);
        mUsernameView.setError(null);
        mFnameView.setError(null);
        mLnameView.setError(null);
        mEmailView.setError(null);

        String password = mPasswordView.getText().toString();
        String birthday = mBirthdayView.getText().toString();
        String phone = mPhoneNumView.getText().toString();
        String confirm_pw = mConfirm_PWView.getText().toString();
        String username = mUsernameView.getText().toString();
        String fname = mFnameView.getText().toString();
        String lname = mLnameView.getText().toString();
        String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        /*

        if(!TextUtils.isEmpty(password) && !isPasswordValid(password))
        {
            mPasswordView.setError("This password is too short");
            focusView = mPasswordView;
            cancel = true;
        }

        if(!TextUtils.isEmpty(fname))
        {
            mFnameView.setError("This field is required");
            focusView = mFnameView;
            cancel = true;
        }
        if(!TextUtils.isEmpty(lname))
        {
            mLnameView.setError("This field is required");
            focusView = mLnameView;
            cancel = true;
        }
        if(!TextUtils.isEmpty(username))
        {
            mUsernameView.setError("This field is required");
            focusView = mUsernameView;
            cancel = true;
        }
        if(!TextUtils.isEmpty(birthday) && !isBirthdayValid(birthday))
        {
            mBirthdayView.setError("This birthday is incorrect");
            focusView = mBirthdayView;
            cancel = true;
        }
        if(!TextUtils.isEmpty(phone) && !isPhoneValid(phone))
        {
            mPhoneNumView.setError("This field is required");
            focusView = mPhoneNumView;
            cancel = true;
        }
        if(cancel)
        {
            focusView.requestFocus();

        }
        else { */
        mAuthTask = new registerUser(fname, lname, birthday, phone, confirm_pw, password, username, email);
        mAuthTask.execute((Void) null);
    }

    private boolean isPasswordValid(String password){
        return password.length() > 4;
    }
    private boolean isBirthdayValid(String birthday){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);

        try{

            Date date = sdf.parse(birthday);
        } catch(ParseException e) {
            return false;
        }
        return true;
    }
    private boolean isPhoneValid(String phone){
        return phone.length() > 4;
    }
    public class registerUser extends AsyncTask<Void, Void, Boolean> {

        private final String mFirst_name;
        private final String mLast_name;
        private final String mBirthday;
        private final String mphone_num;
        private final String mpassword;
        private final String mconfirm_password;
        private final String musername;
        private final String mEmail;

        registerUser(String first_name, String last_name, String Birthday, String phone_num, String password, String confirm_password, String username, String email){
            mFirst_name = first_name;
            mLast_name = last_name;
            mBirthday = Birthday;
            mphone_num = phone_num;
            mpassword = password;
            mconfirm_password = confirm_password;
            musername = username;
            mEmail = email;
        }

        protected Boolean doInBackground(Void... params) {
            try {
                NetworkRequest networkRequest = new NetworkRequest("http://45.55.29.36/");

                JSONObject data = new JSONObject();
                data.put("Users", "Users");
                data.put("first_name", mFirst_name);
                data.put("last_name", mLast_name);
                data.put("uname", musername);
                data.put("birthday", mBirthday);
                data.put("phone_num", mphone_num);
                data.put("password", mpassword);
                data.put("email", mEmail);
                data.put("photo", encoded_photo);

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

                            //error handling
                            //response.getJSONObject(i).get("status") error is here
                            //pop up the error message and allow register button to register again

                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("Debug in RegisterNewUser in background: \n" + e.getMessage());
                return false;
            }
            return true;
        }

    }

}
