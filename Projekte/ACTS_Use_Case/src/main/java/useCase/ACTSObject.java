package useCase;

import edu.uta.cse.fireeye.common.Constraint;
import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.SUT;

import java.util.ArrayList;

public class ACTSObject {

    public static final int BBG_Value = 6800;
    private SUT sut;
    private int interactionParameter = 3;

    public SUT getSut() {
        return sut;
    }

    public ACTSObject() {
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
        //typeOfFunding.addValue("Mischfinanziert");
        typeOfFunding.setType(Parameter.PARAM_TYPE_ENUM);

        Parameter paymentBeginning = sut.addParam("Zuzahlung_zu_Beginn");
        paymentBeginning.addValue("0");
        paymentBeginning.addValue("2000");
        paymentBeginning.setType(Parameter.PARAM_TYPE_INT);

        Parameter amount40b = sut.addParam("Beitrag_40b");
        amount40b.addValue("0");
        amount40b.addValue("2000");
        amount40b.setType(Parameter.PARAM_TYPE_INT);

        Parameter amount363 = sut.addParam("Beitrag_663");
        amount363.addValue("0");
        amount363.addValue("2000");
        amount363.setType(Parameter.PARAM_TYPE_INT);

        Parameter amountEmployee = sut.addParam("AN_Beitrag");
        amountEmployee.addValue("0");
        amountEmployee.addValue("2000");
        amountEmployee.setType(Parameter.PARAM_TYPE_INT);

        Parameter amountEmployer = sut.addParam("AG_Beitrag");
        amountEmployer.addValue("0");
        amountEmployer.addValue("2000");
        amountEmployer.setType(Parameter.PARAM_TYPE_INT);

        // add the default relation with interaction t
        sut.addDefaultRelation(interactionParameter);

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

        Constraint c5 = new Constraint("Art_der_Rueckdeckung = \"Unterstuetzungskasse\"" +
                " => Beitrag_40b = 0 && Beitrag_663 = 0", params);
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

        // print out the sut
        System.out.println(sut);
    }


}
