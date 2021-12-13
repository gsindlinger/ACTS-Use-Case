package useCase.suts;

        import edu.uta.cse.fireeye.common.Constraint;
        import edu.uta.cse.fireeye.common.Parameter;
        import edu.uta.cse.fireeye.common.SUT;

        import java.util.ArrayList;

public class SUTObjectComplex extends SUTObject {


    public SUTObjectComplex(int interactionParameter, SUTObject.IncludeValuesForConstraints includeValuesForConstraints) {

        super(interactionParameter);

        // build a system configuration
        sut.setName("EasyWeb Complex Amounts");

        // create constraints
        setComplexValues(this);

        ArrayList<Parameter> params;

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

        //Constraint: Direktzusage/Rückdeckungsversicherung => Arbeitgeberfinanziert + nur Zuzahlung und keine BBG-Grenze
        params = new ArrayList();
        params.add(typeOfReinsurance);
        params.add(typeOfFunding);
        params.add(amount363);
        params.add(amount40b);
        params.add(paymentBeginning);
        Constraint c10 = new Constraint("Art_der_Rueckdeckung = \"Rueckdeckungsversicherung\"" +
                " => Finanzierungsart = \"AG-finanziert\" && " +
                "Beitrag_40b = 0 && Beitrag_363 = 0", params);
        sut.addConstraint(c10);

        /*********************************
         *
         *
         * In the following there will be declared some constraints for the possible values to
         * choose from the different amounts.
         */

        if(includeValuesForConstraints == IncludeValuesForConstraints.INCLUDE_VALUES_FOR_CONSTRAINTS) {
            //Constraint for the chosen amounts of the payment at beginning

            //Amount Employee
            params = new ArrayList();
            params.add(amountEmployee);
            params.add(implementation);
            params.add(typeOfReinsurance);
            params.add(tariff);
            Constraint cAN1 = new Constraint("Durchfuehrungsweg = \"Direktversicherung\" => " +
                    "AN_Beitrag = 0 || AN_Beitrag = 599 || AN_Beitrag = 600 || " +
                    "AN_Beitrag = 1500", params);
            sut.addConstraint(cAN1);

            Constraint cAN2 = new Constraint("Durchfuehrungsweg = \"Pensionskasse\" => " +
                    "AN_Beitrag = 0 || AN_Beitrag = 299 || AN_Beitrag = 599 || " +
                    "AN_Beitrag = 600 || AN_Beitrag = 1500", params);
            sut.addConstraint(cAN2);

            Constraint cAN3 = new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Unterstuetzungskasse\" && (Tarif = \"AREven\" || Tarif = \"HR20\") => " +
                    "AN_Beitrag = 0 || AN_Beitrag = 599 ||" +
                    "AN_Beitrag = 600 || AN_Beitrag = 125001", params);
            sut.addConstraint(cAN3);

            Constraint cAN3a = new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Unterstuetzungskasse\" && Tarif = \"FR20\" => " +
                    "AN_Beitrag = 0 || AN_Beitrag = 299 ||" +
                    "AN_Beitrag = 600 || AN_Beitrag = 125001", params);
            sut.addConstraint(cAN3a);

            Constraint cAN4 = new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Unterstuetzungskasse\" && Tarif = \"RVx\" => " +
                    "AN_Beitrag = 0 || AN_Beitrag = 299 ||" +
                    "AN_Beitrag = 1500 || AN_Beitrag = 125001 || AG_Beitrag = 24", params);
            sut.addConstraint(cAN4);

            //Amount Employer
            params = new ArrayList();
            params.add(amountEmployer);
            params.add(implementation);
            params.add(typeOfReinsurance);
            params.add(tariff);
            Constraint cAG1 = new Constraint("Durchfuehrungsweg = \"Direktversicherung\" => " +
                    "AG_Beitrag = 0 || AG_Beitrag = 599 || AG_Beitrag = 600 || " +
                    "AG_Beitrag = 1500", params);
            sut.addConstraint(cAG1);

            Constraint cAG2 = new Constraint("Durchfuehrungsweg = \"Pensionskasse\" => " +
                    "AG_Beitrag = 0 || AG_Beitrag = 299 || AG_Beitrag = 599 || " +
                    "AG_Beitrag = 600 || AG_Beitrag = 1500", params);
            sut.addConstraint(cAG2);

            Constraint cAG3 = new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Unterstuetzungskasse\" && (Tarif = \"AREven\" || Tarif = \"HR20\") => " +
                    "AG_Beitrag = 0 || AG_Beitrag = 599 ||" +
                    "AG_Beitrag = 600 || AG_Beitrag = 125001", params);
            sut.addConstraint(cAG3);

            Constraint cAG3a = new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Unterstuetzungskasse\" && Tarif = \"FR20\" => " +
                    "AG_Beitrag = 0 || AG_Beitrag = 299 ||" +
                    "AG_Beitrag = 600 || AG_Beitrag = 125001", params);
            sut.addConstraint(cAG3a);

            Constraint cAG4 = new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Unterstuetzungskasse\" && Tarif = \"RVx\" => " +
                    "AG_Beitrag = 0 || AG_Beitrag = 299 ||" +
                    "AG_Beitrag = 1500 || AG_Beitrag = 125001 || AG_Beitrag = 24", params);
            sut.addConstraint(cAG4);

            Constraint cAG5 = new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Rueckdeckungsversicherung\" && (Tarif = \"AREven\" || Tarif = \"HR20\" || Tarif = \"AROdd\") => " +
                    "AG_Beitrag = 0 || AG_Beitrag = 599 ||" +
                    "AG_Beitrag = 600 || AG_Beitrag = 125001", params);
            sut.addConstraint(cAG5);

            Constraint cAG6 = new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Rueckdeckungsversicherung\" && Tarif = \"RVx\" => " +
                    "AG_Beitrag = 0 || " +
                    "AG_Beitrag = 299 || AG_Beitrag = 1500 || AG_Beitrag = 125001", params);
            sut.addConstraint(cAG6);

            //Payment Beginning
            params.add(paymentBeginning);
            params.add(implementation);
            params.add(typeOfReinsurance);
            params.add(tariff);
            Constraint cPAYBEG1 = new Constraint("Durchfuehrungsweg = \"Direktversicherung\" => " +
                    "Zuzahlung_zu_Beginn = 0 || Zuzahlung_zu_Beginn = 99 || Zuzahlung_zu_Beginn = 2000", params);
            sut.addConstraint(cPAYBEG1);

            Constraint cPAYBEG2 = new Constraint("Durchfuehrungsweg = \"Pensionskasse\" => " +
                    "Zuzahlung_zu_Beginn = 0 || Zuzahlung_zu_Beginn = 299 || Zuzahlung_zu_Beginn = 2000", params);
            sut.addConstraint(cPAYBEG2);

            Constraint cPAYBEG3 = new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Rueckdeckungsversicherung\" && (Tarif = \"AREven\" || Tarif = \"HR20\" || Tarif = \"AROdd\") => " +
                    "Zuzahlung_zu_Beginn= 0 || Zuzahlung_zu_Beginn= 2000 || " +
                    "Zuzahlung_zu_Beginn= 1000001", params);
            sut.addConstraint(cPAYBEG3);

            Constraint cPAYBEG4 = new Constraint("Durchfuehrungsweg = \"Direktzusage\" " +
                    "&& Art_der_Rueckdeckung = \"Rueckdeckungsversicherung\" && Tarif = \"RVx\" => " +
                    "Zuzahlung_zu_Beginn= 0 || Zuzahlung_zu_Beginn= 299 ||" +
                    "Zuzahlung_zu_Beginn= 2000 ||Zuzahlung_zu_Beginn= 5001", params);
            sut.addConstraint(cPAYBEG4);

            //Amount 40b
            params.add(this.amount40b);
            params.add(implementation);
            params.add(typeOfReinsurance);
            params.add(tariff);

            Constraint cAM40B1 = new Constraint("Durchfuehrungsweg = \"Direktversicherung\" => " +
                    "Beitrag_40b= 0 || Beitrag_40b= 2148 || Beitrag_40b= 2149", params);
            sut.addConstraint(cAM40B1);

            Constraint cAM40B2 = new Constraint("Durchfuehrungsweg = \"Pensionskasse\" => " +
                    "Beitrag_40b= 0 || Beitrag_40b= 2148 || Beitrag_40b= 2149", params);
            sut.addConstraint(cAM40B2);

            //Amount 363
            params.add(amount363);
            params.add(implementation);
            params.add(typeOfReinsurance);
            params.add(tariff);

            Constraint cAM363_1 = new Constraint("Durchfuehrungsweg = \"Direktversicherung\" => " +
                    "Beitrag_363= 0 || Beitrag_363= 2000 || Beitrag_363= 4068", params);
            sut.addConstraint(cAM363_1);

            Constraint cAM363_2 = new Constraint("Durchfuehrungsweg = \"Pensionskasse\" => " +
                    "Beitrag_363= 0 || Beitrag_363= 2000 || Beitrag_363= 4068", params);
            sut.addConstraint(cAM363_2);


        }

    }

    public static void setComplexValues(SUTObjectAbstract sutObject) {
        SUT sut = sutObject.getSut();

        sutObject.typeOfFunding = sut.addParam("Finanzierungsart");
        sutObject.typeOfFunding.addValue("AG-finanziert");
        sutObject.typeOfFunding.addValue("AN-finanziert");
        sutObject.typeOfFunding.addValue("Mischfinanziert");
        sutObject.typeOfFunding.setType(Parameter.PARAM_TYPE_ENUM);


        //Values for payment Beginning
        sutObject.paymentBeginning.addValue("0");
        sutObject.paymentBeginning.addValue("2000");
        if(sutObject.includeInvalidValues == IncludeInvalidValues.INCLUDE_INVALID_VALUES) {
            sutObject.paymentBeginning.addInvalidValue("99");
            sutObject.paymentBeginning.addInvalidValue("299");
            sutObject.paymentBeginning.addInvalidValue("5001");
            sutObject.paymentBeginning.addInvalidValue("1000001");
        }else{
            sutObject.paymentBeginning.addValue("99");
            sutObject.paymentBeginning.addValue("299");
            sutObject.paymentBeginning.addValue("5001");
            sutObject.paymentBeginning.addValue("1000001");
        }

        //Values for Amount § 40b
        sutObject.amount40b.addValue("0");
        sutObject.amount40b.addValue("2148");
        if(sutObject.includeInvalidValues == IncludeInvalidValues.INCLUDE_INVALID_VALUES) {
            sutObject.amount40b.addInvalidValue("2149");
        }else{
            sutObject.amount40b.addValue("2149");
        }

        //Values for Amount § 363
        sutObject.amount363.addValue("0");
        sutObject.amount363.addValue("2000");
        sutObject.amount363.addValue("4068");
        sutObject.amount363.setType(Parameter.PARAM_TYPE_INT);


        //Separation between Employer and Employee
        sutObject.amountEmployee = sut.addParam("AN_Beitrag");
        sutObject.amountEmployee.addValue("0");
        sutObject.amountEmployee.addValue("299");
        sutObject.amountEmployee.addValue("599");
        sutObject.amountEmployee.addValue("600");
        sutObject.amountEmployee.addValue("1500");
        if(sutObject.includeInvalidValues == IncludeInvalidValues.INCLUDE_INVALID_VALUES) {
            sutObject.amountEmployee.addInvalidValue("125001");
        }else{
            sutObject.amountEmployee.addValue("125001");
        }
        sutObject.amountEmployee.setType(Parameter.PARAM_TYPE_INT);

        sutObject.amountEmployer = sut.addParam("AG_Beitrag");
        sutObject.amountEmployer.addValue("0");
        sutObject.amountEmployer.addValue("24");
        sutObject.amountEmployer.addValue("299");
        sutObject.amountEmployer.addValue("599");
        sutObject.amountEmployer.addValue("600");
        sutObject.amountEmployer.addValue("1500");
        if(sutObject.includeInvalidValues == IncludeInvalidValues.INCLUDE_INVALID_VALUES) {
            sutObject.amountEmployer.addInvalidValue("125001");
        }else{
            sutObject.amountEmployer.addValue("125001");
        }
        sutObject.amountEmployer.setType(Parameter.PARAM_TYPE_INT);



        ArrayList<Parameter> params;

        //Constraint: Arbeitgeberfinanziert => Arbeitnehmerbeitrag = 0
        params = new ArrayList();
        params.add(sutObject.typeOfFunding);
        params.add(sutObject.amountEmployee);

        Constraint c3 = new Constraint("Finanzierungsart = \"AG-finanziert\" => AN_Beitrag = 0", params);
        sut.addConstraint(c3);

        //Constraint: Arbeitnehmerfinanziert => Arbeitgeberbeitrag = 0

        params = new ArrayList();
        params.add(sutObject.typeOfFunding);
        params.add(sutObject.amountEmployer);

        Constraint c4 = new Constraint("Finanzierungsart = \"AN-finanziert\" => AG_Beitrag = 0", params);
        sut.addConstraint(c4);


    }




}

