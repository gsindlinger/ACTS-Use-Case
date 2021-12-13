package util;

import edu.uta.cse.fireeye.common.Parameter;

import java.util.ArrayList;

public class TWayCoverage {

    private ArrayList<Parameter> paramList;
    private float coverage;


    public TWayCoverage(ArrayList<Parameter> paramList, float coverage) {
        this.paramList = paramList;
        this.coverage = coverage;
    }

    public ArrayList<Parameter> getParamList() {
        return paramList;
    }

    public float getCoverage() {
        return coverage;
    }

    @Override
    public String toString() {
        String paramList = "";
        for(Parameter p : this.paramList) {
            paramList += p.getName() + ",";
        }

        return "TWayCoverage{" +
                "parameter=" + paramList +
                " coverage=" + coverage +
                '}';
    }
}
