package com.example.efnaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import android.content.Context;


public class ProblemActivity extends AppCompatActivity {

    // ArrayList for person names, email Id's and mobile numbers
    ArrayList<String> exerciseId = new ArrayList<>();
    ArrayList<Boolean> finished = new ArrayList<>();
    ArrayList<String> reactantsArray = new ArrayList<>();
    ArrayList<String> productsArray = new ArrayList<>();
    int[] productslengtharray;
    int[] reactantslengtharray;
    RecyclerView recyclerView;
    String json;
    Exercise exercise;
    int exerciseType;


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

    public void initExercise(String dude) {
        // TODO: Change to reading from smiles and format txt files to JSON parsing
        //ProblemActivity ha = new ProblemActivity();
        //productsArray = ha.getProductsArray();
        //reactantsArray = ha.getReactantsArray();
        //reactantsLength = ha.getReactantslengtharray();
        //productsLength = ha.getProductslengtharray();
        int id = Integer.parseInt(dude);

        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        org.openscience.cdk.Reaction reaction = new Reaction();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int reactantNum = 0;
        int productNum = 0;
        for (int i = 0; i < id; i++) {
            reactantNum += reactantslengtharray[i];
            productNum += productslengtharray[i];
        }
        for (int j = 0; j < reactantslengtharray[id]; j++) {
            try {
                IAtomContainer mol = parser.parseSmiles(reactantsArray.get(j + reactantNum));
                reaction.addReactant(mol);
            } catch (InvalidSmilesException e) {
                e.printStackTrace();
            }
        }
        for (int j = 0; j < productslengtharray[id]; j++) {
            try {
                IAtomContainer mol = parser.parseSmiles(productsArray.get(j + productNum));
                reaction.addProduct(mol);
            } catch (InvalidSmilesException e) {
                e.printStackTrace();
            }
        }
        ExerciseInfo.setExercise(new Exercise(reaction, width, height));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);
        // get the reference of RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        exerciseType = getIntent().getExtras().getInt("type");
        createJSON();

    }

    public void createJSON() {

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONObject typeOfProblem = obj.getJSONObject("acid/base");
            switch (exerciseType) {
                case (R.id.button0):
                    typeOfProblem = obj.getJSONObject("acid/base");
                    break;
                case (R.id.button1):
                    typeOfProblem = obj.getJSONObject("addition");
                    break;
                case (R.id.button):
                    typeOfProblem = obj.getJSONObject("elimination");
                    break;
                case (R.id.button3):
                    typeOfProblem = obj.getJSONObject("substitution");
                    break;
                case (R.id.button4):
                    typeOfProblem = obj.getJSONObject("radical");
                    break;
                case (R.id.button5):
                    typeOfProblem = obj.getJSONObject("oxidation/reduction");
                    break;
            }


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
        CustomAdapter customAdapter = new CustomAdapter(this, exerciseId);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
    }



    public String loadJSONFromAsset() {
        try {
            InputStream is = getAssets().open("problems.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            System.out.print("");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
