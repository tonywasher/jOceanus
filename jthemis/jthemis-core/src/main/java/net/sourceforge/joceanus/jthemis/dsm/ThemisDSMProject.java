/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * DSM Project.
 */
public class ThemisDSMProject {
    /**
     * The special directory prefix.
     */
    static final String PFXDIR_SPECIAL = ".";

    /**
     * The source directory.
     */
    static final String DIR_SRC = "src";

    /**
     * The target directory.
     */
    static final String DIR_TARGET = "target";

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
     * Constructor.
     * @param pLocation the location of the project
     */
    public ThemisDSMProject(final File pLocation) {
        /* Store values */
        theLocation = pLocation;
        theProject = pLocation.getName();
        theModules = new ArrayList<>();

        /* Process the modules */
        processModules();
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
     * Process modules.
     */
    private void processModules() {
        /* Loop through the entries in the directory */
        for (File myFile: Objects.requireNonNull(theLocation.listFiles())) {
            /* Ignore files */
            if (!myFile.isDirectory()) {
                continue;
            }

            /* Access the name of the file */
            final String myName = myFile.getName();

            /* Ignore special dircectories and src/target */
            if (!myName.startsWith(PFXDIR_SPECIAL)
                && !myName.equals(DIR_SRC)
                && !myName.equals(DIR_TARGET)) {
                /* Process the module */
                final ThemisDSMModule myModule = new ThemisDSMModule(myFile);
                theModules.add(myModule);
                myModule.processModulesAndPackages();
            }
        }

        /* Sort the modules */
        theModules.sort(Comparator.comparing(ThemisDSMModule::getModuleName));
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
}
