package countCombinations;/*
Copied by https://www.geeksforgeeks.org/print-subsets-given-size-set/
This code is contributed by Devesh Agrawal

Modified by Johannes Gabriel Sindlinger

Java program to print all combination of size r in an array of size n
 */

import java.util.ArrayList;

class Combinations {

    private static ArrayList<ArrayList<Integer>> combinations;

    /* arr[] ---> Input Array
    data[] ---> Temporary array to store current combination
    start & end ---> Starting and Ending indexes in arr[]
    index ---> Current index in data[]
    r ---> Size of a combination to be printed */
    static void combinationUtil(int[] arr, int n, int r,
                                int index, int[] data, int i) {
        // Current combination is ready to be printed,
        // print it
        if (index == r) {
            ArrayList<Integer> helper = new ArrayList<Integer>();
            for (int j = 0; j < r; j++) {
                helper.add(data[j]);
            }
            combinations.add(helper);
            return;
        }

        // When no more elements are there to put in data[]
        if (i >= n)
            return;

        // current is included, put next at next
        // location
        data[index] = arr[i];
        combinationUtil(arr, n, r, index + 1,
                data, i + 1);

        // current is excluded, replace it with
        // next (Note that i+1 is passed, but
        // index is not changed)
        combinationUtil(arr, n, r, index, data, i + 1);
    }

    // The main function that prints all combinations
    // of size r in arr[] of size n. This function
    // mainly uses combinationUtil()
    static ArrayList<ArrayList<Integer>> getAllCombinationsWithSizeN(int[] arr, int r) {
        int n = arr.length;
        combinations = new ArrayList<>();
        // A temporary array to store all combination
        // one by one
        int data[] = new int[r];

        // Print all combination using temporary
        // array 'data[]'
        combinationUtil(arr, n, r, 0, data, 0);
        return combinations;
    }

    /* Driver function to check for above function */
    public static void main(String[] args) {
        int arr[] = {10, 20, 30, 40, 50};
        int r = 3;
        int n = arr.length;
        getAllCombinationsWithSizeN(arr, r);
    }
}
