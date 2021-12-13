package useCase;

import edu.uta.cse.fireeye.common.*;
import edu.uta.cse.fireeye.common.TestGenProfile.ConstraintMode;
import edu.uta.cse.fireeye.service.engine.IpoEngine;
import util.Util;

/**
 * This test class shows how to use the API interface of ACTS to
 * build a test set.
 */


/**
 * Constraints:
 * AR10 Direktversicherung, Direktzusage --> detaillierte Beträge
 * AR10 Unterstützungskasse --> nur Beitrag (also Betrag 3.63 und Betrag 40b = 0) & keine BBG-Grenze?
 *
 * Arbeitgeberfinanziert --> Arbeitnehmerbeitrag = 0?
 *
 * FR20 nur mit Direktversicherung und keine Rückdeckung?
 *
 * PK10 <=> Pensionskasse. Was ist mit Rückdeckung?
 *
 * Berechnung von Direktzusage (mit/ohne Rückdeckung)? Gibt es Rückdeckung nur bei Direktzusage?
 *
 */


/*
Direktversicherung nur ohne Rückversicherung - done

Direktzusage nur Rückdeckung + Unterstützungskasse (alles was Ukasse ist automatisch Direktzusage)

Pensionskasse nur ohne Rückversicherung

Unterstützungskasse keine BBG-Grenze/keine Zuzahlung

Direktzusage immer arbeitgeberfinanziert (keine BBG-Grenze)
*/



/*
Mindestbeitrag/monatlich = 50 Euro
Für die Fälle mit fehlender BBG-Grenze
Ab 125.000 extra Meldung mit Vorschlag bei Betreuer, aber nur wenn keine BBG-Grenze(Pensionskasse & Direktversicherung)

Direktzusage/Rückdeckung: Zuzahlung < 1 Mio., extra Meldung mit Vorschlag bei Betreuer

40b darf 2.148 Euro nicht überschreiten --> siehe Gesetz

 */








public class Test {
    public static void main(String[] argv) {
        ACTSObject acts = new ACTSObject(3);

        // print out the sut
        System.out.println(acts.getSut());

        // set the test generation profile
        // randomize don't care values
        TestGenProfile.instance().setRandstar(TestGenProfile.ON);
        // not ignoring constraints
        TestGenProfile.instance().setIgnoreConstraints(false);
        TestGenProfile.instance().setConstraintMode(ConstraintMode.solver);

        // Create an IPO engine object
        IpoEngine engine = new IpoEngine(acts.getSut());

        // build a test set
        engine.build();

        // get the resulting test set
        TestSet ts = engine.getTestSet();

        // print out the test set
        TestSetWrapper wrapper = new TestSetWrapper(ts, acts.getSut());

        // print into the standard out
        wrapper.outputInCSVFormat();

        // print into a file

        String outputString = String.format("output/Combinations_%s.csv", Util.getDatetimeStamp());
        wrapper.outputInCSVFormat(outputString);

        Util.addExpectedResults(outputString, Tool.ACTS);

    }
}

