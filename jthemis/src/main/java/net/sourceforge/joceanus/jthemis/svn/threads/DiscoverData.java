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

import java.util.List;

import javax.swing.SwingWorker;

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.svn.data.JSvnReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.svn.data.JSvnReporter.ReportTask;
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
    }

    @Override
    protected Void doInBackground() {
        /* Protect against exceptions */
        try {
            /* Discover repository details */
            theRepository = new SvnRepository(thePreferenceMgr, this);

            /* Discover workingSet details */
            theWorkingCopySet = new SvnWorkingCopySet(theRepository, this);
        } catch (JOceanusException e) {
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
