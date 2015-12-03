package ucr.cs180.rlifts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * A login screen that offers login via email/password.
 */
public class editprofile extends AppCompatActivity {
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    ImageView ivImage;

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(editprofile.this);
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

        ivImage = (ImageView) findViewById(R.id.imageButton2);
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

        ivImage = (ImageView) findViewById(R.id.imageButton2);
        ivImage.setImageBitmap(bm);
    }
    //private registerUser mAuthTask = null;

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
    private String Facebook_ID_from_intent;

    //TextView lnameview = (TextView) findViewById(R.id.lastname2);
    //TextView phoneview = (TextView) findViewById(R.id.Phone_num2);
    //TextView birthdayview = (TextView) findViewById(R.id.Birthday2);
    //TextView emailview = (TextView) findViewById(R.id.email2);
    public static Vector<String> parseResponse(JSONArray response) throws JSONException {
        System.out.println("Parsing the profile data: ");
        System.out.println(response);
        // parse the contents of response
        // get the view items you need from R and set the strings accordingly

        JSONObject data = response.getJSONObject(0);
        if(!data.getString("status").equals("ok")) {
            System.out.println("Error in profile data post request or db error");
            return null;
        }

        Vector<String> dataStrings = new Vector<>();
        dataStrings.add(data.getString("uname"));
        dataStrings.add(data.getString("email"));
        dataStrings.add(data.getString("nickname"));
        dataStrings.add(data.getString("birthday"));
        dataStrings.add(data.getString("phone_num"));
        dataStrings.add(data.getString("age"));

        System.out.println(dataStrings);
        return dataStrings;

    }

    public static String parsePicture(JSONArray picture) throws JSONException {
        return picture.getJSONObject(0).getString("photo");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        Bundle extras = getIntent().getExtras();

        Intent getIntent = getIntent();
        Bundle extrasBundle = getIntent.getExtras();



       mFnameView = ((EditText) findViewById(R.id.newfirstname));
        mLnameView = ((EditText) findViewById(R.id.lastname2));
        mUsernameView = ((EditText) findViewById(R.id.Username2));
        mBirthdayView= ((EditText) findViewById(R.id.Birthday2));
        mPhoneNumView = ((EditText) findViewById(R.id.Phone_num2));
        mPasswordView= ((EditText) findViewById(R.id.reg_password2));
        mConfirm_PWView= ((EditText) findViewById(R.id.reg_confirm_password2));
        mEmailView = ((EditText) findViewById(R.id.email2));


        mFnameView.setText(extras.getString("first_name"));
        mLnameView.setText(extras.getString("last_name"));
        mBirthdayView.setText(extras.getString("birthday"));
        mPhoneNumView.setText(extras.getString("phonenumber"));
        mEmailView.setText(extras.getString("email"));

        Button button = (Button) findViewById(R.id.register_button2);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                attemptRegister();
            }
        }); // can't press more than once.

//        ImageButton pic_button = (ImageButton) findViewById(R.id.imageButton2);
//        pic_button.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectImage();
//            }
//        });
    }

    private void attemptRegister(){
        mFnameView = ((EditText) findViewById(R.id.newfirstname));
        Toast.makeText(getApplicationContext(),
                mFnameView.getText(), Toast.LENGTH_SHORT).show();
        finish();


    }
}

