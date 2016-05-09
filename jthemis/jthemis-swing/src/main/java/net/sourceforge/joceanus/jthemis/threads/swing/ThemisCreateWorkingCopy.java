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
package net.sourceforge.joceanus.jthemis.threads.swing;

import java.io.File;
import java.util.Collection;

import org.tmatesoft.svn.core.wc.SVNRevision;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmReporter.ReportTask;
import net.sourceforge.joceanus.jthemis.scm.tasks.ThemisDirectory;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnBranch;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRepository;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnWorkingCopy.SvnWorkingCopySet;
import net.sourceforge.joceanus.jthemis.svn.tasks.ThemisCheckOut;

/**
 * Thread to handle creation of working copy.
 * @author Tony Washer
 */
public class ThemisCreateWorkingCopy
        extends ThemisScmThread {
    /**
     * Branches.
     */
    private final Collection<ThemisSvnBranch> theBranches;

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
    private final ThemisSvnRepository theRepository;

    /**
     * The WorkingCopySet.
     */
    private SvnWorkingCopySet theWorkingCopySet = null;

    /**
     * The Error.
     */
    private OceanusException theError = null;

    /**
     * Constructor.
     * @param pBranches the branches to create the working copy for
     * @param pRevision the revision to check out
     * @param pLocation the location to create into
     * @param pReport the report object
     */
    public ThemisCreateWorkingCopy(final ThemisSvnBranch[] pBranches,
                                   final SVNRevision pRevision,
                                   final File pLocation,
                                   final ReportTask pReport) {
        /* Call super-constructor */
        super(pReport);

        /* Store parameters */
        theLocation = pLocation;
        theRevision = pRevision;
        theReport = pReport;
        theRepository = pBranches[0].getRepository();

        /* protect against exceptions */
        try {
            /* Create new directory for working copy */
            ThemisDirectory.createDirectory(pLocation);

            /* Access branch list for extract */
            // myBranches = SvnBranch.getBranchMap(pBranches).values();
        } catch (OceanusException e) {
            /* Store the error and cancel thread */
            theError = e;
            cancel(true);
        }

        /* Record branches */
        theBranches = null;
    }

    /**
     * Obtain the working copy set.
     * @return the working copy set
     */
    public SvnWorkingCopySet getWorkingCopySet() {
        return theWorkingCopySet;
    }

    @Override
    public OceanusException getError() {
        return theError;
    }

    @Override
    protected Void doInBackground() {
        /* Protect against exceptions */
        try {
            /* Check out the branches */
            ThemisCheckOut myCheckOut = new ThemisCheckOut(theRepository, this);
            myCheckOut.checkOutBranches(theBranches, theRevision, theLocation);

            /* Discover workingSet details */
            theWorkingCopySet = new SvnWorkingCopySet(theRepository, theLocation, this);
        } catch (OceanusException e) {
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
}
