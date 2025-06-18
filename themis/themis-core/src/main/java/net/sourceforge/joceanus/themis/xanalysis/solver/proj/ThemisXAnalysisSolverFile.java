/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.solver.proj;

import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisFile;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverFileDef;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverPackageDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Solver File.
 */
public class ThemisXAnalysisSolverFile
        implements ThemisXAnalysisSolverFileDef {
    /**
     * The owning package.
     */
    private final ThemisXAnalysisSolverPackageDef thePackage;

    /**
     * The underlying file.
     */
    private final ThemisXAnalysisFile theFile;

    /**
     * The top-level class.
     */
    private final ThemisXAnalysisSolverClass theTopLevel;

    /**
     * The classes.
     */
    private final List<ThemisXAnalysisSolverClass> theClasses;

    /**
     * The referenced classes.
     */
    private final List<ThemisXAnalysisSolverClass> theReferenced;

    /**
     * Is the reference list circular?
     */
    private boolean isCircular;

    /**
     * Constructor.
     * @param pPackage the owning package
     * @param pFile the parsed file
     */
    ThemisXAnalysisSolverFile(final ThemisXAnalysisSolverPackageDef pPackage,
                              final ThemisXAnalysisFile pFile) {
        /* Store the parameters */
        thePackage = pPackage;
        theFile = pFile;

        /* Populate the classList */
        theClasses = new ArrayList<>();
        for (ThemisXAnalysisClassInstance myClass : theFile.getClasses()) {
            final ThemisXAnalysisSolverClass mySolverClass = new ThemisXAnalysisSolverClass(this, myClass);
            theClasses.add(mySolverClass);
        }

        /* Determine top-level class */
        theTopLevel = theClasses.stream().filter(ThemisXAnalysisSolverClass::isTopLevel).findFirst().orElse(null);

        /* Create the referenced classes */
        theReferenced = new ArrayList<>();
    }

    @Override
    public ThemisXAnalysisSolverPackageDef getOwningPackage() {
        return thePackage;
    }

    @Override
    public ThemisXAnalysisFile getUnderlyingFile() {
        return theFile;
    }

    /**
     * Obtain the top-level class.
     * @return the top-level
     */
    public ThemisXAnalysisSolverClass getTopLevel() {
        return theTopLevel;
    }

    /**
     * Obtain the classes.
     * @return the classes
     */
    public List<ThemisXAnalysisSolverClass> getClasses() {
        return theClasses;
    }

    /**
     * Is the reference list circular?
     * @return true/false
     */
    public boolean isCircular() {
        return isCircular;
    }

    @Override
    public String toString() {
        return theFile.toString();
    }

    /**
     * Obtain the list of classes that reference the named package.
     * @param pPackage the package
     * @return the list of referencing classes
     */
    public List<ThemisXAnalysisSolverClass> getReferencesTo(final String pPackage) {
        /* Loop through the classes */
        final List<ThemisXAnalysisSolverClass> myReferences = new ArrayList<>();
        for (ThemisXAnalysisSolverClass myReference : theReferenced) {
            if (pPackage.equals(myReference.getPackageName())) {
                myReferences.add(myReference);
            }
        }
        return myReferences;
    }

    /**
     * Obtain the list of local classes that are referenced.
     * @return the list of referencing classes
     */
    private List<ThemisXAnalysisSolverClass> getLocalReferences() {
        return getReferencesTo(thePackage.toString());
    }

    /**
     * Set the referenced classes.
     * @param pReferenced the referenced classes
     */
    public void setReferenced(final List<ThemisXAnalysisSolverClass> pReferenced) {
        /* Add all references except for a self-reference */
        theReferenced.addAll(pReferenced.stream().filter(s -> !s.equals(getTopLevel())).toList());
    }

    /**
     * process local references.
     */
    public void processLocalReferences() {
        /* Create a reference list */
        final List<ThemisXAnalysisSolverClass> myFullyReferenced = new ArrayList<>();

        /* Loop through the referenced local classes */
        for (ThemisXAnalysisSolverClass myClass : getLocalReferences()) {
            /* Process the class */
            processLocalReferences(myFullyReferenced, myClass);
        }

        /* Determine whether we are circular */
        isCircular = myFullyReferenced.contains(theTopLevel);
    }

    /**
     * process local references.
     * @param pReferences the references list
     * @param pClass the class to process local references for
     */
    private void processLocalReferences(final List<ThemisXAnalysisSolverClass> pReferences,
                                        final ThemisXAnalysisSolverClass pClass) {
        /* If this is not already in the local reference list */
        if (!pReferences.contains(pClass)) {
            /* Add the class */
            pReferences.add(pClass);

            /* Only process further if we have not found circularity */
            if (!pClass.equals(theTopLevel)) {
                /* Loop through the local references */
                for (ThemisXAnalysisSolverClass myClass : ((ThemisXAnalysisSolverFile) pClass.getOwningFile()).getLocalReferences()) {
                    /* Process the local references */
                    processLocalReferences(pReferences, myClass);
                }
            }
        }
    }
}
