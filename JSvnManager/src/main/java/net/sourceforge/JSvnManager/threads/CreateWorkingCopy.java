/*******************************************************************************
 * Subversion: Java SubVersion Management
 * Copyright 2012 Tony Washer
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
 * $URL: http://tony-laptop/svn/finance/JSvnManager/branches/v1.1.0/src/main/java/net/sourceforge/JSvnManager/threads/CreateWorkingCopy.java $
 * $Revision: 153 $
 * $Author: Tony $
 * $Date: 2012-09-07 16:50:07 +0100 (Fri, 07 Sep 2012) $
 ******************************************************************************/
package net.sourceforge.JSvnManager.threads;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.swing.SwingWorker;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JSvnManager.data.Branch;
import net.sourceforge.JSvnManager.data.JSvnReporter.ReportStatus;
import net.sourceforge.JSvnManager.data.JSvnReporter.ReportTask;
import net.sourceforge.JSvnManager.data.Repository;
import net.sourceforge.JSvnManager.data.WorkingCopy.WorkingCopySet;
import net.sourceforge.JSvnManager.tasks.CheckOut;
import net.sourceforge.JSvnManager.tasks.Directory;

import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Thread to handle creation of working copy.
 * @author Tony Washer
 */
public class CreateWorkingCopy extends SwingWorker<Void, String> implements ReportStatus {
    /**
     * Branches.
     */
    private final Collection<Branch> theBranches;

    /**
     * Revision.
     */
    private final SVNRevision theRevision;

    /**
     * Location.
     */
    private final File theLocation;

    /**
     * Report object.
     */
    private final ReportTask theReport;

    /**
     * The Repository.
     */
    private final Repository theRepository;

    /**
     * The WorkingCopySet.
     */
    private WorkingCopySet theWorkingCopySet = null;

    /**
     * The Error.
     */
    private JDataException theError = null;

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
     * @param pBranches the branches to create the working copy for
     * @param pRevision the revision to check out
     * @param pLocation the location to create into
     * @param pReport the report object
     */
    public CreateWorkingCopy(final Branch[] pBranches,
                             final SVNRevision pRevision,
                             final File pLocation,
                             final ReportTask pReport) {
        /* Store parameters */
        theLocation = pLocation;
        theRevision = pRevision;
        theReport = pReport;
        theRepository = pBranches[0].getRepository();
        Collection<Branch> myBranches = null;

        /* protect against exceptions */
        try {
            /* Create new directory for working copy */
            Directory.createDirectory(pLocation);

            /* Access branch list for extract */
            myBranches = Branch.getBranchMap(pBranches).values();
        } catch (JDataException e) {
            /* Store the error and cancel thread */
            theError = e;
            cancel(true);
        }

        /* Record branches */
        theBranches = myBranches;
    }

    @Override
    protected Void doInBackground() {
        /* Protect against exceptions */
        try {
            /* Check out the branches */
            CheckOut myCheckOut = new CheckOut(theRepository, theReport);
            myCheckOut.checkOutBranches(theBranches, theRevision, theLocation);

            /* Discover workingSet details */
            theWorkingCopySet = new WorkingCopySet(theRepository, theLocation, this);
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
