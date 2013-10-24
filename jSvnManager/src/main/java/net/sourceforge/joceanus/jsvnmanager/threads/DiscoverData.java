/*******************************************************************************
 * jSvnManager: Java SubVersion Management
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jsvnmanager.threads;

import java.util.List;

import javax.swing.SwingWorker;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jpreferenceset.PreferenceManager;
import net.sourceforge.joceanus.jsvnmanager.data.JSvnReporter.ReportStatus;
import net.sourceforge.joceanus.jsvnmanager.data.JSvnReporter.ReportTask;
import net.sourceforge.joceanus.jsvnmanager.data.Repository;
import net.sourceforge.joceanus.jsvnmanager.data.WorkingCopy.WorkingCopySet;

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
    private Repository theRepository;

    /**
     * The WorkingCopySet.
     */
    private WorkingCopySet theWorkingCopySet;

    /**
     * The Error.
     */
    private JDataException theError = null;

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
        } finally {
            /* Dispose of any connections */
            if (theRepository != null) {
                theRepository.dispose();
            }
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
