/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2023 Tony Washer
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

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisPackage;

/**
 * DSM Class.
 */
public class ThemisDSMClass {
    /**
     * The location of the class.
     */
    private final File theLocation;

    /**
     * The package reference.
     */
    private final ThemisDSMPackage thePackage;

    /**
     * The name of the class.
     */
    private final String theClass;

    /**
     * The list of imports.
     */
    private final List<ThemisDSMClass> theImports;

    /**
     * Constructor.
     * @param pPackage the package
     * @param pLocation the location
     */
    ThemisDSMClass(final ThemisDSMPackage pPackage,
                   final File pLocation) {
        thePackage = pPackage;
        theLocation = pLocation;
        final String myName = pLocation.getName();
        theClass = myName.substring(0, myName.length() - ThemisAnalysisPackage.SFX_JAVA.length());
        theImports = new ArrayList<>();
    }

    /**
     * Return the location of the module.
     * @return the location
     */
    File getLocation() {
        return theLocation;
    }

    /**
     * Obtain the package.
     * @return the package
     */
    ThemisDSMPackage getPackage() {
        return thePackage;
    }

    /**
     * Return the class name.
     * @return the name
     */
    String getClassName() {
        return theClass;
    }

    /**
     * Return the full class name.
     * @return the full name
     */
    String getFullClassName() {
        return thePackage.getPackageName() + ThemisDSMPackage.SEP_PACKAGE + theClass;
    }

    /**
     * Add an import to the list.
     * @param pImport the imported class
     */
    void registerImport(final ThemisDSMClass pImport) {
        if (!theImports.contains(pImport)) {
            theImports.add(pImport);
        }
    }

    /**
     * Sort the imports.
     */
    void sortImports() {
        theImports.sort(Comparator.comparing(ThemisDSMClass::getFullClassName));
    }

    /**
     * Obtain an iterator of the imports.
     * @return the iterator
     */
    Iterator<ThemisDSMClass> importIterator() {
        return theImports.iterator();
    }

    /**
     * Does this class reference the package?
     * @param pPackage the package
     * @return true/false
     */
    boolean references(final ThemisDSMPackage pPackage) {
        /* Loop through the classes */
        for (ThemisDSMClass myReference : theImports) {
            if (pPackage.equals(myReference.getPackage())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtain the list of direct references to the other package.
     * @param pPackage the other package
     * @return the list of direct references
     */
    public List<ThemisDSMClass> getReferencesTo(final ThemisDSMPackage pPackage) {
        /* Loop through the classes */
        final List<ThemisDSMClass> myReferences = new ArrayList<>();
        for (ThemisDSMClass myImport : theImports) {
            if (pPackage.equals(myImport.getPackage())) {
                myReferences.add(myImport);
            }
        }
        return myReferences;
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

        /* Make sure that the object is an Class */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target Class */
        final ThemisDSMClass myThat = (ThemisDSMClass) pThat;

        /* Check Package and class name */
        return thePackage.equals(myThat.getPackage())
               && theClass.equals(myThat.getClassName());
    }

    @Override
    public int hashCode() {
        return thePackage.hashCode() + theClass.hashCode();
    }

    @Override
    public String toString() {
        return getClassName();
    }
 }
