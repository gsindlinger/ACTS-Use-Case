package useCase;

import edu.uta.cse.fireeye.common.Constraint;
import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.SUT;
import edu.uta.cse.fireeye.service.constraint.ConstraintManager;
import useCase.suts.SUTObject;
import useCase.suts.SUTObjectComplex;
import util.Util;

import java.util.ArrayList;

public class BuildCTWedgeTxtRun {

    public static void main(String[] args) {
        SUTObjectComplex acts = new SUTObjectComplex(2,
                SUTObject.IncludeValuesForConstraints.INCLUDE_VALUES_FOR_CONSTRAINTS);
        String fileName = "output/CT_Wedge_SUT_" + Util.getDatetimeStamp() + ".txt" ;
        buildCTWedgeTxt(fileName, acts.getSut());
    }


    public static void buildCTWedgeTxt(String fileName, SUT sut) {
        String lineSep = System.getProperty("line.separator");

        String output = "";

        output += "Model " + sut.getName() + lineSep + lineSep + "Parameters:" + lineSep;

        //Parameter
        for (Parameter param : sut.getParams()) {
            output += param.getName() + ": ";
            switch (param.getParamType()) {
                case Parameter.PARAM_TYPE_INT, Parameter.PARAM_TYPE_ENUM:
                    output+= "{ ";
                    ArrayList<String> values = param.getValues();
                    for (int i = 0; i < values.size(); i++) {
                        if (i < values.size() - 1) {
                            output += values.get(i) + ", ";
                        } else {
                            output += values.get(i);
                        }
                    }
                    ArrayList<String> invalidValues = param.getInvalidValues();
                    if (invalidValues.size() > 0) output += "; ";
                    for (int i = 0; i < invalidValues.size(); i++) {
                        if (i < invalidValues.size() - 1) {
                            output += invalidValues.get(i) + ", ";
                        } else {
                            output += invalidValues.get(i);
                        }
                    }
                    output += " };" + lineSep;
                    break;
                case Parameter.PARAM_TYPE_BOOL:
                    output += "Boolean;" + lineSep;
                    break;
            }

        }

        //Constraints
        output += lineSep + "Constraints:" + lineSep;
        ConstraintManager constraintManager = sut.getConstraintManager();
        ArrayList<Constraint> constraints = constraintManager.getConstraints();
        for (Constraint constraint : constraints) {
            String helper = constraint.getText();
            helper = helper.replaceAll("\"", "");
            helper = helper.replaceAll("&&", " AND ");
            helper = helper.replaceAll("\\|\\|"," OR ");
            output += "# " + helper + " #" + lineSep;
        }

        System.out.println(output);

        Util.writeString2File(fileName, output);
    }
}
