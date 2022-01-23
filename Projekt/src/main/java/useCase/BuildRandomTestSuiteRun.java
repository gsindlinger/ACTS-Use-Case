package useCase;

import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.SUT;
import useCase.suts.SUTObject;
import useCase.suts.SUTObjectComplex;
import useCase.suts.SUTObjectSimple;
import util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.Collectors;

public class BuildRandomTestSuiteRun {

    public static void main(String[] args) {

        int[] arraySizeInt = new int[]{200, 300};
        String resultsOutput = "";
        int maximumInteractionParameter = 3;
        int randomRepeats = 20;


        for(int h = 0; h < arraySizeInt.length; h++) {
            int arraySize = arraySizeInt[h];

            SUT sut = new SUTObjectSimple(2,
                    SUTObject.IncludeValuesForConstraints.INCLUDE_VALUES_FOR_CONSTRAINTS).getSut();


            ArrayList<Integer> currentIterationTime = new ArrayList<>();
            ArrayList<Integer> numberOfAllIterations = new ArrayList<>();
            ArrayList<Float> ratioOfSuccessfulIterations = new ArrayList<>();

            ArrayList<ArrayList<Float>> allTCoverages = new ArrayList<>();
            ArrayList<ArrayList<Float>> allVariableValueCoverage = new ArrayList<>();
            ArrayList<ArrayList<Float>> allP_T_CompletenessCoverage = new ArrayList<>();


            for(int interactionParameter = 2; interactionParameter < maximumInteractionParameter+1; interactionParameter++) {

                allTCoverages.add(new ArrayList<>());
                allVariableValueCoverage.add(new ArrayList<>());
                allP_T_CompletenessCoverage.add(new ArrayList<>());

            }



                for(int k = 0; k < randomRepeats; k++) {

                    ArrayList<Parameter> params = sut.getParams();
                    sut.getConstraintManager().init(params);

                    int paramSize = params.size();

                    ArrayList<String[]> output = new ArrayList();
                    HashSet<String> outputSet = new HashSet<>();
                    String[] headers = params.stream().map(p -> p.getName()).toArray(String[]::new);
                    output.add(headers);


                    int countInvalidCombinations = 0;
                    int countTotalCombinations = 0;

                    long start = System.currentTimeMillis();

                    for(int i = 0; i < arraySize; i++) {
                        int[] rowInt = new int[paramSize];
                        String[] row = new String[paramSize];

                        boolean isValidTest = false;
                        boolean isDuplicate = true;
                        while(!isValidTest || isDuplicate) {
                            for(int j = 0; j < paramSize; j++) {
                                Parameter p = params.get(j);

                                int valueID = getRandomElementOfList(p);
                                rowInt[j] = valueID;
                                row[j] = p.getValue(valueID);
                            }
                            countTotalCombinations++;
                            isValidTest = sut.getConstraintManager().isValid(rowInt);
                            if(isValidTest) {
                                isValidTest = sut.getConstraintManager().isValid(rowInt);
                            }
                            isDuplicate = !outputSet.add(String.join(",",row));

                            if(!isValidTest) {
                                countInvalidCombinations++;
                            }
                        }
                        output.add(row);
                    }

                    long finish = System.currentTimeMillis();
                    long timeElapsed = finish - start;

                    float successRatio = (float) countInvalidCombinations / (float) countTotalCombinations;
                    System.out.println("Ratio of random generated combinations which don't satisfy the constraints: " + successRatio);
                    System.out.println(countInvalidCombinations);
                    System.out.println(countTotalCombinations);
                    System.out.println("Running Time for array size = " + arraySize + ": " + timeElapsed);
                    String fileName = String.format("output/RandomTestSuite_Export/array_size_%d.csv", arraySize);
                    writeListToFile(fileName, output);


                    currentIterationTime.add((int) timeElapsed);
                    numberOfAllIterations.add(countTotalCombinations);
                    ratioOfSuccessfulIterations.add(successRatio);

                    for(int interactionParameter = 2; interactionParameter < maximumInteractionParameter+1; interactionParameter++) {
                        sut = new SUTObjectSimple(interactionParameter,
                                SUTObject.IncludeValuesForConstraints.INCLUDE_VALUES_FOR_CONSTRAINTS).getSut();

                        ArrayList<Float> savedResults = CheckCoverageRun.checkCoverage(interactionParameter, fileName);
                        allTCoverages.get(interactionParameter-2).add(savedResults.get(0));
                        allVariableValueCoverage.get(interactionParameter-2).add(savedResults.get(1));
                        allP_T_CompletenessCoverage.get(interactionParameter-2).add(savedResults.get(2));
                    }

                }

                String lineSep = System.lineSeparator();
                resultsOutput += "Array Size = " + arraySize + ":" + lineSep;
                resultsOutput += "Iteration Time," + currentIterationTime.stream().map(a -> "" + a).collect(Collectors.joining(",")) + lineSep;
                resultsOutput += "Number of All Iterations," + numberOfAllIterations.stream().map(a -> "" + a).collect(Collectors.joining(",")) + lineSep;
                resultsOutput += "Ratio of unsuccessful Iterations," + ratioOfSuccessfulIterations.stream().map(a -> "" + a).collect(Collectors.joining(",")) + lineSep;

                for(int interactionParameter = 2; interactionParameter < maximumInteractionParameter+1; interactionParameter++) {
                    resultsOutput += "Coverage --- t = " + (interactionParameter) + System.lineSeparator();
                    resultsOutput += "(" + (interactionParameter) + ")-Coverage," + allTCoverages.get(interactionParameter-2).stream().map(a -> "" + a).collect(Collectors.joining(",")) + lineSep;
                    resultsOutput += "(" + (interactionParameter) + ")-Variable-Value-Coverage," + allVariableValueCoverage.get(interactionParameter-2).stream().map(a -> "" + a).collect(Collectors.joining(",")) + lineSep;
                    resultsOutput += "(0.75-(" + (interactionParameter) + "))-Completeness," + allP_T_CompletenessCoverage.get(interactionParameter-2).stream().map(a -> "" + a).collect(Collectors.joining(",")) + lineSep;
                }

                resultsOutput += System.lineSeparator();
                resultsOutput += "------------------------";
                resultsOutput += System.lineSeparator();
            }


        System.out.println(resultsOutput);

        Util.writeString2File("output/RandomTestSuite_Export/Analysis_200_300.txt", resultsOutput);


    }

    private static String getRandomGaussianOfAmounts(Parameter p) {
        double randomDouble = new Random().nextGaussian();
        int ret;
        switch(p.getName()) {
            //randomDouble*Standardabweichung + Erwartungswert
            case "Zuzahlung_zu_Beginn":
                ret = (int) Math.max(randomDouble*5000+2000,0);
                break;
            case "Beitrag_40b":
                ret = (int) Math.max(randomDouble*1500+1000,0);
                break;
            case "Beitrag_363":
                ret = (int) Math.max(randomDouble*1000+1000,0);
                break;
            case "AN_Beitrag":
                ret = (int) Math.max(randomDouble*1500+1000,0);
                break;
            case "AG_Beitrag":
                ret = (int) Math.max(randomDouble*1500+1000,0);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + p.getName());
        }
        return ""+ ret;
    }

    private static void writeListToFile(String fileName, ArrayList<String[]> output) {
        String lineSep = System.getProperty("line.separator");
        String out = "";

        for(String[] row : output) {
            out += Arrays.asList(row).stream().collect(Collectors.joining(",")) + lineSep;
        }

        Util.writeString2File(fileName, out);

    }

    private static int getRandomElementOfList(Parameter parameter) {
        return (int)(Math.random() * parameter.getValues().size());
    }
}
