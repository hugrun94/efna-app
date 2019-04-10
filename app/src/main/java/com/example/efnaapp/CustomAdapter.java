package com.example.efnaapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.efnaapp.ExerciseInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    ArrayList<String> exerciseId;
    Context context;
    String dude;

    public CustomAdapter(Context context, ArrayList<String> exerciseId) {
        this.context = context;
        this.exerciseId = exerciseId;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listi, parent, false);
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder

        return vh;
    }

    public String getDude() {
        return dude;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // set the data in items
        holder.exercise.setText(exerciseId.get(position));
        // implement setOnClickListener event on item view.

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
               // Toast.makeText(context, exerciseId.get(position), Toast.LENGTH_SHORT).show();
                dude = exerciseId.get(position);

                Intent startChemTestActivity = new Intent(context, RenderChemsAndArrowsActivity.class);
                startChemTestActivity.putExtra("dude", dude);
                ProblemActivity pa = (ProblemActivity)context;
                pa.initExercise(dude);
                context.startActivity(startChemTestActivity);
            }
        });

    }



    @Override
    public int getItemCount() {
        return exerciseId.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView exercise;// init the item view's

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            exercise = (TextView) itemView.findViewById(R.id.exercise);


        }
    }
}