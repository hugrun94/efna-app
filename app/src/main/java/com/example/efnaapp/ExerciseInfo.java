package com.example.efnaapp;

/**
 * This class reads in and contains all exercises
 * @author Ragnar Pálsson
 */
public class ExerciseInfo {
    private static Exercise exercise;
    public static void setExercise(Exercise e) {
        exercise = e;
    }
    public static Exercise getExercise() {
        return exercise;
    }
}
/*class ExerciseInfo {

    // List of exercises, stored as CDK Reactions which can store molecules as reactants (left side of a reaction)
    // and products (right side of a reaction)
    // TODO: List for each exercise type
    private Exercise exercise;
    ArrayList<String> productsArray;
    ArrayList<String> reactantsArray;
    int[] reactantsLength;
    int[] productsLength;

    // Constructor which reads in all molecules from the exercises.smiles resource file and stores them according to
    // the format given in exercise_format.
    ExerciseInfo(Context ctxt, String dude) {
        // TODO: Change to reading from smiles and format txt files to JSON parsing
        ProblemActivity ha = new ProblemActivity();
        ha.createJSON();
        productsArray = ha.getProductsArray();
        reactantsArray = ha.getReactantsArray();
        reactantsLength = ha.getReactantslengtharray();
        productsLength = ha.getProductslengtharray();
        int id = Integer.parseInt(dude);

        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        Reaction reaction = new Reaction();
        int width = ctxt.getResources().getDisplayMetrics().widthPixels;
        int height = ctxt.getResources().getDisplayMetrics().heightPixels;
        int reactantNum = 0;
        int productNum = 0;
        for (int i = 0; i < id; i++) {
            reactantNum += reactantsLength[i];
            productNum += productsLength[i];
        }
        for (int j = 0; j < reactantsLength[id]; j++) {
            try {
                IAtomContainer mol = parser.parseSmiles(reactantsArray.get(j + reactantNum));
                reaction.addReactant(mol);
            } catch (InvalidSmilesException e) {
                e.printStackTrace();
            }
        }
        for (int j = 0; j < productsLength[id]; j++) {
            try {
                IAtomContainer mol = parser.parseSmiles(productsArray.get(j + productNum));
                reaction.addProduct(mol);
            } catch (InvalidSmilesException e) {
                e.printStackTrace();
            }
        }
        exercise = new Exercise(reaction, width, height);
    }
    public void createJSON() {

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());

            JSONObject typeOfProblem = obj.getJSONObject("acid/base");
            JSONArray allProblemsOfType = typeOfProblem.getJSONArray("exercises");
            productslengtharray = new int[allProblemsOfType.length()];
            reactantslengtharray = new int[allProblemsOfType.length()];

            for (int i = 0; i < allProblemsOfType.length(); i++) {
                JSONObject exerciseDetail = allProblemsOfType.getJSONObject(i);
                exerciseId.add(exerciseDetail.getString("id"));
                finished.add(exerciseDetail.getBoolean("finsished"));
                JSONArray reactants = exerciseDetail.getJSONArray("reactants");
                for (int j = 0; j < reactants.length(); j++) {
                    String dudes = reactants.getString(j);
                    reactantsArray.add(dudes);
                }
                reactantslengtharray[i] = reactants.length();

                JSONArray products = exerciseDetail.getJSONArray("products");

                for (int j = 0; j < products.length(); j++) {
                    String dudes2 = products.getString(j);
                    productsArray.add(dudes2);
                    System.out.println(products.getString(j));
                }
                productslengtharray[i] = products.length();


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
    }

    public String loadJSONFromAsset() {
        String json=null;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            System.out.print("");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }



    Exercise getExercise() { return exercise; }
}*/
