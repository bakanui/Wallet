package com.vct.wallet.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.vct.wallet.R;
import com.vct.wallet.app.AppConfig;
import com.vct.wallet.app.AppController;
import com.vct.wallet.helper.SQLiteHandler;
import com.vct.wallet.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddEntryActivity extends AppCompatActivity {
    private static final String TAG = AddEntryActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    public String category;

    public String[] category_name  = {"Food","Other"};
    public String[] category_value  = {"1","2"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText inputDescription = (EditText) findViewById(R.id.saveDescription);
        final EditText inputAmount = (EditText) findViewById(R.id.saveAmount);
        final Button btnSaveTransaction = (Button) findViewById(R.id.btnSaveTransaction);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Register Button Click event
        btnSaveTransaction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                HashMap<String, String> user = db.getUserDetails();
                String uid = user.get("uid");
                String description = inputDescription.getText().toString().trim();
                String amount = inputAmount.getText().toString().trim();

                if (!uid.isEmpty() && !amount.isEmpty()) {
                    registerTransaction(uid, description, amount);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter thr details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void registerTransaction(final String uid2, final String description, final String amount) {
        // Tag used to cancel the request
        String tag_string_req = "req_regTrans";

        pDialog.setMessage("Adding Entry ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INSERT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid2 = jObj.getString("uid2");

                        JSONObject transaction = jObj.getJSONObject("transaction");
                        String description = transaction.getString("description");
                        String amount = transaction.getString("amount");
                        String created_at = transaction.getString("created_at");

                        // Inserting row in users table
                        db.addTransactions(description, amount, uid2, created_at);

                        Toast.makeText(getApplicationContext(), "Transaction successfully registered!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                AddEntryActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uid2);
                params.put("description", description);
                params.put("amount", amount);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
