package useCase.suts;

import edu.uta.cse.fireeye.common.Constraint;
import edu.uta.cse.fireeye.common.Parameter;

import java.util.ArrayList;

public class SUTObjectSimple extends SUTObject {


    public SUTObjectSimple(int interactionParameter, SUTObject.IncludeValuesForConstraints includeValuesForConstraints) {

        super(interactionParameter);

        // build a system configuration
        sut.setName("EasyWeb Simple Amounts");

        //Values for payment Beginning
        paymentBeginning.addValue("0");
        paymentBeginning.addValue("2000");
        if(includeInvalidValues == IncludeInvalidValues.INCLUDE_INVALID_VALUES) {
            paymentBeginning.addInvalidValue("99");
            paymentBeginning.addInvalidValue("299");
            paymentBeginning.addInvalidValue("5001");
            paymentBeginning.addInvalidValue("1000001");
        }else{
            paymentBeginning.addValue("99");
            paymentBeginning.addValue("299");
            paymentBeginning.addValue("5001");
            paymentBeginning.addValue("1000001");
        }

        //Values for Amount § 40b
        amount40b.addValue("0");
        amount40b.addValue("2148");
        if(includeInvalidValues == IncludeInvalidValues.INCLUDE_INVALID_VALUES) {
            amount40b.addInvalidValue("2149");
        }else{
            amount40b.addValue("2149");
        }

        //Values for Amount § 363
        amount363.addValue("0");
        amount363.addValue("2068");
        amount363.addValue("4068");
        amount363.setType(Parameter.PARAM_TYPE_INT);


        //Separation between Employer and Employee
        Parameter regularAmount = sut.addParam("Beitrag");
        regularAmount.addValue("0");
        regularAmount.addValue("600");
        regularAmount.addValue("299");
        regularAmount.addValue("5000");
        if(includeInvalidValues == IncludeInvalidValues.INCLUDE_INVALID_VALUES) {
            regularAmount.addInvalidValue("24");
            regularAmount.addInvalidValue("599");
            regularAmount.addInvalidValue("125001");
        }else{
            regularAmount.addValue("24");
            regularAmount.addValue("599");
            regularAmount.addValue("125001");
        }
        regularAmount.setType(Parameter.PARAM_TYPE_INT);


        // create constraints

        ArrayList<Parameter> params = new ArrayList();


        //Constraint: Rückdeckungsversicherung immer AG-finanziert & Nur Zuzahlung
        params = new ArrayList();
        params.add(implementation);
        params.add(typeOfReinsurance);
        params.add(amount363);
        params.add(amount40b);
        Constraint c8 = new Constraint("Durchfuehrungsweg = \"Direktzusage\" && Art_der_Rueckdeckung = \"Rueckdeckungsversicherung\"" +
                " => Beitrag_40b = 0 && Beitrag_363 = 0", params);
        sut.addConstraint(c8);


        //Constraint: Unterstützungskasse => keine BBG-Werte
        params = new ArrayList();
        params.add(typeOfReinsurance);
        params.add(amount363);
        params.add(amount40b);
        params.add(paymentBeginning);
        Constraint c9 = new Constraint("Art_der_Rueckdeckung = \"Unterstuetzungskasse\"" +
                " => Beitrag_40b = 0 && Beitrag_363 = 0 && Zuzahlung_zu_Beginn = 0", params);
        sut.addConstraint(c9);

        /*********************************
         *
         *
         * In the following there will be declared some constraints for the possible values to
         * choose from the different amounts.
         */

        if(this.includeValuesForConstraints == IncludeValuesForConstraints.INCLUDE_VALUES_FOR_CONSTRAINTS) {
            //Constraint for the chosen amounts of the payment at beginning

            //Amount
            params = new ArrayList();
            params.add(regularAmount);
            params.add(implementation);
            params.add(typeOfReinsurance);
            params.add(tariff);
            Constraint cAN1 = new Constraint("Durchfuehrungsweg = \"Direktversicherung\" => " +
                    "Beitrag = 0 || Beitrag = 599 || Beitrag = 600",  params);
            sut.addConstraint(cAN1);

            Constraint cAN2 = new Constraint("Durchfuehrungsweg = \"Pensionskasse\" => " +
                    "Beitrag = 0 || Beitrag = 299 || Beitrag = 599 || " +
                    "Beitrag = 600",  params);
            sut.addConstraint(cAN2);

            Constraint cAN3= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Unterstuetzungskasse\" && (Tarif = \"AREven\" || Tarif = \"HR20\") => " +
                    "Beitrag = 0 || Beitrag = 599 ||" +
                    "Beitrag = 600 || Beitrag = 125001",  params);
            sut.addConstraint(cAN3);

            Constraint cAN3a= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Unterstuetzungskasse\" && Tarif = \"FR20\" => " +
                    "Beitrag = 0 || Beitrag = 299 ||" +
                    "Beitrag = 600 || Beitrag = 125001",  params);
            sut.addConstraint(cAN3a);

            Constraint cAN4= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Unterstuetzungskasse\" && Tarif = \"RVx\" => " +
                    "Beitrag = 0 || Beitrag = 299 || Beitrag = 24 ||" +
                    "Beitrag = 600 || Beitrag = 125001",  params);
            sut.addConstraint(cAN4);

            Constraint cAG5= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Rueckdeckungsversicherung\" && (Tarif = \"AREven\" || Tarif = \"HR20\" || Tarif = \"AROdd\") => " +
                    "Beitrag = 0 || Beitrag = 599 ||" +
                    "Beitrag = 600 || Beitrag = 125001",  params);
            sut.addConstraint(cAG5);

            Constraint cAG6= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Rueckdeckungsversicherung\" && Tarif = \"RVx\" => " +
                    "Beitrag = 0 || " +
                    "Beitrag = 299 || Beitrag = 5000 || Beitrag = 125001",  params);
            sut.addConstraint(cAG6);

            //Payment Beginning
            params.add(paymentBeginning);
            params.add(implementation);
            params.add(typeOfReinsurance);
            params.add(tariff);
            Constraint cPAYBEG1 = new Constraint("Durchfuehrungsweg = \"Direktversicherung\" => " +
                    "Zuzahlung_zu_Beginn = 0 || Zuzahlung_zu_Beginn = 99 || Zuzahlung_zu_Beginn = 2000",  params);
            sut.addConstraint(cPAYBEG1);

            Constraint cPAYBEG2 = new Constraint("Durchfuehrungsweg = \"Pensionskasse\" => " +
                    "Zuzahlung_zu_Beginn = 0 || Zuzahlung_zu_Beginn = 299 || Zuzahlung_zu_Beginn = 2000",  params);
            sut.addConstraint(cPAYBEG2);

            Constraint cPAYBEG3= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Rueckdeckungsversicherung\" && (Tarif = \"AREven\" || Tarif = \"HR20\" || Tarif = \"AROdd\") => " +
                    "Zuzahlung_zu_Beginn= 0 || Zuzahlung_zu_Beginn= 2000 || " +
                    "Zuzahlung_zu_Beginn= 1000001",  params);
            sut.addConstraint(cPAYBEG3);

            Constraint cPAYBEG4= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Rueckdeckungsversicherung\" && Tarif = \"RVx\" => " +
                    "Zuzahlung_zu_Beginn= 0 || Zuzahlung_zu_Beginn= 2000 ||" +
                    "Zuzahlung_zu_Beginn= 299 ||Zuzahlung_zu_Beginn= 5001",  params);
            sut.addConstraint(cPAYBEG4);

            //Amount 40b
            params.add(amount40b);
            params.add(implementation);
            params.add(typeOfReinsurance);
            params.add(tariff);

            Constraint cAM40B1 = new Constraint("Durchfuehrungsweg = \"Direktversicherung\" => " +
                    "Beitrag_40b= 0 || Beitrag_40b= 2148 || Beitrag_40b= 2149",  params);
            sut.addConstraint(cAM40B1);

            Constraint cAM40B2 = new Constraint("Durchfuehrungsweg = \"Pensionskasse\" => " +
                    "Beitrag_40b= 0 || Beitrag_40b= 2148 || Beitrag_40b= 2149",  params);
            sut.addConstraint(cAM40B2);

            //Amount 363
            params.add(amount363);
            params.add(implementation);
            params.add(typeOfReinsurance);
            params.add(tariff);

            Constraint cAM363_1 = new Constraint("Durchfuehrungsweg = \"Direktversicherung\" => " +
                    "Beitrag_363= 0 || Beitrag_363= 2068 || Beitrag_363= 4068",  params);
            sut.addConstraint(cAM363_1);

            Constraint cAM363_2 = new Constraint("Durchfuehrungsweg = \"Pensionskasse\" => " +
                    "Beitrag_363= 0 || Beitrag_363= 2068 || Beitrag_363= 4068",  params);
            sut.addConstraint(cAM363_2);
        }

    }




}

