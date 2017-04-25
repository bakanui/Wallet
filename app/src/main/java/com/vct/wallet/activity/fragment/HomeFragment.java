package com.vct.wallet.activity.fragment;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.vct.wallet.R;
import com.vct.wallet.helper.SQLiteHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment{

   // NOTE: Removed Some unwanted Boiler Plate Codes
    private OnFragmentInteractionListener mListener;

    public HomeFragment() {}
    Context context;

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

        SQLiteHandler db = new SQLiteHandler(getActivity().getApplicationContext());

        TextView txtBalances = (TextView) theView.findViewById(R.id.balanceText);
        ListView transactionList=(ListView) theView.findViewById(R.id.transactionList);

        // Fetching user details from SQLite
        HashMap<String, String> users = db.getUserDetails();
        String getUid = users.get("uid");

        ArrayList<String> results = db.getRecentTransactions(getUid);

        // Fetching user details from SQLite
        HashMap<String, String> transactions = db.getTransactionDetails();

        transactionList.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, results));
        transactionList.setTextFilterEnabled(true);

// To handle the click on List View Item
transactionList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3)
{
TextView textViewSMSSender=(TextView)v.findViewById(R.id.textViewSMSSender);
TextView textViewSMSBody=(TextView)v.findViewById(R.id.textViewMessageBody);
String smsSender=textViewSMSSender.getText().toString();
String smsBody=textViewSMSBody.getText().toString();

// Show The Dialog with Selected SMS
AlertDialog dialog = new AlertDialog.Builder(context).create();
dialog.setTitle("SMS From : "+smsSender);
dialog.setIcon(android.R.drawable.ic_dialog_info);
dialog.setMessage(smsBody);
dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int which)
{

dialog.dismiss();
return;
}
});
dialog.show();
}

});


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
