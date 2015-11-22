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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportTask;
import net.sourceforge.joceanus.jthemis.svn.tasks.Backup;

/**
 * Thread to handle subVersion backups.
 * @author Tony Washer
 */
public class SubversionBackup
        extends ScmThread {
    /**
     * The preference manager.
     */
    private final PreferenceManager thePreferenceMgr;

    /**
     * The secure manager.
     */
    private final GordianHashManager theSecureMgr;

    /**
     * Report object.
     */
    private final ReportTask theReport;

    /**
     * Constructor (Event Thread).
     * @param pReport the report object
     */
    public SubversionBackup(final ReportTask pReport) {
        /* Call super-constructor */
        super(pReport);

        /* Store passed parameters */
        theReport = pReport;
        thePreferenceMgr = pReport.getPreferenceMgr();
        theSecureMgr = pReport.getSecureMgr();
    }

    @Override
    public Void doInBackground() throws JOceanusException {
        Backup myAccess = null;

        /* Create backup */
        myAccess = new Backup(this, thePreferenceMgr);
        myAccess.backUpRepositories(theSecureMgr);

        /* Return nothing */
        return null;
    }

    @Override
    public void done() {
        /* Protect against exceptions */
        try {
            /* Force out any exceptions that occurred in the thread */
            get();

            /* Catch exceptions */
        } catch (CancellationException
                | InterruptedException
                | ExecutionException e) {
            setError(new JThemisIOException("Failed to perform background task", e));
        }

        /* Report task complete */
        theReport.completeTask(this);
    }
}
