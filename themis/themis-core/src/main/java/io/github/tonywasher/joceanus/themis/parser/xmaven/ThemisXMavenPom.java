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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Maven Pom.
 */
public class ThemisXMavenPom {
    /**
     * The controller.
     */
    interface ThemisXMavenControl {
        /**
         * Load a pom via its id.
         *
         * @param pId the id of the pom
         * @return the loaded pom
         * @throws OceanusException on error
         */
        ThemisXMavenPom loadPomViaId(ThemisXMavenId pId) throws OceanusException;

        /**
         * Load a pom at the location.
         *
         * @param pLocation the location of the pom
         * @return the loaded pom
         */
        ThemisXMavenPom loadPomAtLocation(File pLocation) throws OceanusException;
    }

    /**
     * The controller.
     */
    private final ThemisXMavenControl theControl;

    /**
     * The location.
     */
    private final File theLocation;

    /**
     * The parser.
     */
    private final ThemisXMavenPomParser theParser;

    /**
     * The Maven Id.
     */
    private final ThemisXMavenId theId;

    /**
     * The parent.
     */
    private final ThemisXMavenPom theParent;

    /**
     * The property cache.
     */
    private final ThemisXMavenPropertyCache theProperties;

    /**
     * The list of extra directories.
     */
    private final List<String> theXtraDirs;

    /**
     * The list of modules.
     */
    private final List<ThemisXMavenPom> theModules;

    /**
     * The version cache.
     */
    private ThemisXMavenVersionCache theVersions;

    /**
     * The list of dependencies.
     */
    private List<ThemisXMavenPom> theDependencies;

    /**
     * Constructor.
     *
     * @param pController the controller
     * @param pLocation   the location
     * @throws OceanusException on error
     */
    ThemisXMavenPom(final ThemisXMavenControl pController,
                    final File pLocation) throws OceanusException {
        /* Store the controller and location */
        theControl = pController;
        theLocation = new File(pLocation.getParent());

        /* Create the cache */
        theProperties = new ThemisXMavenPropertyCache();

        /* Create the parser */
        theParser = new ThemisXMavenPomParser(pLocation, theProperties);

        /* Create the various lists */
        theModules = new ArrayList<>();
        theXtraDirs = new ArrayList<>();

        /* Access the parent Pom */
        final ThemisXMavenId myParentId = theParser.getParent();

        /* If we have a parent */
        if (myParentId != null) {
            /* Access pom details */
            theParent = theControl.loadPomViaId(myParentId);

            /* Inherit the parent properties and read own properties */
            theProperties.setParent(theParent.getProperties());

        } else {
            theParent = null;
        }

        /* Read the properties */
        theParser.readProperties();

        /* Load the id */
        theId = theParser.getId(theParent == null ? null : theParent.getId());
    }

    /**
     * Obtain the id.
     *
     * @return the properties
     */
    ThemisXMavenId getId() {
        return theId;
    }

    /**
     * Is this pom packaging?
     *
     * @return true/false
     * @throws OceanusException on error
     */
    boolean isPomPackaging() throws OceanusException {
        return theParser.isPomPackaging();
    }

    /**
     * Is this jar packaging?
     *
     * @return true/false
     * @throws OceanusException on error
     */
    boolean isJarPackaging() throws OceanusException {
        return theParser.isJarPackaging();
    }

    /**
     * Obtain the modules.
     *
     * @return the modules
     */
    List<ThemisXMavenPom> getModules() {
        return theModules;
    }

    /**
     * Obtain the dependencies.
     *
     * @return the dependencies
     * @throws OceanusException on error
     */
    List<ThemisXMavenPom> getDependencies() throws OceanusException {
        /* If we have not processed dependencies */
        if (theDependencies == null) {
            /* Process dependencies */
            theDependencies = processDependencies();
        }

        /* Return the dependencies */
        return theDependencies;
    }

    /**
     * Obtain the extraDirectories.
     *
     * @return the extra Directories
     */
    List<String> getXtraDirs() {
        return theXtraDirs;
    }

    /**
     * Obtain the versions.
     *
     * @return the versions
     * @throws OceanusException on error
     */
    ThemisXMavenVersionCache getVersions() throws OceanusException {
        /* If we have not processed versions */
        if (theVersions == null) {
            /* Process dependencies */
            theVersions = processDependencyManagement();
        }

        /* Return the dependencies */
        return theVersions;
    }

    /**
     * Obtain the properties.
     *
     * @return the properties
     */
    ThemisXMavenPropertyCache getProperties() {
        return theProperties;
    }

    /**
     * Process local details.
     *
     * @throws OceanusException on error
     */
    void processLocalDetails() throws OceanusException {
        /* Look up any modules */
        final List<String> myModules = theParser.getModules();
        for (String myModule : myModules) {
            /* Load the file at the location */
            final ThemisXMavenPom myLoaded = theControl.loadPomAtLocation(new File(theLocation, myModule));
            theModules.add(myLoaded);
        }

        /* Look up any extra directories */
        final List<String> myXtraDirs = theParser.getXtraDirs();
        theXtraDirs.addAll(myXtraDirs);
    }

    /**
     * Process dependencies.
     *
     * @return the dependencies
     * @throws OceanusException on error
     */
    private ThemisXMavenVersionCache processDependencyManagement() throws OceanusException {
        /* Allocate the cache */
        final ThemisXMavenVersionCache myCache = new ThemisXMavenVersionCache();

        /* Attach to parent if present */
        if (theParent != null) {
            myCache.setParent(theParent.getVersions());
        }

        /* Look up any dependencyManagement */
        final List<ThemisXMavenId> myDependencyMgmt = theParser.getDependencyManagement();
        for (ThemisXMavenId myDependency : myDependencyMgmt) {
            /* If this is a BOM */
            if ("import".equals(myDependency.getScope())) {
                final ThemisXMavenPom myLoaded = theControl.loadPomViaId(myDependency);
                myCache.importDependencies(myLoaded.getVersions());
            } else {
                myCache.addToCache(myDependency);
            }
        }

        /* Return the cache */
        return myCache;
    }

    /**
     * Process dependencies.
     *
     * @return the dependencies
     * @throws OceanusException on error
     */
    private List<ThemisXMavenPom> processDependencies() throws OceanusException {
        /* Allocate the dependencies */
        final List<ThemisXMavenPom> myPoms = new ArrayList<>();

        /* Look up any dependencies */
        final List<ThemisXMavenId> myDependencies = theParser.getDependencies();
        for (ThemisXMavenId myDependency : myDependencies) {
            /* Load the dependency via its id */
            final ThemisXMavenId myId = theVersions.lookUpVersion(myDependency);
            final ThemisXMavenPom myLoaded = theControl.loadPomViaId(myId);
            myPoms.add(myLoaded);
        }

        /* Return the poms */
        return myPoms;
    }
}
