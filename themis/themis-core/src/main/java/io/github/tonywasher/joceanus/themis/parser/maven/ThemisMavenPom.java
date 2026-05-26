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

import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.themis.exc.ThemisDataException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisDataResource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Maven Pom.
 */
public class ThemisMavenPom
        implements MetisFieldItem {
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
        ThemisMavenPom loadPomViaId(ThemisMavenId pId) throws OceanusException;

        /**
         * Load a pom at the location.
         *
         * @param pLocation the location of the pom
         * @return the loaded pom
         */
        ThemisMavenPom loadPomAtLocation(File pLocation) throws OceanusException;
    }

    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisMavenPom> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisMavenPom.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisDataResource.DATA_ID, ThemisMavenPom::getId);
        FIELD_DEFS.declareLocalField(ThemisDataResource.DATA_PARENT, ThemisMavenPom::getParent);
        FIELD_DEFS.declareLocalField(ThemisDataResource.DATA_PROPERTIES, ThemisMavenPom::getProperties);
        FIELD_DEFS.declareLocalField(ThemisDataResource.DATA_XTRADIRS, ThemisMavenPom::getXtraDirs);
        FIELD_DEFS.declareLocalField(ThemisDataResource.DATA_MODULES, ThemisMavenPom::getModules);
        FIELD_DEFS.declareLocalField(ThemisDataResource.DATA_VERSIONS, ThemisMavenPom::getTheVersions);
        FIELD_DEFS.declareLocalField(ThemisDataResource.DATA_DIRECTDEPENDENCIES, ThemisMavenPom::getTheDirectDependencies);
        FIELD_DEFS.declareLocalField(ThemisDataResource.DATA_DEPENDENCIES, ThemisMavenPom::getTheDependencies);
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
    private final ThemisMavenPomParser theParser;

    /**
     * The Maven Id.
     */
    private final ThemisMavenId theId;

    /**
     * The parent.
     */
    private final ThemisMavenPom theParent;

    /**
     * The property cache.
     */
    private final ThemisMavenPropertyCache theProperties;

    /**
     * The list of extra directories.
     */
    private final List<String> theXtraDirs;

    /**
     * The list of modules.
     */
    private final List<ThemisMavenPom> theModules;

    /**
     * Is this a jar package.
     */
    private final boolean isJarPackage;

    /**
     * The version cache.
     */
    private ThemisMavenVersionCache theVersions;

    /**
     * The list of direct dependencies.
     */
    private List<ThemisMavenPom> theDirectDependencies;

    /**
     * The full list of dependencies.
     */
    private List<ThemisMavenId> theDependencies;

    /**
     * Constructor.
     *
     * @param pController the controller
     * @param pLocation   the location
     * @throws OceanusException on error
     */
    ThemisMavenPom(final ThemisXMavenControl pController,
                   final File pLocation) throws OceanusException {
        /* Store the controller and location */
        theControl = pController;
        theLocation = new File(pLocation.getParent());

        /* Create the caches */
        theProperties = new ThemisMavenPropertyCache();

        /* Create the parser */
        theParser = new ThemisMavenPomParser(pLocation, theProperties);

        /* Create the various lists */
        theModules = new ArrayList<>();
        theXtraDirs = new ArrayList<>();

        /* Access the parent Pom */
        final ThemisMavenId myParentId = theParser.getParent();

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

        /* Determine jar packaging */
        isJarPackage = theParser.isJarPackaging();
    }

    /**
     * Constructor.
     *
     * @param pId the id
     * @throws OceanusException on error
     */
    ThemisMavenPom(final ThemisMavenId pId) throws OceanusException {
        /* Store the id */
        theId = pId;

        /* Store the controller and location */
        theControl = null;
        theLocation = null;
        theParent = null;

        /* Create the cache */
        theProperties = new ThemisMavenPropertyCache();
        theVersions = new ThemisMavenVersionCache();

        /* Create the parser */
        theParser = null;

        /* Set Pom packaging */
        isJarPackage = false;

        /* Create the various lists */
        theModules = new ArrayList<>();
        theXtraDirs = new ArrayList<>();
        theDirectDependencies = new ArrayList<>();
        theDependencies = new ArrayList<>();
    }

    @Override
    public MetisFieldSet<ThemisMavenPom> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return getId().formatObject(pFormatter);
    }

    /**
     * Obtain the id.
     *
     * @return the id
     */
    public ThemisMavenId getId() {
        return theId;
    }

    /**
     * Obtain the artifactId.
     *
     * @return the id
     */
    public String getArtifactId() {
        return theId.getArtifactId();
    }

    /**
     * Obtain the location.
     *
     * @return the parent
     */
    public File getLocation() {
        return theLocation;
    }

    /**
     * Obtain the parent.
     *
     * @return the parent
     */
    private ThemisMavenPom getParent() {
        return theParent;
    }

    /**
     * Is this jar packaging?
     *
     * @return true/false
     * @throws OceanusException on error
     */
    public boolean isJarPackaging() throws OceanusException {
        return isJarPackage;
    }

    /**
     * Obtain the modules.
     *
     * @return the modules
     */
    public List<ThemisMavenPom> getModules() {
        return theModules;
    }

    /**
     * Obtain the dependencies.
     *
     * @return the dependencies
     * @throws OceanusException on error
     */
    public List<ThemisMavenPom> getDirectDependencies() throws OceanusException {
        /* If we have not processed dependencies */
        if (theDirectDependencies == null) {
            /* Process dependencies */
            theDirectDependencies = processDependencies();
        }

        /* Return the dependencies */
        return theDirectDependencies;
    }

    /**
     * Obtain the direct dependencies.
     *
     * @return the dependencies
     */
    private List<ThemisMavenPom> getTheDirectDependencies() {
        /* Return the dependencies */
        return theDirectDependencies;
    }

    /**
     * Obtain the dependencies.
     *
     * @return the dependencies
     * @throws OceanusException on error
     */
    public List<ThemisMavenId> getDependencies() throws OceanusException {
        /* If we have not combined dependencies */
        if (theDependencies == null) {
            /* Combine dependencies */
            theDependencies = combineDependencies();
        }

        /* Return the dependencies */
        return theDependencies;
    }

    /**
     * Obtain the dependencies.
     *
     * @return the dependencies
     */
    public List<ThemisMavenId> getTheDependencies() {
        /* Return the dependencies */
        return theDependencies;
    }

    /**
     * Obtain the extraDirectories.
     *
     * @return the extra Directories
     */
    public List<String> getXtraDirs() {
        return theXtraDirs;
    }

    /**
     * Obtain the versions.
     *
     * @return the versions
     * @throws OceanusException on error
     */
    public ThemisMavenVersionCache getVersions() throws OceanusException {
        /* If we have not processed versions */
        if (theVersions == null) {
            /* Process dependencies */
            theVersions = processDependencyManagement();
        }

        /* Return the versions */
        return theVersions;
    }

    /**
     * Obtain the versions.
     *
     * @return the versions
     */
    private ThemisMavenVersionCache getTheVersions() {
        /* Return the versions */
        return theVersions;
    }

    /**
     * Obtain the properties.
     *
     * @return the properties
     */
    ThemisMavenPropertyCache getProperties() {
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
            final ThemisMavenPom myLoaded = theControl.loadPomAtLocation(new File(theLocation, myModule));
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
    private ThemisMavenVersionCache processDependencyManagement() throws OceanusException {
        /* Allocate the cache */
        final ThemisMavenVersionCache myCache = new ThemisMavenVersionCache();

        /* Attach to parent if present */
        if (theParent != null) {
            myCache.setParent(theParent.getVersions());
        }

        /* Look up any dependencyManagement */
        final List<ThemisMavenId> myDependencyMgmt = theParser.getDependencyManagement();
        for (ThemisMavenId myDependency : myDependencyMgmt) {
            /* If this is a BOM */
            if ("import".equals(myDependency.getScope())) {
                final ThemisMavenPom myLoaded = theControl.loadPomViaId(myDependency);
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
    private List<ThemisMavenPom> processDependencies() throws OceanusException {
        /* Allocate the dependencies */
        final List<ThemisMavenPom> myPoms = new ArrayList<>();

        /* Look up any dependencies */
        final List<ThemisMavenId> myDependencies = theParser.getDependencies();
        for (ThemisMavenId myDependency : myDependencies) {
            /* Load the dependency via its id */
            final ThemisMavenId myId = theVersions.lookUpVersion(myDependency);
            final ThemisMavenPom myLoaded = theControl.loadPomViaId(myId);
            myPoms.add(myLoaded);
        }

        /* Return the poms */
        return myPoms;
    }

    /**
     * Combine direct dependencies.
     *
     * @return the dependencies
     * @throws OceanusException on error
     */
    private List<ThemisMavenId> combineDependencies() throws OceanusException {
        /* Allocate the dependencies */
        final List<ThemisMavenId> myIds = new ArrayList<>();

        /* Combine dependencies for this pom */
        combineDependencies(myIds, this);

        /* Return the id */
        return myIds;
    }

    /**
     * Combine direct dependencies.
     *
     * @param pIds the list to populate
     * @param pPom the pom to process
     * @throws OceanusException on error
     */
    private static void combineDependencies(final List<ThemisMavenId> pIds,
                                            final ThemisMavenPom pPom) throws OceanusException {
        /* Loop through direct dependencies */
        for (ThemisMavenPom myDependency : pPom.getDirectDependencies()) {
            /* If we have not processed this dependency before */
            final ThemisMavenId myId = myDependency.getId();
            if (checkDependencyId(pIds, myId)) {
                /* Combine underlying dependencies */
                combineDependencies(pIds, myDependency);
            }
        }
    }

    /**
     * Check id.
     *
     * @param pDependencies the list of existing dependencies
     * @param pId           to id to check
     * @return was the id added true/false
     * @throws OceanusException on error
     */
    static boolean checkDependencyId(final List<ThemisMavenId> pDependencies,
                                     final ThemisMavenId pId) throws OceanusException {
        /* Loop through the existing dependencies */
        for (ThemisMavenId myDependency : pDependencies) {
            /* If we have a match */
            if (myDependency.equalsPrefix(pId)) {
                /* OK as long as version matches */
                if (myDependency.getVersion().equals(pId.getVersion())) {
                    return false;
                }

                /* Reject mismatch of versions */
                throw new ThemisDataException("Mismatch of dependency versions " + myDependency + " vs " + pId);
            }
        }

        /* No match, so add to list */
        pDependencies.add(pId);
        return true;
    }
}
