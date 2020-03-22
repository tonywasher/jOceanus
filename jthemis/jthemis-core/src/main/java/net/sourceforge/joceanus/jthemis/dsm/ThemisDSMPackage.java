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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * DSM Package.
 */
public class ThemisDSMPackage {
    /**
     * The package separator.
     */
    static final String SEP_PACKAGE = ".";

    /**
     * The import prefix.
     */
    private static final String PFX_IMPORT = "import ";

    /**
     * The java suffix.
     */
    private static final String SFX_JAVA = ".java";

    /**
     * The package-info file.
     */
    private static final String PACKAGE_INFO = "package-info.java";

    /**
     * The location of the package.
     */
    private final File theLocation;

    /**
     * The name of the package.
     */
    private final String thePackage;

    /**
     * The module that contains the package.
     */
    private final ThemisDSMModule theModule;

    /**
     * The list of direct references to other packages.
     */
    private final List<ThemisDSMPackage> theDirectReferences;

    /**
     * The list of implied  references to other packages.
     */
    private final List<ThemisDSMPackage> theImpliedReferences;

    /**
     * The list of classes in this package.
     */
    private final List<ThemisDSMClass> theClasses;

    /**
     * Constructor.
     * @param pModule the owning module
     * @param pLocation the location of the package
     */
    ThemisDSMPackage(final ThemisDSMModule pModule,
                     final File pLocation) {
        this(pModule, pLocation, pLocation.getName().replace(File.separatorChar, '.'));
    }

    /**
     * Constructor.
     * @param pPackage the parent package
     * @param pLocation the location of the package
     */
    ThemisDSMPackage(final ThemisDSMPackage pPackage,
                     final File pLocation) {
        this(pPackage.getModule(), pLocation,  pPackage.getPackageName() + SEP_PACKAGE + pLocation.getName());
    }

    /**
     * Constructor.
     * @param pModule the owning module
     * @param pLocation the location of the package
     * @param pName the name of the package
     */
    private ThemisDSMPackage(final ThemisDSMModule pModule,
                             final File pLocation,
                             final String pName) {
        theModule = pModule;
        theLocation = pLocation;
        thePackage = pName;
        theDirectReferences = new ArrayList<>();
        theImpliedReferences = new ArrayList<>();
        theClasses = new ArrayList<>();
    }

    /**
     * Return the owning module.
     * @return the module
     */
    ThemisDSMModule getModule() {
        return theModule;
    }

    /**
     * Return the location of the module.
     * @return the location
     */
    File getLocation() {
        return theLocation;
    }

    /**
     * Return the package name.
     * @return the name
     */
    String getPackageName() {
        return thePackage;
    }

    /**
     * Add a reference to the list.
     * @param pReference the reference
     */
    void registerReference(final ThemisDSMPackage pReference) {
        if (!theDirectReferences.contains(pReference)) {
            theDirectReferences.add(pReference);
        }
    }

    /**
     * Obtain an iterator of the references.
     * @return the iterator
     */
    Iterator<ThemisDSMPackage> referenceIterator() {
        return theDirectReferences.iterator();
    }

    /**
     * Obtain an iterator of the class.
     * @return the iterator
     */
    Iterator<ThemisDSMClass> classIterator() {
        return theClasses.iterator();
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

        /* Make sure that the object is a package */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target Class */
        final ThemisDSMPackage myThat = (ThemisDSMPackage) pThat;

        /* Check name */
        return thePackage.equals(myThat.getPackageName());
    }

    @Override
    public int hashCode() {
        return thePackage.hashCode();
    }

    /**
     * process packages.
     */
    void processPackages() {
        /* Loop through the entries in the directory */
        for (File myFile: Objects.requireNonNull(theLocation.listFiles())) {
            /* Handle files */
            if (!myFile.isDirectory()) {
                /* Access the name of the file */
                final String myName = myFile.getName();

                /* If this is a .java that is not package-info */
                if (myName.endsWith(SFX_JAVA)
                    && !myName.equals(PACKAGE_INFO)) {
                    /* Add the class */
                    final ThemisDSMClass myClass = new ThemisDSMClass(this, myFile);
                    theClasses.add(myClass);
                }
                continue;
            }

            /* Process the subpackage */
            final ThemisDSMPackage myPackage = new ThemisDSMPackage(this, myFile);
            myPackage.processPackages();
        }

        /* Register the package if we have any classes */
        if (!theClasses.isEmpty()) {
            theModule.registerPackage(this);
            theClasses.sort(Comparator.comparing(ThemisDSMClass::getClassName));
        }
    }

    /**
     * process classes.
     */
    void processClasses() {
        /* Loop through the classes */
        for (ThemisDSMClass myClass : theClasses) {
            /* Process the class */
            processClass(myClass);

            /* Sort the imports */
            myClass.sortImports();
        }

        /* Sort the references */
        theDirectReferences.sort(Comparator.comparing(ThemisDSMPackage::getPackageName));
    }

    /**
     * process class.
     * @param pClass the class
     */
    private void processClass(final ThemisDSMClass pClass) {
        /* Process the file */
        try (FileReader myInStream = new FileReader(pClass.getLocation());
             BufferedReader myReader = new BufferedReader(myInStream)) {
            /* Read line by line */
            String myLine;
            while ((myLine = myReader.readLine()) != null) {
                /* If the line starts with import */
                if (myLine.startsWith(PFX_IMPORT)) {
                    /* Process the reference to the class */
                    myLine = myLine.substring(PFX_IMPORT.length()).trim();
                    myLine = myLine.substring(0, myLine.length() - 1);
                    processReference(pClass, myLine);
                }
            }

            /* Handle exceptions */
        } catch (IOException e) {
            throw new IllegalStateException("Help", e);
        }
    }

    /**
     * process reference.
     * @param pClass the class
     * @param pReference the referenced class
     */
    private void processReference(final ThemisDSMClass pClass,
                                  final String pReference) {
        /* If the referenced class is one of ours and not in the same package  */
        final ThemisDSMClass myReferenced = theModule.findClass(pReference);
        if (myReferenced != null
            && !thePackage.equals(myReferenced.getPackage().getPackageName())) {
            /* register the import */
            pClass.registerImport(myReferenced);
            registerReference(myReferenced.getPackage());
        }
    }

    /**
     * Find a class reference.
     * @param pReference the class name
     * @return the found class or null
     */
    ThemisDSMClass findClass(final String pReference) {
        /* Loop through the classes */
        for (ThemisDSMClass myClass : theClasses) {
            /* Look for match or prefix */
            final String myName = myClass.getFullClassName();
            if (pReference.equals(myName)
                || pReference.startsWith(myName + SEP_PACKAGE)) {
                return myClass;
            }
        }

        /* No match */
        return null;
    }

    /**
     * process dependencies.
     */
    void processDependencies() {
        /* Loop through the dependencies */
        for (ThemisDSMPackage myPackage : theDirectReferences) {
            /* Process the class */
            processDependencies(myPackage);
        }

        /* Sort the full references */
        theImpliedReferences.sort(Comparator.comparing(ThemisDSMPackage::getPackageName));
    }

    /**
     * process dependencies.
     * @param pPackage the package to process dependencies for
     */
    void processDependencies(final ThemisDSMPackage pPackage) {
        /* If this is not already in the fullReference list */
        if (!theImpliedReferences.contains(pPackage)) {
            /* Add the package */
            theImpliedReferences.add(pPackage);

           /* Loop through the dependencies */
            for (ThemisDSMPackage myPackage : pPackage.theDirectReferences) {
                /* If this is not already in the full references
                /* Process the class */
                processDependencies(myPackage);
            }
        }
    }

    /**
     * is this package circularly dependent?
     * @return true/false
     */
    public boolean isCircular() {
        /* Loop through the dependencies */
        return theImpliedReferences.contains(this);
    }

    /**
     * Obtain the list of direct references to the other package.
     * @param pPackage the other package
     * @return the list of direct references
     */
    public List<ThemisDSMClass> getReferencesTo(final ThemisDSMPackage pPackage) {
        /* If there are references */
        if (theDirectReferences.contains(pPackage)) {
            /* Loop through the classes */
            final List<ThemisDSMClass> myReferences = new ArrayList<>();
            for (ThemisDSMClass myClass : theClasses) {
                if (myClass.references(pPackage)) {
                    myReferences.add(myClass);
                }
            }
            return myReferences;
        }

        /* No references */
        return Collections.emptyList();
    }

    /**
     * Compare this package to another package for sort order.
     * @param pThat the oother package to compare to
     * @return true/false
     */
    public int compareTo(final ThemisDSMPackage pThat) {
        /* Handle simple dependency */
        if (theImpliedReferences.contains(pThat)
                && !pThat.theImpliedReferences.contains(this)) {
            return -1;
        }
        if (pThat.theImpliedReferences.contains(this)
                && !theImpliedReferences.contains(pThat)) {
            return 1;
        }

        /* Sort on number of dependencies */
        final int iDiff = pThat.theImpliedReferences.size()
                - theImpliedReferences.size();
        if (iDiff != 0) {
            return iDiff;
        }

        /* If all else fails rely on alphabetical */
        return thePackage.compareTo(pThat.getPackageName());
    }

    @Override
    public String toString() {
        return getPackageName();
    }
}
