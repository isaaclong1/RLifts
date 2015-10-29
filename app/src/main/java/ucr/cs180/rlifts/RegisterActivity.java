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

/**
 * A login screen that offers login via email/password.
 */

//written by Terry and Don.
public class RegisterActivity extends AppCompatActivity {
    String first_name_str;
    String last_name_str;
    String birthday_str;
    String phone_num_str;
    String password_str;
    String confirm_password_str;
    String username_str;

    private registerUser mAuthTask = null;
    /*
    private EditText mPasswordView;
    private EditText mBirthdayView;
    private EditText mPhoneNumView;
    private EditText mConfirm_PWView;
    private EditText mUsernameView;
    private EditText mFnameView;
    private EditText mLnameView;
     */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);

        Button button = (Button) findViewById(R.id.register_button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
    }

    public void onRegisterClick(View v) {
        if (v.getId() == R.id.email_sign_in_button) {
            EditText first_name = ((EditText) findViewById(R.id.firstname));
            EditText last_name = ((EditText) findViewById(R.id.lastname));
            EditText username = ((EditText) findViewById(R.id.Username));
            EditText birthday = ((EditText) findViewById(R.id.Birthday));
            EditText phone_num = ((EditText) findViewById(R.id.Phone_num));
            EditText password = ((EditText) findViewById(R.id.reg_password));
            EditText confirm_password = ((EditText) findViewById(R.id.reg_confirm_password));

            first_name_str = first_name.getText().toString();
            last_name_str = last_name.getText().toString();
            username_str = username.getText().toString();
            birthday_str = birthday.getText().toString();
            phone_num_str = phone_num.getText().toString();
            password_str = password.getText().toString();
            confirm_password_str = confirm_password.getText().toString();

            if (!password_str.equals(confirm_password_str)) {
                //popup msg
                Toast pass = Toast.makeText(RegisterActivity.this, "Passwords don't match", Toast.LENGTH_SHORT);
                pass.show();
            }
        }
    }
    private void attemptRegister(){
        if(mAuthTask != null) {
            return;
        }

        /*
        String password = mPasswordView.getText().toString();
        String birthday = mBirthdayView.getText().toString();
        String phone = mPhoneNumView.getText().toString();
        String confirm_pw = mConfirm_PWView.getText().toString();
        String username = mUsernameView.getText().toString();
        String fname = mFnameView.getText().toString();
        String lname = mLnameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

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
        mAuthTask = new registerUser(first_name_str, last_name_str, birthday_str, phone_num_str, confirm_password_str, password_str, username_str);
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

        registerUser(String first_name, String last_name, String Birthday, String phone_num, String password, String confirm_password, String username){
            mFirst_name = first_name;
            mLast_name = last_name;
            mBirthday = Birthday;
            mphone_num = phone_num;
            mpassword = password;
            mconfirm_password = confirm_password;
            musername = username;
        }

        protected Boolean doInBackground(Void... params) {
            try {
                NetworkRequest networkRequest = new NetworkRequest("http://45.55.29.36/");

                JSONObject data = new JSONObject();
                data.put("Users", "");
                data.put("first_name", mFirst_name);
                data.put("last_name", mLast_name);
                data.put("username", musername);
                data.put("birthday", mBirthday);
                data.put("phone_num", mphone_num);
                data.put("password", mpassword);

                JSONArray cred = new JSONArray();
                cred.put(data);

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
