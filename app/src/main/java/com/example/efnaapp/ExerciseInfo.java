package com.example.efnaapp;

import android.content.Context;

import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.geometry.GeometryUtil;
import javax.vecmath.Point2d;

import java.io.*;
import java.util.ArrayList;
import java.lang.Iterable;

/**
 * This class reads in all exercises and contains the necessary info regarding each exercise
 * @author Ragnar Pálsson
 */

class ExerciseInfo {

    // List of exercises, stored as CDK Reactions which can store molecules as reactants (left side of a reaction)
    // and products (right side of a reaction)
    private ArrayList<Reaction> exerciseList = new ArrayList<>();

    // Constructor which reads in all molecules from the exercises.smiles resource file and stores them according to
    // the format given in exercise_format.
    ExerciseInfo(Context ctxt) {
        ArrayList<String> listOfLines = new ArrayList<>();
        try{
            InputStream fstream = ctxt.getResources().openRawResource(R.raw.exercise_format);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));
            String line = bufReader.readLine();
            while (line != null) {
                listOfLines.add(line);
                line = bufReader.readLine();
            }
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        // Change formatting info to int array for easier use
        // The formatting info is one line per exercise, with two integers separated by a whitespace,
        // the first representing the number of reactants, and the second the number of products
        int[][] smilesReaderInfo = new int[listOfLines.size()][2];
        for (int i = 0; i < listOfLines.size(); i++){
            String s = listOfLines.get(i);
            smilesReaderInfo[i][0] = Integer.parseInt(s.split(" ")[0]);
            smilesReaderInfo[i][1] = Integer.parseInt(s.split(" ")[1]);
        }

        // Read in and store the exercises according the the formatting info
        // Also manipulate all molecules to contain the necessary information (valency, etc.)
        InputStream fstream = ctxt.getResources().openRawResource(R.raw.exercises);
        IteratingSMILESReader reader = new IteratingSMILESReader(fstream, DefaultChemObjectBuilder.getInstance());

        Reaction exercise = new Reaction();
        int index = 0;
        int reactNum = 0;
        int prodNum = 0;
        try {
            while (reader.hasNext()) {
                if (reactNum < smilesReaderInfo[index][0]) {
                    IAtomContainer mol = reader.next();
                    AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
                    AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
                    exercise.addReactant(mol);
                    reactNum++;
                } else if (prodNum < smilesReaderInfo[index][1]) {
                    IAtomContainer mol = reader.next();
                    AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
                    exercise.addProduct(mol);
                    prodNum++;
                } else {
                    exerciseList.add(exercise);
                    exercise = new Reaction();
                    index++;
                    reactNum = 0;
                    prodNum = 0;
                } if (!reader.hasNext()) {
                    exerciseList.add(exercise);
                }
            }
        }
        catch (CDKException e) {
            e.printStackTrace();
        }
    }

    ArrayList<String[]> getExercise(int id) {
        ArrayList<String[]> componentsToDraw = new ArrayList<String[]>();
        Iterable<IAtomContainer> reactants = exerciseList.get(id).getReactants().atomContainers();
        Iterable<IAtomContainer> products = exerciseList.get(id).getProducts().atomContainers();
        // Class which gives atoms coordinates
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        try {
            double factor = 80;
            double[] minmax;
            double xOffset;
            double yOffset;
            double xStart = 1;
            int index = 0;
            // Give reactants coordinates and add to componentsToDraw
            for (IAtomContainer mol : reactants) {
                if (index > 0) {
                    componentsToDraw.add(new String[]{"+", Integer.toString((int) (xStart*factor)), Integer.toString(500)});
                    xStart++;
                }
                sdg.generateCoordinates(mol);
                minmax = GeometryUtil.getMinMax(mol);
                xOffset = Math.abs(0 - minmax[0]);
                yOffset = Math.abs(0 - minmax[1]);
                // Move the molecule so all coordinates are positive
                GeometryUtil.translate2D(mol, xOffset, yOffset);
                Point2d center = new Point2d(GeometryUtil.get2DCenter(mol));
                center.set((center.x + xStart)*factor, 500);
                // Move the molecule so its center is in 0,0, scale, and move back to the scaled center y, with x = 500
                GeometryUtil.translate2DCenterTo(mol, new Point2d(0, 0));
                xStart += (minmax[2] - minmax[0]);
                GeometryUtil.scaleMolecule(mol, factor);
                GeometryUtil.translate2DCenterTo(mol, center);
                // Add all atoms to the componentsToDraw for drawing
                Iterable<IAtom> atoms = mol.atoms();
                for (IAtom atom : atoms) {
                    String[] component = new String[3];
                    component[0] = atom.getSymbol();
                    component[1] = Integer.toString((int) atom.getPoint2d().x);
                    component[2] = Integer.toString((int) atom.getPoint2d().y);
                    componentsToDraw.add(component);
                }
                index++;
                xStart++;
            }

            index = 0;

            // Same process as above for the products
            for (IAtomContainer mol : products) {
                if (index == 0) {
                    componentsToDraw.add(new String[]{"->", Integer.toString((int) (xStart*factor)), Integer.toString(500)});
                    xStart += 1.5;
                } else {
                    componentsToDraw.add(new String[]{"+", Integer.toString((int) (xStart*factor)), Integer.toString(500)});
                    xStart++;
                }
                sdg.generateCoordinates(mol);
                minmax = GeometryUtil.getMinMax(mol);
                xOffset = Math.abs(0 - minmax[0]);
                yOffset = Math.abs(0 - minmax[1]);
                GeometryUtil.translate2D(mol, xOffset, yOffset);
                Point2d center = new Point2d(GeometryUtil.get2DCenter(mol));
                center.set((center.x+xStart)*factor, 500);
                GeometryUtil.translate2DCenterTo(mol, new Point2d(0, 0));
                xStart += (minmax[2] - minmax[0]);
                GeometryUtil.scaleMolecule(mol, factor);
                GeometryUtil.translate2DCenterTo(mol, center);
                Iterable<IAtom> atoms = mol.atoms();
                for (IAtom atom : atoms) {
                    String[] component = new String[3];
                    component[0] = atom.getSymbol();
                    component[1] = Integer.toString((int) atom.getPoint2d().x);
                    component[2] = Integer.toString((int) atom.getPoint2d().y);
                    componentsToDraw.add(component);
                    System.out.println(atom.getPoint2d().x);
                }
                index++;
                xStart++;
            }
        } catch (CDKException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return componentsToDraw;
    }
}
