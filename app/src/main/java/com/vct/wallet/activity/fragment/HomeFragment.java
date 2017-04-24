package com.vct.wallet.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vct.wallet.R;
import com.vct.wallet.helper.SQLiteHandler;

import java.util.HashMap;

public class HomeFragment extends Fragment {

   // NOTE: Removed Some unwanted Boiler Plate Codes
    private OnFragmentInteractionListener mListener;

    public HomeFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View theView= inflater.inflate(R.layout.fragment_home, container, false);

        // NOTE : We are calling the onFragmentInteraction() declared in the MainActivity
        // ie we are sending "Fragment 1" as title parameter when fragment1 is activated
        if (mListener != null) {
            mListener.onFragmentInteraction("Dashboard");
        }

        TextView txtBalances = (TextView) theView.findViewById(R.id.balanceText);
        SQLiteHandler db = new SQLiteHandler(getActivity().getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> users = db.getUserDetails();

        String balances = "Rp " + users.get("balance");

        // Displaying the user details on the screen
        txtBalances.setText(balances);

        // Here we will can create click listners etc for all the gui elements on the fragment.
        // For eg: Button btn1= (Button) view.findViewById(R.id.frag1_btn1);
        // btn1.setOnclickListener(...

        return theView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            // NOTE: This is the part that usually gives you the error
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
       // NOTE : We changed the Uri to String.
        void onFragmentInteraction(String title);
    }
}
