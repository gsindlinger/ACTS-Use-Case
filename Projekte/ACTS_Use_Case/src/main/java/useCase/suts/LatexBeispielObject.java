package useCase;

import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.SUT;

public class LatexBeispiel {

    private SUT sut;

    public SUT getSut() {
        return sut;
    }


    public LatexBeispiel(int interactionParameter) {
        sut = new SUT("LatexBeispiel");

        Parameter a = sut.addParam("a");
        a.addValue("A");
        a.addValue("B");
        a.setType(Parameter.PARAM_TYPE_ENUM);


        Parameter b = sut.addParam("b");
        b.addValue("1");
        b.addValue("2");
        b.addValue("3");
        a.setType(Parameter.PARAM_TYPE_INT);

        Parameter c = sut.addParam("c");
        c.addValue("x");
        c.addValue("y");
        c.setType(Parameter.PARAM_TYPE_ENUM);

        sut.addDefaultRelation(interactionParameter);

    }
}
