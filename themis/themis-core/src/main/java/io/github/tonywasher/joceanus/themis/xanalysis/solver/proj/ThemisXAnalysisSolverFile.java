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

import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisFile;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverFileDef;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverPackageDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Solver File.
 */
public class ThemisXAnalysisSolverFile
        implements ThemisXAnalysisSolverFileDef, Comparable<ThemisXAnalysisSolverFile> {
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
     * Obtain the referenced classes.
     *
     * @return the classes
     */
    public List<ThemisXAnalysisSolverClass> getReferenced() {
        return theReferenced;
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

    /**
     * Obtain the location.
     *
     * @return the location
     */
    public String getLocation() {
        return theFile.getLocation();
    }

    @Override
    public String toString() {
        return theFile.toString();
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

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a File */
        if (!(pThat instanceof ThemisXAnalysisSolverFile myThat)) {
            return false;
        }

        /* Check name of package */
        return getLocation().equals(myThat.getLocation());
    }

    @Override
    public int hashCode() {
        return theFile.getLocation().hashCode();
    }

    @Override
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
        return getLocation().compareTo(pThat.getLocation());
    }
}
