package com.example.efnaapp;

import android.content.Context;

import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.*;
import java.util.ArrayList;

/**
 * This class reads in all exercises and contains the necessary info regarding each exercise
 * @author Ragnar Pálsson
 */

class ExerciseInfo {

    // List of exercises, stored as CDK Reactions which can store molecules as reactants (left side of a reaction)
    // and products (right side of a reaction)
    private ArrayList<Exercise> exerciseList = new ArrayList<>();

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

        Reaction reaction = new Reaction();
        int index = 0;
        int reactNum = 0;
        int prodNum = 0;
        try {
            while (reader.hasNext()) {
                if (reactNum < smilesReaderInfo[index][0]) {
                    IAtomContainer mol = reader.next();
                    AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
                    AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
                    reaction.addReactant(mol);
                    reactNum++;
                } else if (prodNum < smilesReaderInfo[index][1]) {
                    IAtomContainer mol = reader.next();
                    AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
                    reaction.addProduct(mol);
                    prodNum++;
                } else {
                    exerciseList.add(new Exercise(reaction));
                    reaction = new Reaction();
                    index++;
                    reactNum = 0;
                    prodNum = 0;
                } if (!reader.hasNext()) {
                    exerciseList.add(new Exercise(reaction));
                }
            }
        }
        catch (CDKException e) {
            e.printStackTrace();
        }
    }

    ArrayList<String[]> getExercise(int id) {
        return exerciseList.get(id).getComponentsToDraw();
    }
}
