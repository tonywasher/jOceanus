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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.dsm.ThemisDSMMaven;

/**
 * Project.
 */
public class ThemisAnalysisProject {
    /**
     * The module name.
     */
    private final String theName;

    /**
     * The location.
     */
    private final File theLocation;

    /**
     * The module list.
     */
    private final List<ThemisAnalysisModule> theModules;

    /**
     * The initial dataMap.
     */
    private final ThemisAnalysisDataMap theDataMap;

    /**
     * The error.
     */
    private OceanusException theError;

    /**
     * Constructor.
     * @param pLocation the project location
     * @throws OceanusException on error
     */
    ThemisAnalysisProject(final File pLocation) throws OceanusException {
        /* Store the name and location */
        theLocation = pLocation;
        theName = pLocation.getName();

        /* Create the list */
        theModules = new ArrayList<>();

        /* Create the dataMap */
        theDataMap = new ThemisAnalysisDataMap();

        /* Initiate search for modules */
        parseProjectFile(new File(theLocation, ThemisDSMMaven.POM));

        /* ConsolidationPass process the packages */
        performConsolidationPass();

        /* FinalPass process the packages */
        performFinalPass();
    }

    /**
     * Obtain the name.
     * @return the name
     */
    String getName() {
        return theName;
    }

    /**
     * Obtain the location.
     * @return the location
     */
    File getLocation() {
        return theLocation;
    }

    /**
     * Obtain the error.
     * @return the error
     */
    OceanusException getError() {
        return theError;
    }

    /**
     * Obtain the modules.
     * @return the modules
     */
    List<ThemisAnalysisModule> getModules() {
        return theModules;
    }

    /**
     * Obtain the dataMap.
     * @return the map
     */
    ThemisAnalysisDataMap getDataMap() {
        return theDataMap;
    }

    /**
     * Parse the maven top-level project file.
     * @param pPom the project file
     * @throws OceanusException on error
     */
    private void parseProjectFile(final File pPom) throws OceanusException {
        /* If the pom file does not exist, just return */
        if (!pPom.exists()) {
            return;
        }

        /* Add module if source directory exists */
        final File mySrc = new File(pPom.getParent(), ThemisAnalysisModule.PATH_XTRA);
        if (mySrc.exists()
                && mySrc.isDirectory()) {
            /* Add the module to the list */
            theModules.add(new ThemisAnalysisModule(this, new File(pPom.getParent())));
        }

        /* Protect against exceptions */
        try (InputStream myInStream = new FileInputStream(pPom)) {
            /* Parse the Project definition file */
            final ThemisDSMMaven myPom = new ThemisDSMMaven(myInStream);

            /* Loop through the modules */
            for (final String myModuleName : myPom.getModules()) {
                final File myModuleDir = new File(pPom.getParentFile(), myModuleName);
                /* Process the project file */
                parseProjectFile(new File(myModuleDir, ThemisDSMMaven.POM));
            }

            /* Catch exceptions */
        } catch (IOException
                | OceanusException e) {
            /* Save Exception */
            theModules.clear();
            theError = new ThemisIOException("Failed to parse Project file", e);
        }
    }

    /**
     * consolidationPass process modules.
     * @throws OceanusException on error
     */
    private void performConsolidationPass() throws OceanusException {
        /* Loop through the modules */
        for (ThemisAnalysisModule myModule : theModules) {
            /* Process the module */
            myModule.performConsolidationPass();
        }
    }

    /**
     * finalPass process modules.
     * @throws OceanusException on error
     */
    private void performFinalPass() throws OceanusException {
        /* Loop through the modules */
        for (ThemisAnalysisModule myModule : theModules) {
            /* Process the module */
            myModule.performFinalPass();
        }
    }
}
