/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.proj;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisIOException;
import net.sourceforge.joceanus.themis.xanalysis.parser.ThemisXAnalysisCodeParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Project.
 */
public class ThemisXAnalysisProject {
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
     * The parser.
     */
    private final ThemisXAnalysisCodeParser theParser;

    /**
     * The module list.
     */
    private final List<ThemisXAnalysisModule> theModules;

    /**
     * The error.
     */
    private OceanusException theError;

    /**
     * Constructor.
     * @param pLocation the project location
     */
    public ThemisXAnalysisProject(final File pLocation) {
        /* Store the name and location */
        theLocation = pLocation;

        /* Create the parser */
        theParser = new ThemisXAnalysisCodeParser();

        /* Create the list */
        theModules = new ArrayList<>();

        /* Initiate search for modules */
        theName = parseProjectFile(new File(theLocation, ThemisXAnalysisMaven.POM));

        /* Parse the code */
        if (theError == null) {
            parseJavaCode();
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
    public List<ThemisXAnalysisModule> getModules() {
        return theModules;
    }

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Parse the maven project file.
     * @param pPom the project file
     * @return the artifact name
     */
    private String parseProjectFile(final File pPom) {
        /* If the pom file does not exist, just return */
        if (!pPom.exists()) {
            return null;
        }

        /* Protect against exceptions */
        try {
            /* Add module if source directory exists */
            final File mySrc = new File(pPom.getParent(), ThemisXAnalysisModule.PATH_XTRA);
            if (mySrc.exists()
                    && mySrc.isDirectory()) {
                /* Add the module to the list */
                theModules.add(new ThemisXAnalysisModule(new File(pPom.getParent())));
            }

            /* Handle exceptions */
        } catch (OceanusException e) {
            /* Save Exception */
            theError = new ThemisIOException(CONSOLIDATION_ERROR, e);
            return null;
        }

        /* Protect against exceptions */
        try (InputStream myInStream = new FileInputStream(pPom)) {
            /* Parse the Project definition file */
            final ThemisXAnalysisMaven myPom = new ThemisXAnalysisMaven(myInStream);

            /* Loop through the modules */
            for (final String myModuleName : myPom.getModules()) {
                /* Access module directory */
                final File myModuleDir = new File(pPom.getParentFile(), myModuleName);

                /* Process the project file */
                parseProjectFile(new File(myModuleDir, ThemisXAnalysisMaven.POM));

                /* Break loop on error */
                if (theError != null) {
                    break;
                }
            }
            return myPom.getMavenId();

            /* Catch exceptions */
        } catch (IOException
                 | OceanusException e) {
            /* Save Exception */
            theError = new ThemisIOException("Failed to parse Project file", e);
            return null;
        }
    }

    /**
     * parse the java code.
     */
    private void parseJavaCode() {
        /* Protect against exceptions */
        try {
            /* Loop through the modules */
            for (ThemisXAnalysisModule myModule : theModules) {
                /* Process the module */
                myModule.parseJavaCode(theParser);
            }

            /* Handle exceptions */
        } catch (OceanusException e) {
            /* Save Exception */
            theError = new ThemisIOException(CONSOLIDATION_ERROR, e);
        }
    }
}
