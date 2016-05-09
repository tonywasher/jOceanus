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

import java.time.LocalTime;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRepository;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmReporter.ReportTask;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnComponent;
import net.sourceforge.joceanus.jthemis.svn.tasks.ThemisBuildGit;

/**
 * Thread to handle creation of GitRepo from Subversion component.
 * @author Tony Washer
 */
public class ThemisCreateGitRepo
        extends ThemisScmThread {
    /**
     * Preference Manager.
     */
    private final MetisPreferenceManager thePreferenceMgr;

    /**
     * Report object.
     */
    private final ReportTask theReport;

    /**
     * The Component.
     */
    private final ThemisSvnComponent theSource;

    /**
     * The Git Repository.
     */
    private ThemisGitRepository theGitRepo;

    /**
     * Constructor.
     * @param pReport the report object
     * @param pSource the source subversion component
     */
    public ThemisCreateGitRepo(final ReportTask pReport,
                               final ThemisSvnComponent pSource) {
        super(pReport);
        thePreferenceMgr = pReport.getPreferenceMgr();
        theReport = pReport;
        theSource = pSource;
    }

    /**
     * Obtain the git repository.
     * @return the git repository
     */
    public ThemisGitRepository getGitRepo() {
        return theGitRepo;
    }

    @Override
    protected Void doInBackground() throws OceanusException {
        /* Access git repository */
        theGitRepo = new ThemisGitRepository(thePreferenceMgr, this);

        /* Create a new Git repository */
        ThemisBuildGit myBuild = new ThemisBuildGit(theSource, theGitRepo);
        Long myStart = System.nanoTime();
        myBuild.buildRepository(this);
        Long myEnd = System.nanoTime();
        LocalTime myDuration = LocalTime.ofNanoOfDay(myEnd - myStart);
        setNewStage("Elapsed: " + myDuration);

        /* Return null */
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
            setError(new ThemisIOException("Failed to perform background task", e));
        }

        /* Report task complete */
        theReport.completeTask(this);
    }
}
