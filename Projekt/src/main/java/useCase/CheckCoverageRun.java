package useCase;

import edu.uta.cse.fireeye.common.*;
import edu.uta.cse.fireeye.common.TestGenProfile.ConstraintMode;
import useCase.suts.SUTObject;
import useCase.suts.SUTObjectSimple;
import util.CoverageCheckerModified;
import edu.uta.cse.fireeye.service.exception.OperationServiceException;

import java.util.ArrayList;

/**
 * This test class shows how to use the API interface of ACTS to
 * import an incomplete test set, and then extend it to a complete t-way test
 * set. This class also shows how to check the coverage of a test set.
 */
public class CheckCoverageRun {
    public static void main(String[] args) {

        for(int i = 2; i < 6; i++) {
            int interactionParameter = i+1;

            String fileName = "";

            /*JFileChooser chooser = new JFileChooser();
            File workingDirectory = new File(System.getProperty("user.dir"));
            chooser.setCurrentDirectory(workingDirectory);

            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = chooser.getSelectedFile().getAbsolutePath();
            }*/


            fileName = "output/ACTS_GUI_Export/t_" + i + ".csv";

            checkCoverage(interactionParameter, fileName);

        }

    }

    public static ArrayList<Float> checkCoverage(int interactionParameter, String fileName) {

        ArrayList<Float> saveResultsList = new ArrayList<>();

        // define the TCAS SUT
        SUT sut = new SUTObjectSimple(interactionParameter,
                SUTObject.IncludeValuesForConstraints.INCLUDE_VALUES_FOR_CONSTRAINTS).getSut();
        //SUT sut = new LatexBeispiel(interactionParameter).getSut();

        // import an existing test set
        TestSetWrapper wrapper = new TestSetWrapper(sut);
        try {
            wrapper.readTestSet(fileName, TestSetWrapper.CSV_R_FORMAT);
        } catch (OperationServiceException ex) {
            ex.printStackTrace();
        }

        // print out the sut
        //System.out.println(sut);

        // set constraints mode before checking coverage
        TestGenProfile.instance().setIgnoreConstraints(false);
        TestGenProfile.instance().setConstraintMode(ConstraintMode.solver);

        // check coverage with mixed strength "-1"
        CoverageCheckerModified checker = new CoverageCheckerModified(sut.getExistingTestSet(), sut, -1);
        System.out.println("Testing Interaction parameter t = " + interactionParameter);
        if (checker.check()) {
            System.out.println("Complete test set.");
        } else {
            System.out.println("Incomplete test set.");
        }

        float[] coverageRatios = checker.getCoverageRatios();


        System.out.println("SIMPLE T-WAY Coverage: " + checker.getSimpleTWayCoverage());
        saveResultsList.add(checker.getSimpleTWayCoverage());
        System.out.println("Ratio of covered variable-value combinations: " + coverageRatios[coverageRatios.length-1]);
        saveResultsList.add(coverageRatios[coverageRatios.length-1]);
        float p = 0.75f;
        System.out.println("(P-t)-Completeness for p = " + p + " and t = " + interactionParameter + ": " + checker.getPCompletnessCoverage(p));
        saveResultsList.add(checker.getPCompletnessCoverage(p));
        System.out.println("Number of generated tests: " + sut.getExistingTestSet().getNumOfTests());

        return saveResultsList;

    }


}
