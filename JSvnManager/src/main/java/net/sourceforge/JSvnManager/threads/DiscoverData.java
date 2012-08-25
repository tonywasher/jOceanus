package net.sourceforge.JSvnManager.threads;

import java.util.List;

import javax.swing.SwingWorker;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JPreferenceSet.PreferenceManager;
import net.sourceforge.JSvnManager.data.JSvnReporter.ReportStatus;
import net.sourceforge.JSvnManager.data.JSvnReporter.ReportTask;
import net.sourceforge.JSvnManager.data.Repository;
import net.sourceforge.JSvnManager.data.WorkingCopy.WorkingCopySet;

public class DiscoverData extends SwingWorker<Void, String> implements ReportStatus {
    /**
     * Preference Manager.
     */
    private final PreferenceManager thePreferenceMgr;

    /**
     * Report object.
     */
    private final ReportTask theReport;

    /**
     * The Repository.
     */
    private Repository theRepository;

    /**
     * The WorkingCopySet.
     */
    private WorkingCopySet theWorkingCopySet;

    /**
     * The Error.
     */
    private JDataException theError;

    /**
     * Obtain the repository.
     * @return the repository
     */
    public Repository getRepository() {
        return theRepository;
    }

    /**
     * Obtain the working copy set.
     * @return the working copy set
     */
    public WorkingCopySet getWorkingCopySet() {
        return theWorkingCopySet;
    }

    /**
     * Obtain the error.
     * @return the error
     */
    public JDataException getError() {
        return theError;
    }

    /**
     * Constructor.
     * @param pPreferenceMgr the preference manager
     * @param pReport the report object
     */
    public DiscoverData(final PreferenceManager pPreferenceMgr,
                        final ReportTask pReport) {
        thePreferenceMgr = pPreferenceMgr;
        theReport = pReport;
    }

    @Override
    protected Void doInBackground() {
        /* Protect against exceptions */
        try {
            /* Discover repository details */
            theRepository = new Repository(thePreferenceMgr, this);

            /* Discover workingSet details */
            theWorkingCopySet = new WorkingCopySet(theRepository, this);
        } catch (JDataException e) {
            /* Store the error */
            theError = e;
        }

        /* Return null */
        return null;
    }

    @Override
    public void done() {
        /* Report task complete */
        theReport.completeTask(this);
    }

    @Override
    public void reportStatus(final String pStatus) {
        publish(pStatus);
    }

    @Override
    public void process(List<String> pStatus) {
        for (String myStatus : pStatus) {
            theReport.reportStatus(myStatus);
        }
    }
}
