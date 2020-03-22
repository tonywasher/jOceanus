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
import java.util.Iterator;
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
    ThemisDSMProject(final File pLocation) {
        theLocation = pLocation;
        theProject = pLocation.getName();
        theModules = new ArrayList<>();
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
     * Obtain an iterator of the modules.
     * @return the iterator
     */
    Iterator<ThemisDSMModule> moduleIterator() {
        return theModules.iterator();
    }

    /**
     * Process modules.
     */
    void processModules() {
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
     * main entry.
     * @param pArgs the program arguments
     */
    public static void main(final String[] pArgs) {
        final ThemisDSMProject myProject = new ThemisDSMProject(new File("c:\\Users\\Tony\\gitNew\\jOceanus"));
        myProject.processModules();
        final String myReport = ThemisDSMReport.reportOnProject(myProject);
        System.out.println(myReport);
    }
}
