package ucr.cs180.rlifts;

import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

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
    public void registerclick(View view) {
        Intent intent = new Intent(this, RiderOrDriverActivity.class);
        startActivity(intent);
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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("username");
            System.out.println(value);
        }

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

        //String gmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
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
                data.put("username", musername);
                data.put("birthday", mBirthday);
                data.put("phone_num", mphone_num);
                data.put("password", mpassword);
                data.put("email", mEmail);

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
