package useCase;

import useCase.suts.ACTSObject;
import util.Util;

import java.io.File;

public class BuildTxtForSUT {

    public static void main(String[] args) {
        ACTSObject acts = new ACTSObject(2);
        File workingDirectory = new File(System.getProperty("user.dir"));
        String fileName = workingDirectory.getAbsolutePath() + "\\output\\" + "acts_system_export_" + Util.getDatetimeStamp() + ".txt" ;
        //System.out.println(fileName);
        acts.buildTxtForSUT(fileName);
    }


}
