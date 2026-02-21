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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Solver Package.
 */
public class ThemisXAnalysisSolverPackage
        implements ThemisXAnalysisSolverPackageDef {
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
     * Is this a standard package?
     */
    private final boolean isStandard;

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

    /**
     * Obtain the package name.
     *
     * @return the package name
     */
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
     * Is the package a placeHolder?
     *
     * @return true/false
     */
    public boolean isPlaceHolder() {
        return thePackage.isPlaceHolder();
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

    /**
     * Find references to the target package and it's children.
     *
     * @param pPackage the target package
     * @return the map of references
     */
    public Map<ThemisXAnalysisSolverClass, List<ThemisXAnalysisSolverClass>> findReferences(final ThemisXAnalysisSolverPackage pPackage) {
        /* Create result map */
        final Map<ThemisXAnalysisSolverClass, List<ThemisXAnalysisSolverClass>> myResult = new HashMap<>();

        /* Look for references */
        findReferences(myResult, pPackage);

        /* Return the results */
        return myResult;
    }

    /**
     * Find references to the target package and it's children.
     *
     * @param pReferenceMap the map of references
     * @param pPackage      the target package
     */
    private void findReferences(final Map<ThemisXAnalysisSolverClass, List<ThemisXAnalysisSolverClass>> pReferenceMap,
                                final ThemisXAnalysisSolverPackage pPackage) {
        /* Loop through the files */
        for (ThemisXAnalysisSolverFile myFile : theFiles) {
            myFile.findReferences(pReferenceMap, pPackage.getPackageName());
        }

        /* Loop through the children to find further references */
        for (ThemisXAnalysisSolverPackage myChild : theChildren) {
            myChild.findReferences(pReferenceMap, pPackage);
        }
    }
}
