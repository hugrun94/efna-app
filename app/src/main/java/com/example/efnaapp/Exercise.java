package com.example.efnaapp;

import com.google.common.collect.Iterables;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.layout.StructureDiagramGenerator;

import javax.vecmath.Point2d;
import java.util.ArrayList;

/**
 * This class contains the molecules for an exercise, coordinates and methods for manipulation.
 * @author Ragnar Pálsson
 */

class Exercise {
    private Reaction reaction;
    private int id;

    Exercise(Reaction reaction) {
        this.reaction = reaction;
        this.calcCoords();
    }

    private void calcCoords() {
        int numReactants = reaction.getReactantCount();
        Iterable<IAtomContainer> agents = Iterables.concat(reaction.getReactants().atomContainers(), reaction.getProducts().atomContainers());
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
            for (IAtomContainer mol : agents) {
                if (index > 0 && index != numReactants) {
                    // Add space between molecules for "+"
                    xStart++;
                }
                if (index == numReactants) {
                    // Add space for "arrow"
                    xStart += 2;
                }
                sdg.generateCoordinates(mol);
                minmax = GeometryUtil.getMinMax(mol);
                xOffset = Math.abs(0 - minmax[0]);
                yOffset = Math.abs(0 - minmax[1]);
                // Move the molecule so all coordinates are positive
                GeometryUtil.translate2D(mol, xOffset, yOffset);
                Point2d center = new Point2d(GeometryUtil.get2DCenter(mol));
                center.set((center.x + xStart) * factor, 500);
                // Move the molecule so its center is in 0,0, scale, and move back to the scaled center y, with x = 500
                GeometryUtil.translate2DCenterTo(mol, new Point2d(0, 0));
                xStart += (minmax[2] - minmax[0]);
                GeometryUtil.scaleMolecule(mol, factor);
                GeometryUtil.translate2DCenterTo(mol, center);

                index++;
                // Add space between molecules
                xStart++;
            }
        } catch (CDKException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<String[]> getComponentsToDraw() {
        int numReactants = reaction.getReactantCount();
        Iterable<IAtomContainer> agents = Iterables.concat(reaction.getReactants().atomContainers(), reaction.getProducts().atomContainers());
        ArrayList<String[]> componentsToDraw = new ArrayList<>();
        int index = 0;
        for (IAtomContainer mol : agents) {
            Iterable<IAtom> atoms = mol.atoms();
            if (index > 0 && index != numReactants) {
                double[] xStart = GeometryUtil.getMinMax(mol);
                componentsToDraw.add(new String[]{"+", Integer.toString((int) (xStart[0]-80)), Integer.toString(500)});
            } if (index == numReactants) {
                double[] xStart = GeometryUtil.getMinMax(mol);
                componentsToDraw.add(new String[]{"-->", Integer.toString((int) (xStart[0]-120)), Integer.toString(500)});
            }
            for (IAtom atom : atoms) {
                String[] component = new String[3];
                component[0] = atom.getSymbol();
                component[1] = Integer.toString((int) atom.getPoint2d().x);
                component[2] = Integer.toString((int) atom.getPoint2d().y);
                componentsToDraw.add(component);
                if (atom.getFormalCharge() != 0) {
                    int x = (int) (atom.getPoint2d().x + 50);
                    int y = (int) (atom.getPoint2d().y - 40);
                    if (atom.getFormalCharge() > 0) {
                        componentsToDraw.add(new String[]{"+", Integer.toString(x), Integer.toString(y)});
                    } else {
                        // TODO: tvípunktar
                        componentsToDraw.add(new String[]{"-", Integer.toString(x), Integer.toString(y)});
                    }
                }
            }
            Iterable<IBond> bonds = mol.bonds();
            for (IBond bond : bonds) {
                // TODO: add offset
                String[] component = new String[5];
                component[0] = bond.getOrder().numeric().toString();
                component[1] = Double.toString(bond.getBegin().getPoint2d().x);
                component[2] = Double.toString(bond.getBegin().getPoint2d().y);
                component[3] = Double.toString(bond.getEnd().getPoint2d().x);
                component[4] = Double.toString(bond.getEnd().getPoint2d().y);
            }
            index++;
        }
        return componentsToDraw;
    }
}
