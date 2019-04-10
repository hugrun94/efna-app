package com.example.efnaapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * This class reads a config file and uses its contents to draw chemical compounds onto the screen.
 * The class assumes that all components (i.e. atoms, bonds, electron pairs, hydrogen atoms, any plus-
 * signs between chemicals and arrows that point from reacting chemicals to product chemicals) have
 * been assigned correct coordinates.
 * @author Karen Ósk Pétursdóttir
 */
public class RenderChemsAndArrowsActivity extends AppCompatActivity {

    // The width and height of the screen (assigned values in onCreate):
    int maxX, maxY;

    // The bitmap used for everything to be rendered
    private Bitmap mChemBitmap;

    // The canvas used for drawing everything onto
    private MyCanvas myCanvas;

    // Holds all individual paths that the user draws on the screen
    private ArrayList<Path> arrows = new ArrayList<>();

    // Stores the points of the path
    private ArrayList<PointF> pointsInPath = new ArrayList<>();

    private ArrayList<PointF> firstLastInPaths = new ArrayList<>();

    // ArrayList that holds string values for components to be drawn along with their coordinates.
    ArrayList<String[]> componentsToDraw;

    // The exercise which the activity will be drawing, initialized in onCreate
    private Exercise exercise;

     public void goToMenu (View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void nextStep(View view){
        if (firstLastInPaths.isEmpty()) {
            return;
        }
        resolveArrows();
        getWindow().getDecorView().invalidate();
        firstLastInPaths.clear();
        arrows.clear();
        myCanvas.clearArrowheads();
    }

    public void prevStep(View view) {
         exercise.previousStep();
         componentsToDraw = exercise.getComponentsToDraw();
         getWindow().getDecorView().invalidate();
        firstLastInPaths.clear();
        arrows.clear();
        myCanvas.clearArrowheads();
    }

    public void finish(View view){
        //Intent intent = new Intent(this, TODO.class);
        // TODO finish exercise and see feedback
        // Tag the exercise as finished in this session
        //startActivity(intent);
    }

    public void startAgain(View view){
        Intent intent = new Intent(this, RenderChemsAndArrowsActivity.class);
        startActivity(intent);
    }

    /**
     * This method draws atoms, bonds e.t.c. onto the screen at their specified coordinates.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void drawCompoundsFromCoordinates(ArrayList<String[]> itemsToDraw) {

        myCanvas = new MyCanvas(this.getBaseContext());

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

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
        private ArrayList<Path> arrowHeads = new ArrayList<>();     // Holds the heads of all arrows (paths)

        public MyCanvas(Context context){
            super(context);
        }

        /**
         * Calculates the angle between two points in radians.
         */
        private double getAngle(PointF point1, PointF point2){
            double dx = point1.x - point2.x;
            double dy = point2.y - point1.y;  // Putting "negative" value on the y to account for
                                              // Android's coordinate system vs Cartesian coordinates
            return Math.atan2(dy, dx);
        }

        public void clearArrowheads() {
            arrowHeads.clear();
        }

        @Override
        public void draw(Canvas canvas){
            super.draw(canvas);

            Paint backgroundPaint = new Paint();
            backgroundPaint.setColor(Color.parseColor("#335599")); // EEEE66 was symbol colour
            backgroundPaint.setStyle(Paint.Style.FILL);                      // 335599 was background colour

            Paint symbolPaint = new Paint();
            symbolPaint.setColor(Color.parseColor("#335599")); // Colour of chemical compounds.
            symbolPaint.setTextSize((int)(maxX*(100.0/1920.0)));         // Text size is in proportion with
            symbolPaint.setStyle(Paint.Style.FILL);                      // device window size.
            symbolPaint.setStrokeWidth(1);

            // Drawing atoms, bonds and all other components onto the screen
            //Rect backgrRectangle = new Rect(0, 160, maxX, maxY);
            //canvas.drawRect(backgrRectangle, backgroundPaint);
            for(String[] f : componentsToDraw){
                // TODO Put more conditionals for double/triple bonds

                if(f.length == 5){
                    canvas.drawLine(Integer.parseInt(f[1]), Integer.parseInt(f[2]), Integer.parseInt(f[3]), Integer.parseInt(f[4]) , symbolPaint);
                }
                else{
                    canvas.drawText(f[0], Integer.parseInt(f[1]), Integer.parseInt(f[2]), symbolPaint);
                }
            }

            // Drawing the path that the user gives via dragging a finger across the screen
            Paint arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // This flag gives us smooth curves
            int arrowColor = Color.parseColor("#DD5599");
            arrowPaint.setColor(arrowColor);  // Colour of user-drawn arrows(/lines)
            arrowPaint.setStrokeWidth(6);
            arrowPaint.setStyle(Paint.Style.STROKE);

            Paint arrowHeadPaint = new Paint();
            arrowHeadPaint.setColor(arrowColor);
            arrowHeadPaint.setStrokeWidth(10);
            arrowHeadPaint.setStyle(Paint.Style.FILL);

            // Draws the arrows if the bitmap is not null.
            // First the arrow shafts, then the heads.
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

            view.setTop(160); // To stop the view from blocking buttons

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
                    firstLastInPaths.add(new PointF(x, y));

                    arrow.moveTo(endPoint.x, endPoint.y);
                    arrows.add(arrow);

                    break;

                case MotionEvent.ACTION_MOVE:

                    endPoint.x = x;
                    endPoint.y = y;
                    arrow.lineTo(x, y);
                    pointsInPath.add(new PointF(x, y));

                    invalidate();

                    break;

                // When the user lifts their finger, the path ends and a triangle
                // is constructed to make an arrowhead.
                    case MotionEvent.ACTION_UP:

                    endPoint.x = x;
                    endPoint.y = y;
                    firstLastInPaths.add(new PointF(x, y));

                    if (pointsInPath.size() > 3) {

                        Path arrowhead = new Path();

                        // We use the last and 3rd last points in the path to find the correct angle
                        // for the arrow head.

                        int last = pointsInPath.size() - 1;
                        PointF lastPointInPath = new PointF(pointsInPath.get(last).x, pointsInPath.get(last).y);
                        PointF somePreviousPoint = new PointF(pointsInPath.get(last - 2).x, pointsInPath.get(last - 2).y);

                        // The direction of the arrow head:
                        double angleLastSecondLast = getAngle(somePreviousPoint, lastPointInPath);

                        // Finding the correct placement for the base of the arrow:
                        int arrowHeadLength = 60;

                        float baseDx = arrowHeadLength * (float) Math.cos(angleLastSecondLast);
                        float baseDy = arrowHeadLength * (-1) * (float) Math.sin(angleLastSecondLast);

                        PointF arrowHeadBasePoint = new PointF(lastPointInPath.x + baseDx, lastPointInPath.y + baseDy);

                        int headHalfWidth = 35;

                        // Below are the two extra points needed to make the arrowhead. These are
                        // on either side of the base of the arrow head, making a triangle.

                        float dx = (float) Math.sin(angleLastSecondLast) * headHalfWidth;
                        float dy = (float) Math.cos(angleLastSecondLast) * headHalfWidth;

                        PointF point1 = new PointF(arrowHeadBasePoint.x + dx, arrowHeadBasePoint.y - dy);
                        PointF point2 = new PointF(arrowHeadBasePoint.x - dx, arrowHeadBasePoint.y + dy);

                        // Drawing the head now that the points are known
                        arrowhead.moveTo(lastPointInPath.x, lastPointInPath.y);
                        arrowhead.lineTo(point1.x, point1.y);
                        arrowhead.lineTo(point2.x, point2.y);

                        arrowHeads.add(arrowhead);
                    }
                    break;

                default:
                    break;
            }

            mChemBitmap = view.getDrawingCache();
            view.invalidate();
            return true;
        }
    }

    public void resolveArrows() {
        ArrayList<PointF> origins = new ArrayList<>();
        ArrayList<PointF> destinations = new ArrayList<>();
        for(int i = 0; i <= firstLastInPaths.size()-2; i += 2) {

            origins.add(new PointF(firstLastInPaths.get(i).x, firstLastInPaths.get(i).y));
            destinations.add(new PointF(firstLastInPaths.get(i+1).x, firstLastInPaths.get(i+1).y));

        }
        exercise.resolveSolutionStep(origins, destinations);
        componentsToDraw = exercise.getComponentsToDraw();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("-------------------BYRJA APPIÐ --------------------------------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        int id = R.id.chemicalsView;

        View drawableArea = findViewById(id);

        //drawableArea.setTop(160); // Does not work here for some reason - done in onTouch instead

        // Gets the size of the screen and its max x and y values.
        Display display = getWindowManager().getDefaultDisplay();
        Point mdispSize = new Point();
        display.getSize(mdispSize);

        maxX = mdispSize.x;
        maxY = mdispSize.y;

        // Stores items and their coordinates from config file to be able to draw them in the
        // right place
        exercise = ExerciseInfo.getExercise();

        componentsToDraw = exercise.getComponentsToDraw();

       drawCompoundsFromCoordinates(componentsToDraw);

        // gera readme eða einhverja skrá þar sem lýst er hvaða tákn eru notuð fyrir hvaða fyrirbæri?
        //      : eða .. fyrir rafeindapar, | eða -- fyrir efnatengi ef annað en hreinn texti.
        //      (kannski nota bara drawLine fyrir -- og jafnvel | líka? Gæti verið vesen)
    }
}
