package com.example.efnaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ProblemActivity extends AppCompatActivity {

    private String items[] = new String[]{"floki", "egill", "hinrik", "egill", "hinrik", "egill", "hinrik", "egill", "hinrik"};
    private String[] exerciseList = new String[1]; // TODO add the exercises of the correct type to this list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);

        ListView listView = findViewById(R.id.coll1list);

        // Get all the exercises of the type that is being shown:
        // For all exercises of type <what is being asked for, what the user has clicked>






        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items); // TODO Change items to exerciseList
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO KALLA Á ÞAÐ SEM JOJO ER AÐ GERA
            }
        });


    }

}
