package com.example.efnaapp;

import android.graphics.PointF;

import com.google.common.collect.Iterables;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.qsar.AtomValenceTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;

import javax.vecmath.Point2d;

import java.util.ArrayList;

/**
 * This class contains the molecules for an exercise, coordinates and methods for manipulation.
 *
 * @author Ragnar Pálsson
 */

class Exercise {
    // Original reaction for the exercise
    private Reaction exercise;
    // Reaction currently being worked on, can change;
    private Reaction reaction;
    // List of the steps (reactions) that the user inputs
    private ArrayList<Reaction> userSolution = new ArrayList<>();
    private int id;
    private int maxX;
    private int maxY;

    Exercise(Reaction reaction, int x, int y) {
        this.exercise = reaction;
        this.reaction = reaction;
        maxX = x;
        maxY = y;
        initialize();
        calcCoords();
    }

    private void calcCoords() {
        int numReactants = reaction.getReactantCount();
        Iterable<IAtomContainer> agents = Iterables.concat(reaction.getReactants().atomContainers(),
                reaction.getProducts().atomContainers());
        // Class which gives atoms coordinates
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        try {
            double factor = maxX * (80.0 / 1920.0);
            double[] minmax;
            double xOffset;
            double yOffset;
            double xStart = 0.5;
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
                center.set((center.x + xStart) * factor, maxY / 2);
                // Move the molecule so its center is in 0,0, scale, and move to the scaled center
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

    private void initialize() {
        try {
            // Initialize atom info, such as valence and formal charge
            Iterable<IAtomContainer> agents = Iterables.concat(reaction.getReactants().atomContainers(),
                    reaction.getProducts().atomContainers());
            for (IAtomContainer mol : agents) {
                AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
                for (IAtom atom : mol.atoms()) {
                    atom.setValency(AtomValenceTool.getValence(atom));
                    if (atom.getSymbol() == "N") {
                        atom.setValency(3);
                    }
                    int bondSum = 0;
                    for (IBond bond : atom.bonds()) {
                        bondSum += bond.getOrder().numeric();
                    }
                    atom.setFormalCharge(- atom.getValency() + bondSum);
                }
            }
        } catch (CDKException e) {
            e.printStackTrace();
        }
    }

    ArrayList<String[]> getComponentsToDraw() {
        // Returns a list of string arrays with the components to draw on the screen
        int numReactants = reaction.getReactantCount();
        // Gets all the molecules to be drawn to iterate through them
        Iterable<IAtomContainer> agents = Iterables.concat(reaction.getReactants().atomContainers(),
                reaction.getProducts().atomContainers());
        ArrayList<String[]> componentsToDraw = new ArrayList<>();
        int index = 0;
        double factor = maxX * (80.0 / 1920.0);
        for (IAtomContainer mol : agents) {
            Iterable<IAtom> atoms = mol.atoms();
            // Moves the molecules so that their symbol gets drawn where there coordinates point
            GeometryUtil.translate2D(mol, -factor / 2.5, factor / 2);
            if (index > 0 && index != numReactants) {
                double[] xStart = GeometryUtil.getMinMax(mol);
                // Add in "+" symbols between molecules
                componentsToDraw.add(new String[]{"+", Integer.toString((int) (xStart[0] - factor * 0.9)),
                        Integer.toString((int) (maxY + 0.7 * factor) / 2)});
            }
            if (index == numReactants) {
                // Add an arrow symbol between the reactants and products
                double[] xStart = GeometryUtil.getMinMax(mol);
                componentsToDraw.add(new String[]{"->", Integer.toString((int) (xStart[0] - factor * 1.5)),
                        Integer.toString((int) (maxY + 0.7 * factor) / 2)});
            }
            // Iterate through the atoms in the current molecule and add them to the component String[] list
            for (IAtom atom : atoms) {
                String[] component = new String[3];
                component[0] = atom.getSymbol();
                component[1] = Integer.toString((int) atom.getPoint2d().x);
                component[2] = Integer.toString((int) atom.getPoint2d().y);
                componentsToDraw.add(component);
                if (atom.getFormalCharge() != 0) {
                    int x = (int) (atom.getPoint2d().x + factor * 4 / 5);
                    int y = (int) (atom.getPoint2d().y - factor * 2 / 3);
                    // Add charges to components to draw
                    if (atom.getFormalCharge() > 0) {
                        componentsToDraw.add(new String[]{"+", Integer.toString(x), Integer.toString(y)});
                    } else {
                        // TODO: tvípunktar
                        componentsToDraw.add(new String[]{"-", Integer.toString(x), Integer.toString(y)});
                    }
                }
            }
            Iterable<IBond> bonds = mol.bonds();
            GeometryUtil.translate2D(mol, factor / 2.5, -factor / 2);
            // Iterate through bonds in current molecule to add them to the component list
            for (IBond bond : bonds) {
                int x1 = (int) (bond.getBegin().getPoint2d().x);
                int y1 = (int) (bond.getBegin().getPoint2d().y);
                int x2 = (int) (bond.getEnd().getPoint2d().x);
                int y2 = (int) (bond.getEnd().getPoint2d().y);
                // Move (and shorten) the bonds so they appear between the atoms being drawn
                if (x1 == x2) {
                    if (y1 > y2) {
                        y1 -= 0.5 * factor;
                        y2 += 0.5 * factor;
                    } else {
                        y2 -= 0.5 * factor;
                        y1 += 0.5 * factor;
                    }
                } else if (y1 == y2) {
                    if (x1 > x2) {
                        x2 += 0.4 * factor;
                        x1 -= 0.4 * factor;
                    } else {
                        x1 += 0.4 * factor;
                        x2 -= 0.4 * factor;
                    }
                } else {
                    double m = (y2 - y1 + 0.0) / (x2 - x1 + 0.0);
                    if (m > 1 || m < -1) {
                        if (y1 > y2) {
                            y1 -= 0.45 * factor;
                            x1 -= (0.45 * factor) / m;
                            y2 += 0.55 * factor;
                            x2 += (0.55 * factor) / m;
                        } else {
                            y2 -= 0.45 * factor;
                            x2 -= (0.45 * factor) / m;
                            y1 += 0.55 * factor;
                            x1 += (0.55 * factor) / m;
                        }
                    } else {
                        if (x1 > x2) {
                            x1 -= 0.4 * factor;
                            y1 -= 0.4 * factor * m;
                            x2 += 0.45 * factor;
                            y2 += 0.45 * factor * m;
                        } else {
                            x1 += 0.45 * factor;
                            y1 += 0.45 * factor * m;
                            x2 -= 0.4 * factor;
                            y2 -= 0.4 * factor * m;
                        }
                    }
                }
                String[] component = new String[5];
                component[0] = bond.getOrder().numeric().toString();
                component[1] = Integer.toString(x1);
                component[2] = Integer.toString(y1);
                component[3] = Integer.toString(x2);
                component[4] = Integer.toString(y2);
                componentsToDraw.add(component);
            }
            index++;
        }
        return componentsToDraw;
    }

    void resolveSolutionStep(ArrayList<PointF> origins, ArrayList<PointF> destinations) {
        try {
            Reaction solutionStep = (Reaction) reaction.clone();
            // Go through all the arrows and resolve the changes
            Iterable<IAtomContainer> mols = solutionStep.getReactants().atomContainers();
            for (int i = 0; i < origins.size(); i++) {
                // Extract the origin and destination coordinates of the current arrow
                float origX = origins.get(i).x;
                float origY = origins.get(i).y;
                float destX = destinations.get(i).x;
                float destY = destinations.get(i).y;
                // Containers for possible origins and destinations
                AtomContainer closestOrigins = new AtomContainer();
                ArrayList<IBond> closestBonds = new ArrayList<>();
                AtomContainer closestDestAtoms = new AtomContainer();
                // Add the closest atoms/bonds of each mol to their appropriate containers/list
                boolean hasBonds = false;
                for (IAtomContainer mol : mols) {
                    // Check if there are bonds
                    if (mol.getBondCount() > 0) {
                        hasBonds = true;
                        IBond closestBond = GeometryUtil.getClosestBond(origX, origY, mol);
                        if (GeometryUtil.has2DCoordinates(closestBond)) {
                            closestBonds.add(closestBond);
                        }
                    }
                    closestOrigins.addAtom(GeometryUtil.getClosestAtom(origX, origY, mol));
                    closestDestAtoms.addAtom(GeometryUtil.getClosestAtom(destX, destY, mol));
                }
                // Set the origin/destination atoms as appropriate
                IAtom origAtom = GeometryUtil.getClosestAtom(origX, origY, closestOrigins);
                IAtom destAtom = GeometryUtil.getClosestAtom(destX, destY, closestDestAtoms);
                // set the origin bond as an empty bond and update if appropriate
                IBond origBond = new Bond();
                for (int j = 0; j < closestBonds.size(); j++) {
                    if (j == 0) {
                        origBond = closestBonds.get(j);
                    }
                    double x = closestBonds.get(j).get2DCenter().x;
                    double y = closestBonds.get(j).get2DCenter().y;
                    double closestBondDist = Math.sqrt(Math.pow((x - origX), 2) + Math.pow((y - origY), 2));
                    x = origBond.get2DCenter().x;
                    y = origBond.get2DCenter().y;
                    double origBondDist = Math.sqrt(Math.pow((x - origX), 2) + Math.pow((y - origY), 2));
                    if (origBondDist > closestBondDist) {
                        origBond = closestBonds.get(j);
                    }
                }
                // Check whether the atom or bond is closer to the origin point
                double x = origAtom.getPoint2d().x;
                double y = origAtom.getPoint2d().y;
                double atomDist = Math.sqrt(Math.pow((x - origX), 2) + Math.pow((y - origY), 2));
                // Set the bond distance to a high number to avoid picking the bond in cases where there is none
                double bondDist = 100000;
                // Check if there is a bond and if so update the bondDist
                if (GeometryUtil.has2DCoordinates(origBond) && hasBonds) {
                    x = origBond.get2DCenter().x;
                    y = origBond.get2DCenter().y;
                    bondDist = Math.sqrt(Math.pow((x - origX), 2) + Math.pow((y - origY), 2));
                }

                IAtomContainer mol = origAtom.getContainer();

                // If the atom has been selected and it has a negative charge (electrons to spare)
                // Do the following: A:  X -> A-X
                if (atomDist < bondDist && origAtom.getFormalCharge() < 0) {
                    // Check whether the two atoms are in the same molecule
                    if (mol.contains(destAtom)) {
                        // See if the are bonded, if so increase the bond Order
                        boolean bonded = false;
                        for (IBond bond : mol.bonds()) {
                            if (bond.contains(origAtom) && bond.contains(destAtom)) {
                                BondManipulator.increaseBondOrder(bond);
                                bonded = true;
                                break;
                            }
                        }
                        // If they are not bonded and the arrow is not pointing to itself
                        // Create a bond between the two atoms of Single order
                        if (!bonded && (mol.indexOf(origAtom) != mol.indexOf(destAtom))) {
                            mol.addBond(mol.indexOf(origAtom), mol.indexOf(destAtom), IBond.Order.SINGLE);
                        }
                    // If they are in different molecules, add one molecule to the other and create
                    // a single order bond between them
                    } else {
                        IAtomContainer mol2 = destAtom.getContainer();
                        solutionStep.getReactants().removeAtomContainer(mol2);
                        mol.add(mol2);
                        mol.addBond(mol.indexOf(origAtom), mol.indexOf(destAtom), IBond.Order.SINGLE);
                    }
                } else {
                    // TODO: ensure molecule order within the AtomContainerSet is kept the same
                    // Check to make sure there is a bond available to select and if there is select
                    // the new origin atom from the selected bond and change the selected mol
                    if (hasBonds) {
                        AtomContainer bondAtoms = new AtomContainer();
                        bondAtoms.addAtom(origBond.getBegin());
                        bondAtoms.addAtom(origBond.getEnd());
                        origAtom = GeometryUtil.getClosestAtom(origX, origY, bondAtoms);
                        mol = origAtom.getContainer();
                    }
                    // If the molecule contains the destination atom perform the right changes
                    if (mol.contains(destAtom)) {
                        // Make sure there are bonds to avoid null pointer exceptions
                        if (hasBonds) {
                            // If the bond selected is single order, delete it and create a new bond
                            // between the origin and destination atoms and partition the molecule
                            if (origBond.getOrder().numeric() == 1) {
                                mol.removeBond(origBond);
                                IAtomContainerSet molSet = ConnectivityChecker.partitionIntoMolecules(mol);
                                IAtomContainerSet reactants = solutionStep.getReactants();
                                reactants.removeAtomContainer(mol);
                                reactants.add(molSet);
                                // If it is not single order decrease the bond order and add a new single
                                // order bond if the origin and destination are not the same and the two
                                // atoms are not bonded by the original bond
                            } else {
                                BondManipulator.decreaseBondOrder(origBond);
                                if (mol.indexOf(origAtom) != mol.indexOf(destAtom) &&
                                        !(origBond.contains(origAtom) && origBond.contains(destAtom))) {
                                    mol.addBond(mol.indexOf(origAtom), mol.indexOf(destAtom), IBond.Order.SINGLE);
                                }
                            }
                        }
                    } else {
                        IAtomContainer mol2 = destAtom.getContainer();
                        if (hasBonds) {
                            // If the origin and destination atoms are in different molecules, remove
                            // and create, or change bond orders as appropriate and partition the result
                            // if need be.
                            if (origBond.getOrder().numeric() == 1) {
                                mol.removeBond(origBond);
                                mol.add(mol2);
                                mol.addBond(mol.indexOf(origAtom), mol.indexOf(destAtom), IBond.Order.SINGLE);
                                IAtomContainerSet molSet = ConnectivityChecker.partitionIntoMolecules(mol);
                                IAtomContainerSet reactants = solutionStep.getReactants();
                                reactants.removeAtomContainer(mol);
                                reactants.removeAtomContainer(mol2);
                                reactants.add(molSet);
                            } else {
                                BondManipulator.decreaseBondOrder(origBond);
                                solutionStep.getReactants().removeAtomContainer(mol2);
                                mol.add(mol2);
                                mol.addBond(mol.indexOf(origAtom), mol.indexOf(destAtom), IBond.Order.SINGLE);
                            }
                        }
                    }
                }
            }
            // Add the solution step to the list of solution steps and set the working reaction
            userSolution.add(solutionStep);
            reaction = solutionStep;

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        // Initialize the information and calculate coordinates for the newly updated working reaction
        initialize();
        calcCoords();
    }
}
