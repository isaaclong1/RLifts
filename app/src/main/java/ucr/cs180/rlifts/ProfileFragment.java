package ucr.cs180.rlifts;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private JSONArray mProfileData;
    private String uname;
    private String email;
    private String nick;
    private String bday;
    private String phone;
    private String age;
    private String encodedPhoto;
    private String tokens;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param2 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2, JSONArray response, JSONArray picture) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        Vector<String> data = new Vector<>();
        try {
           data = parseResponse(response);
            args.putString("ARG_PIC", parsePicture(picture));
        } catch (Exception e) {
            System.out.println("Exception in parse profile response call" + e.getMessage());
        }

        System.out.println("Size of data: " + data.size());

        for(int i = 0; i < data.size(); i++) {
            System.out.println(data.get(i));
        }

        args.putString("ARG_UNAME", data.get(0));
        args.putString("ARG_EMAIL", data.get(1));
        args.putString("ARG_NICK", data.get(2));
        args.putString("ARG_BIRTHDAY", data.get(3));
        args.putString("ARG_PHONE", data.get(4));
        args.putString("ARG_AGE", data.get(5));
        args.putString("ARG_TOK", data.get(6));

        // somehow get response into args, and in on create do the display
        fragment.setArguments(args);
        return fragment;
    }

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
        dataStrings.add(data.getString("token"));

        System.out.println(dataStrings);
        return dataStrings;

    }

    public static String parsePicture(JSONArray picture) throws JSONException {
        return picture.getJSONObject(0).getString("photo");
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            uname = getArguments().getString("ARG_UNAME", "");
            email = getArguments().getString("ARG_EMAIL", "");
            nick = getArguments().getString("ARG_NICK", "");
            bday = getArguments().getString("ARG_BIRTHDAY", "");
            phone = getArguments().getString("ARG_PHONE", "");
            age = getArguments().getString("ARG_AGE", "");
            tokens = getArguments().getString("ARG_TOK", "");
            // TODO: set the global photo argument here
            encodedPhoto = getArguments().getString("ARG_PIC", "");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View base = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView unameText = (TextView)base.findViewById(R.id.uname);
        TextView emailText = (TextView)base.findViewById(R.id.email_prof);
        TextView nickText = (TextView)base.findViewById(R.id.nickname);
        TextView bdayText = (TextView)base.findViewById(R.id.birthday);
        TextView phoneText = (TextView)base.findViewById(R.id.phone_num);
        TextView ageText = (TextView)base.findViewById(R.id.age);
        TextView tokenText = (TextView)base.findViewById(R.id.tokens);
        unameText.setText(uname);
        emailText.setText(email);
        nickText.setText(nick);
        bdayText.setText(bday);
        phoneText.setText(phone);
        ageText.setText(age);
        tokenText.setText(tokens);

        // TODO: decode the image and display it here
        // follow the stack overflow: byte[] gets decode, and bitmap gets string, then bitmap goes
        // to imageView
        byte[] decodedString = Base64.decode(encodedPhoto, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ImageView image = (ImageView)base.findViewById(R.id.profile_picture);
        image.setImageBitmap(decodedByte);
        unameText.setText("Username: " + uname);
        emailText.setText("Email: " + email);
        nickText.setText("Nickname: " + nick);
        bdayText.setText("Birthday : " + bday);
        phoneText.setText("Phone Number: " + phone);
        ageText.setText("Age: " + age);
        tokenText.setText("Ride Tokens: " + tokens);

        return base;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionP(uri);
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
        public void onFragmentInteractionP(Uri uri);
    }

}


