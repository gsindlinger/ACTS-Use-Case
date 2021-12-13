package useCase.suts;

import edu.uta.cse.fireeye.common.Constraint;
import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.SUT;

import java.util.ArrayList;

public class SUTObject {

    private SUT sut;



    private int interactionParameter;
    public static IncludeInvalidValues includeInvalidValues = IncludeInvalidValues.EXCLUDE_INVALID_VALUES;
    public static IncludeValuesForConstraints includeValuesForConstraints = IncludeValuesForConstraints.INCLUDE_VALUES_FOR_CONSTRAINTS;
    public static Approach approach = Approach.COMPLEX_AMOUNT;


    //It's quite hard to consider invalid values at Easy Web since the constraints for invalid values are given by calculated values and
    //cannot be assigned to single parameters

    public enum Approach{
        SINGLE_AMOUNT,
        COMPLEX_AMOUNT
    }

    public enum IncludeInvalidValues{
        INCLUDE_INVALID_VALUES,
        EXCLUDE_INVALID_VALUES
    }

    public enum IncludeValuesForConstraints{
        INCLUDE_VALUES_FOR_CONSTRAINTS,
        EXCLUDE_VALUES_FOR_CONSTRAINTS
    }

    public SUT getSut() {
        return sut;
    }

    public int getInteractionParameter() {
        return interactionParameter;
    }


    public SUTObject(int interactionParameter, IncludeValuesForConstraints includeValuesForConstraints, Approach approach) {

        this.interactionParameter = interactionParameter;
        this.includeValuesForConstraints = includeValuesForConstraints;

        // build a system configuration
        sut = new SUT("EasyWeb");

        // it is recommended to create a new parameter from the SUT object
        // doing so will assign the parameter with an ID automatically
        Parameter tariff = sut.addParam("Tarif");

        // all the parameter values are originally string values
        // but they may be interpreted differently based on parameter type
        // when processing constraints
        tariff.addValue("AREven");
        tariff.addValue("AROdd");
        tariff.addValue("FR20");
        tariff.addValue("HR20");
        tariff.addValue("PK10");
        tariff.addValue("RVx");
        tariff.setType(Parameter.PARAM_TYPE_ENUM);

        Parameter implementation = sut.addParam("Durchfuehrungsweg");
        implementation.addValue("Direktversicherung");
        implementation.addValue("Direktzusage");
        implementation.addValue("Pensionskasse");
        implementation.setType(Parameter.PARAM_TYPE_ENUM);


        Parameter typeOfReinsurance = sut.addParam("Art_der_Rueckdeckung");
        typeOfReinsurance.addValue("keine");
        typeOfReinsurance.addValue("Rueckdeckungskasse");
        typeOfReinsurance.addValue("Unterstuetzungskasse");
        typeOfReinsurance.setType(Parameter.PARAM_TYPE_ENUM);

        Parameter typeOfFunding = sut.addParam("Finanzierungsart");
        typeOfFunding.addValue("AG-finanziert");
        typeOfFunding.addValue("AN-finanziert");
        typeOfFunding.addValue("Mischfinanziert");
        typeOfFunding.setType(Parameter.PARAM_TYPE_ENUM);

        Parameter paymentBeginning = sut.addParam("Zuzahlung_zu_Beginn");
        paymentBeginning.addValue("0");
        paymentBeginning.addValue("2000");
        if(includeInvalidValues == IncludeInvalidValues.INCLUDE_INVALID_VALUES) {
            paymentBeginning.addInvalidValue("200");
            paymentBeginning.addInvalidValue("6000");
            paymentBeginning.addInvalidValue("1200000");
        }else{
            paymentBeginning.addValue("200");
            paymentBeginning.addValue("6000");
            paymentBeginning.addValue("1200000");
        }
        paymentBeginning.setType(Parameter.PARAM_TYPE_INT);


        Parameter amount40b = sut.addParam("Beitrag_40b");
        amount40b.addValue("0");
        amount40b.addValue("2148");
        if(includeInvalidValues == IncludeInvalidValues.INCLUDE_INVALID_VALUES) {
            amount40b.addInvalidValue("2149");
        }else{
            amount40b.addValue("2149");
        }
        amount40b.setType(Parameter.PARAM_TYPE_INT);

        Parameter amount363 = sut.addParam("Beitrag_363");
        amount363.addValue("0");
        amount363.addValue("2000");
        amount363.addValue("2400");
        amount363.setType(Parameter.PARAM_TYPE_INT);

        Parameter amountEmployee = sut.addParam("AN_Beitrag");
        amountEmployee.addValue("0");
        amountEmployee.addValue("208");
        amountEmployee.addValue("209");
        amountEmployee.addValue("600");
        amountEmployee.addValue("2600");
        amountEmployee.addValue("2417");
        if(includeInvalidValues == IncludeInvalidValues.INCLUDE_INVALID_VALUES) {
            amountEmployee.addInvalidValue("130000");
        }else{
            amountEmployee.addValue("130000");
        }
        amountEmployee.setType(Parameter.PARAM_TYPE_INT);

        Parameter amountEmployer = sut.addParam("AG_Beitrag");
        amountEmployer.addValue("0");
        amountEmployer.addValue("24");
        amountEmployer.addValue("208");
        amountEmployer.addValue("209");
        amountEmployer.addValue("600");
        amountEmployer.addValue("2600");
        amountEmployer.addValue("2416");
        if(includeInvalidValues == IncludeInvalidValues.INCLUDE_INVALID_VALUES) {
            amountEmployer.addInvalidValue("130000");
        }else{
            amountEmployer.addValue("130000");
        }
        amountEmployer.setType(Parameter.PARAM_TYPE_INT);

        // add the default relation with interaction t
        sut.addDefaultRelation(this.interactionParameter);

        // create constraints

        //Constraints: Fitting the tariffs to the possible combinations visible on the web platform

        //AREven and HR20 have no restrictions, since those tariffs are available as Direktversicherung, U-Kasse and Direktzusage
        //AROdd only for for Rückdeckungstyp: Rückdeckungskasse
        ArrayList<Parameter> params = new ArrayList();
        params.add(tariff);
        params.add(typeOfReinsurance);
        params.add(implementation);
        Constraint cAROdd = new Constraint("Tarif = \"AROdd\" => Durchfuehrungsweg = \"Direktzusage\" && " +
                "Art_der_Rueckdeckung = \"Rueckdeckungskasse\"",  params);
        sut.addConstraint(cAROdd);

        //FR20 only for Direktversicherung
        params = new ArrayList();
        params.add(tariff);
        params.add(implementation);
        Constraint cFR20 = new Constraint("Tarif = \"FR20\" => Durchfuehrungsweg = \"Direktversicherung\"",
                params);
        sut.addConstraint(cFR20);

        //RVx only for Direktzusage
        params = new ArrayList();
        params.add(tariff);
        params.add(implementation);
        Constraint cRVx = new Constraint("Tarif = \"RVx\" => Durchfuehrungsweg = \"Direktzusage\"",
                params);
        sut.addConstraint(cRVx);


        //PK10 <=> Pensionskasse und Pensionskasse => keine Rückdeckung
        params = new ArrayList();
        params.add(tariff);
        params.add(implementation);
        Constraint c1 = new Constraint("Tarif = \"PK10\" => Durchfuehrungsweg = \"Pensionskasse\"",
                params);
        sut.addConstraint(c1);

        params.add(typeOfReinsurance);
        Constraint c2 = new Constraint("Durchfuehrungsweg = \"Pensionskasse\" => " +
                "Tarif = \"PK10\" && Art_der_Rueckdeckung = \"keine\"", params);
        sut.addConstraint(c2);

        //Constraint: Arbeitgeberfinanziert => Arbeitnehmerbeitrag = 0
        params = new ArrayList();
        params.add(typeOfFunding);
        params.add(amountEmployee);

        Constraint c3 = new Constraint("Finanzierungsart = \"AG-finanziert\" => AN_Beitrag = 0", params);
        sut.addConstraint(c3);

        //Constraint: Arbeitnehmerfinanziert => Arbeitgeberbeitrag = 0

        params = new ArrayList();
        params.add(typeOfFunding);
        params.add(amountEmployer);

        Constraint c4 = new Constraint("Finanzierungsart = \"AN-finanziert\" => AG_Beitrag = 0", params);
        sut.addConstraint(c4);


        //Constraint: Unterstützungskasse => nur einfacher Beitrag
        params = new ArrayList();
        params.add(typeOfReinsurance);
        params.add(amount363);
        params.add(amount40b);
        params.add(paymentBeginning);

        Constraint c5 = new Constraint("Art_der_Rueckdeckung = \"Unterstuetzungskasse\"" +
                " => Beitrag_40b = 0 && Beitrag_363 = 0 && Zuzahlung_zu_Beginn = 0", params);
        sut.addConstraint(c5);

        //Constraint: Direktversicherung nur ohne Rückversicherung
        params = new ArrayList();
        params.add(implementation);
        params.add(typeOfReinsurance);

        Constraint c6 = new Constraint("Durchfuehrungsweg = \"Direktversicherung\"" +
                " => Art_der_Rueckdeckung = \"keine\"", params);
        sut.addConstraint(c6);

        //Constraint: Direktzusage nur mit Rückdeckung + Unterstützungskasse
        //Alles was Unterstützungskasse in der Weboberfläche ist, ist automatisch Direktzusage

        Constraint c7 = new Constraint("Durchfuehrungsweg = \"Direktzusage\"" +
                " => (Art_der_Rueckdeckung = \"Unterstuetzungskasse\" || Art_der_Rueckdeckung = \"Rueckdeckungskasse\")", params);
        sut.addConstraint(c7);

        //Constraint: Rückdeckungskasse immer AG-finanziert & Nur Zuzahlung
        params = new ArrayList();
        params.add(implementation);
        params.add(typeOfFunding);
        params.add(amount363);
        params.add(amount40b);
        Constraint c8 = new Constraint("Durchfuehrungsweg = \"Direktzusage\"" +
                " => Finanzierungsart = \"AG-finanziert\" && " +
                "Beitrag_40b = 0 && Beitrag_363 = 0", params);
        sut.addConstraint(c8);

        //Constraint: Unterstützungskasse => keine Mischfinanzierung und keine BBG-Grenze
        params = new ArrayList();
        params.add(typeOfReinsurance);
        params.add(typeOfFunding);
        params.add(amount363);
        params.add(amount40b);
        params.add(paymentBeginning);
        Constraint c9 = new Constraint("Art_der_Rueckdeckung = \"Unterstuetzungskasse\"" +
                " => Finanzierungsart != \"Mischfinanziert\" && " +
                "Beitrag_40b = 0 && Beitrag_363 = 0 && Zuzahlung_zu_Beginn = 0", params);
        sut.addConstraint(c9);

        //Constraint: Direktzusage/Rückdeckungskasse => Arbeitgeberfinanziert + nur Zuzahlung und keine BBG-Grenze
        params = new ArrayList();
        params.add(typeOfReinsurance);
        params.add(typeOfFunding);
        params.add(amount363);
        params.add(amount40b);
        params.add(paymentBeginning);
        Constraint c10 = new Constraint("Art_der_Rueckdeckung = \"Rueckdeckungskasse\"" +
                " => Finanzierungsart = \"AG-finanziert\" && " +
                "Beitrag_40b = 0 && Beitrag_363 = 0", params);
        sut.addConstraint(c10);

        // Mischfinanzierung sollte auf alle Fälle auch Werte > 0 für AN-Beitrag und


        /*********************************
         *
         *
         * In the following there will be declared some constraints for the possible values to
         * choose from the different amounts.
         */

        if(this.includeValuesForConstraints == IncludeValuesForConstraints.INCLUDE_VALUES_FOR_CONSTRAINTS) {
            //Constraint for the chosen amounts of the payment at beginning

            //Amount Employee
            params = new ArrayList();
            params.add(amountEmployee);
            params.add(implementation);
            params.add(typeOfReinsurance);
            params.add(tariff);
            Constraint cAN1 = new Constraint("Durchfuehrungsweg = \"Direktversicherung\" => " +
                    "AN_Beitrag = 0 || AN_Beitrag = 208 || AN_Beitrag = 209 || " +
                    "AN_Beitrag = 600 || AN_Beitrag = 2600 || AN_Beitrag = 2417",  params);
            sut.addConstraint(cAN1);

            Constraint cAN2 = new Constraint("Durchfuehrungsweg = \"Pensionskasse\" => " +
                    "AN_Beitrag = 0 || AN_Beitrag = 208 || AN_Beitrag = 209 || " +
                    "AN_Beitrag = 600 || AN_Beitrag = 2600 || AN_Beitrag = 2417",  params);
            sut.addConstraint(cAN2);

            Constraint cAN3= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Unterstuetzungskasse\" && (Tarif = \"AREven\" || Tarif = \"HR20\") => " +
                    "AN_Beitrag = 0 || AN_Beitrag = 209 ||" +
                    "AN_Beitrag = 600 || AN_Beitrag = 2600 || AN_Beitrag = 130000",  params);
            sut.addConstraint(cAN3);

            Constraint cAN4= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Unterstuetzungskasse\" && Tarif = \"RVx\" => " +
                    "AN_Beitrag = 0 || AN_Beitrag = 209 ||" +
                    "AN_Beitrag = 600 || AN_Beitrag = 130000",  params);
            sut.addConstraint(cAN4);

            //Amount Employer
            params = new ArrayList();
            params.add(amountEmployer);
            params.add(implementation);
            params.add(typeOfReinsurance);
            params.add(tariff);
            Constraint cAG1 = new Constraint("Durchfuehrungsweg = \"Direktversicherung\" => " +
                    "AG_Beitrag = 0 || AG_Beitrag = 208 || AG_Beitrag = 209 || " +
                    "AG_Beitrag = 600 || AG_Beitrag = 2600 || AG_Beitrag = 2416",  params);
            sut.addConstraint(cAG1);

            Constraint cAG2 = new Constraint("Durchfuehrungsweg = \"Pensionskasse\" => " +
                    "AG_Beitrag = 0 || AG_Beitrag = 208 || AG_Beitrag = 209 || " +
                    "AG_Beitrag = 600 || AG_Beitrag = 2600 || AG_Beitrag = 2416",  params);
            sut.addConstraint(cAG2);

            Constraint cAG3= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Unterstuetzungskasse\" && (Tarif = \"AREven\" || Tarif = \"HR20\") => " +
                    "AG_Beitrag = 0 || AG_Beitrag = 209 ||" +
                    "AG_Beitrag = 600 || AG_Beitrag = 2600 || AG_Beitrag = 130000",  params);
            sut.addConstraint(cAG3);

            Constraint cAG4= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Unterstuetzungskasse\" && Tarif = \"RVx\" => " +
                    "AG_Beitrag = 0 || AG_Beitrag = 209 ||" +
                    "AG_Beitrag = 600 || AG_Beitrag = 130000",  params);
            sut.addConstraint(cAG4);

            Constraint cAG5= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Rueckdeckungskasse\" && (Tarif = \"AREven\" || Tarif = \"HR20\" || Tarif = \"AROdd\") => " +
                    "AG_Beitrag = 0 || AG_Beitrag = 209 ||" +
                    "AG_Beitrag = 600 || AG_Beitrag = 2600 || AG_Beitrag = 130000",  params);
            sut.addConstraint(cAG5);

            Constraint cAG6= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Rueckdeckungskasse\" && Tarif = \"RVx\" => " +
                    "AG_Beitrag = 0 || AG_Beitrag = 209 ||" +
                    "AG_Beitrag = 600 || AG_Beitrag = 2600 || AG_Beitrag = 130000 || AG_Beitrag = 24",  params);
            sut.addConstraint(cAG6);

            //Payment Beginning
            params.add(paymentBeginning);
            params.add(implementation);
            params.add(typeOfReinsurance);
            params.add(tariff);
            Constraint cPAYBEG1 = new Constraint("Durchfuehrungsweg = \"Direktversicherung\" => " +
                    "Zuzahlung_zu_Beginn = 0 || Zuzahlung_zu_Beginn = 2000",  params);
            sut.addConstraint(cPAYBEG1);

            Constraint cPAYBEG2 = new Constraint("Durchfuehrungsweg = \"Pensionskasse\" => " +
                    "Zuzahlung_zu_Beginn = 0 || Zuzahlung_zu_Beginn = 200 || Zuzahlung_zu_Beginn = 2000",  params);
            sut.addConstraint(cPAYBEG2);

            Constraint cPAYBEG3= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Rueckdeckungskasse\" && (Tarif = \"AREven\" || Tarif = \"HR20\" || Tarif = \"AROdd\") => " +
                    "Zuzahlung_zu_Beginn= 0 || Zuzahlung_zu_Beginn= 2000 || " +
                    "Zuzahlung_zu_Beginn= 1200000",  params);
            sut.addConstraint(cPAYBEG3);

            Constraint cPAYBEG4= new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Rueckdeckungskasse\" && Tarif = \"RVx\" => " +
                    "Zuzahlung_zu_Beginn= 0 || Zuzahlung_zu_Beginn= 2000 ||" +
                    "Zuzahlung_zu_Beginn= 200 ||Zuzahlung_zu_Beginn= 6000",  params);
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

            //Amount 40b
            params.add(amount363);
            params.add(implementation);
            params.add(typeOfReinsurance);
            params.add(tariff);

            Constraint cAM363_1 = new Constraint("Durchfuehrungsweg = \"Direktversicherung\" => " +
                    "Beitrag_363= 0 || Beitrag_363= 2000 || Beitrag_363= 2400",  params);
            sut.addConstraint(cAM363_1);

            Constraint cAM363_2 = new Constraint("Durchfuehrungsweg = \"Pensionskasse\" => " +
                    "Beitrag_363= 0 || Beitrag_363= 2000 || Beitrag_363= 2400",  params);
            sut.addConstraint(cAM363_2);
        }

    }




}

