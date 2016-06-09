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
package net.sourceforge.joceanus.jthemis.threads;

import java.io.File;
import java.util.Collection;

import org.tmatesoft.svn.core.wc.SVNRevision;

import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.scm.tasks.ThemisDirectory;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnBranch;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRepository;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnWorkingCopy.SvnWorkingCopySet;
import net.sourceforge.joceanus.jthemis.svn.tasks.ThemisCheckOut;

/**
 * Thread to handle creation of working copy.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class ThemisCreateWorkingCopy<N, I>
        implements MetisThread<Void, N, I> {
    /**
     * Branches.
     */
    private Collection<ThemisSvnBranch> theBranches;

    /**
     * Revision.
     */
    private final SVNRevision theRevision;

    /**
     * Location.
     */
    private final File theLocation;

    /**
     * The Repository.
     */
    private final ThemisSvnRepository theRepository;

    /**
     * The WorkingCopySet.
     */
    private SvnWorkingCopySet theWorkingCopySet;

    /**
     * Constructor.
     * @param pBranches the branches to create the working copy for
     * @param pRevision the revision to check out
     * @param pLocation the location to create into
     */
    public ThemisCreateWorkingCopy(final ThemisSvnBranch[] pBranches,
                                   final SVNRevision pRevision,
                                   final File pLocation) {
        /* Store parameters */
        theLocation = pLocation;
        theRevision = pRevision;
        theRepository = pBranches[0].getRepository();
    }

    /**
     * Obtain the working copy set.
     * @return the working copy set
     */
    public SvnWorkingCopySet getWorkingCopySet() {
        return theWorkingCopySet;
    }

    @Override
    public String getTaskName() {
        return "CreateWorkingCopy";
    }

    @Override
    public void prepareTask(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Create new directory for working copy */
        ThemisDirectory.createDirectory(theLocation);
    }

    @Override
    public Void performTask(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the thread manager */
            MetisThreadManager<N, I> myManager = pToolkit.getThreadManager();

            /* Check out the branches */
            ThemisCheckOut myCheckOut = new ThemisCheckOut(theRepository, myManager);
            myCheckOut.checkOutBranches(theBranches, theRevision, theLocation);

            /* Discover workingSet details */
            theWorkingCopySet = new SvnWorkingCopySet(theRepository, theLocation, myManager);
        } finally {
            /* Dispose of any connections */
            if (theRepository != null) {
                theRepository.dispose();
            }
        }

        /* Return null */
        return null;
    }
}
