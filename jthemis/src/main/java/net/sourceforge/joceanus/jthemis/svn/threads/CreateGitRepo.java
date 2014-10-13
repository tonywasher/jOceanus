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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.git.data.GitRepository;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportTask;
import net.sourceforge.joceanus.jthemis.svn.data.SvnComponent;
import net.sourceforge.joceanus.jthemis.svn.tasks.BuildGit;

/**
 * Thread to handle creation of GitRepo from Subversion component.
 * @author Tony Washer
 */
public class CreateGitRepo
        extends ScmThread {
    /**
     * Preference Manager.
     */
    private final PreferenceManager thePreferenceMgr;

    /**
     * Report object.
     */
    private final ReportTask theReport;

    /**
     * The Component.
     */
    private final SvnComponent theSource;

    /**
     * The Git Repository.
     */
    private GitRepository theGitRepo;

    /**
     * Obtain the git repository.
     * @return the git repository
     */
    public GitRepository getGitRepo() {
        return theGitRepo;
    }

    /**
     * Constructor.
     * @param pReport the report object
     * @param pSource the source subversion component
     */
    public CreateGitRepo(final ReportTask pReport,
                         final SvnComponent pSource) {
        super(pReport);
        thePreferenceMgr = pReport.getPreferenceMgr();
        theReport = pReport;
        theSource = pSource;
    }

    @Override
    protected Void doInBackground() throws JOceanusException {
        /* Access git repository */
        theGitRepo = new GitRepository(thePreferenceMgr, this);

        /* Create a new Git repository */
        BuildGit myBuild = new BuildGit(theSource, theGitRepo);
        Long myStart = System.currentTimeMillis();
        myBuild.buildRepository(this);
        Long myDuration = System.currentTimeMillis() - myStart;
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
            setError(new JThemisIOException("Failed to perform background task", e));
        }

        /* Report task complete */
        theReport.completeTask(this);
    }
}
