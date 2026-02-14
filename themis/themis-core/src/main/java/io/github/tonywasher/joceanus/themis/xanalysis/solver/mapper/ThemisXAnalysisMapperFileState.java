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

package io.github.tonywasher.joceanus.themis.xanalysis.solver.mapper;

import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisChar;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeImport;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.type.ThemisXAnalysisTypeClassInterface;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverClass;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverFile;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverPackage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * File State.
 */
public class ThemisXAnalysisMapperFileState {
    /**
     * The project state.
     */
    private final ThemisXAnalysisMapperProjectState theProject;

    /**
     * Map of all known short names in file.
     */
    private final Map<String, ThemisXAnalysisClassInstance> theKnownClasses;

    /**
     * The referenced classes.
     */
    private final List<ThemisXAnalysisSolverClass> theReferenced;

    /**
     * Constructor.
     *
     * @param pProject the project
     */
    ThemisXAnalysisMapperFileState(final ThemisXAnalysisMapperProjectState pProject) {
        /* Store the project */
        theProject = pProject;

        /* Create the maps and lists */
        theKnownClasses = new LinkedHashMap<>();
        theReferenced = new ArrayList<>();
    }

    /**
     * InitialiseForFile.
     *
     * @param pFile the file
     */
    void initForFile(final ThemisXAnalysisSolverFile pFile) {
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
    private void determineKnownClasses(final ThemisXAnalysisSolverFile pFile) {
        /* Initialise the map with the javaLang classes */
        for (ThemisXAnalysisClassInstance myClass : theProject.getJavaLangMap().values()) {
            theKnownClasses.put(myClass.getName(), myClass);
        }

        /* Add all the package top-level classes */
        final ThemisXAnalysisSolverPackage myPackage = (ThemisXAnalysisSolverPackage) pFile.getOwningPackage();
        for (ThemisXAnalysisSolverFile myFile : myPackage.getFiles()) {
            final ThemisXAnalysisSolverClass myClass = myFile.getTopLevel();
            if (myClass != null) {
                theKnownClasses.put(myClass.getName(), myClass.getUnderlyingClass());
            }
        }

        /* Process the imports */
        for (ThemisXAnalysisNodeInstance myNode : pFile.getUnderlyingFile().getContents().getImports()) {
            /* LookUp the import and record it */
            final ThemisXAnalysisClassInstance myImport = lookUpImport((ThemisXAnalysisNodeImport) myNode);
            theKnownClasses.put(myImport.getName(), myImport);
        }

        for (ThemisXAnalysisTypeInstance myExtend : pFile.getTopLevel().getUnderlyingClass().getExtends()) {
            final ThemisXAnalysisTypeClassInterface myClass = (ThemisXAnalysisTypeClassInterface) myExtend;
            final ThemisXAnalysisClassInstance myClassDef = theKnownClasses.get(myClass.getName());
            final List<ThemisXAnalysisSolverClass> mySubClasses = theProject.getProjectSubClassMap().get(myClassDef.getFullName());
            if (mySubClasses != null) {
                for (ThemisXAnalysisSolverClass mySubClass : mySubClasses) {
                    theKnownClasses.put(mySubClass.getName(), mySubClass.getUnderlyingClass());
                }
            }
        }

        for (ThemisXAnalysisTypeInstance myImplement : pFile.getTopLevel().getUnderlyingClass().getImplements()) {
            final ThemisXAnalysisTypeClassInterface myClass = (ThemisXAnalysisTypeClassInterface) myImplement;
            final ThemisXAnalysisClassInstance myClassDef = theKnownClasses.get(myClass.getName());
            final List<ThemisXAnalysisSolverClass> mySubClasses = theProject.getProjectSubClassMap().get(myClassDef.getFullName());
            if (mySubClasses != null) {
                for (ThemisXAnalysisSolverClass mySubClass : mySubClasses) {
                    theKnownClasses.put(mySubClass.getName(), mySubClass.getUnderlyingClass());
                }
            }
        }

        /* Process the classes in the file */
        for (ThemisXAnalysisSolverClass myClass : pFile.getClasses()) {
            if (!myClass.getUnderlyingClass().isAnonClass()) {
                theKnownClasses.put(myClass.getName(), myClass.getUnderlyingClass());
            }
        }
    }

    /**
     * Obtain ancestors for a class.
     *
     * @param pClass the class
     * @return the list of ancestors
     */
    private List<ThemisXAnalysisClassInstance> getAncestors(final ThemisXAnalysisClassInstance pClass) {
        for (ThemisXAnalysisTypeInstance myExtends : pClass.getExtends()) {
            final ThemisXAnalysisTypeClassInterface myClass = (ThemisXAnalysisTypeClassInterface) myExtends;
        }
        return null;
    }

    /**
     * Look up import.
     *
     * @param pImport the import definition.
     * @return the import class
     */
    private ThemisXAnalysisClassInstance lookUpImport(final ThemisXAnalysisNodeImport pImport) {
        /* Determine full name */
        final String myFullName = pImport.getFullName();

        /* Look for project class of this name */
        final ThemisXAnalysisSolverClass myClass = theProject.getProjectClassMap().get(myFullName);
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
    ThemisXAnalysisClassInstance processPossibleReference(final String pReference) {
        /* Look up the reference in the list of known classes */
        ThemisXAnalysisClassInstance myReference = theKnownClasses.get(pReference);

        /* If it is not a known class, but contains a period */
        final int iIndex = pReference.indexOf(ThemisXAnalysisChar.PERIOD);
        if (myReference == null && iIndex != -1) {
            /* Look for a fully qualified class in external and project classes */
            myReference = theProject.getExternalClassMap().get(pReference);
            if (myReference == null) {
                final ThemisXAnalysisSolverClass myClass = theProject.getProjectClassMap().get(pReference);
                if (myClass != null) {
                    myReference = myClass.getUnderlyingClass();
                }
            }

            /* If still not found */
            if (myReference == null) {
                /* Try just the first part */
                final String myBase = pReference.substring(0, iIndex);
                myReference = theKnownClasses.get(myBase);
            }
        }

        /* If we found the reference, process it */
        if (myReference != null) {
            declareReferencedClass(myReference);
        }
        return myReference;
    }

    /**
     * Declare referenced class.
     *
     * @param pClass the class
     */
    private void declareReferencedClass(final ThemisXAnalysisClassInstance pClass) {
        /* Lookup the project class and return if not in project */
        final Map<String, ThemisXAnalysisSolverClass> myProjectClasses = theProject.getProjectClassMap();
        ThemisXAnalysisSolverClass myClass = myProjectClasses.get(pClass.getFullName());
        if (myClass == null) {
            return;
        }

        /* Convert to top-level class */
        if (!myClass.isTopLevel()) {
            myClass = ((ThemisXAnalysisSolverFile) myClass.getOwningFile()).getTopLevel();
        }

        /* If this is the first instance of the reference */
        if (myClass != null && !theReferenced.contains(myClass)) {
            /* Add to the list of referenced classes */
            theReferenced.add(myClass);
        }
    }
}
