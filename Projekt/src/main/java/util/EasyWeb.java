package util;

import java.util.regex.Pattern;

public class EasyWeb {



    public static final String TARIF = "Tarif";
    public static final String DURCHFUEHRUNG = "Durchfuehrungsweg";
    public static final String RUECKDECKUNG = "Rueckdeckung";
    public static final String FINANZIERUNG = "Finanzierung";
    public static final String AN = "AN_Beitrag";
    public static final String AG = "AG_Beitrag";
    public static final String SINGLE_BEITRAG = "Beitrag";
    public static final String B40b = "Beitrag_40b";
    public static final String B363 = "Beitrag_363";
    public static final String ZUZAHLUNG = "Zuzahlung";

    //Names of Tarifs
    public static final String AR_EVEN = "AREven";
    public static final String AR_ODD = "AROdd";
    public static final String FR20 = "FR20";
    public static final String HR20 = "HR20";
    public static final String PK10 = "PK10";
    public static final String RV_X = "RVx";

    //Names of Durchführungswege
    public static final String DIREKTZUSAGE = "Direktzusage";
    public static final String PENSIONSKASSE = "Pensionskasse";
    public static final String DIREKTVERSICHERUNG = "Direktversicherung";

    //Names of Art der Rückdeckung
    public static final String KEINE = "keine";
    public static final String RUECKDECKUNGSVERSICHERUNG = "Rueckdeckungsversicherung";
    public static final String UNTERSTUETZUNGSKASSE = "Unterstuetzungskasse";


    private int indexTarif;
    private int indexDurchfuehrung;
    private int indexRueckdeckung;
    private int indexFinanzierung;
    private int indexAN;
    private int indexAG;



    private int indexBeitrag;
    private int index40b;
    private int index363;
    private int indexZuzahlung;

    boolean isComplexObject;



    public EasyWeb() {
        this.indexTarif = -1;
        this.indexDurchfuehrung = -1;
        this.indexRueckdeckung = -1;
        this.indexFinanzierung = -1;
        this.indexAN = -1;
        this.indexAG = -1;
        this.index40b = -1;
        this.index363 = -1;
        this.indexZuzahlung = -1;

    }



    /**
     * Method which takes the names of the headers of the csv file and tries to find
     * the index of the different amounts
     * @param headerLine Line of headers
     * @author Johannes Gabriel Sindlinger
     */
    public void findIndexes(String[] headerLine) {
        //Variable to verify that all names were found



        for(int i = 0; i < headerLine.length; i++) {

            if(headerLine[i].contains(TARIF)) {
                indexTarif = i;
            }else if(headerLine[i].contains(DURCHFUEHRUNG)){
                indexDurchfuehrung = i;
            }else if(headerLine[i].contains(RUECKDECKUNG)){
                indexRueckdeckung = i;
            }else if(headerLine[i].contains(FINANZIERUNG)){
                indexFinanzierung = i;
            }else if(headerLine[i].contains(AN)){
                indexAN = i;
                isComplexObject = true;
            }else if(headerLine[i].contains(AG)) {
                indexAG = i;
                isComplexObject = true;
            }else if(Pattern.compile("(?<=\\w)" + SINGLE_BEITRAG).matcher(headerLine[i]).find()){
                indexBeitrag = i;
                isComplexObject = false;
            }else if(headerLine[i].contains(B40b)) {
                index40b = i;
            }else if(headerLine[i].contains(B363)) {
                index363 = i;
            }else if(headerLine[i].contains(ZUZAHLUNG)) {
                indexZuzahlung = i;
            }
        }

    }

    public int getIndexTarif() {
        return indexTarif;
    }

    public int getIndexDurchfuehrung() {
        return indexDurchfuehrung;
    }

    public int getIndexRueckdeckung() {
        return indexRueckdeckung;
    }

    public int getIndexFinanzierung() {
        return indexFinanzierung;
    }

    public int getIndexAN() {
        return indexAN;
    }

    public int getIndexAG() {
        return indexAG;
    }

    public int getIndexBeitrag() {
        return indexBeitrag;
    }

    public int getIndex40b() {
        return index40b;
    }

    public int getIndex363() {
        return index363;
    }

    public int getIndexZuzahlung() {
        return indexZuzahlung;
    }

    /**
     * Method to calculate the expected result in terms of verifying whether
     * an error message occurs during execution. Takes a single row of the test data
     * as a splitted string array and checks against the constraints of the EasyWeb system.
     * For further details check the thesis paper.
     *
     * @param splittedLine
     * @return
     * @author Johannes Gabriel Sindlinger
     */
    public boolean getExpectedResults(String[] splittedLine) {
        String curTarif = splittedLine[getIndexTarif()];
        String curDurchfuehrung = splittedLine[getIndexDurchfuehrung()];
        String curRueckdeckung = splittedLine[getIndexRueckdeckung()];
        int curAmount;
        if(isComplexObject) {
            int curAN = Integer.parseInt(splittedLine[getIndexAN()]);
            int curAG = Integer.parseInt(splittedLine[getIndexAG()]);
            curAmount = curAN + curAG;
        }else{
            curAmount = Integer.parseInt(splittedLine[getIndexBeitrag()]);
        }

        int cur40b = Integer.parseInt(splittedLine[getIndex40b()]);
        int cur363 = Integer.parseInt(splittedLine[getIndex363()]);
        int curZuz = Integer.parseInt(splittedLine[getIndexZuzahlung()]);

        switch(curDurchfuehrung) {
            case DIREKTVERSICHERUNG:
                if(curAmount < 600 || curAmount + cur40b + cur363 + curZuz > 6816 ||
                        cur40b > 2148 || (curZuz < 100 && curZuz != 0)) {
                    return false;
                }
                break;
            case PENSIONSKASSE:
                //Achtung: Sonderregelung
                if(curAmount < 300 || curAmount + cur40b + cur363 + curZuz > 6816 ||
                        (curZuz < 300 && curZuz != 0) || cur40b > 2148) {
                    return false;
                }
                break;
            case DIREKTZUSAGE:
                switch(curRueckdeckung) {
                    case UNTERSTUETZUNGSKASSE:
                        //Kein Einfluss auf BBG-Grenzen
                        if(cur40b !=0 || cur363 != 0 || curZuz != 0) {
                            return false;
                        }
                        switch(curTarif) {
                            case AR_EVEN, HR20:
                                if(curAmount < 600 || curAmount > 125000) {
                                    return false;
                                }
                                break;
                            case FR20:
                                if(curAmount < 300 || curAmount > 125000) {
                                    return false;
                                }
                                break;
                            case RV_X:
                                if(curAmount < 598 || curAmount > 125000){
                                    return false;
                                }
                                break;
                        }
                        break;
                    case RUECKDECKUNGSVERSICHERUNG:
                        switch(curTarif) {
                            case AR_EVEN, AR_ODD, HR20:
                                if(curAmount < 600 || curAmount > 125000 || curZuz > 1000000) {
                                    return false;
                                }
                                break;
                            case RV_X:
                                if(curAmount < 577 || curAmount < 25 || curAmount > 125000 ||
                                        (curZuz < 500 && curZuz != 0) || curZuz > 5000) {
                                    return false;
                                }
                                break;

                        }
                        break;
                }
                break;
        }
        return true;
    }
}

