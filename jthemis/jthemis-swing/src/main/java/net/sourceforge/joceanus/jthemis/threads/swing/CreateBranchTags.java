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

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportTask;
import net.sourceforge.joceanus.jthemis.scm.tasks.Directory2;
import net.sourceforge.joceanus.jthemis.svn.data.SvnBranch;
import net.sourceforge.joceanus.jthemis.svn.data.SvnRepository;
import net.sourceforge.joceanus.jthemis.svn.data.SvnWorkingCopy.SvnWorkingCopySet;
import net.sourceforge.joceanus.jthemis.svn.tasks.VersionMgr;

/**
 * Thread to handle creation of branch tags.
 * @author Tony Washer
 */
public class CreateBranchTags
        extends ScmThread {
    /**
     * Branches.
     */
    private final Collection<SvnBranch> theBranches;

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
    private final SvnRepository theRepository;

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
     * @param pBranches the branches to create the tags for
     * @param pLocation the location to create into
     * @param pReport the report object
     */
    public CreateBranchTags(final SvnBranch[] pBranches,
                            final File pLocation,
                            final ReportTask pReport) {
        /* Call super-constructor */
        super(pReport);

        /* Store parameters */
        theLocation = pLocation;
        theReport = pReport;
        theRepository = pBranches[0].getRepository();

        /* protect against exceptions */
        try {
            /* Create new directory for working copy */
            Directory2.createDirectory(pLocation);

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
            /* Create the tags */
            VersionMgr myVersionMgr = new VersionMgr(theRepository, theLocation, this);
            myVersionMgr.createTags(theBranches);

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
