/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.threads;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitBundle;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRepository;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmComponent;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnComponent;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRepository;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnWorkingCopy.ThemisSvnWorkingCopySet;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnExtract;

/**
 * Thread to handle analysis of repository.
 */
public class ThemisDiscoverData
        implements MetisThread<Void> {
    /**
     * The SubVersion Repository.
     */
    private ThemisSvnRepository theSvnRepository;

    /**
     * The Git Repository.
     */
    private ThemisGitRepository theGitRepository;

    /**
     * The WorkingCopySet.
     */
    private ThemisSvnWorkingCopySet theWorkingCopySet;

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
    public ThemisSvnRepository getSvnRepository() {
        return theSvnRepository;
    }

    /**
     * Obtain the repository.
     * @return the repository
     */
    public ThemisGitRepository getGitRepository() {
        return theGitRepository;
    }

    /**
     * Obtain the working copy set.
     * @return the working copy set
     */
    public ThemisSvnWorkingCopySet getWorkingCopySet() {
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
        final Iterator<ThemisScmComponent> myIterator = theSvnRepository.getComponents().iterator();
        while (myIterator.hasNext()) {
            final ThemisSvnComponent myComp = (ThemisSvnComponent) myIterator.next();

            /* Create an extract plan for the component */
            final ThemisSvnExtract myPlan = new ThemisSvnExtract(myComp, theGitRepository);
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
        final Iterator<ThemisSvnExtract> myIterator = theExtractPlanMap.values().iterator();
        while (myIterator.hasNext()) {
            final ThemisSvnExtract myPlan = myIterator.next();

            /* Create the data entry */
            final MetisViewerEntry myEntry = pDataMgr.newEntry(pParent, myPlan.getName());
            myEntry.setObject(myPlan);
        }
    }

    /**
     * Obtain the extract for the component.
     * @param pComponent the component
     * @return the extract plan
     */
    public ThemisSvnExtract getExtractForComponent(final ThemisSvnComponent pComponent) {
        return theExtractPlanMap.get(pComponent.getName());
    }

    @Override
    public Void performTask(final MetisToolkit pToolkit) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the thread manager */
            final MetisThreadManager myManager = pToolkit.getThreadManager();
            final MetisPreferenceManager myPreferences = pToolkit.getPreferenceManager();

            /* Start the analyse svnRepository task */
            final MetisProfile myBaseTask = myManager.getActiveTask();
            MetisProfile myTask = myBaseTask.startTask("analyseSvnRepository");

            /* Discover subVersion repository details */
            theSvnRepository = new ThemisSvnRepository(myPreferences, myManager);

            /* Start the discoverWorkingSet task */
            myTask = myBaseTask.startTask("analyseWorkingSet");

            /* Discover workingSet details */
            myManager.checkForCancellation();
            theWorkingCopySet = new ThemisSvnWorkingCopySet(theSvnRepository, myManager);

            /* Start the discover gitRepository task */
            myTask = myBaseTask.startTask("analyseGitRepository");

            /* Discover Git repository details */
            myManager.checkForCancellation();
            theGitRepository = new ThemisGitRepository(myPreferences, myManager);

            /* Start the derivePlans task */
            myTask = myBaseTask.startTask("deriveExtractPlans");

            /* Build the Extract Plans */
            myManager.checkForCancellation();
            deriveExtractPlans();

            /* Complete the task */
            myTask.end();

            /* Return null */
            return null;

        } finally {
            /* Dispose of any connections */
            if (theSvnRepository != null) {
                theSvnRepository.dispose();
            }
        }
    }

    /**
     * Test backup.
     * @param pReport the report status
     * @throws OceanusException on error
     */
    protected void testBackup(final MetisThreadStatusReport pReport) throws OceanusException {
        /* Create a backup */
        final ThemisGitBundle myBundle = new ThemisGitBundle(pReport);
        final File myFile = new File("c:\\Users\\Tony\\jhunters.bdl");
        myBundle.createBundleFromComponent(theGitRepository.locateComponent("jhunters"), myFile);
        myBundle.createComponentFromBundle(theGitRepository, "Test", myFile);
    }

    /**
     * Test backup2.
     * @param pReport the report status
     * @throws OceanusException on error
     */
    protected void testBackup2(final MetisThreadStatusReport pReport) throws OceanusException {
        /* Create a backup */
        final ThemisGitBundle myBundle = new ThemisGitBundle(pReport);
        final File myFile = new File("c:\\Users\\Tony\\jhunters.bdl");
        myBundle.createBundleFromComponent(theGitRepository.locateComponent("jhunters"), myFile);
        myBundle.createComponentFromBundle(theGitRepository, myFile);
    }
}
