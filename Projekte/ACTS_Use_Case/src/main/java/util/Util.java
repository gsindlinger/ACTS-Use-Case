package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Util {

    public static int[] getLastNElementsOfArray(int[] arr, int n) {
        int[] ret = new int[n];
        for(int i = 0; i < n; i++) {
            ret[i] = arr[i + arr.length - n];
        }

        return ret;
    }

    public static int multiplyArray(int[] arr) {
        int ret = 1;
        for(int i = 0; i < arr.length; i++) {
            ret *= arr[i];
        }
        return ret;
    }

    public static int[] IntegerArray2Int(ArrayList<Integer> arr) {
        int[] ret = new int[arr.size()];
        for(int i = 0; i < arr.size(); i++) {
            ret[i] = arr.get(i).intValue(); // returns int value
        }
        return ret;
    }

    public static String getDatetimeStamp() {
        Date myDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        return sdf.format(myDate);
    }



}
