/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadData;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitBundle;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRepository;

/**
 * Thread to handle analysis of repository.
 */
public class ThemisDiscoverData
        implements MetisThread<Void> {
    /**
     * The Git Repository.
     */
    private ThemisGitRepository theGitRepository;

    /**
     * Constructor.
     */
    public ThemisDiscoverData() {
    }

    /**
     * Obtain the repository.
     * @return the repository
     */
    public ThemisGitRepository getGitRepository() {
        return theGitRepository;
    }

    @Override
    public String getTaskName() {
        return "DiscoverData";
    }


    @Override
    public Void performTask(final MetisThreadData pThreadData) throws OceanusException {
        /* Access the thread manager */
        final PrometheusToolkit myToolkit = (PrometheusToolkit) pThreadData;
        final MetisThreadManager myManager = myToolkit.getThreadManager();
        final MetisPreferenceManager myPreferences = myToolkit.getPreferenceManager();

        /* Start the analyse svnRepository task */
        final MetisProfile myBaseTask = myManager.getActiveTask();

        /* Start the discover gitRepository task */
        final MetisProfile myTask = myBaseTask.startTask("analyseGitRepository");

        /* Discover Git repository details */
        myManager.checkForCancellation();
        theGitRepository = new ThemisGitRepository(myPreferences, myManager);

        /* Complete the task */
        myTask.end();

        testBackup2(myManager);

        /* Return null */
        return null;
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
        myBundle.createBundleFromComponent(theGitRepository.locateComponent("jHunters"), myFile);
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
        myBundle.createBundleFromComponent(theGitRepository.locateComponent("jHunters"), myFile);
        myBundle.createComponentFromBundle(theGitRepository, myFile);
    }
}
