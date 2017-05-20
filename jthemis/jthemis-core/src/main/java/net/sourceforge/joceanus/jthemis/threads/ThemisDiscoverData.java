/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2016 Tony Washer
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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.lethe.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.lethe.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnComponent;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnExtract;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRepository;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnWorkingCopy.SvnWorkingCopySet;

/**
 * Thread to handle analysis of repository.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class ThemisDiscoverData<N, I>
        implements MetisThread<Void, N, I> {
    /**
     * The Repository.
     */
    private ThemisSvnRepository theRepository;

    /**
     * The WorkingCopySet.
     */
    private SvnWorkingCopySet theWorkingCopySet;

    /**
     * The extract plan map.
     */
    private final Map<String, ThemisSvnExtract> theExtractPlanMap;

    /**
     * Constructor.
     */
    public ThemisDiscoverData() {
        theExtractPlanMap = new LinkedHashMap<>();
    }

    /**
     * Obtain the repository.
     * @return the repository
     */
    public ThemisSvnRepository getRepository() {
        return theRepository;
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
        return "DiscoverData";
    }

    /**
     * Derive the extract plan for component.
     * @throws OceanusException on error
     */
    private void deriveExtractPlans() throws OceanusException {
        /* Loop through the components */
        Iterator<ThemisSvnComponent> myIterator = theRepository.getComponents().iterator();
        while (myIterator.hasNext()) {
            /* Create an extract plan for the component */
            ThemisSvnComponent myComp = myIterator.next();
            ThemisSvnExtract myPlan = new ThemisSvnExtract(myComp);
            theExtractPlanMap.put(myComp.getName(), myPlan);
        }
    }

    /**
     * Declare extract plans.
     * @param pDataMgr the data manager
     * @param pParent the parent entry
     */
    public void declareExtractPlans(final MetisViewerManager pDataMgr,
                                    final MetisViewerEntry pParent) {
        /* Loop through the plans */
        Iterator<ThemisSvnExtract> myIterator = theExtractPlanMap.values().iterator();
        while (myIterator.hasNext()) {
            ThemisSvnExtract myPlan = myIterator.next();

            /* Create the data entry */
            MetisViewerEntry myEntry = pDataMgr.newEntry(pParent, myPlan.getName());
            myEntry.setObject(myPlan);
        }
    }

    @Override
    public Void performTask(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the thread manager */
            MetisThreadManager<N, I> myManager = pToolkit.getThreadManager();
            MetisPreferenceManager myPreferences = pToolkit.getPreferenceManager();

            /* Discover repository details */
            theRepository = new ThemisSvnRepository(myPreferences, myManager);

            /* Discover workingSet details */
            myManager.checkForCancellation();
            theWorkingCopySet = new SvnWorkingCopySet(theRepository, myManager);

            /* Build the Extract Plans */
            myManager.checkForCancellation();
            deriveExtractPlans();

            /* Return null */
            return null;

        } finally {
            /* Dispose of any connections */
            if (theRepository != null) {
                theRepository.dispose();
            }
        }
    }
}
