/*
 * Themis: Java Project Framework
 * Copyright 2012-2026. Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.parser.proj;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisIOException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;
import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisMaven.ThemisXAnalysisMavenId;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
     * The module map.
     */
    private final Map<ThemisXAnalysisMavenId, ThemisXAnalysisModule> theModules;

    /**
     * The dependencies.
     */
    private final List<ThemisXAnalysisMavenId> theDependencies;

    /**
     * Constructor.
     *
     * @param pLocation the project location
     * @throws OceanusException on error
     */
    public ThemisXAnalysisProject(final File pLocation) throws OceanusException {
        /* Store the name and location */
        theLocation = pLocation;

        /* Create the list */
        theModules = new LinkedHashMap<>();
        theDependencies = new ArrayList<>();

        /* Initiate search for modules */
        final ThemisXAnalysisMaven myPom = parseProjectFile(null, new File(theLocation, ThemisXAnalysisMaven.POM));
        theName = myPom == null ? null : myPom.getMavenId().getArtifactId();

        /* Remove own mavenIds from dependency list */
        theDependencies.removeIf(theModules::containsKey);

        /* For all dependencies */
        final List<ThemisXAnalysisMavenId> myDependencies = new ArrayList<>(theDependencies);
        for (ThemisXAnalysisMavenId myId : myDependencies) {
            processDependency(myId);
        }
    }

    /**
     * Process dependency.
     *
     * @param pId the maven id
     */
    private void processDependency(final ThemisXAnalysisMavenId pId) throws OceanusException {
        final File myFile = pId.getMavenPomPath();
        /* Protect against exceptions */
        try (InputStream myInStream = new FileInputStream(myFile)) {
            /* Parse the Project definition file */
            final ThemisXAnalysisMaven myPom = new ThemisXAnalysisMaven(null, myInStream);

            /* Add any unique dependencies */
            for (final ThemisXAnalysisMavenId myDepId : myPom.getDependencies()) {
                if (!theDependencies.contains(myDepId)) {
                    theDependencies.add(myDepId);
                    if (myDepId.getClassifier() == null) {
                        processDependency(myDepId);
                    }
                }
            }
        } catch (IOException e) {
            throw new ThemisIOException("Failed to parse pom file", e);
        }
    }

    /**
     * Obtain the name.
     *
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the location.
     *
     * @return the location
     */
    File getLocation() {
        return theLocation;
    }

    /**
     * Obtain the modules.
     *
     * @return the modules
     */
    public List<ThemisXAnalysisModule> getModules() {
        return new ArrayList<>(theModules.values());
    }

    /**
     * Obtain the dependencies.
     *
     * @return the dependencies
     */
    public List<ThemisXAnalysisMavenId> getDependencies() {
        return theDependencies;
    }

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Parse the maven project file.
     *
     * @param pParent the parent
     * @param pPom    the project file
     * @return the artifact name
     * @throws OceanusException on error
     */
    private ThemisXAnalysisMaven parseProjectFile(final ThemisXAnalysisMaven pParent,
                                                  final File pPom) throws OceanusException {
        /* If the pom file does not exist, just return */
        if (!pPom.exists()) {
            return null;
        }

        /* Protect against exceptions */
        try (InputStream myInStream = new FileInputStream(pPom)) {
            /* Parse the Project definition file */
            final ThemisXAnalysisMaven myPom = new ThemisXAnalysisMaven(pParent, myInStream);

            /* If source directory exists */
            final File mySrc = new File(pPom.getParent(), ThemisXAnalysisModule.PATH_XTRA);
            if (mySrc.exists()
                    && mySrc.isDirectory()) {
                /* Add the module to the list */
                theModules.put(myPom.getMavenId(), new ThemisXAnalysisModule(new File(pPom.getParent()), myPom));

                /* Add any unique dependencies */
                for (final ThemisXAnalysisMavenId myDepId : myPom.getDependencies()) {
                    if (!theDependencies.contains(myDepId)) {
                        theDependencies.add(myDepId);
                    }
                }
            }

            /* Loop through the modules */
            for (final String myModuleName : myPom.getModules()) {
                /* Access module directory */
                final File myModuleDir = new File(pPom.getParentFile(), myModuleName);

                /* Process the project file */
                parseProjectFile(myPom, new File(myModuleDir, ThemisXAnalysisMaven.POM));
            }

            /* Return the POM */
            return myPom;

            /* Catch exceptions */
        } catch (IOException e) {
            /* Convert Exception */
            throw new ThemisIOException("Failed to parse Project file", e);
        }
    }

    /**
     * parse the java code.
     *
     * @param pParser the parser
     * @throws OceanusException on error
     */
    public void parseJavaCode(final ThemisXAnalysisParserDef pParser) throws OceanusException {
        /* Loop through the modules */
        for (ThemisXAnalysisModule myModule : theModules.values()) {
            /* Process the module */
            myModule.parseJavaCode(pParser);
        }
    }
}
