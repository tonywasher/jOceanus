/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.themis.solver.mapper;

import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisClassInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisNodeInstance;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeImport;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverClass;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverFile;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverPackage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * File State.
 */
public class ThemisMapperFileState {
    /**
     * The project state.
     */
    private final ThemisMapperProjectState theProject;

    /**
     * Map of all known short names in file.
     */
    private final Map<String, ThemisClassInstance> theKnownClasses;

    /**
     * The referenced classes.
     */
    private final List<ThemisSolverClass> theReferenced;

    /**
     * Constructor.
     *
     * @param pProject the project
     */
    ThemisMapperFileState(final ThemisMapperProjectState pProject) {
        /* Store the project */
        theProject = pProject;

        /* Create the maps and lists */
        theKnownClasses = new LinkedHashMap<>();
        theReferenced = new ArrayList<>();
    }

    /**
     * Obtain the referenced classes.
     *
     * @return the referenced classes
     */
    List<ThemisSolverClass> getReferenced() {
        return theReferenced;
    }

    /**
     * InitialiseForFile.
     *
     * @param pFile the file
     */
    void initForFile(final ThemisSolverFile pFile) {
        /* Clear the maps and lists */
        theKnownClasses.clear();
        theReferenced.clear();

        /* Determine the possible references */
        determineKnownClasses(pFile);
    }

    /**
     * Determine known classes for file.
     *
     * @param pFile the file to process.
     */
    private void determineKnownClasses(final ThemisSolverFile pFile) {
        /* Add all the package top-level classes */
        final ThemisSolverPackage myPackage = (ThemisSolverPackage) pFile.getOwningPackage();
        for (ThemisSolverFile myFile : myPackage.getFiles()) {
            final ThemisSolverClass myClass = myFile.getTopLevel();
            if (myClass != null) {
                theKnownClasses.put(myClass.getName(), myClass.getUnderlyingClass());
            }
        }

        /* Process the imports */
        for (ThemisNodeInstance myNode : pFile.getUnderlyingFile().getContents().getImports()) {
            /* LookUp the import and record it */
            final ThemisClassInstance myImport = lookUpImport((ThemisNodeImport) myNode);
            theKnownClasses.put(myImport.getName(), myImport);
        }

        /* Process the classes in the file */
        for (ThemisSolverClass myClass : pFile.getClasses()) {
            if (!myClass.getUnderlyingClass().isAnonClass()) {
                theKnownClasses.put(myClass.getName(), myClass.getUnderlyingClass());
            }
        }
    }

    /**
     * Process inherited.
     *
     * @param pClass the class to process
     */
    void processInherited(final ThemisSolverClass pClass) {
        for (ThemisClassInstance myInherited : theProject.listAllInherited(pClass.getFullName())) {
            theKnownClasses.put(myInherited.getName(), myInherited);
        }
    }

    /**
     * Look up import.
     *
     * @param pImport the import definition.
     * @return the import class
     */
    private ThemisClassInstance lookUpImport(final ThemisNodeImport pImport) {
        /* Determine full name */
        final String myFullName = pImport.getFullName();

        /* Look for project class of this name */
        final ThemisSolverClass myClass = theProject.getProjectClassMap().get(myFullName);
        return myClass != null
                ? myClass.getUnderlyingClass()
                : theProject.getExternalClassMap().get(myFullName);
    }

    /**
     * process possible reference.
     *
     * @param pReference the possible reference.
     * @return the resolved class (if found)
     */
    ThemisClassInstance processPossibleReference(final String pReference) {
        /* Look up the reference in the list of known classes */
        ThemisClassInstance myReference = theKnownClasses.get(pReference);

        /* If it is not a known class */
        if (myReference == null) {
            /* If it contains a period */
            final int iIndex = pReference.indexOf(ThemisChar.PERIOD);
            if (iIndex != -1) {
                /* Look for a fully qualified class in external and project classes */
                myReference = lookUpFullyNamedClass(pReference);

                /* If still not found */
                if (myReference == null) {
                    /* Try just the first part */
                    myReference = lookUpPartiallyNamedClass(pReference, iIndex);
                }

                /* else just a simple name */
            } else {
                myReference = lookUpJavaLangClass(pReference);
            }
        }

        /* If we found the reference, process it */
        if (myReference != null) {
            declareReferencedClass(myReference);
        }
        return myReference;
    }

    /**
     * lookUp a fullyNamed class.
     *
     * @param pReference the possible reference.
     * @return the resolved class (if found)
     */
    private ThemisClassInstance lookUpFullyNamedClass(final String pReference) {
        /* Look for a fully qualified class in external and project classes */
        ThemisClassInstance myReference = theProject.getExternalClassMap().get(pReference);
        if (myReference == null) {
            final ThemisSolverClass myClass = theProject.getProjectClassMap().get(pReference);
            if (myClass != null) {
                myReference = myClass.getUnderlyingClass();
            }
        }

        /* If not found, try to load the class */
        if (myReference == null) {
            myReference = theProject.tryNamedClass(pReference);
        }

        /* If we have now found the class, add to knownClasses */
        if (myReference != null) {
            theKnownClasses.put(pReference, myReference);
        }

        /* Return resolved reference (or null) */
        return myReference;
    }

    /**
     * lookUp a partiallyNamed class.
     *
     * @param pReference the possible reference.
     * @param pIndex     the index of the first period
     * @return the resolved class (if found)
     */
    private ThemisClassInstance lookUpPartiallyNamedClass(final String pReference,
                                                          final int pIndex) {
        /* Try just the first part of the name */
        final String myBase = pReference.substring(0, pIndex);
        ThemisClassInstance myReference = theKnownClasses.get(myBase);

        /* If we found a parent */
        if (myReference != null) {
            /* Build the full name of the class */
            final String myName = myReference.getFullName() + pReference.substring(pIndex);
            final ThemisSolverClass myProjectClass = theProject.getProjectClassMap().get(myName);
            myReference = myProjectClass == null ? theProject.tryNamedClass(myName) : myProjectClass.getUnderlyingClass();

            /* If we have now found the class, add to knownClasses */
            if (myReference != null) {
                theKnownClasses.put(pReference, myReference);
            }
        }

        /* Return resolved reference (or null) */
        return myReference;
    }

    /**
     * lookUp a javaLang class.
     *
     * @param pReference the possible reference.
     * @return the resolved class (if found)
     */
    private ThemisClassInstance lookUpJavaLangClass(final String pReference) {
        /* Look for a fully qualified class in external and project classes */
        final ThemisClassInstance myReference = theProject.tryJavaLang(pReference);

        /* If we have now found the class, add to knownClasses */
        if (myReference != null) {
            theKnownClasses.put(pReference, myReference);
        }

        /* Return resolved reference (or null) */
        return myReference;
    }

    /**
     * Declare referenced class.
     *
     * @param pClass the class
     */
    private void declareReferencedClass(final ThemisClassInstance pClass) {
        /* Lookup the project class and return if not in project */
        final Map<String, ThemisSolverClass> myProjectClasses = theProject.getProjectClassMap();
        ThemisSolverClass myClass = myProjectClasses.get(pClass.getFullName());
        if (myClass == null) {
            return;
        }

        /* Convert to top-level class */
        if (!myClass.isTopLevel()) {
            myClass = ((ThemisSolverFile) myClass.getOwningFile()).getTopLevel();
        }

        /* If this is the first instance of the reference */
        if (myClass != null && !theReferenced.contains(myClass)) {
            /* Add to the list of referenced classes */
            theReferenced.add(myClass);
        }
    }
}
