package net.sourceforge.joceanus.jmoneywise.test.lethe.data.statics;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet.MetisEnumPreference;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.lethe.database.MoneyWiseDatabase;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusDatabase.PrometheusDatabasePreferenceKey;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusJDBCDriver;
import net.sourceforge.joceanus.jtethys.OceanusException;

public class CreateH2Database {
    /**
     * Main test program
     * @param args the arguments
     */
    public static void main(final String[] args) {
        /* Protect against failures */
        try {
            /* Access Preferences */
            final MetisViewerManager myViewer = new MetisViewerManager();
            final MetisPreferenceManager myPrefMgr = new MetisPreferenceManager(myViewer);
            final PrometheusDatabasePreferences myPrefs = myPrefMgr.getPreferenceSet(PrometheusDatabasePreferences.class);
            final MetisEnumPreference<PrometheusDatabasePreferenceKey, PrometheusJDBCDriver> myDBType
                    =  myPrefs.getEnumPreference(PrometheusDatabasePreferenceKey.DBDRIVER, PrometheusJDBCDriver.class);
            myDBType.setValue(PrometheusJDBCDriver.H2);
            myPrefs.storeChanges();

            /* Access Database */
            final MoneyWiseDatabase myDatabase = new MoneyWiseDatabase(myPrefs);

            /* Create database */
            myDatabase.createTables(new NullStatusReport());

            /* Make sure that the database is closed */
        } catch (Exception e) {
            /* Close the database */
            e.printStackTrace();
        }
    }

    /**
     * Report status.
     */
    public static class NullStatusReport
            implements MetisThreadStatusReport {
        private final MetisProfile myProfile = new MetisProfile("Test");

        @Override
        public void initTask(String pTask) throws OceanusException {

        }

        @Override
        public void setNumStages(int pNumStages) throws OceanusException {

        }

        @Override
        public void setNewStage(String pStage) throws OceanusException {

        }

        @Override
        public void setNumSteps(int pNumSteps) throws OceanusException {

        }

        @Override
        public void setStepsDone(int pSteps) throws OceanusException {

        }

        @Override
        public void setNextStep() throws OceanusException {

        }

        @Override
        public void setNextStep(String pStep) throws OceanusException {

        }

        @Override
        public void setCompletion() throws OceanusException {

        }

        @Override
        public void checkForCancellation() throws OceanusException {

        }

        @Override
        public void throwCancelException() throws OceanusException {

        }

        @Override
        public MetisProfile getActiveTask() {
            return myProfile;
        }
    }
}
