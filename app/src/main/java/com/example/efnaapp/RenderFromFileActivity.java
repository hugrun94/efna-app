package com.example.efnaapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
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
 * The class assumes that all components (i.e. atoms, bonds, electron pairs, hydrogen atoms, any plus-
 * signs between chemicals and arrows that point from reacting chemicals to product chemicals) have
 * been assigned correct coordinates.
 * @author Karen Ósk Pétursdóttir
 */
public class RenderFromFileActivity extends AppCompatActivity {

    // The width and height of the screen (assigned values in onCreate):
    int maxX, maxY;

    private Bitmap mChemBitmap;

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
    }

    /**
     * This class has the drawing logic of all chemical compounds and user-drawn arrows(/paths).
     */
    private class MyCanvas extends View implements View.OnTouchListener {

        private float x, y;
        private Path arrow = new Path();
        private ArrayList<PointF> pointsInPath = new ArrayList<>();
        private ArrayList<Path> arrows = new ArrayList<>();
        private ArrayList<Path> arrowHeads = new ArrayList<>();

        public MyCanvas(Context context){
            super(context);
        }

        /**
         * Calculates the angle between two points in radians.
         */
        private double getAngle(PointF point1, PointF point2){
            double dx = point1.x - point2.x;
            double dy = point2.y - point1.y;  // Putting "negative" value on the y to account for
                                              // Android's coordinate system
            return Math.atan2(dy, dx);
        }

        /**
         * Determines the direction of the arrow head (left/right, down/up) depending on either the
         * x or y coordinates of two points.
         * @param num1 represents the last point in the path,
         * @param num2 represents some previous point in the same path
         */
        private int getDirection(float num1, float num2){
            int direction = 1;
            if(num2-num1 < 0){
                direction = -1;
            }
            return direction;
        }

        @Override
        public void draw(Canvas canvas){
            super.draw(canvas);

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

            Paint arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // This flag gives us smooth curves
            arrowPaint.setColor(Color.parseColor("#DD5599"));  // Colour of user-drawn arrows(/lines)
            arrowPaint.setStrokeWidth(10);
            arrowPaint.setStyle(Paint.Style.STROKE);

            Paint arrowHeadPaint = new Paint();
            arrowHeadPaint.setColor(Color.GREEN);
            arrowHeadPaint.setStrokeWidth(7);
            arrowHeadPaint.setStyle(Paint.Style.FILL);

            // Draws the arrows if the bitmap is not null
            if(mChemBitmap != null) {

                for (Path path : arrows) {
                    canvas.drawPath(path, arrowPaint);

                }

                for (Path path : arrowHeads) {
                    canvas.drawPath(path, arrowHeadPaint);
                }
            }
        }

        /*
         * This method catches and stores the arrows that the user draws on the screen.
         */
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            x = event.getX();
            y = event.getY();

            PointF endPoint = new PointF(0,0);

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    endPoint = new PointF();
                    arrow = new Path();
                    endPoint.x = x;
                    endPoint.y = y;

                    arrow.moveTo(endPoint.x,endPoint.y);
                    arrows.add(arrow);
                    break;

                case MotionEvent.ACTION_MOVE:
                    endPoint.x = x;
                    endPoint.y = y;
                    arrow.lineTo(x, y);
                    pointsInPath.add(new PointF(x,y));

                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    endPoint.x = x;
                    endPoint.y = y;

                    ////

                    if(pointsInPath.size() > 5) {

                        Path arrowhead = new Path();

                        // We use the last and 8th last points in the path to find the correct angle
                        // for the arrow head.

                        int last = pointsInPath.size() - 1;
                        PointF lastPointInPath = new PointF(pointsInPath.get(last).x, pointsInPath.get(last).y);
                        PointF somePreviousPoint = new PointF(pointsInPath.get(last-5).x, pointsInPath.get(last-5).y);

                        // The direction of the arrow head:
                        double angleLastSecondLast = getAngle(somePreviousPoint, lastPointInPath);

                        // Finding the correct placement for the base of the arrow:
                        int arrowHeadLength = 120;

                        float baseY = (float)Math.sin(angleLastSecondLast) * arrowHeadLength
                                * getDirection(lastPointInPath.y, somePreviousPoint.y);

                        float baseX = (float)Math.sqrt(arrowHeadLength * arrowHeadLength + baseY * baseY)
                                * getDirection(lastPointInPath.x, somePreviousPoint.x);

                        PointF arrowHeadBasePoint = new PointF(lastPointInPath.x + baseX, lastPointInPath.y + baseY);

                        System.out.println("------------------------------------------------------");
                        System.out.println("------------------------------------------------------");
                        System.out.println("------------------------" + angleLastSecondLast + "------------------------");

                        int headHalfWidth = 45;

                        // Below are the two extra points needed to make the arrowhead. These are
                        // on either side of the base of the arrow head, making a triangle.

                        float dx = (float)Math.sin(angleLastSecondLast) * headHalfWidth;
                        float dy = (float)Math.cos(angleLastSecondLast) * headHalfWidth;

                        PointF point1 = new PointF(arrowHeadBasePoint.x + dx, arrowHeadBasePoint.y - dy);
                        PointF point2 = new PointF(arrowHeadBasePoint.x - dx, arrowHeadBasePoint.y + dy);

                        // Drawing the head now that the points are known
                        arrowhead.moveTo(lastPointInPath.x, lastPointInPath.y);
                        arrowhead.lineTo(point1.x, point1.y);
                        arrowhead.lineTo(point2.x, point2.y);

                        arrowHeads.add(arrowhead);
                    }

                    ////

                    break;

                default:
                    break;
            }

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

        // Stores items and their coordinates from config file to be able to draw them in the right place
        componentsToDraw = configFileToCoordinates();

        drawCompoundsFromCoordinates(componentsToDraw);

        // drawPoint fyrir rafeindapör eða sér fall sem teiknar 2 filled circles?
        // gera readme eða einhverja skrá þar sem lýst er hvaða tákn eru notuð fyrir hvaða fyrirbæri?
        //      : eða .. fyrir rafeindapar, | eða -- fyrir efnatengi ef annað en hreinn texti.
        //      (kannski nota bara drawLine fyrir -- og jafnvel | líka? Gæti verið vesen)
    }
}
