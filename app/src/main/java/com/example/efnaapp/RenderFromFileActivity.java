package com.example.efnaapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

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

    // Bitmap is necessary for being able to draw on the screen
    private Bitmap mBitmap;
    private int x = 500;
    private int y = 500;

    // ArrayList that holds string values for components to be drawn along with their coordinates.
    ArrayList<String[]> componentsToDraw;

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


    /**
     * This method draws atoms, bonds e.t.c. onto the screen at their specified coordinates.
     */
    private void drawCompoundsFromCoordinates(ArrayList<String[]> itemsToDraw) {

        MyCanvas myCanvas = new MyCanvas(getApplicationContext());

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        addContentView(myCanvas, layoutParams);
    }

    /**
     * This class has the drawing logic of all drawn chemical compounds and user-drawn arrows.
     */
    private class MyCanvas extends View implements View.OnTouchListener {

        Bitmap bitmap;
        int x, y;

        public MyCanvas(Context context){
            super(context);
        }

        @Override
        public void draw(Canvas canvas){
            super.draw(canvas);

            Paint background = new Paint();
            background.setColor(Color.parseColor("#112288")); // Background colour

            background.setStyle(Paint.Style.FILL);
            Paint symbol = new Paint();
            symbol.setColor(Color.YELLOW);
            symbol.setTextSize(100);
            symbol.setStyle(Paint.Style.FILL);
            symbol.setStrokeWidth(1);

            canvas.drawPaint(background);
            for(String[] f : componentsToDraw){
                canvas.drawText(f[0], Integer.parseInt(f[1]), Integer.parseInt(f[2]), symbol);
            }
            /*

            Testing testing... delete this later asdf
            if(mBitmap != null) {
                mBitmap.setPixel(x, y, Color.YELLOW);
                canvas.drawBitmap(mBitmap, x, y, paint);
            }*/
        }

        /*
         * This method catches what the user draws on the screen and turns it into arrows that will
         * make chemical reactions happen.
         */
        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            // todo
            return false;
        }
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
        componentsToDraw = configFileToCoordinates();

        drawCompoundsFromCoordinates(componentsToDraw);

        // ASDF nota drawPath til að teikna örvarnar frá notanda inn á
        // drawPoint fyrir rafeindapör eða sér fall sem teiknar 2 filled circles?
        // gera readme eða einhverja skrá þar sem lýst er hvaða tákn eru notuð fyrir hvaða
        //      fyrirbæri; : eða .. fyrir rafeindapar, | eða -- fyrir efnatengi ef annað
        //      en hreinn texti.
        //      (kannski nota bara drawLine fyrir -- og jafnvel | líka? Gæti verið vesen)
    }
}
