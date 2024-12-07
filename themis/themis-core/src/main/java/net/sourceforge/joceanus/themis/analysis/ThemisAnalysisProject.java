/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.themis.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisIOException;

/**
 * Project.
 */
public class ThemisAnalysisProject
        implements ThemisAnalysisElement {
    /**
     * Consolidation Pass error.
     */
    private static final String CONSOLIDATION_ERROR = "Failed on consolidation Pass";

    /**
     * The project name.
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
     */
    public ThemisAnalysisProject(final File pLocation) {
        /* Store the name and location */
        theLocation = pLocation;
        theName = pLocation.getName();

        /* Create the list */
        theModules = new ArrayList<>();

        /* Create the dataMap */
        theDataMap = new ThemisAnalysisDataMap();

        /* Initiate search for modules */
        parseProjectFile(new File(theLocation, ThemisAnalysisMaven.POM));

        /* InitialPass */
        if (theError == null) {
            performInitialPass();
        }

        /* ConsolidationPass */
        if (theError == null) {
            performConsolidationPass();
        }

        /* FinalPass */
        if (theError == null) {
            performFinalPass();
        }
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
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
    public OceanusException getError() {
        return theError;
    }

    /**
     * Obtain the modules.
     * @return the modules
     */
    public List<ThemisAnalysisModule> getModules() {
        return theModules;
    }

    /**
     * Obtain the dataMap.
     * @return the map
     */
    ThemisAnalysisDataMap getDataMap() {
        return theDataMap;
    }

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Parse the maven top-level project file.
     * @param pPom the project file
     */
    private void parseProjectFile(final File pPom) {
        /* If the pom file does not exist, just return */
        if (!pPom.exists()) {
            return;
        }

        /* Protect against exceptions */
        try {
            /* Add module if source directory exists */
            final File mySrc = new File(pPom.getParent(), ThemisAnalysisModule.PATH_XTRA);
            if (mySrc.exists()
                && mySrc.isDirectory()) {
                /* Add the module to the list */
                theModules.add(new ThemisAnalysisModule(this, new File(pPom.getParent())));
            }

            /* Handle exceptions */
        } catch (OceanusException e) {
            /* Save Exception */
            theError = new ThemisIOException(CONSOLIDATION_ERROR, e);
            return;
        }

        /* Protect against exceptions */
        try (InputStream myInStream = new FileInputStream(pPom)) {
            /* Parse the Project definition file */
            final ThemisAnalysisMaven myPom = new ThemisAnalysisMaven(myInStream);

            /* Loop through the modules */
            for (final String myModuleName : myPom.getModules()) {
                /* Access module directory */
                final File myModuleDir = new File(pPom.getParentFile(), myModuleName);

                /* Process the project file */
                parseProjectFile(new File(myModuleDir, ThemisAnalysisMaven.POM));

                /* Break loop on error */
                if (theError != null) {
                    break;
                }
            }

            /* Catch exceptions */
        } catch (IOException
                | OceanusException e) {
            /* Save Exception */
            theError = new ThemisIOException("Failed to parse Project file", e);
        }
    }

    /**
     * initialPass.
     */
    private void performInitialPass() {
        /* Protect against exceptions */
        try {
            /* Loop through the modules */
            for (ThemisAnalysisModule myModule : theModules) {
                /* Process the module */
                myModule.performInitialPass();
            }

            /* Handle exceptions */
        } catch (OceanusException e) {
            /* Save Exception */
            theError = new ThemisIOException(CONSOLIDATION_ERROR, e);
        }
    }

    /**
     * consolidationPass.
     */
    private void performConsolidationPass() {
        /* Protect against exceptions */
        try {
            /* Loop through the modules */
            for (ThemisAnalysisModule myModule : theModules) {
                /* Process the module */
                myModule.performConsolidationPass();
            }

            /* Handle exceptions */
        } catch (OceanusException e) {
            /* Save Exception */
            theError = new ThemisIOException(CONSOLIDATION_ERROR, e);
        }
    }

    /**
     * finalPass.
     */
    private void performFinalPass() {
        /* Protect against exceptions */
        try {
            /* Loop through the modules */
            for (ThemisAnalysisModule myModule : theModules) {
                /* Process the module */
                myModule.performFinalPass();
            }

            /* Handle exceptions */
        } catch (OceanusException e) {
            /* Save Exception */
            theError = new ThemisIOException("Failed on final pass", e);
        }
    }
}
