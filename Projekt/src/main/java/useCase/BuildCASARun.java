package useCase;

import useCase.suts.CASAObject;
import useCase.suts.SUTObject;
import useCase.suts.SUTObjectSimple;
import util.Util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class BuildCASARun {

    public static void main(String[] args) {


        String execPath = "casa.exe";
        String inputPath = "output/CASA_Export/casa_model.txt";
        String constraintPath = "output/CASA_Export/casa_constraints.txt";
        String outputString = "";

        int [][] startingBordersForSimAnnealing = new int[][]{new int[]{30,80},
                                                            new int[]{120,200},
                                                            new int[]{230,350},
                                                            new int[]{350,520},
                                                            new int[]{350,550}};


        for(int i = 2; i < 7; i++) {
            int smallestTestCaseNumber = Integer.MAX_VALUE;
            ArrayList<Integer> currentNumberOfTests = new ArrayList<>();
            ArrayList<Integer> currentIterationTime = new ArrayList<>();
            ArrayList<Float> currentTPlus1Coverage = new ArrayList<>();
            ArrayList<Float> currentVariableValueCoverage = new ArrayList<>();
            ArrayList<Float> currentP_T_CompletenessCoverage = new ArrayList<>();

            for(int j = 0; j < 30; j++) {
                ArrayList<String> command = new ArrayList<>();

                CASAObject casa = new CASAObject(new SUTObjectSimple(i, SUTObject.IncludeValuesForConstraints.INCLUDE_VALUES_FOR_CONSTRAINTS));
                Util.writeString2File(inputPath, casa.generateModel());
                Util.writeString2File(constraintPath, casa.generateConstraints());

                String outputPath = String.format("output/CASA_Export/t_%d.csv",i);


                command.add(execPath);
                command.add("-o");
                command.add(outputPath);
                command.add("-c");
                command.add(constraintPath);
                //Changing the seed for every iteration
                command.add("-s");
                command.add("" + (j+1)*(i+1));

                // set preferences (as default)
                /*command.add("-i");
                command.add("" + 1000);
                command.add("-r");
                command.add("" + 2);
                command.add("-p");
                command.add("" + (2.0 / 3.0));*/
                command.add("-u");
                command.add("" + startingBordersForSimAnnealing[i-2][1]);
                command.add("-l");
                command.add(""+ startingBordersForSimAnnealing[i-2][0]);


                command.add(inputPath);

                long start = System.currentTimeMillis();
                runCasa(command);
                long finish = System.currentTimeMillis();
                long timeElapsed = finish - start;

                System.out.println("Running Time for interaction parameter t = " + i + ": " + timeElapsed);
                try {
                    String filename = "CASA_time.txt";
                    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
                    fw.write("" + i + ", " + timeElapsed + "\n"); //appends the string to the file
                    fw.close();
                } catch(IOException ioe) {
                    System.err.println("IOException: " + ioe.getMessage());
                }


                int numberOfTests = casa.convertCasaFile2CSV(outputPath);
                if(numberOfTests < smallestTestCaseNumber) {
                    smallestTestCaseNumber = numberOfTests;
                    try {
                        Files.copy(new File(outputPath).toPath(), new File("output/CASA_Export/Smallest_Set_t_" + i + ".csv").toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //new File(inputPath);//.deleteOnExit();
                //new File(constraintPath);//.deleteOnExit();

                currentNumberOfTests.add(numberOfTests);
                currentIterationTime.add((int) timeElapsed);

                if(i < 6) {
                    ArrayList<Float> savedResults = CheckCoverageRun.checkCoverage(i+1, outputPath);
                    currentTPlus1Coverage.add(savedResults.get(0));
                    currentVariableValueCoverage.add(savedResults.get(1));
                    currentP_T_CompletenessCoverage.add(savedResults.get(2));
                }



            }

            String lineSep = System.lineSeparator();
            outputString += "t = " + i + ":" + lineSep;
            outputString += "Number of Tests," + currentNumberOfTests.stream().map(a -> "" + a).collect(Collectors.joining(",")) + lineSep;
            outputString += "Iteration Time," + currentIterationTime.stream().map(a -> "" + a).collect(Collectors.joining(",")) + lineSep;
            if(i < 6) {
                outputString += "(t+1)-Coverage," + currentTPlus1Coverage.stream().map(a -> "" + a).collect(Collectors.joining(",")) + lineSep;
                outputString += "(t+1)-Variable-Value-Coverage," + currentVariableValueCoverage.stream().map(a -> "" + a).collect(Collectors.joining(",")) + lineSep;
                outputString += "(0.75-(t+1))-Completeness," + currentP_T_CompletenessCoverage.stream().map(a -> "" + a).collect(Collectors.joining(",")) + lineSep;

            }
            outputString += lineSep;

        }

        System.out.println(outputString);

        Util.writeString2File("output/CASA_Export/Test_Values.txt", outputString);


    }


    /**
     *
     * @param command
     */
    public static void runCasa(ArrayList<String> command) {
        ProcessBuilder builder = new ProcessBuilder(command);

        System.out.println("Starting command: " + command.toString());
        builder.redirectOutput(new File("output/CASA_Export/CASA_Command_Line_Output.txt"));
        Process p = null;
        try {
            p = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String readline;
            int i = 0;
            while ((readline = reader.readLine()) != null) {
                System.out.println(++i + " " + readline);
            }
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
