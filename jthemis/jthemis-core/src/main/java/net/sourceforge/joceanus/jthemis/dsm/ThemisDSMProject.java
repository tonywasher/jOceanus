/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jthemis.dsm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisMaven;

/**
 * DSM Project.
 */
public class ThemisDSMProject {
    /**
     * The location of the project.
     */
    private final File theLocation;

    /**
     * The name of the project.
     */
    private final String theProject;

    /**
     * The list of Modules.
     */
    private final List<ThemisDSMModule> theModules;

    /**
     * The error.
     */
    private OceanusException theError;

    /**
     * Constructor.
     * @param pLocation the location of the project
     */
    public ThemisDSMProject(final File pLocation) {
        /* Store values */
        theLocation = pLocation;
        theProject = pLocation.getName();
        theModules = new ArrayList<>();

        /* Process the project */
        parseProjectFile(new File(theLocation, ThemisAnalysisMaven.POM));
    }

    /**
     * Return the location of the project.
     * @return the location
     */
    File getLocation() {
        return theLocation;
    }

    /**
     * Return the project name.
     * @return the name
     */
    String getProjectName() {
        return theProject;
    }

    /**
     * Obtain the default module.
     * @return the default
     */
    public ThemisDSMModule getDefaultModule() {
        final List<ThemisDSMModule> myList = listModules();
        return myList.isEmpty() ? null : myList.get(0);
    }

    /**
     * Does the project have modules.
     * @return true/false
     */
    public boolean hasModules() {
        return !theModules.isEmpty();
    }

    /**
     * Obtain the error.
     * @return error
     */
    public OceanusException getError() {
        return theError;
    }

    @Override
    public String toString() {
        return getProjectName();
    }

    /**
     * Obtain a list of all modules and submodules that contain packages.
     * @return the list
     */
    public List<ThemisDSMModule> listModules() {
        /* Create result list and loop through the modules */
        final List<ThemisDSMModule> myList = new ArrayList<>();
        for (ThemisDSMModule myModule : theModules) {
            /* Only add the module if it has packages */
            if (myModule.hasPackages()) {
                myList.add(myModule);
            }

            /* Add any subModules */
            myList.addAll(myModule.listModules());
        }

        /* Sort the modules */
        myList.sort(ThemisDSMModule::compareTo);

        /* Return the list */
        return myList;
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
        try (InputStream myInStream = new FileInputStream(pPom)) {
            /* Parse the Project definition file */
            final ThemisAnalysisMaven myPom = new ThemisAnalysisMaven(myInStream);

            /* Loop through the modules */
            for (final String myModuleName : myPom.getModules()) {
                 final File myModuleDir = new File(pPom.getParentFile(), myModuleName);
                final ThemisDSMModule myModule = new ThemisDSMModule(myModuleDir);
                theModules.add(myModule);
                myModule.processModulesAndPackages();
            }

            /* If we have no modules in the project, then we are a single module project */
            if (theModules.isEmpty()) {
                final ThemisDSMModule myModule = new ThemisDSMModule(theLocation);
                theModules.add(myModule);
                myModule.processModulesAndPackages();
            }

            /* Sort the modules */
            theModules.sort(Comparator.comparing(ThemisDSMModule::getModuleName));

            /* Catch exceptions */
        } catch (IOException
                | OceanusException e) {
            /* Save Exception */
            theModules.clear();
            theError = new ThemisIOException("Failed to parse Project file", e);
        }
    }
}
