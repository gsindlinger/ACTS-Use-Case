package useCase;

import edu.uta.cse.fireeye.common.Constraint;
import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.SUT;
import edu.uta.cse.fireeye.service.constraint.ConstraintManager;
import useCase.suts.SUTObject;
import useCase.suts.SUTObjectSimple;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildPICTRun {

    /**
     * Copied and modified by https://stackoverflow.com/questions/55021369/i-want-to-run-pict-exe-from-a-java-program
     */

    public static void main(String[] args) {

        for(int i = 2; i < 7; i++) {
            int interactionParameter = i;
            String execPath = "pict.exe";
            String inputPath = "output/test.txt";
            String outputPath = String.format("output/PICT_Export/t_%d.csv",interactionParameter);

            SUT sut = new SUTObjectSimple(interactionParameter, SUTObject.IncludeValuesForConstraints.INCLUDE_VALUES_FOR_CONSTRAINTS).getSut();
            createPictModelFromSut(sut, inputPath);


            long start = System.currentTimeMillis();
            runPict(execPath, inputPath, outputPath, interactionParameter);
            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            System.out.println("Running Time for interaction parameter t = " + interactionParameter + ": " + timeElapsed);
            Util.removeTxt(inputPath);
            Util.tab2CommaSeparatedCSV(outputPath);

            if(i < 6) {
                CheckCoverageRun.checkCoverage(i+1, outputPath);
            }
        }


    }


    /**
     * Method to translate the given SUT from ACTS framework to text logic for PICT usage
     * The specification of the file for PICT can be found here: https://github.com/microsoft/pict/blob/main/doc/pict.md
     * @param sut
     * @param inputPath
     * @author Johannes Gabriel Sindlinger
     */
    public static void createPictModelFromSut(SUT sut, String inputPath) {
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

            if(constraintText.contains("=>")) {
                constraintText = constraintText.replaceAll("=>"," THEN");
                constraintText = "IF" + constraintText;
            }
            constraintText = constraintText.replaceAll("&&"," AND");
            constraintText = constraintText.replaceAll("\\|\\|"," OR");
            constraintText = constraintText.replaceAll("\\!=","<> ");
            constraintText = constraintText.replaceAll("=","= ");
            constraintText = constraintText.replaceAll("\\("," \\(");
            constraintText = constraintText.replaceAll("\\)"," \\)");

            constraintText = constraintText.trim();

            pictOutput += constraintText + ";" + lineSep;
        }

        System.out.println(pictOutput);
        Util.writeString2File(inputPath, pictOutput);


    }


    /**
     * Method to execute the command
     * @param execPath
     * @param inputPath
     * @param outputPath
     * @param interactionParameter
     */
    public static void runPict(String execPath, String inputPath, String outputPath, int interactionParameter) {
        ProcessBuilder builder = new ProcessBuilder(execPath, inputPath, "/o:" + interactionParameter);
        builder.redirectOutput(new File(outputPath));
        Process p = null;
        try {
            p = builder.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
