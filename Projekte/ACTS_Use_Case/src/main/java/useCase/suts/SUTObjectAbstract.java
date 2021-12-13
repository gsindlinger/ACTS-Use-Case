package useCase.suts;

import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.SUT;

public abstract class SUTObject {

    public SUT sut;
    public int interactionParameter;
    protected Parameter tariff;
    protected Parameter paymentBeginning;
    protected Parameter amount40b;
    protected Parameter amount363;


    public SUTObject(int interactionParameter) {

        this.sut = new SUT();
        this.interactionParameter = interactionParameter;

        // it is recommended to create a new parameter from the SUT object
        // doing so will assign the parameter with an ID automatically
        tariff = sut.addParam("Tarif");

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

        paymentBeginning = sut.addParam("Zuzahlung_zu_Beginn");
        paymentBeginning.setType(Parameter.PARAM_TYPE_INT);

        amount40b = sut.addParam("Beitrag_40b");
        amount40b.setType(Parameter.PARAM_TYPE_INT);

        amount363 = sut.addParam("Beitrag_363");
        amount363.setType(Parameter.PARAM_TYPE_INT);


        // add the default relation with interaction t
        sut.addDefaultRelation(this.interactionParameter);
    }
}
