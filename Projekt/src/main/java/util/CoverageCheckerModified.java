//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package util;

import edu.uta.cse.fireeye.common.Constraint;
import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.Relation;
import edu.uta.cse.fireeye.common.SUT;
import edu.uta.cse.fireeye.common.TestGenProfile;
import edu.uta.cse.fireeye.common.TestSet;
import edu.uta.cse.fireeye.common.TestGenProfile.Algorithm;
import edu.uta.cse.fireeye.service.CoverageCheckInfo;
import edu.uta.cse.fireeye.service.engine.Combinatorics;
import edu.uta.cse.fireeye.service.engine.PVPair;
import edu.uta.cse.fireeye.service.engine.Tuple;
import edu.uta.cse.fireeye.util.Util;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Copied and modified version of the {@link edu.uta.cse.fireeye.service.engine.CoverageChecker} class of the ACTS tool
 * This modified version allows to count not only the variable-value-coverage but the simple t-way-coverage.
 *
 * Edited by Johannes Gabriel Sindlinger
 */

public class CoverageCheckerModified {
    private SUT sut;
    private TestSet ts;
    private int doi;
    private int allCoveredTuples;
    private int allCoverableTuples;
    private float[] coverageRatios;
    private ArrayList<Relation> relations = new ArrayList();
    private int foundOverall = 0;
    private ArrayList<TWayCoverage> coverageForEachParameterCombination = new ArrayList();

    public CoverageCheckerModified(TestSet ts, SUT sut, int doi) {
        this.sut = sut;
        this.ts = ts;
        this.doi = doi;

        for(int i = 0; i < ts.getNumOfParams(); ++i) {
            ts.getParam(i).setActiveID(i);
        }

        this.setAllCoveredTuples(0);
        this.setAllCoverableTuples(0);
        if (-1 == doi) {
            ArrayList<Relation> relationsOriginal = sut.getRelationManager().getRelations();
            sut.getRelationManager().removeRedundantRelations();
            this.relations = sut.getRelationManager().getRelations();
            sut.getRelationManager().setRelations(relationsOriginal);
        } else {
            Relation e = new Relation(doi, sut.getParams());
            this.relations.add(e);
        }

    }

    public ArrayList<int[]> getParamGroupsForNT(Parameter param) {
        if (param.getInvalidValues().size() <= 0) {
            System.out.println("The parameter " + param.toString() + " doesn't have invalid values!");
            return null;
        } else {
            ArrayList<int[]> rval = new ArrayList();
            Iterator var4 = this.relations.iterator();

            while(true) {
                while(true) {
                    Relation relation;
                    do {
                        do {
                            if (!var4.hasNext()) {
                                if (this.relations.size() > 1) {
                                    rval = Util.removeRedundantIntArrayElements(rval);
                                }

                                return rval;
                            }

                            relation = (Relation)var4.next();
                        } while(!relation.getParams().contains(param));
                    } while(relation.getStrength() == 0);

                    if (relation.getStrength() == 1) {
                        int[] group = new int[]{this.ts.getColumnID(param.getID())};
                        rval.add(group);
                    } else {
                        ArrayList<Parameter> reducedRelationParams = new ArrayList(relation.getParams());
                        reducedRelationParams.remove(param);
                        int reducedStrength = relation.getStrength() - 1;
                        Relation reducedRelation = new Relation();
                        reducedRelation.setParams(reducedRelationParams);
                        reducedRelation.setStrength(reducedStrength);
                        ArrayList<int[]> validGroups = this.getParamGroups(reducedRelation);
                        ArrayList<int[]> groups = new ArrayList();
                        Iterator var11 = validGroups.iterator();

                        while(var11.hasNext()) {
                            int[] validGroup = (int[])var11.next();
                            int[] group = new int[1 + validGroup.length];
                            group[0] = this.ts.getColumnID(param.getID());

                            for(int i = 0; i < validGroup.length; ++i) {
                                group[i + 1] = validGroup[i];
                            }

                            groups.add(group);
                        }

                        rval.addAll(groups);
                    }
                }
            }
        }
    }

    public ArrayList<int[]> getParamGroups() {
        ArrayList<int[]> rval = new ArrayList();
        Iterator var3 = this.relations.iterator();

        while(var3.hasNext()) {
            Relation relation = (Relation)var3.next();
            ArrayList<int[]> groups = this.getParamGroups(relation);
            rval.addAll(groups);
        }

        if (this.relations.size() > 1) {
            rval = Util.removeRedundantIntArrayElements(rval);
        }

        return rval;
    }

    public ArrayList<int[]> getParamGroups(Relation relation) {
        ArrayList<int[]> rval = new ArrayList();
        ArrayList<Parameter> params = relation.getParams();
        ArrayList<int[]> paramCombos = Combinatorics.getParamCombos(params.size(), relation.getStrength());
        Iterator var6 = paramCombos.iterator();

        while(var6.hasNext()) {
            int[] paramCombo = (int[])var6.next();
            ArrayList<Parameter> allParams = this.ts.getParams();
            int[] array = new int[relation.getStrength()];
            int j = 0;

            for(int i = 0; i < paramCombo.length; ++i) {
                if (paramCombo[i] == 1) {
                    for(int k = 0; k < allParams.size(); ++k) {
                        if (((Parameter)allParams.get(k)).getID() == ((Parameter)params.get(i)).getID()) {
                            array[j++] = k;
                            break;
                        }
                    }
                }
            }

            rval.add(array);
        }

        return rval;
    }

    private void add(ArrayList<Parameter> group, ArrayList<ArrayList<Parameter>> groups) {
        boolean flag = false;
        Iterator var5 = groups.iterator();

        while(var5.hasNext()) {
            ArrayList<Parameter> it = (ArrayList)var5.next();
            if (this.isContains(group, it)) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            groups.add(group);
        }

    }

    private boolean isContains(ArrayList<Parameter> first, ArrayList<Parameter> second) {
        boolean rval = true;
        Iterator var5 = first.iterator();

        while(var5.hasNext()) {
            Parameter aParam = (Parameter)var5.next();
            boolean flag = false;
            Iterator var8 = second.iterator();

            while(var8.hasNext()) {
                Parameter bParam = (Parameter)var8.next();
                if (aParam.getInputOrOutput() == 0 && bParam.getInputOrOutput() == 0 && aParam.getID() == bParam.getID()) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                rval = false;
                break;
            }
        }

        return rval;
    }

    public int getCoveredTuples(TestSet ts) {
        int rval = 0;
        ArrayList<int[]> groups = this.getParamGroups();

        HashSet tuples;
        for(Iterator var5 = groups.iterator(); var5.hasNext(); rval += tuples.size()) {
            int[] list = (int[])var5.next();
            ArrayList<Parameter> group = this.getParamList(list);
            tuples = new HashSet();
            int[] weights = new int[group.size()];
            int maxTuples = 1;
            int w = 0;

            Parameter param;
            for(Iterator var12 = group.iterator(); var12.hasNext(); maxTuples *= param.getDomainSize()) {
                param = (Parameter)var12.next();
                weights[w++] = maxTuples;
            }

            for(int row = 0; row < ts.getNumOfTests(); ++row) {
                int hash = 0;
                boolean j = false;
                boolean hasDontCares = false;

                for(int i = 0; i < group.size(); ++i) {
                    int column = ts.getParams().indexOf(group.get(i));
                    int v = ts.getValue(row, column);
                    if (v == -1) {
                        hasDontCares = true;
                        break;
                    }

                    hash += v * weights[i];
                }

                if (!hasDontCares) {
                    tuples.add(hash);
                    if (tuples.size() == maxTuples) {
                        break;
                    }
                }
            }
        }

        return rval;
    }

    public int getTotalCountOfAllTuples() {
        int rval = 0;
        ArrayList<int[]> groups = this.getParamGroups();
        ArrayList<Parameter> params = this.ts.getParams();

        int countOfTuples;
        int i;
        for(Iterator var5 = groups.iterator(); var5.hasNext(); rval += countOfTuples) {
            int[] group = (int[])var5.next();
            countOfTuples = 1;
            int[] var10 = group;
            int var9 = group.length;

            for(i = 0; i < var9; ++i) {
                countOfTuples = var10[i];
                countOfTuples *= ((Parameter)params.get(countOfTuples)).getDomainSize();
            }
        }

        ArrayList<int[]> groupsForNT = new ArrayList();
        Iterator var14 = this.sut.getParams().iterator();

        while(var14.hasNext()) {
            Parameter param = (Parameter)var14.next();
            if (param.getInvalidValues().size() > 0) {
                ArrayList<int[]> groupsForOneInvalidParam = this.getParamGroupsForNT(param);
                groupsForNT.addAll(groupsForOneInvalidParam);
            }
        }

        for(var14 = groupsForNT.iterator(); var14.hasNext(); rval += countOfTuples) {
            int[] group = (int[])var14.next();
            countOfTuples = ((Parameter)params.get(group[0])).getInvalidValues().size();

            for(i = 1; i < group.length; ++i) {
                countOfTuples *= ((Parameter)params.get(group[i])).getDomainSize();
            }
        }

        return rval;
    }

    public int getTotalCountOfCoverableTuples() {
        if (TestGenProfile.instance().isIgnoreConstraints()) {
            return this.getTotalCountOfAllTuples();
        } else {
            ArrayList[] missingTuples = this.generateAllMissingTuples();
            return this.getTotalCount(missingTuples);
        }
    }

    public boolean check() {
        boolean rval = true;
        if (this.sut.getNumOfParams() != this.ts.getNumOfParams()) {
            return false;
        } else {
            boolean progressOn = TestGenProfile.instance().isProgressOn();
            Boolean originalIgnoreConstraintsFlag = TestGenProfile.instance().isIgnoreConstraints();
            ArrayList<int[]>[] missingTuples = this.generateAllMissingTuples();


            if (missingTuples == null) {
                return false;
            } else {
                int totalCountOfCoverableTuples = this.getTotalCount(missingTuples);
                int countOfCoveredTuples = 0;
                int numOfTests = this.ts.getNumOfTests();

                int row;
                for(row = 0; row < numOfTests; ++row) {
                    if (Thread.currentThread().isInterrupted()) {
                        TestGenProfile.instance().setIgnoreConstraints(originalIgnoreConstraintsFlag);
                        return false;
                    }

                    if (progressOn) {
                        System.out.print(".");
                    }

                    int invalidValueCounter = 0;
                    boolean invalidValueLastIndex = true;

                    int numOfParams;
                    for(numOfParams = 0; numOfParams < this.ts.getTest(row).length; ++numOfParams) {
                        if (this.ts.getTest(row)[numOfParams] <= -10) {
                            ++invalidValueCounter;
                        }
                    }

                    int[] expandedRow;
                    int col;
                    if (invalidValueCounter == 0) {
                        numOfParams = this.ts.getNumOfParams();
                        expandedRow = new int[numOfParams];
                        ArrayList<Parameter> cmParams = this.sut.getConstraintManager().getParams();
                        if (cmParams != null) {
                            for(int j = 0; j < numOfParams; ++j) {
                                Parameter param = (Parameter)cmParams.get(j);
                                col = this.ts.getColumnID(param.getID());
                                col = this.ts.getValue(row, col);
                                expandedRow[j] = col;
                            }
                        } else {
                            expandedRow = this.ts.getTest(row);
                        }

                        TestGenProfile.instance().setIgnoreConstraints(originalIgnoreConstraintsFlag);
                        if (this.sut.getConstraintManager().isValid(expandedRow)) {
                            countOfCoveredTuples += this.removeCoveredTuples(row, missingTuples);

                        } else {
                            System.out.println("Found a positive test violating constraints at " + (row + 1) + "-th row");
                        }
                    } else if (invalidValueCounter != 1) {
                        System.out.println("Found a test containing two or more invalid values at " + (row + 1) + "-th row");
                    } else {
                        numOfParams = this.ts.getNumOfParams();
                        expandedRow = new int[numOfParams];

                        for(int i = 0; i < this.ts.getTest(row).length; ++i) {
                            if (this.ts.getTest(row)[i] <= -10) {
                                expandedRow[i] = -1 * (this.ts.getTest(row)[i] - -10) + this.ts.getParam(i).getDomainSize();
                            } else {
                                expandedRow[i] = this.ts.getTest(row)[i];
                            }
                        }

                        int[] reorderedRow = new int[numOfParams];
                        ArrayList<Parameter> cmParams = this.sut.getConstraintManagerForNT().getParams();
                        if (cmParams != null) {
                            for(int j = 0; j < numOfParams; ++j) {
                                Parameter param = (Parameter)cmParams.get(j);
                                col = this.ts.getColumnID(param.getID());
                                int value = expandedRow[col];
                                reorderedRow[j] = value;
                            }
                        } else {
                            reorderedRow = expandedRow;
                        }

                        if (originalIgnoreConstraintsFlag) {
                            TestGenProfile.instance().setIgnoreConstraints(false);
                        }

                        if (this.sut.getConstraintManagerForNT().isValid(reorderedRow)) {
                            countOfCoveredTuples += this.removeCoveredTuplesForNT(row, missingTuples);
                        } else {
                            System.out.println("Found a negative test violating constraints at " + (row + 1) + "-th row");
                        }
                    }
                }

                TestGenProfile.instance().setIgnoreConstraints(originalIgnoreConstraintsFlag);
                row = this.getTotalCount(missingTuples);
                if (row > 0) {
                    rval = false;
                }

                this.setAllCoverableTuples(totalCountOfCoverableTuples);
                this.setAllCoveredTuples(countOfCoveredTuples);
                if (progressOn) {
                    System.out.println("\n\nCoverage Statistics:");
                    System.out.println("--------------------------------------");
                    System.out.println("Total Count of All Possible Combinations: " + this.getTotalCountOfAllTuples());
                    System.out.println("Total Count of Coverable Combinations: " + totalCountOfCoverableTuples);
                    System.out.println("Count of Covered Combinations: " + countOfCoveredTuples);
                    System.out.println("Count of Missed Combinations: " + row);
                }

                return rval;
            }
        }
    }

    private void printMissingTuples(ArrayList<int[]>[] missingTuples) {
        for(ArrayList<int[]> ar : missingTuples) {
            System.out.print("[");
            for(int[] list : ar) {
                System.out.print("[");
                for(int i = 0; i<list.length; i++) {

                    System.out.print(list[i]);
                    if(i != list.length-1) {
                        System.out.print(",");
                    }
                }
                System.out.print("],");
            }
            System.out.print("]");
            System.out.println();


        }
    }

    private int getProgressInfoUpdateValue() {
        int testCount = this.ts.getNumOfTests();
        if (testCount <= 100) {
            return testCount;
        } else {
            BigDecimal noOfTestCases = new BigDecimal(testCount);
            BigDecimal divisor = new BigDecimal(100);
            BigDecimal multiplier = new BigDecimal(10);
            BigDecimal numerator = noOfTestCases.multiply(multiplier);
            BigDecimal result = numerator.divide(divisor, 1);
            return result.intValue();
        }
    }

    public float getCoverageRatio(int index) {
        if (this.coverageRatios == null) {
            this.computeCoverageRatios();
        }

        return this.coverageRatios[index];
    }

    public float[] getCoverageRatios() {
        if (this.coverageRatios == null) {
            this.computeCoverageRatios();
        }

        return this.coverageRatios;
    }

    public float[] getCoverageRatios(CoverageCheckInfo cover) {
        if (this.coverageRatios == null) {
            this.computeCoverageRatios(cover);
        }

        return this.coverageRatios;
    }

    private void computeCoverageRatios(CoverageCheckInfo cover) {
        this.coverageRatios = new float[this.ts.getNumOfTests()];
        ArrayList[] missingTuples = this.generateAllMissingTuples();
        if (missingTuples == null) {
            this.coverageRatios = null;
        } else {
            int totalCountOfTuples = this.getTotalCount(missingTuples);
            int countOfCoveredTuples = 0;
            int inc = this.getProgressInfoUpdateValue();
            int sum = 0;
            int mult = 1;
            int numOfTests = this.ts.getNumOfTests();

            for(int row = 0; row < numOfTests; ++row) {
                if (Thread.currentThread().isInterrupted()) {
                    this.coverageRatios = null;
                    return;
                }

                int invalidValueCounter = 0;

                for(int j = 0; j < this.ts.getTest(row).length; ++j) {
                    if (this.ts.getTest(row)[j] <= -10) {
                        ++invalidValueCounter;
                    }
                }

                if (invalidValueCounter == 0) {
                    countOfCoveredTuples += this.removeCoveredTuples(row, missingTuples);
                } else if (invalidValueCounter == 1) {
                    countOfCoveredTuples += this.removeCoveredTuplesForNT(row, missingTuples);
                }

                this.coverageRatios[row] = (float)countOfCoveredTuples / (float)totalCountOfTuples;
                if (sum == inc) {
                    cover.setProgress(10 * mult);
                    sum = 0;
                    ++mult;
                }

                ++sum;
            }

        }
    }


    /* Modified to calculate simple t-way coverage */
    private void computeCoverageRatios() {
        this.coverageRatios = new float[this.ts.getNumOfTests()];
        ArrayList[] missingTuples = this.generateAllMissingTuples();

        ArrayList[] missingTuplesHelper = this.generateAllMissingTuples();

        ArrayList<int[]> groups = this.getParamGroups();
        if (missingTuples == null) {
            this.coverageRatios = null;
        } else {
            int totalCountOfTuples = this.getTotalCount(missingTuples);
            int countOfCoveredTuples = 0;
            int numOfTests = this.ts.getNumOfTests();

            for(int row = 0; row < numOfTests; ++row) {
                countOfCoveredTuples += this.removeCoveredTuples(row, missingTuples);
                this.coverageRatios[row] = (float)countOfCoveredTuples / (float)totalCountOfTuples;

            }

            /*Modification: Count occurrences of tuples which are still missing after removing all tuples from test data*/
            for(int i = 0; i < missingTuples.length; i++) {

                int[] groupHelper = groups.get(i);
                ArrayList<Parameter> paramList = new ArrayList();
                for(int j : groupHelper) {
                    paramList.add(this.ts.getParam(j));
                }
                coverageForEachParameterCombination.add(new TWayCoverage(paramList, 1- (float) missingTuples[i].size()/missingTuplesHelper[i].size()));
            }

        }
    }


    /**
     * Returns the simple T-way coverage for the given test set
     * This method uses the list of TWayCoverages for every parameter combination which is generated by the
     * computeCoverageRatios() method. So if the corresponding object is null, this object firstly must be generated.
     *
     * @return T-Way-Coverage
     * @author Johannes Gabriel Sindlinger
     */
    public float getSimpleTWayCoverage() {
        if (coverageForEachParameterCombination == null) {
            computeCoverageRatios();
        }

        float sum = 0;

        for(TWayCoverage cov : coverageForEachParameterCombination) {
            if(cov.getCoverage() < 1) {
                sum++;
            }
        }
        return (1-sum/getParamGroups().size());
    }

    /**
     * Calculates the (p-t)-completeness for given ratio of p. For more details check out the section in the thesis paper.
     * The interaction parameter t is given by the definition of the test set and won't be specified via this method.
     * @param p
     * @return (P-t)-Completeness
     * @author Johannes Gabriel Sindlinger
     */
    public float getPCompletnessCoverage(float p) {
        if (coverageForEachParameterCombination == null) {
            computeCoverageRatios();
        }

        float sum = 0;

        for(TWayCoverage cov : coverageForEachParameterCombination) {
            if(cov.getCoverage() <= p) {
                sum++;
            }
        }
        return (1-sum/getParamGroups().size());
    }

    private ArrayList[] generateAllMissingTuples() {

        ArrayList<int[]> groups = this.getParamGroups();
        int numberOfGroupsForNT = 0;
        ArrayList<int[]> groupsForNT = new ArrayList();
        Iterator var5 = this.sut.getParams().iterator();

        while(var5.hasNext()) {
            Parameter param = (Parameter)var5.next();
            if (param.getInvalidValues().size() > 0) {
                ArrayList<int[]> groupsForOneInvalidParam = this.getParamGroupsForNT(param);
                groupsForNT.addAll(groupsForOneInvalidParam);
                numberOfGroupsForNT += groupsForOneInvalidParam.size();
            }
        }

        int numOfParamCombos = groups.size() + numberOfGroupsForNT;
        ArrayList[] rval = new ArrayList[numOfParamCombos];
        int index = 0;

        ArrayList<Parameter> group;
        ArrayList validValueCombos;
        for(Iterator var8 = groups.iterator(); var8.hasNext(); rval[index++] = validValueCombos) {
            int[] list = (int[])var8.next();
            if (Thread.currentThread().isInterrupted()) {
                return null;
            }

            group = this.getParamList(list);
            ArrayList<int[]> valueCombos = Combinatorics.getValueCombos(group);
            validValueCombos = new ArrayList();

            for(int[] valueCombo : valueCombos) {
                Tuple tuple = this.buildTuple(group, valueCombo);
                if (this.sut.getConstraintManager().isValid(tuple)) {
                    validValueCombos.add(valueCombo);
                }
            }

        }

        Boolean originalIgnoreConstraintsFlag = TestGenProfile.instance().isIgnoreConstraints();
        if (!groupsForNT.isEmpty()) {
            if (originalIgnoreConstraintsFlag) {
                TestGenProfile.instance().setIgnoreConstraints(false);
            }

            if (this.sut.getConstraintManagerForNT().choco == null || this.sut.getConstraintManagerForNT().isIgnoreConstraints() != originalIgnoreConstraintsFlag || TestGenProfile.instance().getAlgorithm() == Algorithm.basechoice) {
                ArrayList<Parameter> expandedParams = expandedParametersForNT(this.ts.getParams());
                initConstraintManagerForNT(this.sut, expandedParams, originalIgnoreConstraintsFlag);
            }
        }

        Iterator var28 = groupsForNT.iterator();

        while(var28.hasNext()) {
            int[] list = (int[])var28.next();
            if (Thread.currentThread().isInterrupted()) {
                return null;
            }

            group = this.getParamList(list);
            Parameter invalidParam = (Parameter)group.get(0);
            int i;
            ArrayList invalidValueCombos;
            if (group.size() == 1) {
                invalidValueCombos = new ArrayList();

                for(i = invalidParam.getInvalidValues().size() - 1; i >= 0; --i) {
                    int[] invalidValueCombo = new int[1];
                    i = -10 + -1 * i;
                    invalidValueCombo[0] = i;
                    Tuple tuple = this.buildTuple(group, invalidValueCombo);
                    int invalidValueIndexInExpandedParam = invalidParam.getDomainSize() + i;
                    tuple.getPair(invalidParam).value = invalidValueIndexInExpandedParam;
                    if (this.sut.getConstraintManagerForNT().isValid(tuple)) {
                        invalidValueCombos.add(invalidValueCombo);
                    }
                }

                rval[index++] = invalidValueCombos;
            } else {
                invalidValueCombos = new ArrayList(group.subList(1, group.size()));
                ArrayList<int[]> valueCombos = Combinatorics.getValueCombos(invalidValueCombos);
                ArrayList<int[]> invalidPlusValidValuesCombos = new ArrayList();

                for(i = invalidParam.getInvalidValues().size() - 1; i >= 0; --i) {
                    Iterator var17 = valueCombos.iterator();

                    while(var17.hasNext()) {
                        int[] valueCombo = (int[])var17.next();
                        int[] invalidPlusValidValuesCombo = new int[1 + valueCombo.length];
                        int invalidValueIndex = -10 + -1 * i;
                        invalidPlusValidValuesCombo[0] = invalidValueIndex;

                        for(int j = 0; j < valueCombo.length; ++j) {
                            invalidPlusValidValuesCombo[j + 1] = valueCombo[j];
                        }

                        Tuple tuple = this.buildTuple(group, invalidPlusValidValuesCombo);
                        int invalidValueIndexInExpandedParam = invalidParam.getDomainSize() + i;
                        tuple.getPair(invalidParam).value = invalidValueIndexInExpandedParam;
                        if (this.sut.getConstraintManagerForNT().isValid(tuple)) {
                            invalidPlusValidValuesCombos.add(invalidPlusValidValuesCombo);
                        }
                    }
                }

                rval[index++] = invalidPlusValidValuesCombos;
            }
        }

        TestGenProfile.instance().setIgnoreConstraints(originalIgnoreConstraintsFlag);
        return rval;
    }

    private Tuple buildTuple(ArrayList<Parameter> group, int[] valueCombo) {
        Tuple rval = new Tuple(group.size());
        for(int i = 0; i<group.size(); i++) {
            PVPair pair = new PVPair(group.get(i), valueCombo[i]);
            rval.addPair(pair);
        }

        return rval;
    }

    private int getTotalCount(ArrayList<int[]>[] missingTuples) {
        int rval = 0;

        for(int i = 0; i < missingTuples.length; ++i) {
            if (missingTuples[i].size() > 0) {
                rval += missingTuples[i].size();
            }
        }

        return rval;
    }

    private int removeCoveredTuplesForNT(int row, ArrayList<int[]>[] missingTuples) {
        int rval = 0;
        ArrayList<int[]> groups = this.getParamGroups();
        int numberOfGroupsForNT = 0;
        ArrayList<int[]> groupsForNT = new ArrayList();
        Iterator var8 = this.sut.getParams().iterator();

        while(var8.hasNext()) {
            Parameter param = (Parameter)var8.next();
            if (param.getInvalidValues().size() > 0) {
                ArrayList<int[]> groupsForOneInvalidParam = this.getParamGroupsForNT(param);
                groupsForNT.addAll(groupsForOneInvalidParam);
                numberOfGroupsForNT += groupsForOneInvalidParam.size();
            }
        }

        int k = groups.size();

        for(Iterator var18 = groupsForNT.iterator(); var18.hasNext(); ++k) {
            int[] list = (int[])var18.next();
            ArrayList<Parameter> group = this.getParamList(list);
            int[] values = new int[group.size()];
            int j = 0;
            boolean hasDontCares = false;

            int found;
            for(found = 0; found < group.size(); ++found) {
                int column = this.ts.getColumnID(((Parameter)group.get(found)).getID());
                if ((values[j++] = this.ts.getValue(row, column)) == -1) {
                    hasDontCares = true;
                    break;
                }
            }

            if (!hasDontCares) {
                found = this.search(values, missingTuples[k]);
                if (found != -1) {
                    ++rval;
                    missingTuples[k].remove(found);
                } else {
                    this.search(values, missingTuples[k]);
                }
            }
        }

        return rval;
    }

    private int removeCoveredTuples(int row, ArrayList<int[]>[] missingTuples) {
        int rval = 0;
        ArrayList<int[]> groups = this.getParamGroups();
        int k = 0;

        for(Iterator var7 = groups.iterator(); var7.hasNext(); ++k) {
            int[] list = (int[])var7.next();
            ArrayList<Parameter> group = this.getParamList(list);
            int[] values = new int[group.size()];
            int j = 0;
            boolean hasDontCares = false;

            int found;
            for(found = 0; found < group.size(); ++found) {
                int column = this.ts.getColumnID(((Parameter) group.get(found)).getID());
                if ((values[j++] = this.ts.getValue(row, column)) == -1) {
                    hasDontCares = true;
                    break;
                }
            }
            /*
            System.out.println(group);
            System.out.println("Values: " + Arrays.toString(values));
            System.out.println("Missing Tuples: ");
            printMissingTuples(missingTuples);
            System.out.println("k: " + k);
            System.out.println();

             */
            if (!hasDontCares) {
                found = this.search(values, missingTuples[k]);
                if (found != -1) {
                    ++rval;
                    missingTuples[k].remove(found);
                } else {
                    this.search(values, missingTuples[k]);
                }

            }
        }


        //System.out.println("Found tuples: " + foundOverall);

        return rval;
    }

    private int search(int[] combo, ArrayList<int[]> combos) {
        int rval = -1;
        int start = 0;
        int end = combos.size() - 1;

        while(start <= end) {
            int mid = (start + end) / 2;
            if (this.compare(combo, (int[])combos.get(mid)) < 0) {
                end = mid - 1;
            } else {
                if (this.compare(combo, (int[])combos.get(mid)) <= 0) {
                    rval = mid;
                    break;
                }

                start = mid + 1;
            }
        }

        return rval;
    }

    private int compare(int[] a, int[] b) {
        int rval = 0;

        for(int i = 0; i < a.length; ++i) {
            if (a[i] < b[i]) {
                rval = -1;
                break;
            }

            if (a[i] > b[i]) {
                rval = 1;
                break;
            }
        }

        return rval;
    }

    public ArrayList<Parameter> getParamList(int[] t) {
        ArrayList<Parameter> list = new ArrayList();
        ArrayList<Parameter> params = this.ts.getParams();
        int[] var7 = t;
        int var6 = t.length;

        for(int var5 = 0; var5 < var6; ++var5) {
            int i = var7[var5];
            list.add((Parameter)params.get(i));
        }

        return list;
    }

    public TestSet getTs() {
        return this.ts;
    }

    public void setTs(TestSet ts) {
        this.ts = ts;
    }

    public int getAllCoveredTuples() {
        return this.allCoveredTuples;
    }

    public void setAllCoveredTuples(int allPossibleTuples) {
        this.allCoveredTuples = allPossibleTuples;
    }

    public int getAllCoverableTuples() {
        return this.allCoverableTuples;
    }

    public void setAllCoverableTuples(int allCoverableTuples) {
        this.allCoverableTuples = allCoverableTuples;
    }

    public int getDoi() {
        return this.doi;
    }

    public static void initConstraintManagerForNT(SUT sut, ArrayList<Parameter> expandedParamsInOrder, boolean ignoreConstraints) {
        ArrayList<Constraint> expandedConstraints = new ArrayList();
        sut.getConstraintManagerForNT().setIgnoreConstraints(ignoreConstraints);
        ArrayList additionalConstraints;
        Iterator var6;
        ArrayList expandedParamsInAdditionalConstraint;
        if (!ignoreConstraints) {
            additionalConstraints = new ArrayList(sut.getConstraintManager().getConstraints());
            var6 = additionalConstraints.iterator();

            while(var6.hasNext()) {
                Constraint originalConstraint = (Constraint)var6.next();
                expandedParamsInAdditionalConstraint = originalConstraint.getParams();
                ArrayList<Parameter> expandedParamsInCons = expandedParametersForNT(expandedParamsInAdditionalConstraint);
                Constraint expandedUserConstraint = new Constraint(originalConstraint.getText(), expandedParamsInCons);
                expandedConstraints.add(expandedUserConstraint);
            }
        }

        additionalConstraints = new ArrayList();
        var6 = sut.getParameters().iterator();

        String ifText;
        Iterator var26;
        while(var6.hasNext()) {
            Parameter originalParam = (Parameter)var6.next();
            var26 = originalParam.getInvalidValues().iterator();

            label87:
            while(var26.hasNext()) {
                String invalidValue = (String)var26.next();
                ArrayList<Parameter> paramsInAdditionalConstraint = new ArrayList();
                String invalidValueStr = invalidValue;
                if (originalParam.getParamType() == 1) {
                    invalidValueStr = "\"" + invalidValue + "\"";
                }

                ifText = originalParam.getName() + " = " + invalidValueStr;
                paramsInAdditionalConstraint.add(originalParam);
                String thenText = "";
                int outsideCounter = 0;
                Iterator var15 = sut.getParameters().iterator();

                while(true) {
                    Parameter anotherParam;
                    do {
                        do {
                            if (!var15.hasNext()) {
                                if (!thenText.isEmpty()) {
                                    String constraintText = ifText + " => " + thenText;
                                    expandedParamsInAdditionalConstraint = expandedParametersForNT(paramsInAdditionalConstraint);
                                    Constraint additionalConstraint = new Constraint(constraintText, expandedParamsInAdditionalConstraint);
                                    additionalConstraints.add(additionalConstraint);
                                }
                                continue label87;
                            }

                            anotherParam = (Parameter)var15.next();
                        } while(anotherParam.getName().equals(originalParam.getName()));
                    } while(anotherParam.getInvalidValues().isEmpty());

                    if (outsideCounter >= 1) {
                        thenText = thenText + " && ";
                    }

                    String appendStr = "(";
                    int insideCounter = 0;

                    for(Iterator var19 = anotherParam.getInvalidValues().iterator(); var19.hasNext(); ++insideCounter) {
                        String invalidValueOfAnotherParam = (String)var19.next();
                        if (insideCounter >= 1) {
                            appendStr = appendStr + " && ";
                        }

                        String invalidValueOfAnotherParamStr = invalidValueOfAnotherParam;
                        if (anotherParam.getParamType() == 1) {
                            invalidValueOfAnotherParamStr = "\"" + invalidValueOfAnotherParam + "\"";
                        }

                        appendStr = appendStr + anotherParam.getName() + " != " + invalidValueOfAnotherParamStr;
                        paramsInAdditionalConstraint.add(anotherParam);
                    }

                    appendStr = appendStr + ")";
                    thenText = thenText + appendStr;
                    ++outsideCounter;
                }
            }
        }

        ArrayList<Parameter> paramsInAdditionalConstraint = new ArrayList();
        String constraintText = "";
        var26 = sut.getParameters().iterator();

        while(var26.hasNext()) {
            Parameter originalParam = (Parameter)var26.next();
            Iterator var30 = originalParam.getInvalidValues().iterator();

            while(var30.hasNext()) {
                String invalidValue = (String)var30.next();
                ifText = invalidValue;
                if (originalParam.getParamType() == 1) {
                    ifText = "\"" + invalidValue + "\"";
                }

                if (!constraintText.isEmpty()) {
                    constraintText = constraintText + " || ";
                }

                constraintText = constraintText + originalParam.getName() + " = " + ifText;
                paramsInAdditionalConstraint.add(originalParam);
            }
        }

        expandedParamsInAdditionalConstraint = expandedParametersForNT(paramsInAdditionalConstraint);
        Constraint additionalConstraint = new Constraint(constraintText, expandedParamsInAdditionalConstraint);
        additionalConstraints.add(additionalConstraint);
        expandedConstraints.addAll(additionalConstraints);
        sut.getConstraintManagerForNT().setConstraints(expandedConstraints);
        sut.getConstraintManagerForNT().init(expandedParamsInOrder);
    }

    public static ArrayList<Parameter> expandedParametersForNT(ArrayList<Parameter> originalParams) {
        ArrayList<Parameter> expandedParams = new ArrayList();
        Iterator var3 = originalParams.iterator();

        while(var3.hasNext()) {
            Parameter originalParam = (Parameter)var3.next();
            Parameter expandedParam = new Parameter(originalParam.getName());
            expandedParam.setType(originalParam.getParamType());
            expandedParam.setID(originalParam.getID());
            expandedParam.setActiveID(originalParam.getActiveID());
            Iterator var6 = originalParam.getValues().iterator();

            String invalidValue;
            while(var6.hasNext()) {
                invalidValue = (String)var6.next();
                expandedParam.addValue(invalidValue);
            }

            var6 = originalParam.getInvalidValues().iterator();

            while(var6.hasNext()) {
                invalidValue = (String)var6.next();
                expandedParam.addValue(invalidValue);
            }

            expandedParam.setBaseChoiceValues(originalParam.getBaseChoiceValues());
            expandedParam.setInvalidValues(originalParam.getInvalidValues());
            expandedParams.add(expandedParam);
        }

        return expandedParams;
    }
}
