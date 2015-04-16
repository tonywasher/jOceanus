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

import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportTask;
import net.sourceforge.joceanus.jthemis.svn.data.SvnRepository;
import net.sourceforge.joceanus.jthemis.svn.data.SvnWorkingCopy.SvnWorkingCopySet;
import net.sourceforge.joceanus.jthemis.svn.tasks.CheckOut;

/**
 * Thread to handle revert of working copy.
 * @author Tony Washer
 */
public class RevertWorkingCopy
        extends ScmThread {
    /**
     * Report object.
     */
    private final ReportTask theReport;

    /**
     * The Repository.
     */
    private final SvnRepository theRepository;

    /**
     * The Location.
     */
    private final File theLocation;

    /**
     * The WorkingCopySet.
     */
    private SvnWorkingCopySet theWorkingCopySet;

    /**
     * The Error.
     */
    private JOceanusException theError;

    /**
     * Constructor.
     * @param pWorkingSet the working set to update
     * @param pReport the report object
     */
    public RevertWorkingCopy(final SvnWorkingCopySet pWorkingSet,
                             final ReportTask pReport) {
        /* Call super-constructor */
        super(pReport);

        /* Store parameters */
        theWorkingCopySet = pWorkingSet;
        theLocation = pWorkingSet.getLocation();
        theRepository = pWorkingSet.getRepository();
        theReport = pReport;
    }

    /**
     * Obtain the working copy set.
     * @return the working copy set
     */
    public SvnWorkingCopySet getWorkingCopySet() {
        return theWorkingCopySet;
    }

    @Override
    public JOceanusException getError() {
        return theError;
    }

    @Override
    protected Void doInBackground() {
        /* Protect against exceptions */
        try {
            /* Update the working copy set */
            CheckOut myCheckOut = new CheckOut(theRepository, this);
            myCheckOut.revertWorkingCopySet(theWorkingCopySet);

            /* Discover new workingSet details */
            theWorkingCopySet = new SvnWorkingCopySet(theRepository, theLocation, this);
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
}