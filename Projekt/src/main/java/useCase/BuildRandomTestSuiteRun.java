package useCase;

import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.SUT;
import useCase.suts.SUTObject;
import useCase.suts.SUTObjectComplex;
import util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class BuildRandomTestSuiteRun {

    public static void main(String[] args) {

        int arraySize = 1000;
        ArrayList<String[]> output = new ArrayList();

        int countInvalidCombinations = 0;
        int countTotalCombinations = 0;


        SUT sut = new SUTObjectComplex(2,
                SUTObject.IncludeValuesForConstraints.EXCLUDE_VALUES_FOR_CONSTRAINTS).getSut();

        System.out.println(sut.getConstraints());

        ArrayList<Parameter> params = sut.getParams();
        sut.getConstraintManager().init(params);

        int paramSize = params.size();
        String[] headers = params.stream().map(p -> p.getName()).toArray(String[]::new);
        output.add(headers);

        for(int i = 0; i < arraySize; i++) {
            int[] rowInt = new int[paramSize];
            String[] row = new String[paramSize];

            boolean isValidTest = false;
            while(!isValidTest) {
                for(int j = 0; j < paramSize; j++) {
                    Parameter p = params.get(j);

                    int valueID = getRandomElementOfList(p);
                    rowInt[j] = valueID;
                    row[j] = p.getValue(valueID);
                }
                countTotalCombinations++;
                isValidTest = sut.getConstraintManager().isValid(rowInt);
                if(!isValidTest) {
                    countInvalidCombinations++;
                }
            }
            output.add(row);
            if(i == arraySize-1) {
                System.out.println(Arrays.toString(row));
            }
        }

        System.out.println("Ratio of random generated combinations which don't satisfy the constraints: " + ((float) countInvalidCombinations/ (float) countTotalCombinations));
        System.out.println(countInvalidCombinations);
        System.out.println(countTotalCombinations);
        String fileName = String.format("output/Random_Combinations_%s.csv", Util.getDatetimeStamp());
        writeListToFile(fileName, output);

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
            out += Arrays.asList(row).stream().collect(Collectors.joining(", ")) + lineSep;
        }

        Util.writeString2File(fileName, out);

    }

    private static int getRandomElementOfList(Parameter parameter) {
        return (int)(Math.random() * parameter.getValues().size());
    }
}
