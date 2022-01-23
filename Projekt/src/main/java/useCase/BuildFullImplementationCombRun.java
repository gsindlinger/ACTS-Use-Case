package useCase;

import useCase.suts.SUTObjectFullImplementationComb;

public class BuildFullImplementationCombRun {

    public static void main(String[] args) {
        for(int i = 2; i < 7; i++) {
            boolean isACTS = true;
            SUTObjectFullImplementationComb.runFullCombinations(i, isACTS);
        }
    }



}
