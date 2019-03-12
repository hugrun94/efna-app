package com.example.efnaapp;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.graphics.Color.BLUE;


/**
 * This class reads a config file and uses its contents to draw chemical compounds onto the screen.
 * @author Karen Ósk Pétursdóttir
 */
public class RenderFromFileActivity extends AppCompatActivity {

    // The width and height of the screen (assigned values in onCreate):
    int maxX, maxY;

    // Need a class/method that:
    // CHECK - reads config file and stores coordinates of items to draw
    // goes through through this info and draws items in correct coordinates

    // New class for:
    // getting user tapping coordinates (class must implement onTouchListener)
    //      ("class <Name> extends Activity implements onTouchListener")
    // comparing those to coordinates of chemicals


    /** This method reads a file which contains symbols (atoms, bonds, lone electron pairs, ...)
     *  along with their coordinates.
     *  @return an ArrayList of String arrays where each array contains an item to be drawn along
     *  with its screen coordinates.
     */
    private ArrayList<String[]> configFileToCoordinates(){

        ArrayList<String> listOfLines = new ArrayList<>();
        ArrayList<String[]> elementsToDraw = new ArrayList<>();

        try{
            InputStream fstream = this.getResources().openRawResource(R.raw.datafile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));

            String line = bufReader.readLine();
            while (line != null) {
                listOfLines.add(line);
                line = bufReader.readLine();
            }

            bufReader.close();
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        for(String s : listOfLines){
            String[] strArray = s.split(" ");
            elementsToDraw.add(strArray);
        }

        return elementsToDraw;
    }


    /** This method draws atoms, bonds e.t.c. onto the screen at their specified coordinates.
     *  todo
     */
    private void drawCompoundsFromCoordinates(ArrayList<String[]> itemsToDraw) {

        for (String[] item : itemsToDraw) {
            //draw item[0]
            //at coordinates (item[1],item[2])
            System.out.println(item[0]);
        }

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLUE);
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

        // Stores items and their coordinates from config file to be able to draw them in the
        // right place
        ArrayList<String[]> drawComponents = configFileToCoordinates();

        /*
        for(String[] f : drawComponents){
            for(String s : f){
                System.out.println(s);
            }
        }*/

        drawCompoundsFromCoordinates(drawComponents);
    }
}
