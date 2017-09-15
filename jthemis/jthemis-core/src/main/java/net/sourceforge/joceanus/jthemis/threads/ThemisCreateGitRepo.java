/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jthemis.threads;

import java.time.LocalTime;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRepository;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnComponent;
import net.sourceforge.joceanus.jthemis.svn.tasks.ThemisBuildGit;

/**
 * Thread to handle creation of GitRepo from Subversion component.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class ThemisCreateGitRepo<N, I>
        implements MetisThread<Void, N, I> {
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
     * @param pSource the source subversion component
     */
    public ThemisCreateGitRepo(final ThemisSvnComponent pSource) {
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
    public String getTaskName() {
        return ThemisThreadId.CREATEGITREPO.toString();
    }

    @Override
    public Void performTask(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Access the thread manager */
        final MetisThreadManager<N, I> myManager = pToolkit.getThreadManager();
        final MetisPreferenceManager myPreferences = pToolkit.getPreferenceManager();

        /* Access git repository */
        theGitRepo = new ThemisGitRepository(myPreferences, myManager);

        /* Create a new Git repository */
        final ThemisBuildGit myBuild = new ThemisBuildGit(theSource, theGitRepo);
        final Long myStart = System.nanoTime();
        myBuild.buildRepository(myManager);
        final Long myEnd = System.nanoTime();
        final LocalTime myDuration = LocalTime.ofNanoOfDay(myEnd - myStart);
        myManager.setNewStage("Elapsed: " + myDuration);

        /* Return null */
        return null;
    }
}
