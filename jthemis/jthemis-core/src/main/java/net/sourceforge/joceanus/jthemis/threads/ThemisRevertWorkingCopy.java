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

import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRepository;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnWorkingCopy.SvnWorkingCopySet;
import net.sourceforge.joceanus.jthemis.svn.tasks.ThemisCheckOut;

/**
 * Thread to handle revert of working copy.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class ThemisRevertWorkingCopy<N, I>
        implements MetisThread<Void, N, I> {
    /**
     * The Repository.
     */
    private final ThemisSvnRepository theRepository;

    /**
     * The Location.
     */
    private final File theLocation;

    /**
     * The WorkingCopySet.
     */
    private SvnWorkingCopySet theWorkingCopySet;

    /**
     * Constructor.
     * @param pWorkingSet the working set to update
     */
    public ThemisRevertWorkingCopy(final SvnWorkingCopySet pWorkingSet) {
        /* Store parameters */
        theWorkingCopySet = pWorkingSet;
        theLocation = pWorkingSet.getLocation();
        theRepository = pWorkingSet.getRepository();
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
        return "RevertWorkingCopy";
    }

    @Override
    public Void performTask(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the thread manager */
            MetisThreadManager<N, I> myManager = pToolkit.getThreadManager();

            /* Update the working copy set */
            ThemisCheckOut myCheckOut = new ThemisCheckOut(theRepository, myManager);
            myCheckOut.revertWorkingCopySet(theWorkingCopySet);

            /* Discover new workingSet details */
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
