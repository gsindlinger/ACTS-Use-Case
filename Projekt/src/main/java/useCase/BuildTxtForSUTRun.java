package useCase;

import edu.uta.cse.fireeye.common.Constraint;
import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.Relation;
import edu.uta.cse.fireeye.common.SUT;
import edu.uta.cse.fireeye.service.constraint.ConstraintManager;
import edu.uta.cse.fireeye.service.engine.RelationManager;
import useCase.suts.SUTObject;
import useCase.suts.SUTObjectSimple;
import util.Util;

import java.util.ArrayList;

public class BuildTxtForSUTRun {

    public static void main(String[] args) {
        SUTObject acts = new SUTObjectSimple(2,
                SUTObject.IncludeValuesForConstraints.INCLUDE_VALUES_FOR_CONSTRAINTS);
        String fileName = "output/GUI_ACTS_SUT_Export_" + Util.getDatetimeStamp() + ".txt" ;
        buildTxtForSUT(fileName, acts.getSut());
    }


    public static void buildTxtForSUT(String fileName, SUT sut) {
        String lineSep = System.getProperty("line.separator");

        String output = "";

        output += "[System]" + lineSep + "Name: " + sut.getName() + lineSep + lineSep + "[Parameter]" + lineSep;

        //Parameter
        for (Parameter param : sut.getParams()) {
            output += param.getName() + " ";
            switch (param.getParamType()) {
                case Parameter.PARAM_TYPE_INT:
                    output += "(int) : ";
                    break;
                case Parameter.PARAM_TYPE_ENUM:
                    output += "(enum) : ";
                    break;
                case Parameter.PARAM_TYPE_BOOL:
                    output += "(bool) : ";
                    break;
            }
            ArrayList<String> values = param.getValues();
            for (int i = 0; i < values.size(); i++) {
                if (i < values.size() - 1) {
                    if (param.isBaseChoice(i)) {
                        output += "[" + values.get(i) + "], ";
                    } else {
                        output += values.get(i) + ", ";
                    }
                } else {
                    if (param.isBaseChoice(i)) {
                        output += "[" + values.get(i) + "]";
                    } else {
                        output += values.get(i);
                    }
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
            output += lineSep;
        }

        //Relation
        output += lineSep + "[Relation]" + lineSep;
        RelationManager relationManager = sut.getRelationManager();
        ArrayList<Relation> relations = relationManager.getRelations();
        int i = 1;
        for (Relation relation : relations) {
            output += "R" + i + " : (" + relation.getParamNames() + ", " + relation.getStrength() + ")" + lineSep;
            i++;

        }

        //Constraints
        output += lineSep + "[Constraint]" + lineSep;
        ConstraintManager constraintManager = sut.getConstraintManager();
        ArrayList<Constraint> constraints = constraintManager.getConstraints();
        for (Constraint constraint : constraints) {
            output += constraint.getText() + lineSep;
        }

        //System.out.println(output);


        Util.writeString2File(fileName, output);
    }


}
