package countCombinations;

import util.Util;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        int[] testArrayInput = new int[]{2,3,2};
        int n = testArrayInput.length;
        /* Interaction strength*/
        int t = 3;

        /* Overall number of t-way combinations*/
        int numberOfCombinations = 0;

        for(int i = 0; i < n-t+1; i++) {
            int[] y_i = Util.getLastNElementsOfArray(testArrayInput, testArrayInput.length-1-i);
            ArrayList<ArrayList<Integer>> u_i = Combinations.getAllCombinationsWithSizeN(y_i,t-1);

            for(ArrayList<Integer> z : u_i) {
                int[] z_helper = Util.IntegerArray2Int(z);
                int sumOfAllCombinations = Util.multiplyArray(z_helper);
                sumOfAllCombinations *= testArrayInput[i];
                numberOfCombinations += sumOfAllCombinations;
            }
        }

        System.out.println("Zähle t-way Kombinationen: " + numberOfCombinations);

        
    }
}
