package useCase;

import edu.uta.cse.fireeye.common.*;
import edu.uta.cse.fireeye.common.TestGenProfile.ConstraintMode;
import edu.uta.cse.fireeye.service.engine.IpoEngine;
import useCase.suts.SUTObject;
import useCase.suts.SUTObjectAbstract;
import useCase.suts.SUTObjectComplex;
import useCase.suts.SUTObjectSimple;
import util.Tool;
import util.Util;

import java.util.function.DoubleToLongFunction;

/**
 * This test class shows how to use the API interface of ACTS to
 * build a test set.
 */








public class BuildACTSRun {
    public static void main(String[] argv) {
        SUTObject acts = new SUTObjectComplex(3,
                SUTObject.IncludeValuesForConstraints.INCLUDE_VALUES_FOR_CONSTRAINTS);


        // print out the sut
        System.out.println(acts.getSut());


        //runACTS(acts, "output/Combinations_%s.csv");

        //Util.addExpectedResults(outputString, Tool.ACTS);

    }

    public static void runACTS(SUTObjectAbstract acts, String outputString) {
        // set the test generation profile
        // randomize don't care values
        TestGenProfile.instance().setRandstar(TestGenProfile.ON);
        // not ignoring constraints
        TestGenProfile.instance().setIgnoreConstraints(false);
        TestGenProfile.instance().setConstraintMode(ConstraintMode.solver);

        // Create an IPO engine object
        IpoEngine engine = new IpoEngine(acts.getSut());

        // build a test set
        engine.build();

        // get the resulting test set
        TestSet ts = engine.getTestSet();

        // print out the test set
        TestSetWrapper wrapper = new TestSetWrapper(ts, acts.getSut());

        // print into the standard out
        // wrapper.outputInCSVFormat();

        // print into a file

        String.format(outputString, Util.getDatetimeStamp());
        wrapper.outputInCSVFormat(outputString);
    }


}

