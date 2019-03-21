package com.example.efnaapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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

/**
 * This class reads a config file and uses its contents to draw chemical compounds onto the screen.
 * @author Karen Ósk Pétursdóttir
 */
public class RenderFromFileActivity extends AppCompatActivity {

    // The width and height of the screen (assigned values in onCreate):
    int maxX, maxY;

    private Bitmap mChemBitmap;
    //private Bitmap mArrowBitmap; // ASDF don't think we need a new bitmap for the arrows

    // ArrayList that holds string values for components to be drawn along with their coordinates.
    ArrayList<String[]> componentsToDraw;

    // Need new class for:
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
    @SuppressLint("ClickableViewAccessibility")
    private void drawCompoundsFromCoordinates(ArrayList<String[]> itemsToDraw) {

        MyCanvas myCanvas = new MyCanvas(getApplicationContext());

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        addContentView(myCanvas, layoutParams);

        myCanvas.setOnTouchListener(myCanvas);
        myCanvas.setDrawingCacheEnabled(true);
        mChemBitmap = myCanvas.getDrawingCache();
        //mArrowBitmap = myCanvas.getDrawingCache();
        //mChemBitmap.recycle();// ASDF
        //mArrowBitmap.recycle();
    }

    /**
     * This class has the drawing logic of all chemical compounds and user-drawn arrows(/paths).
     */
    private class MyCanvas extends View implements View.OnTouchListener {

        private float x, y;
        private ArrayList<Point> points = new ArrayList<>();

        public MyCanvas(Context context){
            super(context);
        }

        @Override
        public void draw(Canvas canvas){
            super.draw(canvas);

            //mArrowBitmap = Bitmap.createBitmap(maxX, maxY, Bitmap.Config.ARGB_8888);

            Paint background = new Paint();
            background.setColor(Color.parseColor("#335599")); // Background colour.
            background.setStyle(Paint.Style.FILL);

            Paint symbol = new Paint();
            symbol.setColor(Color.parseColor("#EEEE66")); // Colour of chemical compounds.
            symbol.setTextSize((int)(maxX*(100.0/1920.0)));         // Text size is in proportion with
            symbol.setStyle(Paint.Style.FILL);                      // device window size.
            symbol.setStrokeWidth(1);

            canvas.drawPaint(background);
            for(String[] f : componentsToDraw){
                canvas.drawText(f[0], Integer.parseInt(f[1]), Integer.parseInt(f[2]), symbol);
            }

            Paint arrows = new Paint();
            arrows.setColor(Color.parseColor("#DD5599"));  // Colour of user-drawn arrows(/lines)
            arrows.setStrokeWidth(25);
            symbol.setStyle(Paint.Style.STROKE);

            /*
            if(mArrowBitmap != null) {
                mArrowBitmap.setPixel((int) x, (int) y, arrows.getColor());
            }*/

            if(mChemBitmap != null) {
                mChemBitmap.setPixel((int)x, (int)y, arrows.getColor());

                Path path = new Path();
                boolean first = true;

                if(points != null) {
                    for (Point point : points) {
                        if (first) {
                            first = false;
                            path.moveTo(point.x, point.y);
                        } else {
                            path.lineTo(point.x, point.y);
                        }
                    }
                }
                canvas.drawPath(path, arrows);
                //canvas.drawBitmap(mArrowBitmap, x, y, arrows); // Var commentað út
                //canvas.drawBitmap(mChemBitmap, x, y, arrows);
                //canvas.drawCircle(x, y, 7, arrows);
            }
        }

        /*
         * This method catches what the user draws on the screen and stores it in the ArrayList 'points'.
         */
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            if(event.getX() < 0){
                x = 0;
            }
            else {
                x = event.getX();
            }

            if(event.getY() < 0){
                y = 0;
            }
            else {
                y = event.getY();
            }

            Point newPoint = new Point((int)x,(int)y);
            if(points != null) {
                points.add(newPoint);

                for (Point p : points) {
                    System.out.println("------------------------------------------");
                    System.out.println("------------------PUNKTUR:------------------");
                    System.out.println(p);
                }
            }
            else {
                System.out.println("------------------------------------------");
                System.out.println("------------------points ER NULL :( ------------------");
            }

            //mArrowBitmap = view.getDrawingCache();
            mChemBitmap = view.getDrawingCache();
            view.invalidate();
            return true;
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
