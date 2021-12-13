package useCase.suts;

import edu.uta.cse.fireeye.common.Constraint;
import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.SUT;
import useCase.BuildPICTRun;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SUTObjectFullImplementationComb extends SUTObjectAbstract {

    public ArrayList<String> combinationsList;
    public final int DIREKTVERSICHERUNG = 0;
    public final int PENSIONSKASSE = 1;
    public final int RUECKDECKUNGSVERSICHERUNG = 2;
    public final int UNTERSTUETZUNGSKASSE = 3;


    public SUTObjectFullImplementationComb(int interactionParameter, int currentNum) {
        super(interactionParameter);

        combinationsList = new ArrayList<>();
        combinationsList.add("Direktversicherung,keine");
        combinationsList.add("Pensionskasse,keine");
        combinationsList.add("Direktzusage,Rueckdeckungsversicherung");
        combinationsList.add("Direktzusage,Unterstuetzungskasse");

        sut.setName(combinationsList.get(currentNum));
        SUTObjectComplex.setComplexValues(this);


        ArrayList<Parameter> params;

        switch (currentNum) {
            case DIREKTVERSICHERUNG:
                //Possible Tariffs
                params = new ArrayList();
                params.add(tariff);
                Constraint cTariff = new Constraint("Tarif = \"AREven\" || Tarif = \"FR20\" || Tarif = \"HR20\"", params);
                sut.addConstraint(cTariff);

                //Amount Employee
                params = new ArrayList();
                params.add(amountEmployee);
                Constraint cAN1 = new Constraint("AN_Beitrag = 0 || AN_Beitrag = 599 || AN_Beitrag = 600 || " +
                        "AN_Beitrag = 1500", params);
                sut.addConstraint(cAN1);

                params = new ArrayList();
                params.add(amountEmployer);
                Constraint cAG1 = new Constraint(
                        "AG_Beitrag = 0 || AG_Beitrag = 599 || AG_Beitrag = 600 || " +
                                "AG_Beitrag = 1500", params);
                sut.addConstraint(cAG1);

                //Payment Beginning
                params = new ArrayList();
                params.add(paymentBeginning);
                Constraint cPAYBEG1 = new Constraint(
                        "Zuzahlung_zu_Beginn = 0 || Zuzahlung_zu_Beginn = 2000", params);
                sut.addConstraint(cPAYBEG1);

                //Amount 40b
                params = new ArrayList();
                params.add(this.amount40b);

                Constraint cAM40B1 = new Constraint(
                        "Beitrag_40b= 0 || Beitrag_40b= 2148 || Beitrag_40b= 2149", params);
                sut.addConstraint(cAM40B1);

                //Amount 363
                params.add(amount363);
                params.add(tariff);

                Constraint cAM363_1 = new Constraint(
                        "Beitrag_363= 0 || Beitrag_363= 2000 || Beitrag_363= 4068", params);
                sut.addConstraint(cAM363_1);
                break;
            case PENSIONSKASSE:

                //Possible Tariffs
                params = new ArrayList();
                params.add(tariff);
                Constraint cTariff1 = new Constraint("Tarif = \"PK10\"", params);
                sut.addConstraint(cTariff1);

                //Amount Employee
                params = new ArrayList();
                params.add(amountEmployee);
                Constraint cAN2 = new Constraint("AN_Beitrag = 0 || AN_Beitrag = 299 || AN_Beitrag = 599 || " +
                        "AN_Beitrag = 600 || AN_Beitrag = 1500", params);
                sut.addConstraint(cAN2);

                params = new ArrayList();
                params.add(amountEmployer);
                Constraint cAG2 = new Constraint(
                        "AG_Beitrag = 0 || AG_Beitrag = 299 || AG_Beitrag = 599 || " +
                                "AG_Beitrag = 600 || AG_Beitrag = 1500", params);
                sut.addConstraint(cAG2);

                //Payment Beginning
                params = new ArrayList();
                params.add(paymentBeginning);
                Constraint cPAYBEG2 = new Constraint(
                        "Zuzahlung_zu_Beginn = 0 || Zuzahlung_zu_Beginn = 299 || Zuzahlung_zu_Beginn = 2000", params);
                sut.addConstraint(cPAYBEG2);

                //Amount 40b
                params = new ArrayList();
                params.add(this.amount40b);

                Constraint cAM40B2 = new Constraint(
                        "Beitrag_40b= 0 || Beitrag_40b= 2148 || Beitrag_40b= 2149", params);
                sut.addConstraint(cAM40B2);

                //Amount 363
                params.add(amount363);
                params.add(tariff);

                Constraint cAM363_2 = new Constraint(
                        "Beitrag_363= 0 || Beitrag_363= 2000 || Beitrag_363= 4068", params);
                sut.addConstraint(cAM363_2);

                break;
            case RUECKDECKUNGSVERSICHERUNG:
                //Possible Tariffs
                params = new ArrayList();
                params.add(tariff);
                Constraint cTariff2 = new Constraint("Tarif = \"AREven\" || Tarif = \"AROdd\" || Tarif = \"RVx\" || Tarif = \"HR20\"", params);
                sut.addConstraint(cTariff2);

                //Constraint: Direktzusage/Rückdeckungsversicherung => Arbeitgeberfinanziert + nur Zuzahlung und keine BBG-Grenze
                params = new ArrayList();
                params.add(typeOfFunding);
                params.add(amount363);
                params.add(amount40b);
                params.add(paymentBeginning);
                Constraint c10 = new Constraint("Finanzierungsart = \"AG-finanziert\" && " +
                        "Beitrag_40b = 0 && Beitrag_363 = 0", params);
                sut.addConstraint(c10);

                params = new ArrayList();
                params.add(amountEmployee);
                params.add(tariff);
                Constraint cAG5 = new Constraint("Tarif = \"AREven\" || Tarif = \"HR20\" || Tarif = \"AROdd\" => " +
                        "AG_Beitrag = 0 || AG_Beitrag = 599 ||" +
                        "AG_Beitrag = 600 || AG_Beitrag = 125001", params);
                sut.addConstraint(cAG5);

                Constraint cAG6 = new Constraint("Tarif = \"RVx\" => " +
                        "AG_Beitrag = 0 || AG_Beitrag = 24 ||" +
                        "AG_Beitrag = 299 || AG_Beitrag = 1500 || AG_Beitrag = 125001", params);
                sut.addConstraint(cAG6);

                //Payment Beginning
                params = new ArrayList();
                params.add(paymentBeginning);
                params.add(tariff);
                Constraint cPAYBEG3 = new Constraint("Tarif = \"AREven\" || Tarif = \"HR20\" || Tarif = \"AROdd\" => " +
                        "Zuzahlung_zu_Beginn= 0 || Zuzahlung_zu_Beginn= 2000 || " +
                        "Zuzahlung_zu_Beginn= 1000001", params);
                sut.addConstraint(cPAYBEG3);

                Constraint cPAYBEG4 = new Constraint("Tarif = \"RVx\" => " +
                        "Zuzahlung_zu_Beginn= 0 || Zuzahlung_zu_Beginn= 299 ||" +
                        "Zuzahlung_zu_Beginn= 2000 ||Zuzahlung_zu_Beginn= 5001", params);
                sut.addConstraint(cPAYBEG4);
                break;
            case UNTERSTUETZUNGSKASSE:
                //Possible Tariffs
                params = new ArrayList();
                params.add(tariff);
                Constraint cTariff3 = new Constraint("Tarif = \"AREven\" || Tarif = \"FR20\" || Tarif = \"RVx\" || Tarif = \"HR20\"", params);
                sut.addConstraint(cTariff3);


                //Constraint: Unterstützungskasse => keine Mischfinanzierung und keine BBG-Grenze
                params = new ArrayList();
                params.add(typeOfFunding);
                params.add(amount363);
                params.add(amount40b);
                params.add(paymentBeginning);
                Constraint c9 = new Constraint("Finanzierungsart != \"Mischfinanziert\" && " +
                        "Beitrag_40b = 0 && Beitrag_363 = 0 && Zuzahlung_zu_Beginn = 0", params);
                sut.addConstraint(c9);

                params = new ArrayList();
                params.add(amountEmployee);
                params.add(tariff);
                Constraint cAN3 = new Constraint("Tarif = \"AREven\" || Tarif = \"HR20\" => " +
                        "AN_Beitrag = 0 || AN_Beitrag = 599 ||" +
                        "AN_Beitrag = 600 || AN_Beitrag = 125001", params);
                sut.addConstraint(cAN3);

                Constraint cAN3a = new Constraint("Tarif = \"FR20\" => " +
                        "AN_Beitrag = 0 || AN_Beitrag = 299 ||" +
                        "AN_Beitrag = 600 || AN_Beitrag = 125001", params);
                sut.addConstraint(cAN3a);

                Constraint cAN4 = new Constraint("Tarif = \"RVx\" => " +
                        "AN_Beitrag = 0 || AN_Beitrag = 299 ||" +
                        "AN_Beitrag = 1500 || AN_Beitrag = 125001", params);
                sut.addConstraint(cAN4);

                //Amount Employer
                params = new ArrayList();
                params.add(amountEmployer);
                params.add(tariff);
                Constraint cAG3 = new Constraint("Tarif = \"AREven\" || Tarif = \"HR20\" => " +
                        "AG_Beitrag = 0 || AG_Beitrag = 599 ||" +
                        "AG_Beitrag = 600 || AG_Beitrag = 125001", params);
                sut.addConstraint(cAG3);

                Constraint cAG3a = new Constraint("Tarif = \"FR20\" => " +
                        "AG_Beitrag = 0 || AG_Beitrag = 299 ||" +
                        "AG_Beitrag = 600 || AG_Beitrag = 125001", params);
                sut.addConstraint(cAG3a);

                Constraint cAG4 = new Constraint("Tarif = \"RVx\" => " +
                        "AG_Beitrag = 0 || AG_Beitrag = 299 ||" +
                        "AG_Beitrag = 1500 || AG_Beitrag = 125001", params);
                sut.addConstraint(cAG4);

                break;
        }
    }

    public String getStringFromCombinationsList(int i) {
        return this.combinationsList.get(i);
    }


    public static void runFullCombinations() {
        int interactionParameter = 3;
        String execPath = "pict.exe";
        String inputPath = "output/test.txt";
        String tempOutputPath = String.format("output/Full_Combination_Export/Temp_PICT_t_%d.csv", interactionParameter);
        String finalOutputPath = String.format("output/Full_Combination_Export/PICT_t_%d.csv", interactionParameter);
        new File(finalOutputPath).delete();


        for (int i = 0; i < 4; i++) {
            SUTObjectFullImplementationComb sutObject = new SUTObjectFullImplementationComb(interactionParameter, i);
            SUT sut = sutObject.getSut();
            System.out.println(sut);
            BuildPICTRun.createPictModelFromSut(sut, inputPath);

            long start = System.currentTimeMillis();
            BuildPICTRun.runPict(execPath, inputPath, tempOutputPath, interactionParameter);
            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            //System.out.println("Running Time for interaction parameter t = " + interactionParameter + ": " + timeElapsed);
            Util.removeTxt(inputPath);
            Util.tab2CommaSeparatedCSV(tempOutputPath);

            try {
                boolean firstIteration;
                if(i == 0) {
                    firstIteration = true;
                }else {
                    firstIteration = false;
                }
                Util.appendImplementationFile(finalOutputPath, tempOutputPath, sutObject.getStringFromCombinationsList(i), firstIteration);
                new File(tempOutputPath).delete();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


}

