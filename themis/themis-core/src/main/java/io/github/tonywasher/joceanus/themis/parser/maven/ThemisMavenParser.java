/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.github.tonywasher.joceanus.themis.parser.maven;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadStatusReport;
import io.github.tonywasher.joceanus.themis.exc.ThemisDataException;
import io.github.tonywasher.joceanus.themis.exc.ThemisLogicException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisDataResource;
import io.github.tonywasher.joceanus.themis.parser.maven.ThemisMavenPom.ThemisXMavenControl;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Maven Parser.
 */
public class ThemisMavenParser
        implements ThemisXMavenControl {
    /**
     * The POM file name.
     */
    private static final String POM_NAME = "pom.xml";

    /**
     * The map of maven artifacts.
     */
    private final Map<ThemisMavenId, ThemisMavenPom> thePoms;

    /**
     * The list of local modules.
     */
    private final List<ThemisMavenPom> theModules;

    /**
     * The dependencies.
     */
    private final List<ThemisMavenId> theDependencies;

    /**
     * The pom.
     */
    private final ThemisMavenPom thePom;

    /**
     * Have we processed local definitions?
     */
    private boolean processedLocal;

    /**
     * Constructor.
     *
     * @param pReport   the reporter
     * @param pLocation the location of the project
     * @throws OceanusException on error
     */
    public ThemisMavenParser(final TethysUIThreadStatusReport pReport,
                             final File pLocation) throws OceanusException {
        /* Obtain the active profile */
        OceanusProfile myTask = pReport.getActiveTask();
        myTask = myTask.startTask(ThemisDataResource.TASK_DISCOVER);
        pReport.initTask(ThemisDataResource.TASK_DISCOVER);
        pReport.setNumStages(2);

        /* Create the map */
        thePoms = new LinkedHashMap<>();
        processedLocal = false;

        /* Parse the local project */
        thePom = loadPomAtLocation(pLocation);
        processedLocal = true;

        /* Loop through the local modules */
        OceanusProfile mySubTask = myTask.startTask(ThemisDataResource.TASK_DISCOVERLOCAL);
        final List<ThemisMavenPom> myModules = new ArrayList<>(thePoms.values());
        pReport.setNewStage(ThemisDataResource.TASK_DISCOVERLOCAL);
        pReport.setNumSteps(myModules.size());
        for (ThemisMavenPom myModule : myModules) {
            /* Process the module */
            final String myName = myModule.getId().toString();
            mySubTask.startTask(myName);
            pReport.setNextStep();
            myModule.getVersions();
            myModule.getDirectDependencies();
        }

        /* Loop through the local modules */
        mySubTask = myTask.startTask(ThemisDataResource.TASK_DISCOVERDEPENDENCY);
        pReport.setNewStage(ThemisDataResource.TASK_DISCOVERDEPENDENCY);
        pReport.setNumSteps(myModules.size());
        for (ThemisMavenPom myModule : myModules) {
            /* Process the module */
            final String myName = myModule.getId().toString();
            mySubTask.startTask(myName);
            pReport.setNextStep();
            myModule.getDependencies();
        }

        /* Create list of local modules */
        theModules = getModuleList(myModules);
        theDependencies = thePom.getDependencies();
        myTask.end();
    }

    /**
     * Obtain the name.
     *
     * @return the name
     */
    public String getName() {
        return thePom.getArtifactId();
    }

    /**
     * Obtain the local modules.
     *
     * @return the local modules
     */
    public List<ThemisMavenId> getModules() {
        return theModules.stream().map(ThemisMavenPom::getId).toList();
    }

    /**
     * Obtain the parsed modules.
     *
     * @return the parsed modules
     */
    public List<ThemisMavenPom> getParsedModules() {
        return theModules;
    }

    /**
     * Obtain the dependencies.
     *
     * @return the dependencies
     */
    public List<ThemisMavenId> getDependencies() {
        return theDependencies;
    }

    @Override
    public ThemisMavenPom loadPomViaId(final ThemisMavenId pId) throws OceanusException {
        /* Look for already known Pom */
        final ThemisMavenPom myExisting = thePoms.get(pId);
        if (myExisting != null) {
            return myExisting;
        }

        /* Reject if we have not fully processed local poms */
        if (!processedLocal) {
            throw new ThemisLogicException("Processed pom via id before finished local processing");
        }

        /* Load the pom file and store into cache */
        final ThemisMavenPom myPom = doLoadPomViaId(pId);
        thePoms.put(pId, myPom);

        /* If the pom has an associated jar */
        if (myPom.isJarPackaging()) {
            /* Make sure that the pom has been downloaded */
            ThemisMavenLocation.ensureJarArtifact(pId);
        }

        /* Process dependencies */
        myPom.getVersions();
        myPom.getDirectDependencies();
        myPom.getDependencies();

        /* Return the pom */
        return myPom;
    }

    /**
     * Do load pom via Id.
     *
     * @param pId the id of the pom
     * @return the loaded pom
     * @throws OceanusException on error
     */
    private ThemisMavenPom doLoadPomViaId(final ThemisMavenId pId) throws OceanusException {
        /* If the id has a classifier */
        if (pId.getClassifier() != null) {
            /* There is no pom , so just fake it */
            return new ThemisMavenPom(pId);
        }

        /* Make sure that the pom has been downloaded */
        ThemisMavenLocation.ensurePomArtifact(pId);

        /* Just do a normal load */
        return new ThemisMavenPom(this, new File(ThemisMavenLocation.getLocalPomFileName(pId)));
    }

    @Override
    public ThemisMavenPom loadPomAtLocation(final File pLocation) throws OceanusException {
        /* Parse the pom and obtain the id */
        final ThemisMavenPom myPom = new ThemisMavenPom(this, new File(pLocation, POM_NAME));
        final ThemisMavenId myId = myPom.getId();

        /* Make sure that the pom has not been seen before */
        final ThemisMavenPom myExisting = thePoms.get(myId);
        if (myExisting != null) {
            throw new ThemisDataException("Duplicate POM - " + myId);
        }

        /* Store into map */
        thePoms.put(myId, myPom);

        /* Process local details */
        myPom.processLocalDetails();

        /* Return the Pom */
        return myPom;
    }

    /**
     * Create list of local jar modules.
     *
     * @param pLocal the list of local modules
     * @return the list.
     * @throws OceanusException on error
     */
    private List<ThemisMavenPom> getModuleList(final List<ThemisMavenPom> pLocal) throws OceanusException {
        /* Loop through the local modules */
        final List<ThemisMavenPom> myModules = new ArrayList<>();
        for (ThemisMavenPom myModule : pLocal) {
            if (myModule.isJarPackaging()) {
                myModules.add(myModule);
            }
        }
        return myModules;
    }

    /**
     * Combine module dependencies.
     *
     * @return the dependencies
     * @throws OceanusException on error
     */
    public List<ThemisMavenId> getProjectDependencies() throws OceanusException {
        /* Allocate the dependencies */
        final List<ThemisMavenId> myIds = new ArrayList<>();

        /* Loop through the modules */
        for (ThemisMavenPom myModule : theModules) {
            /* Loop through direct dependencies */
            for (ThemisMavenId myDependency : myModule.getDependencies()) {
                /* Check the dependency and add if required */
                ThemisMavenPom.checkDependencyId(myIds, myDependency);
            }
        }

        /* Return the id */
        return myIds;
    }

}
