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
 * DSM Module.
 */
public class ThemisDSMModule {
    /**
     * The java directory.
     */
    private static final String DIR_JAVA = "main/java";

    /**
     * The location of the module.
     */
    private final File theLocation;

    /**
     * The name of the module.
     */
    private final String theModule;

    /**
     * The list of subModules.
     */
    private final List<ThemisDSMModule> theSubModules;

    /**
     * The list of packages.
     */
    private final List<ThemisDSMPackage> thePackages;

    /**
     * Constructor.
     * @param pLocation the location of the project
     */
    ThemisDSMModule(final File pLocation) {
        theLocation = pLocation;
        theModule = theLocation.getName();
        theSubModules = new ArrayList<>();
        thePackages = new ArrayList<>();
    }

    /**
     * Return the location of the module.
     * @return the location
     */
    File getLocation() {
        return theLocation;
    }

    /**
     * Return the module name.
     * @return the name
     */
    String getModuleName() {
        return theModule;
    }

    /**
     * Add a subModule to the list.
     * @param pModule the subModule
     */
    void registerSubModule(final ThemisDSMModule pModule) {
        if (theSubModules.contains(pModule)) {
            throw new IllegalArgumentException("Already included");
        }

        /* Add the module */
        theSubModules.add(pModule);
    }

    /**
     * Add a package to the list.
     * @param pPackage the package
     */
    void registerPackage(final ThemisDSMPackage pPackage) {
        if (thePackages.contains(pPackage)) {
            throw new IllegalArgumentException("Already included");
        }

        /* Add the package */
        thePackages.add(pPackage);
    }

    /**
     * Obtain an iterator of the imports.
     * @return the iterator
     */
    Iterator<ThemisDSMModule> moduleIterator() {
        return theSubModules.iterator();
    }

    /**
     * Does the module have subModules?
     * @return true/false
     */
    boolean hasSubModules() {
        return !theSubModules.isEmpty();
    }

    /**
     * Does the module have packages?
     * @return true/false
     */
    boolean hasPackages() {
        return !thePackages.isEmpty();
    }

    /**
     * Obtain the count of packages.
     * @return the count
     */
    int getPackageCount() {
        return thePackages.size();
    }

    /**
     * Obtain an iterator of the imports.
     * @return the iterator
     */
    Iterator<ThemisDSMPackage> packageIterator() {
        return thePackages.iterator();
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a module */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target Class */
        final ThemisDSMModule myThat = (ThemisDSMModule) pThat;

        /* Check name */
        return theModule.equals(myThat.getModuleName());
    }

    @Override
    public int hashCode() {
        return theModule.hashCode();
    }

    /**
     * process modules and packages.
     */
    void processModulesAndPackages() {
        /* Loop through the entries in the directory */
        for (File myFile: Objects.requireNonNull(theLocation.listFiles())) {
            /* Ignore files */
            if (!myFile.isDirectory()) {
                continue;
            }

            /* Access the name of the file */
            final String myName = myFile.getName();

            /* Ignore special files and target */
            if (myName.equals(ThemisDSMProject.DIR_SRC)) {
                final File myRoot = new File(myFile, DIR_JAVA);
                if (myRoot.exists()) {
                    processPackages(myRoot);
                }

                /* Ignore special files and target */
            } else if (!myName.startsWith(ThemisDSMProject.PFXDIR_SPECIAL)
                && !myName.equals(ThemisDSMProject.DIR_TARGET)) {
                /* Process the submodule */
                final ThemisDSMModule myModule = new ThemisDSMModule(myFile);
                myModule.processModulesAndPackages();
                if (myModule.getPackageCount() > 1
                    || myModule.hasSubModules()) {
                    theSubModules.add(myModule);
                }
            }
        }

        /* Sort the modules */
        theSubModules.sort(Comparator.comparing(ThemisDSMModule::getModuleName));

        /* Process the classes */
        processClasses();

        /* Process the dependencies */
        processDependencies();
    }

    /**
     * process packages.
     * @param pPath the location of the package
     */
    void processPackages(final File pPath) {
        /* Loop through the entries in the directory */
        for (File myFile: Objects.requireNonNull(pPath.listFiles())) {
            /* Ignore files */
            if (!myFile.isDirectory()) {
                continue;
            }

            /* Process the package */
            final ThemisDSMPackage myPackage = new ThemisDSMPackage(this, myFile);
            myPackage.processPackages();
        }
    }

    /**
     * process classes.
     */
    private void processClasses() {
        /* Loop through the packages */
        for (ThemisDSMPackage myPackage : thePackages) {
            /* Process the classes */
            myPackage.processClasses();
        }
    }

    /**
     * process dependencies.
     */
    private void processDependencies() {
        /* Loop through the packages */
        for (ThemisDSMPackage myPackage : thePackages) {
            /* Process the classes */
            myPackage.processDependencies();
        }

        /* Sort the packages */
        thePackages.sort(ThemisDSMPackage::compareTo);
    }

    /**
     * Find a class reference.
     * @param pReference the class name
     * @return the found class or null
     */
    ThemisDSMClass findClass(final String pReference) {
        /* Loop through the packages */
        for (ThemisDSMPackage myPackage : thePackages) {
            /* Ignore if the package is not a possibility */
            final String myPrefix = myPackage.getPackageName() + ThemisDSMPackage.SEP_PACKAGE;
            if (!pReference.startsWith(myPrefix)) {
                continue;
            }

            /* Loop through the classes */
            final ThemisDSMClass myClass = myPackage.findClass(pReference);
            if (myClass != null) {
                return myClass;
            }
        }

        /* No match */
        return null;
    }

    @Override
    public String toString() {
        return getModuleName();
    }
}
