/*
 * Themis: Java Project Framework
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.themis.xanalysis.solver.proj;

import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisChar;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisFile;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisPackage;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverFileDef;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverPackageDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * The referenced classes in all packages.
     */
    private final List<ThemisXAnalysisSolverClass> theReferenced;

    /**
     * The referenced classes in local package.
     */
    private final List<ThemisXAnalysisSolverClass> theLocalReferences;

    /**
     * The implied referenced classes in local package.
     */
    private final List<ThemisXAnalysisSolverClass> theImpliedReferences;

    /**
     * Does the file need preProcessing?
     */
    private boolean needsPreProcess;

    /**
     * Is the reference list circular?
     */
    private boolean isCircular;

    /**
     * Constructor.
     *
     * @param pPackage the owning package
     * @param pFile    the parsed file
     */
    ThemisXAnalysisSolverFile(final ThemisXAnalysisSolverPackageDef pPackage,
                              final ThemisXAnalysisFile pFile) {
        /* Store the parameters */
        thePackage = pPackage;
        theFile = pFile;

        /* Note that we need preProcessing */
        needsPreProcess = true;

        /* Populate the classList */
        theClasses = new ArrayList<>();
        for (ThemisXAnalysisClassInstance myClass : theFile.getClasses()) {
            final ThemisXAnalysisSolverClass mySolverClass = new ThemisXAnalysisSolverClass(this, myClass);
            theClasses.add(mySolverClass);
        }

        /* Determine top-level class */
        theTopLevel = theClasses.stream().filter(ThemisXAnalysisSolverClass::isTopLevel).findFirst().orElse(null);

        /* Create the reference lists */
        theReferenced = new ArrayList<>();
        theLocalReferences = new ArrayList<>();
        theImpliedReferences = new ArrayList<>();
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
     *
     * @return the top-level
     */
    public ThemisXAnalysisSolverClass getTopLevel() {
        return theTopLevel;
    }

    /**
     * Obtain the classes.
     *
     * @return the classes
     */
    public List<ThemisXAnalysisSolverClass> getClasses() {
        return theClasses;
    }

    /**
     * Obtain the local references.
     *
     * @return the local references
     */
    public List<ThemisXAnalysisSolverClass> getLocalReferences() {
        return theLocalReferences;
    }

    /**
     * Obtain the implied references.
     *
     * @return the implied references
     */
    public List<ThemisXAnalysisSolverClass> getImpliedReferences() {
        return theImpliedReferences;
    }

    /**
     * Mark the file as pre-processed.
     */
    public void markPreProcessed() {
        needsPreProcess = false;
    }

    /**
     * Does this file need preProcessing?
     *
     * @return true/false
     */
    public boolean needsPreProcess() {
        return needsPreProcess;
    }

    /**
     * Is the reference list circular?
     *
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
     * Find references to the target package and it's children.
     *
     * @param pReferenceMap the map of references
     * @param pPackage      the target package
     */
    void findReferences(final Map<ThemisXAnalysisSolverClass, List<ThemisXAnalysisSolverClass>> pReferenceMap,
                        final String pPackage) {
        /* Create list and prefix */
        final List<ThemisXAnalysisSolverClass> myReferences = new ArrayList<>();
        final String myPrefix = pPackage + ThemisXAnalysisChar.PERIOD;

        /* Loop through the references */
        for (ThemisXAnalysisSolverClass myReference : theReferenced) {
            final String myName = myReference.getFullName();
            if (myName.startsWith(myPrefix)) {
                myReferences.add(myReference);
            }
        }

        /* If we have references, add to map */
        if (!myReferences.isEmpty()) {
            pReferenceMap.put(theTopLevel, myReferences);
        }
    }

    /**
     * Set the referenced classes.
     *
     * @param pReferenced the referenced classes
     */
    public void setReferenced(final List<ThemisXAnalysisSolverClass> pReferenced) {
        /* Add all references except for a self-reference */
        theReferenced.addAll(pReferenced.stream().filter(s -> !s.equals(getTopLevel())).toList());

        /* Build local reference list */
        final String myPackage = theTopLevel.getPackageName();
        for (ThemisXAnalysisSolverClass myClass : theReferenced) {
            /* Add reference if this is a local class */
            if (myClass.getPackageName().equals(myPackage)) {
                theLocalReferences.add(myClass);
            }
        }
    }

    /**
     * process local references.
     */
    public void processLocalReferences() {
        /* Loop through the referenced local classes */
        for (ThemisXAnalysisSolverClass myClass : theLocalReferences) {
            /* Process the class */
            processLocalReferences(myClass);
        }

        /* Determine whether we are circular */
        isCircular = theImpliedReferences.contains(theTopLevel);
    }

    /**
     * process local references.
     *
     * @param pClass the class to process local references for
     */
    private void processLocalReferences(final ThemisXAnalysisSolverClass pClass) {
        /* If this is not already in the local reference list */
        if (!theImpliedReferences.contains(pClass)) {
            /* Add the class */
            theImpliedReferences.add(pClass);

            /* Only process further if we have not found circularity */
            if (!pClass.equals(theTopLevel)) {
                /* Loop through the local references */
                for (ThemisXAnalysisSolverClass myClass : ((ThemisXAnalysisSolverFile) pClass.getOwningFile()).getLocalReferences()) {
                    /* Process the local references */
                    processLocalReferences(myClass);
                }
            }
        }
    }

    /**
     * Compare this package to another package for sort order.
     *
     * @param pThat the other package to compare to
     * @return true/false
     */
    public int compareTo(final ThemisXAnalysisSolverFile pThat) {
        /* Access top-level class */
        final ThemisXAnalysisSolverClass myClass = pThat.getTopLevel();

        /* Handle simple dependency */
        if (theImpliedReferences.contains(myClass)
                && !pThat.getImpliedReferences().contains(theTopLevel)) {
            return -1;
        }
        if (pThat.getImpliedReferences().contains(theTopLevel)
                && !theImpliedReferences.contains(myClass)) {
            return 1;
        }

        /* Sort on number of dependencies */
        final int iDiff = pThat.theImpliedReferences.size()
                - theImpliedReferences.size();
        if (iDiff != 0) {
            return iDiff;
        }

        /* If all else fails rely on alphabetical */
        final ThemisXAnalysisPackage myPackage = (ThemisXAnalysisPackage) thePackage;
        final ThemisXAnalysisPackage myThatPackage = (ThemisXAnalysisPackage) pThat.getOwningPackage();
        return myPackage.getPackage().compareTo(myThatPackage.getPackage());
    }
}
