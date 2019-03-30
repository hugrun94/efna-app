package com.example.efnaapp;


import android.graphics.Path;
import android.graphics.PointF;
import java.util.ArrayList;

/**
 * Finds the atoms at the bases and tips of arrows drawn by the user. Uses this information
 * to make the new molecules to be displayed on the screen when the user taps "next".
 */
public class ReactionStep {

    private RenderChemsAndArrows render = new RenderChemsAndArrows();

    //private ArrayList<Path> arrows =  render.getPaths();

    private ArrayList<PointF> firstLastPoints = render.getPathFirstsAndLasts();

    private void hallo() {

        for(int i = 0; i <= firstLastPoints.size()-2; i += 2) {

            PointF firstPoint = new PointF(firstLastPoints.get(i).x, firstLastPoints.get(i).y);
            PointF lastPoint = new PointF(firstLastPoints.get(i+1).x, firstLastPoints.get(i+1).y);


        }
    }

    public void haee(){
        hallo();
    }

    // vinna með exercise til að breyta efnum
    // atomcontainermanipulator getur farið yfir efni og breytt þeim rétt.
        // perceiveAtom....dót
    // geometryutil getclosestatom og bond


}
