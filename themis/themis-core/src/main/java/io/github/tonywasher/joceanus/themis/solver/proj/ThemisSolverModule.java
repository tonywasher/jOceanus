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
package io.github.tonywasher.joceanus.themis.solver.proj;

import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisModule;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisPackage;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverDef.ThemisSolverModuleDef;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverDef.ThemisSolverProjectDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Solver Module.
 */
public class ThemisSolverModule
        implements ThemisSolverModuleDef {
    /**
     * The root package.
     */
    public static final String ROOT = "";

    /**
     * The owning project.
     */
    private final ThemisSolverProjectDef theProject;

    /**
     * The underlying module.
     */
    private final ThemisModule theModule;

    /**
     * The list of packages.
     */
    private final Map<String, ThemisSolverPackage> thePackages;

    /**
     * Constructor.
     *
     * @param pProject the owning project
     * @param pModule  the parsed module
     */
    ThemisSolverModule(final ThemisSolverProjectDef pProject,
                       final ThemisModule pModule) {
        /* Store the parameters */
        theProject = pProject;
        theModule = pModule;

        /* Initialise the packages */
        thePackages = new LinkedHashMap<>();
        for (ThemisPackage myPackage : theModule.getPackages()) {
            final ThemisSolverPackage mySolverPackage = new ThemisSolverPackage(this, myPackage);
            thePackages.put(mySolverPackage.getPackageName(), mySolverPackage);
        }

        /* Create placeHolders*/
        createPlaceHolders();
    }

    @Override
    public ThemisSolverProjectDef getOwningProject() {
        return theProject;
    }

    @Override
    public ThemisModule getUnderlyingModule() {
        return theModule;
    }

    /**
     * Obtain the packages.
     *
     * @return the packages
     */
    public Map<String, ThemisSolverPackage> getPackages() {
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
        final HashMap<String, ThemisSolverPackage> myPackageMap = new HashMap<>(thePackages);
        final List<ThemisSolverPackage> myPackages = new ArrayList<>(thePackages.values());

        /* Loop through the full packages */
        for (ThemisSolverPackage myPackage : myPackages) {
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
    private void addParentLink(final Map<String, ThemisSolverPackage> pPackageMap,
                               final ThemisSolverPackage pPackage) {
        /* Determine name of parent package */
        final String myName = pPackage.getPackageName();
        final int iIndex = myName.lastIndexOf(ThemisChar.PERIOD);
        final String myParentName = iIndex == -1 ? ROOT : myName.substring(0, iIndex);

        /* Look up parent */
        ThemisSolverPackage myParent = pPackageMap.get(myParentName);

        /* If we did not find a parent */
        if (myParent == null) {
            /* Create a placeholder parent and put into maps */
            myParent = new ThemisSolverPackage(this, new ThemisPackage(myParentName));
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
    public ThemisSolverPackage getRoot() {
        /* Return the root */
        return thePackages.get(ROOT);
    }
}
