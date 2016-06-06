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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmReporter.ReportTask;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnComponent;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnExtract;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRepository;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnWorkingCopy.SvnWorkingCopySet;

/**
 * Thread to handle analysis of repository.
 * @author Tony Washer
 */
public class ThemisDiscoverData
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
     * @param pReport the report object
     */
    public ThemisDiscoverData(final ReportTask pReport) {
        super(pReport);
        thePreferenceMgr = pReport.getPreferenceMgr();
        theReport = pReport;
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
    protected Void doInBackground() throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Discover repository details */
            theRepository = new ThemisSvnRepository(thePreferenceMgr, this);

            /* Discover workingSet details */
            if (!isCancelled()) {
                theWorkingCopySet = new SvnWorkingCopySet(theRepository, this);
            }

            /* Build the Extract Plans */
            if (!isCancelled()) {
                deriveExtractPlans();
            }

            /* Return null */
            return null;

        } finally {
            /* Dispose of any connections */
            if (theRepository != null) {
                theRepository.dispose();
            }
        }
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
