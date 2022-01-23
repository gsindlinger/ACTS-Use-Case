package useCase;

import useCase.suts.SUTObjectFullImplementationComb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ReadFullCombinationFiles {
    public static void main(String[] args) throws IOException {

        String fileName = "";
        String output = "";
        String outputFile = "output/Full_Combination_Export/ACTS_Number_of_Tests.txt";
        SUTObjectFullImplementationComb sutObject = new SUTObjectFullImplementationComb(3,2);
        for(int i = 2; i < 7; i++) {
            fileName = "output/Full_Combination_Export/ACTS_t_" + i + ".csv";
            ArrayList<String> combinationsList = sutObject.combinationsList;
            //combinationsList.set(0, "Durchführungsweg, Art der Rückdeckung");
            ArrayList<Integer> sumOfTests = new ArrayList<>();
            for(int j = 0; j < combinationsList.size(); j++) {
                sumOfTests.add(0);
            }
            try {
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                String line = null;
                while ((line = br.readLine()) != null) {
                    for(int j = 0; j < combinationsList.size(); j++) {
                        if(line.contains(combinationsList.get(j))){
                            sumOfTests.set(j, sumOfTests.get(j)+1);
                        }
                    }
                }
                br.close();

                int totalSum = 0;
                for(int j = 0; j < sumOfTests.size(); j++) {
                    totalSum += sumOfTests.get(j);
                }
                output += "t = " + i + ", Summe = " + totalSum + "Einzeln: " + sumOfTests;


                System.out.println("t = " + i + ", Summe = " + totalSum + ", Einzeln: " + sumOfTests);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        FileWriter fw = new FileWriter(outputFile, false); //false to replace file contents, your code has true for append to file contents
        fw.write(output);
        fw.flush();
        fw.close();

    }
}
