package com.example.efnaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
    }

    public void goToMenu (View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void nextStep(View view){
        //Intent intent = new Intent(this, TODO.class);
        // TODO make the next step in the reaction appear
        //startActivity(intent);
    }

    public void prevStep(View view){
        //Intent intent = new Intent(this, TODO.class);
        // TODO make the next step in the reaction appear
        //startActivity(intent);
    }

    public void finish(View view){
        //Intent intent = new Intent(this, TODO.class);
        // TODO finish exercise and see feedback
        // Tag the exercise as finished in this session
        //startActivity(intent);
    }

    public void startAgain(View view){
        //Intent intent = new Intent(this, TODO.class);
        // TODO restart the exercise
        //startActivity(intent);
    }
}
