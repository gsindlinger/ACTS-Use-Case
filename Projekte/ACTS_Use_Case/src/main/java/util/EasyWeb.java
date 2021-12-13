package useCase;

public class EasyWeb {
    public static final String TARIF = "Tarif";
    public static final String DURCHFUEHRUNG = "Durchfuehrungsweg";
    public static final String RUECKDECKUNG = "Rueckdeckung";
    public static final String FINANZIERUNG = "Finanzierung";
    public static final String AN = "AN";
    public static final String AG = "AG";
    public static final String B40b = "40b";
    public static final String B363 = "363";
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
    public static final String RUECKDECKUNGSKASSE = "Rueckdeckungskasse";
    public static final String UNTERSTUETZUNGSKASSE = "Unterstuetzungskasse";


    private int indexTarif;
    private int indexDurchfuehrung;
    private int indexRueckdeckung;
    private int indexFinanzierung;
    private int indexAN;
    private int indexAG;
    private int index40b;
    private int index363;
    private int indexZuzahlung;



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
        int countOccurences = 0;

        for(int i = 0; i < headerLine.length; i++) {

            if(headerLine[i].contains(TARIF)) {
                indexTarif = i;
                countOccurences++;
                continue;
            }else if(headerLine[i].contains(DURCHFUEHRUNG)){
                indexDurchfuehrung = i;
                countOccurences++;
                continue;
            }else if(headerLine[i].contains(RUECKDECKUNG)){
                indexRueckdeckung = i;
                countOccurences++;
                continue;
            }else if(headerLine[i].contains(FINANZIERUNG)){
                indexFinanzierung = i;
                countOccurences++;
                continue;
            }else if(headerLine[i].contains(AN)){
                indexAN = i;
                countOccurences++;
                continue;
            }else if(headerLine[i].contains(AG)){
                indexAG = i;
                countOccurences++;
                continue;
            }else if(headerLine[i].contains(B40b)) {
                index40b = i;
                countOccurences++;
                continue;
            }else if(headerLine[i].contains(B363)) {
                index363 = i;
                countOccurences++;
                continue;
            }else if(headerLine[i].contains(ZUZAHLUNG)) {
                indexZuzahlung = i;
                countOccurences++;
                continue;
            }
        }

        if(countOccurences != 9) {
            System.err.println("Couldn't find all header names! Please check the headers.");
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
        int curAN = Integer.parseInt(splittedLine[getIndexAN()]);
        int curAG = Integer.parseInt(splittedLine[getIndexAG()]);
        int cur40b = Integer.parseInt(splittedLine[getIndex40b()]);
        int cur363 = Integer.parseInt(splittedLine[getIndex363()]);
        int curZuz = Integer.parseInt(splittedLine[getIndexZuzahlung()]);

        switch(curDurchfuehrung) {
            case DIREKTVERSICHERUNG:
                if(curAN + curAG < 600 || curAN + curAG + cur40b + cur363 + curZuz > 6816 || cur40b > 2148) {
                    return false;
                }
                break;
            case PENSIONSKASSE:
                //Achtung: Sonderregelung
                if(curAN + curAG < 300 || curAN + curAG + cur40b + cur363 + curZuz > 6816 ||
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
                                if(curAN + curAG < 600 || curAN + curAG > 125000) {
                                    return false;
                                }
                                break;
                            case RV_X:
                                if(curAN + curAG < 598 || curAN + curAG > 125000){
                                    return false;
                                }
                                break;
                        }
                        break;
                    case RUECKDECKUNGSKASSE:
                        if(curAN != 0) {
                            return false;
                        }
                        switch(curTarif) {
                            case AR_EVEN, AR_ODD, HR20:
                                if(curAG < 600 || curAG > 125000 || curZuz > 1000000) {
                                    return false;
                                }
                                break;
                            case RV_X:
                                if(curAG < 577 || curAG < 25 || curAG > 125000 ||
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
