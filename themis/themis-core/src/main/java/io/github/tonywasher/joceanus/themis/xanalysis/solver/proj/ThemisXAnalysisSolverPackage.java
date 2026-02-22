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
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisFile;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisPackage;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverModuleDef;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverPackageDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Solver Package.
 */
public class ThemisXAnalysisSolverPackage
        implements ThemisXAnalysisSolverPackageDef, Comparable<ThemisXAnalysisSolverPackage> {
    /**
     * The owning module.
     */
    private final ThemisXAnalysisSolverModuleDef theModule;

    /**
     * The underlying package.
     */
    private final ThemisXAnalysisPackage thePackage;

    /**
     * The shortName.
     */
    private final String theShortName;

    /**
     * The files.
     */
    private final List<ThemisXAnalysisSolverFile> theFiles;

    /**
     * The list of child packages.
     */
    private final List<ThemisXAnalysisSolverPackage> theChildren;

    /**
     * The referenceMap.
     */
    private final ThemisXAnalysisSolverReference theReferenceMap;

    /**
     * The referenced sibling packages in local package.
     */
    private final List<ThemisXAnalysisSolverPackage> theLocalReferences;

    /**
     * The implied referenced sibling packages in local package.
     */
    private final List<ThemisXAnalysisSolverPackage> theImpliedReferences;

    /**
     * Is this a standard package?
     */
    private final boolean isStandard;

    /**
     * Is the reference list circular?
     */
    private boolean isCircular;

    /**
     * Constructor.
     *
     * @param pModule  the owning module
     * @param pPackage the parsed package
     */
    ThemisXAnalysisSolverPackage(final ThemisXAnalysisSolverModuleDef pModule,
                                 final ThemisXAnalysisPackage pPackage) {
        /* Store the package and register with parser */
        theModule = pModule;
        thePackage = pPackage;
        isStandard = pPackage.isStandard();

        /* Determine the short name */
        final String myName = getPackageName();
        final int iIndex = myName.lastIndexOf(ThemisXAnalysisChar.PERIOD);
        theShortName = iIndex == -1 ? myName : myName.substring(iIndex + 1);

        /* Create the referenceMap and lists */
        theReferenceMap = new ThemisXAnalysisSolverReference();
        theLocalReferences = new ArrayList<>();
        theImpliedReferences = new ArrayList<>();

        /* Populate the fileList */
        theFiles = new ArrayList<>();
        theChildren = new ArrayList<>();
        for (ThemisXAnalysisFile myFile : thePackage.getFiles()) {
            final ThemisXAnalysisSolverFile mySolverFile = new ThemisXAnalysisSolverFile(this, myFile);
            theFiles.add(mySolverFile);
        }
    }

    @Override
    public ThemisXAnalysisSolverModuleDef getOwningModule() {
        return theModule;
    }

    @Override
    public ThemisXAnalysisPackage getUnderlyingPackage() {
        return thePackage;
    }

    @Override
    public String getPackageName() {
        return thePackage.getPackage();
    }

    /**
     * Obtain the short package name.
     *
     * @return the shirt package name
     */
    public String getShortName() {
        return theShortName;
    }

    /**
     * Obtain the file list.
     *
     * @return the file list
     */
    public List<ThemisXAnalysisSolverFile> getFiles() {
        return theFiles;
    }

    /**
     * Obtain the list of child packages.
     *
     * @return the list of child packages
     */
    public List<ThemisXAnalysisSolverPackage> getChildren() {
        return theChildren;
    }

    /**
     * Obtain the referenceMap.
     *
     * @return the referenceMap
     */
    public ThemisXAnalysisSolverReference getReferenceMap() {
        return theReferenceMap;
    }

    /**
     * Obtain the local references.
     *
     * @return the local references
     */
    public List<ThemisXAnalysisSolverPackage> getLocalReferences() {
        return theLocalReferences;
    }

    /**
     * Obtain the implied references.
     *
     * @return the implied references
     */
    public List<ThemisXAnalysisSolverPackage> getImpliedReferences() {
        return theImpliedReferences;
    }

    /**
     * Add the child package.
     *
     * @param pChild the child package
     */
    public void addChild(final ThemisXAnalysisSolverPackage pChild) {
        theChildren.add(pChild);
    }

    @Override
    public boolean isStandard() {
        return isStandard;
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
     * Is the package a placeHolder?
     *
     * @return true/false
     */
    public boolean isPlaceHolder() {
        return thePackage.isPlaceHolder();
    }

    /**
     * Set the referenced classes.
     *
     * @param pReferenced the referenced classes
     */
    public void setReferenced(final List<ThemisXAnalysisSolverPackage> pReferenced) {
        /* Add all references except for a self-reference */
        theLocalReferences.addAll(pReferenced.stream().filter(s -> !s.equals(this)).toList());
    }

    /**
     * process local references.
     */
    public void processLocalReferences() {
        /* Loop through the referenced local classes */
        for (ThemisXAnalysisSolverPackage myPackage : theLocalReferences) {
            /* Process the class */
            processLocalReferences(myPackage);
        }

        /* Determine whether we are circular */
        isCircular = theImpliedReferences.contains(this);
    }

    /**
     * process local references.
     *
     * @param pPackage the class to process local references for
     */
    private void processLocalReferences(final ThemisXAnalysisSolverPackage pPackage) {
        /* If this is not already in the local reference list */
        if (!theImpliedReferences.contains(pPackage)) {
            /* Add the class */
            theImpliedReferences.add(pPackage);

            /* Only process further if we have not found circularity */
            if (!pPackage.equals(this)) {
                /* Loop through the local references */
                for (ThemisXAnalysisSolverPackage myPackage : pPackage.getLocalReferences()) {
                    /* Process the local references */
                    processLocalReferences(myPackage);
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

        /* Make sure that the object is a Package */
        if (!(pThat instanceof ThemisXAnalysisSolverPackage myThat)) {
            return false;
        }

        /* Check name of package */
        return getPackageName().equals(myThat.getPackageName());
    }

    @Override
    public int hashCode() {
        return getPackageName().hashCode();
    }

    @Override
    public String toString() {
        return thePackage.toString();
    }

    @Override
    public int compareTo(final ThemisXAnalysisSolverPackage pThat) {
        /* Handle simple dependency */
        if (theImpliedReferences.contains(pThat)
                && !pThat.getImpliedReferences().contains(this)) {
            return -1;
        }
        if (pThat.getImpliedReferences().contains(this)
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
        return getPackageName().compareTo(pThat.getPackageName());
    }
}
