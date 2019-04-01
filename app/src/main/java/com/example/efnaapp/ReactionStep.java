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

    // These ArrayLists store the source and destination points for all drawn arrows separately.
    private ArrayList<PointF> origins = new ArrayList<>();
    private ArrayList<PointF> destinations = new ArrayList<>();

    private void getOriginAndDestinationCoordinates() {

        for(int i = 0; i <= firstLastPoints.size()-2; i += 2) {

            origins.add(new PointF(firstLastPoints.get(i).x, firstLastPoints.get(i).y));
            destinations.add(new PointF(firstLastPoints.get(i+1).x, firstLastPoints.get(i+1).y));

        }
    }

    private void chemicalReaction(){
        // Gets the source and moves the bond/lone pair to a destination

        // If the origin is a bond, then delete that bond
        // If the origin is a lone pair, make a bond from it and decrease atom charge

        // If the destination is a bond, double it/triple it
        // If the destination is an atom, increase its charge
    }



    // vinna með exercise til að breyta efnum
    // atomcontainermanipulator getur farið yfir efni og breytt þeim rétt.
        // perceiveAtom....dót
    // geometryutil getclosestatom og bond
}
