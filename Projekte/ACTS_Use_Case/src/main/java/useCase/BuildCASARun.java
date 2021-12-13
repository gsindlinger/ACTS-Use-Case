package useCase;

import edu.uta.cse.fireeye.common.Constraint;
import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.SUT;
import edu.uta.cse.fireeye.service.constraint.ConstraintManager;
import useCase.suts.CASAObject;
import useCase.suts.SUTObject;
import util.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildCASA {

    public static void main(String[] args) {

        int interactionParameter = 2;
        ArrayList<String> command = new ArrayList<String>();

        String execPath = "casa.exe";
        String inputPath = "output/casa_model_new.txt";
        String outputPath = String.format("output/CASA_Combinations_%s.csv", Util.getDatetimeStamp());
        String constraintPath = "output/casa_constraints.txt";

        CASAObject casa = new CASAObject(new SUTObject(interactionParameter, SUTObject.IncludeValuesForConstraints.INCLUDE_VALUES_FOR_CONSTRAINTS));
        Util.writeString2File(inputPath, casa.generateModel());
        System.out.println(casa.generateModel());

        Util.writeString2File(constraintPath, casa.generateConstraints());
        System.out.println(casa.generateConstraints());


        //createPictModelFromSut(sut, inputPath);


        command.add(execPath);
        command.add("--output");
        command.add(outputPath);
        command.add("--constrain");
        command.add(constraintPath);
        command.add(inputPath);
        runCasa(command);
        //Util.removeTxt(inputPath);
        //Util.removeTxt(constraintPath);
        //Util.Tab2CommaSeparatedCSV(outputPath);

        casa.convertCasaFile2CSV(outputPath);

    }




        /**
     * Method to translate the given SUT from ACTS framework to text logic for PICT usage
     * The specification of the file for PICT can be found here: https://github.com/microsoft/pict/blob/main/doc/pict.md
     * @param sut
     * @param inputPath
     * @author Johannes Gabriel Sindlinger
     */
    private static void createPictModelFromSut(SUT sut, String inputPath) {
        String lineSep = System.getProperty("line.separator");
        String pictOutput = "";

        pictOutput += "# --------------------------------------------------------" + lineSep +
                "# Parameters " + lineSep +
                "# --------------------------------------------------------" + lineSep;

        //Parameter
        for (Parameter param : sut.getParams()) {
            pictOutput += param.getName() + ": ";
            ArrayList<String> values = param.getValues();
            for (int i = 0; i < values.size(); i++) {
                if (i < values.size() - 1) {
                    pictOutput += values.get(i) + ", ";
                } else {
                    pictOutput += values.get(i);
                }
            }
            ArrayList<String> invalidValues = param.getInvalidValues();
            for (int i = 0; i < invalidValues.size(); i++) {
                if (i < invalidValues.size() - 1) {
                    pictOutput += "~" + invalidValues.get(i) + ", ";
                } else {
                    pictOutput += "~" + invalidValues.get(i);
                }
            }
            pictOutput += lineSep;
        }

        pictOutput += "# --------------------------------------------------------" + lineSep +
                "# Constraints " + lineSep +
                "# --------------------------------------------------------" + lineSep;

        //Constraints
        ConstraintManager constraintManager = sut.getConstraintManager();
        ArrayList<Constraint> constraints = constraintManager.getConstraints();
        for (Constraint constraint : constraints) {
            String constraintText = constraint.getText();
            constraintText = constraintText.replaceAll("\\s","");



            Pattern p1 = Pattern.compile("\"\\w[\\w\\d_-]*[\\w\\d]\"|[\\d]+");
            Matcher m1 = p1.matcher(constraintText);

            HashSet<String> valueSet = new HashSet();


            int i = 0;
            while(m1.find()) {
                String helper = m1.group(i);
                String reducedHelper = helper.replace("\"", "");
                valueSet.add(reducedHelper);
            }

            Pattern p2 = Pattern.compile("(\\w[\\w\\d_-]+[\\w\\d])(?!.*\\1)");
            Matcher m2 = p2.matcher(constraintText);

            i = 0;
            while(m2.find()) {
                String helper = m2.group(i);
                if(!valueSet.contains(helper)){
                    constraintText = constraintText.replace(helper, " [" + helper + "] ");
                }
            }

            constraintText = constraintText.replaceAll("=>"," THEN");
            constraintText = constraintText.replaceAll("&&"," AND");
            constraintText = constraintText.replaceAll("\\|\\|"," OR");
            constraintText = constraintText.replaceAll("\\!=","<> ");
            constraintText = constraintText.replaceAll("=","= ");
            constraintText = constraintText.replaceAll("\\("," \\(");
            constraintText = constraintText.replaceAll("\\)"," \\)");

            pictOutput += "IF" + constraintText + ";" + lineSep;
        }

        System.out.println(pictOutput);
        Util.writeString2File(inputPath, pictOutput);


    }


    /**
     *
     * @param command
     */
    public static void runCasa(ArrayList<String> command) {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectOutput(new File("output/CASA_output.txt"));
        Process p = null;
        try {
            p = builder.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
