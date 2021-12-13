package useCase.suts;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import edu.uta.cse.fireeye.common.Constraint;
import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.SUT;
import edu.uta.cse.fireeye.service.constraint.ConstraintManager;
import org.apache.commons.lang3.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CASAObject {

    private SUT sut;
    private int interactionParameter;
    private ArrayList<String[]> matchingTable; // [0] = Name, [1] = value
    private ArrayList<Pair<String, Integer>> sizeOfEachParameter;
    private int numberOfConstraints;


    public CASAObject(SUTObject sutObjectComplex){
        this.sut = sutObjectComplex.getSut();
        this.interactionParameter = sutObjectComplex.getInteractionParameter();
        this.sizeOfEachParameter = new ArrayList<>();
        this.matchingTable = new ArrayList();


        ArrayList<Parameter> params = sut.getParams();
        for(Parameter p : params) {
            int countOccurences = 0;
            for(String value : p.getValues()) {
                matchingTable.add(new String[]{p.getName(), value});
                countOccurences++;
            }
            sizeOfEachParameter.add(new Pair(p.getName(),countOccurences));
        }

    };

    public String getSizeOfParameters() {
        return sizeOfEachParameter.stream()
                .map(x -> x.right.toString())
                .collect(Collectors.joining(" "));
    }

    public String generateModel() {
        String lineSep = System.getProperty("line.separator");

        String ret = "" + interactionParameter + lineSep;
        ret += sizeOfEachParameter.size() + lineSep;
        ret += getSizeOfParameters();

        return ret;
    }

    public int findIndexOfParameterValue(String parameter, String value) {
        int ret = -1;
        for(int i = 0; i < matchingTable.size(); i++) {
            String[] pair = matchingTable.get(i);

            if(pair[0].equals(parameter) && pair[1].equals(value)) {
                ret = i;
                break;
            }
        }
        return ret;
    }


    public String generateConstraints() {
        this.numberOfConstraints = 0;

        String ret = "";
        String lineSep = System.getProperty("line.separator");

        //Constraints
        ConstraintManager constraintManager = sut.getConstraintManager();
        ArrayList<Constraint> constraints = constraintManager.getConstraints();
        for (Constraint constraint : constraints) {
            String constraintText = constraint.getText();
            constraintText = constraintText.replaceAll("\\s", "");


            Pattern p1 = Pattern.compile("(\\w[\\w\\d_-]*[\\w\\d])\\s?(=|\\!=)\\s?(\"\\w[\\w\\d_-]*[\\w\\d]\"|[\\d]+)");
            Matcher m1 = p1.matcher(constraintText);

            while (m1.find()) {
                String helper = m1.group(0);
                String[] split = helper.split("=");


                //Special handling for the case having inequality

                if(split[0].contains("!")) {
                    Parameter param = sut.getParam(split[0].replace("!", ""));
                    String value = split[1].substring(0, split[1].length()-1).replaceAll("\"", "");
                    String replacedValues = "( ";

                    for(int j = 0; j < param.getValues().size(); j++) {
                        String diffValues = param.getValue(j);
                        if(!diffValues.equals(value)) {
                            if(j < param.getValues().size()-2) {
                                replacedValues += findIndexOfParameterValue(param.getName(),diffValues) + " ||";
                            }else{
                                replacedValues += findIndexOfParameterValue(param.getName(),diffValues);
                            }

                        }
                    }
                    replacedValues += ")";
                    constraintText = constraintText.replaceAll(helper, replacedValues);

                }else{
                    String replacedNumber = "" + findIndexOfParameterValue(split[0], split[1].replaceAll("\"", ""));
                    constraintText = constraintText.replaceAll(helper + "(?!\\w)", replacedNumber);
                }

            }

            constraintText = constraintText.replaceAll("&&", " & ");
            constraintText = constraintText.replaceAll("\\|\\|"," | ");

            String[] splitImplication = constraintText.split("=>");
            constraintText = "! ( " + splitImplication[0] + ") |" + splitImplication[1];



            Expression<String> parsedExpression = RuleSet.toCNF(ExprParser.parse(constraintText));


            constraintText = "" + parsedExpression;



            String[] splitLines = constraintText.split("&");
            for(int j = 0; j < splitLines.length; j++) {
                numberOfConstraints++;
                int countTermsOfDisjunction = 0;

                constraintText = splitLines[j];

                Pattern p2 = Pattern.compile("(?<!\\!)[\\s\\(]\\d+[\\s\\)]");
                Matcher m2 = p2.matcher(constraintText);

                HashSet<String> valueSet = new HashSet();
                while(m2.find()) {
                    String helper = m2.group(0);
                    helper = helper.replaceAll("\\(", "");
                    helper = helper.replaceAll("\\)", "");
                    if(valueSet.add(helper)) {
                        constraintText = constraintText.replaceAll(helper, "+" + helper);
                    }
                }
                constraintText = constraintText.replaceAll("\\!", "- ");
                constraintText = constraintText.replaceAll("\\(", "");
                constraintText = constraintText.replaceAll("\\)", "");
                constraintText = constraintText.replaceAll("\\|", "");
                constraintText = constraintText.replaceAll("[ \\t]+", " ");


                Pattern p3 = Pattern.compile("[\\+-]\\d+[\\+\\s-]");
                Matcher m3 = p3.matcher(constraintText);
                while(m3.find()) {
                    String helper = m3.group(0);
                    constraintText = constraintText.replace(helper, helper.substring(0,1) + " " + helper.substring(1));
                }



                Pattern p4 = Pattern.compile("[\\+-]");
                Matcher m4 = p4.matcher(constraintText);
                while(m4.find()) {
                    countTermsOfDisjunction++;
                }



                ret+= countTermsOfDisjunction + lineSep;
                ret+=constraintText.trim() + lineSep;


            }


        }



        return numberOfConstraints + lineSep + ret;
    }

    public int convertCasaFile2CSV(String fileName) {
        String lineSep = System.getProperty("line.separator");
        String output = "";
        int i = 0;

        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = null;
            while ((line = br.readLine()) != null) {
                if(i != 0) {
                    String[] helperArray = line.split(" ");
                    String[] lineArray = new String[helperArray.length];
                    for(int j = 0; j < helperArray.length; j++) {
                        int currentIndex = Integer.parseInt(helperArray[j]);
                        lineArray[j] = matchingTable.get(currentIndex)[1];
                    }
                    output += Arrays.stream(lineArray).collect(Collectors.joining(",")) + lineSep;

                }else{
                    output += sut.getParams().stream().map(x -> x.getName()).collect(Collectors.joining(",")) + lineSep;
                }
                i++;
            }
            br.close();
            //System.out.println(output);
            FileWriter fw = new FileWriter(fileName, false); //false to replace file contents, your code has true for append to file contents
            fw.write(output);
            fw.flush();
            fw.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return i-1;
    }
}
