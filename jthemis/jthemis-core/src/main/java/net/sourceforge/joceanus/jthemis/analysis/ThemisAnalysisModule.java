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
package net.sourceforge.joceanus.jthemis.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Module.
 */
public class ThemisAnalysisModule {
    /**
     * The path xtra.
     */
    static final String PATH_XTRA = "/src/main/java";

    /**
     * The module-info file.
     */
    private static final String MODULE_INFO = "module-info" + ThemisAnalysisPackage.SFX_JAVA;

    /**
     * The module name.
     */
    private final String theName;

    /**
     * The location.
     */
    private final File theLocation;

    /**
     * The package list.
     */
    private final List<ThemisAnalysisPackage> thePackages;

    /**
     * The initial dataMap.
     */
    private final ThemisAnalysisDataMap theDataMap;

    /**
     * Constructor.
     * @param pProject the project
     * @param pLocation the module location
     * @throws OceanusException on error
     */
    ThemisAnalysisModule(final ThemisAnalysisProject pProject,
                         final File pLocation) throws OceanusException {
        /* Initialise class */
        this(pLocation, new ThemisAnalysisDataMap(pProject.getDataMap()));
    }

    /**
     * Constructor.
     * @param pLocation the module location
     * @throws OceanusException on error
     */
    ThemisAnalysisModule(final File pLocation) throws OceanusException {
        /* Initialise class */
        this(pLocation, new ThemisAnalysisDataMap());

        /* consolidationPass process the packages */
        performConsolidationPass();

        /* finalPass process the packages */
        performFinalPass();
    }

    /**
     * Constructor.
     * @param pLocation the module location
     * @param pDataMap the dataMap
     * @throws OceanusException on error
     */
    private ThemisAnalysisModule(final File pLocation,
                                 final ThemisAnalysisDataMap pDataMap) throws OceanusException {
        /* Store the name and location */
        theLocation = new File(pLocation, PATH_XTRA);
        theName = pLocation.getName();
        theDataMap = pDataMap;

        /* Create the list */
        thePackages = new ArrayList<>();

        /* Initiate search for packages */
        checkForPackage(null);
    }

    /**
     * Obtain the name.
     * @return the name
     */
    String getName() {
        return theName;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Obtain the location.
     * @return the location
     */
    File getLocation() {
        return theLocation;
    }

    /**
     * Obtain the packages.
     * @return the packages
     */
    List<ThemisAnalysisPackage> getPackages() {
        return thePackages;
    }

    /**
     * Obtain the dataMap.
     * @return the map
     */
    ThemisAnalysisDataMap getDataMap() {
        return theDataMap;
    }

    /**
     * Check for package.
     * @param pPackage the package name
     * @throws OceanusException on error
     */
    void checkForPackage(final String pPackage) throws OceanusException {
        /* Assume not a package */
        boolean isPackage = false;

        /* Determine the location to search */
        final File myLocation = pPackage == null
                                ? theLocation
                                : new File(theLocation, pPackage.replace(ThemisAnalysisChar.PERIOD, ThemisAnalysisChar.COMMENT));

        /* Look for java files or further packages */
        for (File myFile: Objects.requireNonNull(myLocation.listFiles())) {
            /* Access file name */
            final String myName = myFile.getName();

            /* If this is a directory */
            if (myFile.isDirectory()) {
                final String myPackage = pPackage == null
                                         ? myName
                                         : pPackage + ThemisAnalysisChar.PERIOD + myName;
                checkForPackage(myPackage);
            }

            /* If this is aAccess file name */
            if (myName.endsWith(ThemisAnalysisPackage.SFX_JAVA)
                    && !myName.equals(MODULE_INFO)) {
                isPackage = pPackage != null;
            }
        }

        /* If this is a package */
        if (isPackage) {
            /* Add the package to the list */
            thePackages.add(new ThemisAnalysisPackage(this, pPackage));
        }
    }

    /**
     * consolidationPass process packages.
     * @throws OceanusException on error
     */
    void performConsolidationPass() throws OceanusException {
        /* Loop through the packages */
        for (ThemisAnalysisPackage myPackage : thePackages) {
            /* Process the package */
            myPackage.performConsolidationPass();
        }
    }

    /**
     * secondPass process packages.
     * @throws OceanusException on error
     */
    void performFinalPass() throws OceanusException {
        /* Loop through the packages */
        for (ThemisAnalysisPackage myPackage : thePackages) {
            /* Process the package */
            myPackage.performFinalPass();
        }
    }
}
