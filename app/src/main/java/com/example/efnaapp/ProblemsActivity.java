package com.example.efnaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * Displays a list of all exercises of a given reaction type. The reaction type depends on the name
 * of the button.
 */
public class ProblemsActivity extends AppCompatActivity {

    private String[] exerciseList = new String[]{"floki", "egill", "hinrik", "egill", "hinrik", "egill", "hinrik", "egill", "hinrik"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problems);

        ListView listView = findViewById(R.id.coll1list);

        // Identify which button was pressed and store its name
        String exerciseType = getIntent().getStringExtra("Reaction type");

        // Shows all the exercises in a list on the screen

        /*
         * For all exercises in exerciseType: b
         *  exerciseList.add(exercises)
         */


        // Það eru strengir sem eru nöfnin á types og hafa exercise-in í lista.


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exerciseList); // TODO Change items to exerciseList
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO KALLA Á ÞAÐ SEM JOJO ER AÐ GERA
            }
        });
    }
}
