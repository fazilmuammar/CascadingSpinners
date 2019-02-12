package com.androidcodefinder.cascadingspinners;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerMobile,spinnerModel;
    Button buttonSubmit;
    ProgressDialog pDialog;
    private String mobileName;
    // array list for spinner adapter
    private ArrayList<Model> modelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerMobile= (Spinner)findViewById(R.id.spinnerMobile);
        spinnerModel=(Spinner)findViewById(R.id.spinnerModel);
        modelList = new ArrayList<Model>();

        spinnerMobile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Refresh Spinner
                modelList.clear();
                new GetModelFromServer().execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private class GetModelFromServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Fetching Data");
            pDialog.show();

        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... arg0) {
            mobileName = spinnerMobile.getSelectedItem().toString();
            Handler jsonParser = new Handler();
            String json = jsonParser.makeServiceCall("http://fazilmuammar007.com/spinner//get_model.php?mobile="+mobileName, Handler.GET);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray model = jsonObj
                                .getJSONArray("model");

                        for (int i = 0; i < model.length(); i++) {
                            JSONObject modObj = (JSONObject) model.get(i);
                            Model mod = new Model(modObj.getString("model"));
                            modelList.add(mod);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
                populateSpinnerModel();
        }
    }

    private void populateSpinnerModel() {
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < modelList.size(); i++) {
            lables.add(modelList.get(i).getModel());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerModel.setAdapter(spinnerAdapter);
    }
}
