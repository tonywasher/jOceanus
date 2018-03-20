package net.sourceforge.joceanus.jmoneywise.lethe.swing;

import java.util.List;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusAtlasDatabase;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusAtlasDatabaseType;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Test Database.
 */
public class TestDB {

    /**
     * Main test case.
     * @param pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        MetisSwingToolkit myToolkit;
        try {
            myToolkit = new MetisSwingToolkit();
            MetisPreferenceManager myManager = myToolkit.getPreferenceManager();
            PrometheusDatabasePreferences myPreferences = myManager.getPreferenceSet(PrometheusDatabasePreferences.class);

            PrometheusAtlasDatabase myDatabase = PrometheusAtlasDatabaseType.POSTGRES.getClient(myPreferences);
            myDatabase.setPrefix("M");
            myDatabase.connectToDatabase(null);
            List<String> myList = myDatabase.listDatabases();
            myDatabase.createDatabase("a123");
            myDatabase.dropDatabase("a123");

        } catch (OceanusException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
