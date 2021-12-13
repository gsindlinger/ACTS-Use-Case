package useCase;

import edu.uta.cse.fireeye.common.*;
import edu.uta.cse.fireeye.common.TestGenProfile.ConstraintMode;
import edu.uta.cse.fireeye.service.engine.CoverageChecker;
import util.CoverageCheckerModified;
import edu.uta.cse.fireeye.service.exception.OperationServiceException;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;

/**
 * This test class shows how to use the API interface of ACTS to
 * import an incomplete test set, and then extend it to a complete t-way test
 * set. This class also shows how to check the coverage of a test set.
 */
public class CheckCoverageDemo {
    public static void main(String[] argv) {
        int interactionParameter = 4;

        // define the TCAS SUT
        SUT sut = new ACTSObject(interactionParameter).getSut();
        //SUT sut = new LatexBeispiel(interactionParameter).getSut();

        String fileName = "";

        /*JFileChooser chooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);

        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileName = chooser.getSelectedFile().getAbsolutePath();
        }*/

        fileName = "C:\\Users\\Gabriel\\Dropbox\\02_Uni\\Bachelorarbeit_21\\Projekte\\ACTS_Use_Case\\output\\Combinations_2021-11-15-14-28.csv";
        // import an existing test set
        TestSetWrapper wrapper = new TestSetWrapper(sut);
        try {
            wrapper.readTestSet(fileName, TestSetWrapper.CSV_R_FORMAT);
        } catch (OperationServiceException ex) {
            ex.printStackTrace();
        }

        // print out the sut
        System.out.println(sut);

        // set constraints mode before checking coverage
        TestGenProfile.instance().setIgnoreConstraints(false);
        TestGenProfile.instance().setConstraintMode(ConstraintMode.solver);

        // check coverage with mixed strength "-1"
        CoverageCheckerModified checker = new CoverageCheckerModified(sut.getExistingTestSet(), sut, -1);
        System.out.println("Interaction parameter t = " + interactionParameter);
        if (checker.check()) {
            System.out.println("Complete test set.");
        } else {
            System.out.println("Incomplete test set.");
        }

        float[] coverageRatios = checker.getCoverageRatios();

        System.out.println("SIMPLE T-WAY Coverage: " + checker.getSimpleTWayCoverage());
        System.out.println("Ratio of covered variable-value combinations: " + coverageRatios[coverageRatios.length-1]);
        float p = 0.75f;
        System.out.println("(P-t)-Completeness for p = " + p + " and t = " + interactionParameter + ": " + checker.getPCompletnessCoverage(p));






        /*
        // Set test generation mode as extend
        // This must be done before creating the IPO engine object
        TestGenProfile.instance().setMode(TestGenProfile.PV_EXTEND);

        // Create an IPO engine object
        IpoEngine engine = new IpoEngine(sut);

        // build this test set
        engine.build();

        // get the resulting test set
        TestSet ts = engine.getTestSet();

        // print out the test set
        TestSetWrapper wrapper2 = new TestSetWrapper(ts, sut);

        // print into the standard out
        wrapper2.outputInCSVFormat();

        // print into a file
        wrapper2.outputInCSVFormat("output.txt");

         */
    }


}
