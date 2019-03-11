package com.example.efnaapp;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderFromFileActivity extends AppCompatActivity {

    // The width and height of the screen (assigned values in onCreate):
    int maxX, maxY;

    //ArrayList<String[]> elementsToDraw;

    // Need a class/method that gets user tapping coordinates
    // Need a class/method that compares those to coordinates of chemicals


    /** This method reads a file which contains symbols (atoms, bonds or lone electron pairs, ...)
     *  along with their coordinates and returns an ArrayList of ArrayLists(?) with those pairings
     *  of symbol and coordinates.
     * @return
     */
    private ArrayList<String[]> readChemFile(){

        ArrayList<String> listOfLines = new ArrayList<>();
        ArrayList<String[]> elementsToDraw = new ArrayList<>();

        try{
            InputStream fstream = this.getResources().openRawResource(R.raw.datafile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));

            //ArrayList<String> listOfLines = new ArrayList<>();
            String line = bufReader.readLine();
            while (line != null) {
                listOfLines.add(line);
                line = bufReader.readLine();
            }

            bufReader.close();
            System.out.println("************************************Content of ArrayList no. 0:");
            System.out.println(listOfLines.get(0));

            // use string.split(" "); for any String called string.
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        //for(int i = 0; i < listOfLines.size(); i++){
        for(String s : listOfLines){
            String[] strArray = s.split(" ");
            elementsToDraw.add(strArray);
        }

        return elementsToDraw;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("-------------------BYRJA APPIÐ --------------------------------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render_from_file);

        // Gets the size of the screen and its max x and y values.
        Display display = getWindowManager().getDefaultDisplay();
        Point mdispSize = new Point();
        display.getSize(mdispSize);

        maxX = mdispSize.x;
        maxY = mdispSize.y;

        //System.out.println(maxX + " " + maxY);

        ArrayList<String[]> drawComponents = readChemFile();

        for(String[] f : drawComponents){
            for(String s : f){
                System.out.println(s);
            }
        }
    }
}
