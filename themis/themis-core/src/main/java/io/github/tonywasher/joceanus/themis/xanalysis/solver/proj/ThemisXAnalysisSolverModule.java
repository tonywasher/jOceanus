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
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisModule;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisPackage;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverModuleDef;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverProjectDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Solver Module.
 */
public class ThemisXAnalysisSolverModule
        implements ThemisXAnalysisSolverModuleDef {
    /**
     * The root package.
     */
    private static final String ROOT = "";

    /**
     * The owning project.
     */
    private final ThemisXAnalysisSolverProjectDef theProject;

    /**
     * The underlying module.
     */
    private final ThemisXAnalysisModule theModule;

    /**
     * The list of packages.
     */
    private final Map<String, ThemisXAnalysisSolverPackage> thePackages;

    /**
     * Constructor.
     *
     * @param pProject the owning project
     * @param pModule  the parsed module
     */
    ThemisXAnalysisSolverModule(final ThemisXAnalysisSolverProjectDef pProject,
                                final ThemisXAnalysisModule pModule) {
        /* Store the parameters */
        theProject = pProject;
        theModule = pModule;

        /* Initialise the packages */
        thePackages = new LinkedHashMap<>();
        for (ThemisXAnalysisPackage myPackage : theModule.getPackages()) {
            final ThemisXAnalysisSolverPackage mySolverPackage = new ThemisXAnalysisSolverPackage(this, myPackage);
            thePackages.put(mySolverPackage.getPackageName(), mySolverPackage);
        }

        /* Create placeHolders*/
        createPlaceHolders();
    }

    @Override
    public ThemisXAnalysisSolverProjectDef getOwningProject() {
        return theProject;
    }

    @Override
    public ThemisXAnalysisModule getUnderlyingModule() {
        return theModule;
    }

    /**
     * Obtain the packages.
     *
     * @return the packages
     */
    public Map<String, ThemisXAnalysisSolverPackage> getPackages() {
        return thePackages;
    }

    @Override
    public String toString() {
        return theModule.toString();
    }

    /**
     * Create placeHolder packages.
     */
    private void createPlaceHolders() {
        /* Create a list and map of real packages */
        final HashMap<String, ThemisXAnalysisSolverPackage> myPackageMap = new HashMap<>(thePackages);
        final List<ThemisXAnalysisSolverPackage> myPackages = new ArrayList<>(thePackages.values());

        /* Loop through the full packages */
        for (ThemisXAnalysisSolverPackage myPackage : myPackages) {
            /* Add the parent link */
            addParentLink(myPackageMap, myPackage);
        }
    }

    /**
     * Create placeHolder packages.
     *
     * @param pPackageMap the referenceMap
     * @param pPackage    the package
     */
    private void addParentLink(final Map<String, ThemisXAnalysisSolverPackage> pPackageMap,
                               final ThemisXAnalysisSolverPackage pPackage) {
        /* Determine name of parent package */
        final String myName = pPackage.getPackageName();
        final int iIndex = myName.lastIndexOf(ThemisXAnalysisChar.PERIOD);
        final String myParentName = iIndex == -1 ? ROOT : myName.substring(0, iIndex);

        /* Look up parent */
        ThemisXAnalysisSolverPackage myParent = pPackageMap.get(myParentName);

        /* If we did not find a parent */
        if (myParent == null) {
            /* Create a placeholder parent and put into maps */
            myParent = new ThemisXAnalysisSolverPackage(this, new ThemisXAnalysisPackage(myParentName));
            pPackageMap.put(myParentName, myParent);
            thePackages.put(myParentName, myParent);

            /* Add further links if we have not reached ROOT */
            if (!ROOT.equals(myParentName)) {
                addParentLink(pPackageMap, myParent);
            }
        }

        /* Add child to parent */
        myParent.addChild(pPackage);
    }

    /**
     * Look for packages that are immediate roots.
     *
     * @return the immediate roots
     */
    public ThemisXAnalysisSolverPackage getRoot() {
        /* Return the root */
        return thePackages.get(ROOT);
    }
}
