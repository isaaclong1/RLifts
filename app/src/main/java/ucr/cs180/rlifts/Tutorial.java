package ucr.cs180.rlifts;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tutorial.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tutorial#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tutorial extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ListView lv;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tutorial.
     */
    // TODO: Rename and change types and number of parameters
    public static Tutorial newInstance(String param1, String param2) {
        Tutorial fragment = new Tutorial();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Tutorial() {
        // Required empty public constructor
    }

    @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
//        View base = inflater.inflate(R.layout.fragment_tutorial, container, false);
//        lv = (ListView) base.findViewById(R.id.listView2);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity().getApplicationContext(),
//                        "hello", Toast.LENGTH_SHORT).show();
//                switch (position)
//                {
//                    case 0:
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                "Profile", Toast.LENGTH_SHORT).show();
//                        break;
//                    case 1:
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                "Rider", Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case 2:
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                "Driver", Toast.LENGTH_SHORT).show();
//                        break;
//                    case 3:
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                "Add token", Toast.LENGTH_SHORT).show();
//                        break;
//                    case 4:
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                "Ride History", Toast.LENGTH_SHORT).show();
//                        break;
//                    case 5:
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                "Logout", Toast.LENGTH_SHORT).show();
//                        break;
//
//
//                    default:
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                "Wrong spot", Toast.LENGTH_SHORT).show();
//                        break;
//
//                }
//        }});




        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tutorial, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionF(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListenerjknjknjknjknjknjknjknjk");
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
        public void onFragmentInteractionF(Uri uri);
    }

}
