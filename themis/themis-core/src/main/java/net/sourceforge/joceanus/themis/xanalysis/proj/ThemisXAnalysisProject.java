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
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParserDef;
import net.sourceforge.joceanus.themis.xanalysis.proj.ThemisXAnalysisMaven.ThemisXAnalysisMavenId;

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
     * The module list.
     */
    private final List<ThemisXAnalysisModule> theModules;

    /**
     * Constructor.
     * @param pLocation the project location
     * @throws OceanusException on error
     */
    public ThemisXAnalysisProject(final File pLocation) throws OceanusException {
        /* Store the name and location */
        theLocation = pLocation;

        /* Create the list */
        theModules = new ArrayList<>();

        /* Initiate search for modules */
        final ThemisXAnalysisMavenId myId = parseProjectFile(new File(theLocation, ThemisXAnalysisMaven.POM));
        theName = myId == null ? null : myId.getArtifactId();
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
     * @throws OceanusException on error
     */
    private ThemisXAnalysisMavenId parseProjectFile(final File pPom) throws OceanusException {
        /* If the pom file does not exist, just return */
        if (!pPom.exists()) {
            return null;
        }

        /* Add module if source directory exists */
        final File mySrc = new File(pPom.getParent(), ThemisXAnalysisModule.PATH_XTRA);
        if (mySrc.exists()
                && mySrc.isDirectory()) {
            /* Add the module to the list */
            theModules.add(new ThemisXAnalysisModule(new File(pPom.getParent())));
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
            }
            return myPom.getMavenId();

            /* Catch exceptions */
        } catch (IOException e) {
            /* Convert Exception */
            throw new ThemisIOException("Failed to parse Project file", e);
        }
    }

    /**
     * parse the java code.
     * @param pParser the parser
     * @throws OceanusException on error
     */
    public void parseJavaCode(final ThemisXAnalysisParserDef pParser) throws OceanusException {
        /* Loop through the modules */
        for (ThemisXAnalysisModule myModule : theModules) {
            /* Process the module */
            myModule.parseJavaCode(pParser);
        }
    }
}
