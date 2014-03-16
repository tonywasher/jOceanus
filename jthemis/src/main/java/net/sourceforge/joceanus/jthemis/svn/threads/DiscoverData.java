/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.svn.threads;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.git.data.GitRepository;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportTask;
import net.sourceforge.joceanus.jthemis.svn.data.SvnComponent;
import net.sourceforge.joceanus.jthemis.svn.data.SvnExtract;
import net.sourceforge.joceanus.jthemis.svn.data.SvnRepository;
import net.sourceforge.joceanus.jthemis.svn.data.SvnWorkingCopy.SvnWorkingCopySet;

/**
 * Thread to handle analysis of repository.
 * @author Tony Washer
 */
public class DiscoverData
        extends SwingWorker<Void, String>
        implements ReportStatus {
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
    private SvnRepository theRepository;

    /**
     * The WorkingCopySet.
     */
    private SvnWorkingCopySet theWorkingCopySet;

    /**
     * The Git Repository.
     */
    private GitRepository theGitRepo;

    /**
     * The extract plan map.
     */
    private final Map<String, SvnExtract> theExtractPlanMap;

    /**
     * The Error.
     */
    private JOceanusException theError = null;

    /**
     * Obtain the repository.
     * @return the repository
     */
    public SvnRepository getRepository() {
        return theRepository;
    }

    /**
     * Obtain the working copy set.
     * @return the working copy set
     */
    public SvnWorkingCopySet getWorkingCopySet() {
        return theWorkingCopySet;
    }

    /**
     * Obtain the git repository.
     * @return the git repository
     */
    public GitRepository getGitRepo() {
        return theGitRepo;
    }

    /**
     * Derive the extract plan for component.
     * @throws JOceanusException on error
     */
    private void deriveExtractPlans() throws JOceanusException {
        /* Loop through the components */
        Iterator<SvnComponent> myIterator = theRepository.getComponents().iterator();
        while (myIterator.hasNext()) {
            /* Create an extract plan for the component */
            SvnComponent myComp = myIterator.next();
            SvnExtract myPlan = new SvnExtract(myComp);
            theExtractPlanMap.put(myComp.getName(), myPlan);
        }
    }

    /**
     * Declare extract plan.
     * @param pDataMgr the data manager
     */
    public void declareExtractPlans(final JDataManager pDataMgr) {
        /* Loop through the plans */
        Iterator<SvnExtract> myIterator = theExtractPlanMap.values().iterator();
        while (myIterator.hasNext()) {
            SvnExtract myPlan = myIterator.next();

            /* Create the data entry */
            JDataEntry myEntry = pDataMgr.new JDataEntry(myPlan.getName());
            myEntry.addAsRootChild();
            myEntry.setObject(myPlan);
        }
    }

    /**
     * Obtain the error.
     * @return the error
     */
    public JOceanusException getError() {
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
        theExtractPlanMap = new LinkedHashMap<String, SvnExtract>();
    }

    @Override
    protected Void doInBackground() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Discover repository details */
            theRepository = new SvnRepository(thePreferenceMgr, this);

            /* Discover workingSet details */
            theWorkingCopySet = new SvnWorkingCopySet(theRepository, this);

            /* Build the Extract Plans */
            deriveExtractPlans();

            /* Discover git repository details */
            theGitRepo = new GitRepository(thePreferenceMgr, this);

            /* Return null */
            return null;

        } finally {
            /* Dispose of any connections */
            if (theRepository != null) {
                theRepository.dispose();
            }
        }
    }

    @Override
    public void done() {
        /* Protect against exceptions */
        try {
            /* Force out any exceptions that occurred in the thread */
            get();

            /* Catch exceptions */
        } catch (InterruptedException
                | ExecutionException e) {
            theError = new JThemisIOException("Failed to perform background task", e);
        }

        /* Report task complete */
        theReport.completeTask(this);
    }

    @Override
    public boolean initTask(final String pTask) {
        publish(pTask);
        return true;
    }

    @Override
    public boolean setNewStage(final String pStage) {
        publish(pStage);
        return true;
    }

    @Override
    public boolean setNumStages(final int pNumStages) {
        return true;
    }

    @Override
    public void process(final List<String> pStatus) {
        for (String myStatus : pStatus) {
            theReport.setNewStage(myStatus);
        }
    }
}
