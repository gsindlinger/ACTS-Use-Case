package util;

import useCase.suts.SUTObjectComplex;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Util {


    /**
     * Returns all n last Elements of a given array
     *
     * @param arr
     * @param n
     * @return
     */

    public static int[] getLastNElementsOfArray(int[] arr, int n) {
        int[] ret = new int[n];
        for (int i = 0; i < n; i++) {
            ret[i] = arr[i + arr.length - n];
        }

        return ret;
    }


    /**
     * Multiplies all given integers in an array.
     *
     * @param arr
     * @return
     */
    public static int multiplyArray(int[] arr) {
        return Arrays.stream(arr).reduce(1, Math::multiplyExact);
    }

    /**
     * Converts an Integer Array to an int array.
     *
     * @param arr
     * @return
     */

    public static int[] IntegerArray2Int(ArrayList<Integer> arr) {
        int[] ret = new int[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            ret[i] = arr.get(i).intValue(); // returns int value
        }
        return ret;
    }

    /**
     * Returns the current timestamp in the specified format as String
     *
     * @return
     * @author Johannes Gabriel Sindlinger
     */

    public static String getDatetimeStamp() {
        Date myDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        return sdf.format(myDate);
    }

    public static void removeNLinesFromTxt(String fileName, int numberOfLines) {
        String lineSep = System.getProperty("line.separator");
        String output = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = null;
            int i = 0;
            while ((line = br.readLine()) != null) {
                if (i >= numberOfLines) {
                    output += line + lineSep;
                }
                i++;
            }
            br.close();
            FileWriter fw = new FileWriter(fileName, false); //false to replace file contents, your code has true for append to file contents
            fw.write(output);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        /**
     * Adds the expected result with respect to the BBG-border which is given at {@link SUTObjectComplex} class.
     * Adapted from: https://stackoverflow.com/questions/34590971/java-append-new-column-to-csv-file
     * <p>
     * Reads a text file specified by parameter fileName and adds the expected result for each column.
     * Considers the fact that the first lines (< 6) are just information about the generation of the covering array
     * and the headers of the columns (line 6).
     * <p>
     * The expected result is specified by the constraints given in the thesis paper
     *
     * @param fileName
     * @author Johannes Gabriel Sindlinger
     */
    public static void addExpectedResults(String fileName, Tool tool) {
        EasyWeb easyWeb = new EasyWeb();

        String lineSep = System.getProperty("line.separator");
        String output = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = null;
            int i = 0;
            while ((line = br.readLine()) != null) {

                switch (tool) {
                    case ACTS_PRETTY:
                        if (i < 6) {
                            output += line + lineSep;
                        } else if (i == 6) {
                            easyWeb.findIndexes(line.split(","));
                            output += replaceGermanUmlauts(line.replace(line, line + "," + "Erwartetes Ergebnis" + lineSep)).replace("_", " ");
                        } else {
                            String[] splittedLine = line.split(",");
                            boolean isValidResult = easyWeb.getExpectedResults(splittedLine);
                            String isValidResult2String = isValidResult ? "Gültig" : "Nicht Gültig";
                            output += replaceGermanUmlauts(line.replace(line, line + "," + isValidResult2String + lineSep)).replace("_", " ");
                        }
                        break;
                    case ACTS:
                        if (i >= 6) {
                            output += line + lineSep;
                        }
                }

                i++;
            }
            br.close();
            FileWriter fw = new FileWriter(fileName, false); //false to replace file contents, your code has true for append to file contents
            fw.write(output);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Method to replace the small capitalized umlauts in German from a reverse point of view.
     * So 'ue' is changed to 'ü'.
     * Copied by: https://stackoverflow.com/questions/32696273/java-replace-german-umlauts/32696479
     *
     * @param line
     * @return
     */
    private static String replaceGermanUmlauts(String line) {
        return line.replace("ue", "ü")
                .replace("oe", "ö")
                .replace("ae", "ä");
    }

    /**
     * Method to verify whether a given string is numeric.
     * Copied by https://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java
     *
     * @param str Input String
     * @return true if String is numeric, false if not
     */
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static void tab2CommaSeparatedCSV(String fileName) {

        String lineSep = System.getProperty("line.separator");
        String output = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = null;
            int i = 0;
            while ((line = br.readLine()) != null) {
                output += line.replace("\t", ",") + lineSep;
            }
            br.close();
            FileWriter fw = new FileWriter(fileName, false); //false to replace file contents, your code has true for append to file contents
            fw.write(output);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeTxt(String path) {
        File file = new File(path);
        file.delete();
    }

    public static void writeString2File(String fileName, String output) {
        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println(output);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void appendImplementationFile(String finalOutputPath, String tempOutputPath, String currentImplementation, boolean firstIteration) throws IOException {
        String output = "";

        BufferedReader br = new BufferedReader(new FileReader(tempOutputPath));
        String line = null;
        int i = 0;
        while ((line = br.readLine()) != null) {
            if(firstIteration && i == 0) {
                output += "Durchführungsweg, Art der Rückdeckung," + line + System.lineSeparator();
            }else if(i > 0) {
                output += currentImplementation + "," + line + System.lineSeparator();
            }
            i++;
        }
        br.close();

        FileWriter fw = new FileWriter(finalOutputPath, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(output);
        //bw.newLine();
        bw.close();

    }


}
