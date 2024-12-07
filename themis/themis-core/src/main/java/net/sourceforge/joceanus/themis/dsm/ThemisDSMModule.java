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
package net.sourceforge.joceanus.themis.dsm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisIOException;
import net.sourceforge.joceanus.themis.analysis.ThemisAnalysisMaven;

/**
 * DSM Module.
 */
public class ThemisDSMModule {
    /**
     * The java directory.
     */
    private static final String DIR_JAVA = "src/main/java";

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
     * The error.
     */
    private OceanusException theError;

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
     * Obtain the error.
     * @return error
     */
    public OceanusException getError() {
        return theError;
    }

    /**
     * Obtain an iterator of the imports.
     * @return the iterator
     */
    Iterator<ThemisDSMPackage> packageIterator() {
        return thePackages.iterator();
    }

    /**
     * Obtain an iterator of the imports.
     * @return the iterator
     */
    public ThemisDSMPackage getDefaultPackage() {
        final List<ThemisDSMPackage> myList = listPackages();
        return myList.isEmpty() ? null : myList.get(0);
    }

    /**
     * Obtain an indexed package.
     * @param pIndex the index of the package
     * @return the package
     */
    public ThemisDSMPackage getIndexedPackage(final int pIndex) {
        final List<ThemisDSMPackage> myList = listPackages();
        return myList.size() <= pIndex || pIndex < 0 ? null : myList.get(pIndex);
    }

    /**
     * Obtain a list of all modules and submodules that contain packages.
     * @return the list
     */
    List<ThemisDSMModule> listModules() {
        /* Create result list and loop through the modules */
        final List<ThemisDSMModule> myList = new ArrayList<>();
        for (ThemisDSMModule myModule : theSubModules) {
            /* Only add the module if it has packages */
            if (myModule.hasPackages()) {
                myList.add(myModule);
            }

            /* Add any subModules */
            myList.addAll(myModule.listModules());
        }

        /* Return the list */
        return myList;
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
        /* Process the project */
        parseProjectFile(new File(theLocation, ThemisAnalysisMaven.POM));

        /* Look for the source directory */
        final File mySrc = new File(theLocation, DIR_JAVA);
        if (mySrc.isDirectory()) {
            /* Process any packages */
            processPackages(mySrc);
        }

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

    /**
     * Does this module contain circular package references?
     * @return true/false
     */
    public boolean isCircular() {
        /* Loop through the dependencies */
        for (ThemisDSMPackage myPackage : thePackages) {
            if (myPackage.isCircular()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtain a list of all packages that have dependencies.
     * @return the list
     */
    public List<ThemisDSMPackage> listPackages() {
        /* Create result list and loop through the modules */
        final List<ThemisDSMPackage> myList = new ArrayList<>();
        for (ThemisDSMPackage myPackage : thePackages) {
            myList.add(myPackage);
        }

        /* Return the list */
        return myList;
    }

    /**
     * Compare this package to another module for sort order.
     * @param pThat the other module to compare to
     * @return true/false
     */
    public int compareTo(final ThemisDSMModule pThat) {
        /* Handle circular status */
        if (isCircular() != pThat.isCircular()) {
            return isCircular() ? -1 : 1;
        }

        /* If all else fails rely on alphabetical */
        return theModule.compareTo(pThat.getModuleName());
    }

    @Override
    public String toString() {
        return getModuleName();
    }

    /**
     * Parse the maven project file.
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
                myModule.processModulesAndPackages();
                if (myModule.getPackageCount() > 1
                      || myModule.hasSubModules()) {
                    theSubModules.add(myModule);
                }
            }

            /* Sort the modules */
            theSubModules.sort(Comparator.comparing(ThemisDSMModule::getModuleName));

            /* Catch exceptions */
        } catch (IOException
                | OceanusException e) {
            /* Save Exception */
            theSubModules.clear();
            theError = new ThemisIOException("Failed to parse Project file", e);
        }
    }
}
