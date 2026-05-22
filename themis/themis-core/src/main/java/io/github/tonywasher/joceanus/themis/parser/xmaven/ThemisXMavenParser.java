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

package io.github.tonywasher.joceanus.themis.parser.xmaven;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.exc.ThemisDataException;
import io.github.tonywasher.joceanus.themis.parser.xmaven.ThemisXMavenPom.ThemisXMavenControl;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Maven Parser.
 */
public class ThemisXMavenParser
        implements ThemisXMavenControl {
    /**
     * The POM file name.
     */
    private static final String POM_NAME = "pom.xml";

    /**
     * The map of maven artifacts.
     */
    private final Map<ThemisXMavenId, ThemisXMavenPom> thePoms;

    /**
     * The list of local modules.
     */
    private final List<ThemisXMavenPom> theModules;

    /**
     * Have we processed local definitions?
     */
    private boolean processedLocal;

    /**
     * Constructor.
     *
     * @param pLocation the location of the project
     * @throws OceanusException on error
     */
    public ThemisXMavenParser(final File pLocation) throws OceanusException {
        /* Create the map */
        thePoms = new LinkedHashMap<>();
        processedLocal = false;

        /* Parse the local project */
        final ThemisXMavenPom myPom = loadPomAtLocation(pLocation);
        processedLocal = true;

        /* Loop through the local modules */
        final List<ThemisXMavenPom> myModules = new ArrayList<>(thePoms.values());
        for (ThemisXMavenPom myModule : myModules) {
            myModule.getVersions();
            myModule.getDependencies();
        }

        /* Create list of local modules */
        theModules = getModuleList(myModules);
    }

    /**
     * Obtain the local modules.
     *
     * @return the local modules
     */
    List<ThemisXMavenPom> getModules() {
        return theModules;
    }

    @Override
    public ThemisXMavenPom loadPomViaId(final ThemisXMavenId pId) throws OceanusException {
        /* Look for already known Pom */
        final ThemisXMavenPom myExisting = thePoms.get(pId);
        if (myExisting != null) {
            return myExisting;
        }

        /* Make sure that the pom has been downloaded */
        ThemisXMavenLocation.ensurePomArtifact(pId);

        /* Load the pom file and store into cache */
        final ThemisXMavenPom myPom = new ThemisXMavenPom(this,
                new File(ThemisXMavenLocation.getLocalPomFileName(pId)));
        thePoms.put(pId, myPom);

        /* If the pom has an associated jar */
        if (myPom.isJarPackaging()) {
            /* Make sure that the pom has been downloaded */
            ThemisXMavenLocation.ensureJarArtifact(pId);
        }

        /* Process dependencies */
        myPom.getVersions();
        myPom.getDependencies();

        /* Return the pom */
        return myPom;
    }

    @Override
    public ThemisXMavenPom loadPomAtLocation(final File pLocation) throws OceanusException {
        /* Parse the pom and obtain the id */
        final ThemisXMavenPom myPom = new ThemisXMavenPom(this, new File(pLocation, POM_NAME));
        final ThemisXMavenId myId = myPom.getId();

        /* Make sure that the pom has not been seen before */
        final ThemisXMavenPom myExisting = thePoms.get(myId);
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
    private List<ThemisXMavenPom> getModuleList(final List<ThemisXMavenPom> pLocal) throws OceanusException {
        /* Loop through the local modules */
        final List<ThemisXMavenPom> myModules = new ArrayList<>();
        for (ThemisXMavenPom myModule : pLocal) {
            if (myModule.isJarPackaging()) {
                myModules.add(myModule);
            }
        }
        return myModules;
    }
}
