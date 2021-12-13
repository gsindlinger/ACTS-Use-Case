package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TempExtractIterationTimes {
    public static void main(String[] args) {
        String output = "";


        for(int i = 2; i < 7; i++) {

            Pattern pattern = Pattern.compile("(?<=Running Time for interaction parameter t = " + i + ": )\\d+(?=[\\n\\r\\s])");
            Matcher matcher = pattern.matcher("output/casa_running_time.txt");

            while(matcher.find()) {
                String helper = matcher.group(0);
                output += helper + ",";

            }

            output += System.lineSeparator();

        }

        Util.writeString2File("output/casa_running_time_extracted.txt", output);

    }

}
