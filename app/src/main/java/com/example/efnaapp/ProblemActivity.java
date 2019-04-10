package com.example.efnaapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ProblemActivity extends AppCompatActivity {

    // ArrayList for person names, email Id's and mobile numbers
    ArrayList<String> exerciseId = new ArrayList<>();
    ArrayList<Boolean> finished = new ArrayList<>();
    ArrayList<String> reactantsArray = new ArrayList<>();
    ArrayList<String> productsArray = new ArrayList<>();
    int[] productslengtharray;
    int[] reactantslengtharray;


    public ArrayList<String> getExerciseId() {
        return exerciseId;
    }

    public ArrayList<String> getProductsArray() {
        return productsArray;
    }

    public ArrayList<String> getReactantsArray() {
        return reactantsArray;
    }

    public ArrayList<Boolean> getFinished() {
        return finished;
    }

    public int[] getProductslengtharray() {
        return productslengtharray;
    }

    public int[] getReactantslengtharray() {
        return reactantslengtharray;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);
        // get the reference of RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());

            JSONObject typeOfProblem = obj.getJSONObject("acid/base");
            JSONArray allProblemsOfType = typeOfProblem.getJSONArray("exercises");
            productslengtharray = new int[allProblemsOfType.length()];
            reactantslengtharray = new int[allProblemsOfType.length()];

            for (int i = 0; i < allProblemsOfType.length(); i++) {
                JSONObject exerciseDetail = allProblemsOfType.getJSONObject(i);
                exerciseId.add(exerciseDetail.getString("id"));
                finished.add(exerciseDetail.getBoolean("finsished"));
                JSONArray reactants = exerciseDetail.getJSONArray("reactants");
                for (int j = 0; j < reactants.length(); j++) {
                    String dudes = reactants.getString(j);
                    reactantsArray.add(dudes);
                }
                reactantslengtharray[i] = reactants.length();

                JSONArray products = exerciseDetail.getJSONArray("products");

                for (int j = 0; j < products.length(); j++) {
                    String dudes2 = products.getString(j);
                    productsArray.add(dudes2);
                    System.out.println(products.getString(j));
                }
                productslengtharray[i] = products.length();


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        CustomAdapter customAdapter = new CustomAdapter(ProblemActivity.this, exerciseId);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("problems.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
