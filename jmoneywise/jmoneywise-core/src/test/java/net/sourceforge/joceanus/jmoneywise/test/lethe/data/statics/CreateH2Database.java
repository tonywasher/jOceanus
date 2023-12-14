package net.sourceforge.joceanus.jmoneywise.test.lethe.data.statics;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet.MetisEnumPreference;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.lethe.database.MoneyWiseXDatabase;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusDatabase.PrometheusDatabasePreferenceKey;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusJDBCDriver;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;

/**
 * H2 Database Create Test.
 */
public final class CreateH2Database {
    /**
     * private constructor.
     */
    private CreateH2Database() {
    }

    /**
     * Main test program.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        /* Protect against failures */
        try {
            /* Access Preferences */
            final MetisViewerManager myViewer = new MetisViewerManager();
            final MetisPreferenceManager myPrefMgr = new MetisPreferenceManager(myViewer);
            final PrometheusDatabasePreferences myPrefs = myPrefMgr.getPreferenceSet(PrometheusDatabasePreferences.class);
            final MetisEnumPreference<PrometheusJDBCDriver> myDBType
                    =  myPrefs.getEnumPreference(PrometheusDatabasePreferenceKey.DBDRIVER, PrometheusJDBCDriver.class);
            myDBType.setValue(PrometheusJDBCDriver.H2);
            myPrefs.storeChanges();

            /* Access Database */
            final MoneyWiseXDatabase myDatabase = new MoneyWiseXDatabase(myPrefs);

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
            implements TethysUIThreadStatusReport {
        /**
         * Profile.
         */
        private final TethysProfile myProfile = new TethysProfile("Test");

        @Override
        public void initTask(final String pTask) {
        }

        @Override
        public void setNumStages(final int pNumStages) {
        }

        @Override
        public void setNewStage(final String pStage) {
        }

        @Override
        public void setNumSteps(final int pNumSteps) {
        }

        @Override
        public void setStepsDone(final int pSteps) {
        }

        @Override
        public void setNextStep() {
        }

        @Override
        public void setNextStep(final String pStep) {
        }

        @Override
        public void setCompletion() {
        }

        @Override
        public void checkForCancellation() {
        }

        @Override
        public void throwCancelException() {
        }

        @Override
        public TethysProfile getActiveTask() {
            return myProfile;
        }
    }
}
