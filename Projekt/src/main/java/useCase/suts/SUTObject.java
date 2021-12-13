package useCase.suts;

import edu.uta.cse.fireeye.common.Constraint;
import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.SUT;

import java.util.ArrayList;

public abstract class SUTObject extends SUTObjectAbstract {

    public enum IncludeInvalidValues{
        INCLUDE_INVALID_VALUES,
        EXCLUDE_INVALID_VALUES
    }

    public enum IncludeValuesForConstraints{
        INCLUDE_VALUES_FOR_CONSTRAINTS,
        EXCLUDE_VALUES_FOR_CONSTRAINTS
    }


    public IncludeValuesForConstraints includeValuesForConstraints = IncludeValuesForConstraints.INCLUDE_VALUES_FOR_CONSTRAINTS;






    public SUTObject(int interactionParameter) {

        super(interactionParameter);


        implementation = sut.addParam("Durchfuehrungsweg");
        implementation.addValue("Direktversicherung");
        implementation.addValue("Direktzusage");
        implementation.addValue("Pensionskasse");
        implementation.setType(Parameter.PARAM_TYPE_ENUM);


        typeOfReinsurance = sut.addParam("Art_der_Rueckdeckung");
        typeOfReinsurance.addValue("keine");
        typeOfReinsurance.addValue("Rueckdeckungsversicherung");
        typeOfReinsurance.addValue("Unterstuetzungskasse");
        typeOfReinsurance.setType(Parameter.PARAM_TYPE_ENUM);




        //Constraints: Fitting the tariffs to the possible combinations visible on the web platform

        //AREven and HR20 have no restrictions, since those tariffs are available as Direktversicherung, U-Kasse and Direktzusage
        //AROdd only for for Rückdeckungstyp: Rückdeckungsversicherung
        ArrayList<Parameter> params = new ArrayList();
        params.add(tariff);
        params.add(typeOfReinsurance);
        params.add(implementation);
        Constraint cAROdd = new Constraint("Tarif = \"AROdd\" => Durchfuehrungsweg = \"Direktzusage\" && " +
                "Art_der_Rueckdeckung = \"Rueckdeckungsversicherung\"",  params);
        sut.addConstraint(cAROdd);

        //FR20 only for Direktversicherung
        params = new ArrayList();
        params.add(tariff);
        params.add(implementation);
        params.add(typeOfReinsurance);
        Constraint cFR20 = new Constraint("Tarif = \"FR20\" => Durchfuehrungsweg = \"Direktversicherung\" || Art_der_Rueckdeckung = \"Rueckdeckungsversicherung\"",
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
                " => (Art_der_Rueckdeckung = \"Unterstuetzungskasse\" || Art_der_Rueckdeckung = \"Rueckdeckungsversicherung\")", params);
        sut.addConstraint(c7);


    }


    //It's quite hard to consider invalid values at Easy Web since the constraints for invalid values are given by calculated values and
    //cannot be assigned to single parameters



    public SUT getSut() {
        return sut;
    }

    public int getInteractionParameter() {
        return interactionParameter;
    }



}

