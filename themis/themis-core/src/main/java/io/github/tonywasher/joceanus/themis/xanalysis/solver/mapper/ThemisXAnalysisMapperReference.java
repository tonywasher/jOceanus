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
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverClass;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverFile;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverModule;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverPackage;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverProject;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverReference;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverReference.ThemisXAnalysisRefType;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverReference.ThemisXAnalysisSolverRefClass;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverReference.ThemisXAnalysisSolverRefPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to analyse references.
 */
public class ThemisXAnalysisMapperReference {
    /**
     * Process references for a project.
     *
     * @param pProject the project
     */
    public void processReferences(final ThemisXAnalysisSolverProject pProject) {
        /* Process references for all modules */
        for (ThemisXAnalysisSolverModule myModule : pProject.getModules()) {
            /* Process references for all roots */
            for (ThemisXAnalysisSolverPackage myRoot : myModule.findRoots()) {
                processReferences(myRoot);
            }
        }
    }

    /**
     * Process references for a package and it's children.
     *
     * @param pPackage the package
     */
    private void processReferences(final ThemisXAnalysisSolverPackage pPackage) {
        /* Obtain the map for the package */
        final ThemisXAnalysisSolverReference myMap = pPackage.getReferenceMap();

        /* Process references for all children */
        for (ThemisXAnalysisSolverPackage myChild : pPackage.getChildren()) {
            /* Obtain the map for the child */
            final ThemisXAnalysisSolverReference myChildMap = myChild.getReferenceMap();

            /* Process references for all siblings */
            for (ThemisXAnalysisSolverPackage mySibling : pPackage.getChildren()) {
                /* Avoid self references */
                if (!myChild.equals(mySibling)) {
                    findReferences(myChildMap, ThemisXAnalysisRefType.SIBLING, mySibling);
                }
            }

            /* find Exact references from the child to the parent */
            findReferences(myChildMap, ThemisXAnalysisRefType.PARENT, pPackage);

            /* find references from the parent to the child */
            findReferences(myMap, ThemisXAnalysisRefType.CHILD, myChild);
        }
    }

    /**
     * Find references to the target package and it's children.
     *
     * @param pMap     the referenceMap
     * @param pRefType the referenceType
     * @param pPackage the target package
     */
    private void findReferences(final ThemisXAnalysisSolverReference pMap,
                                final ThemisXAnalysisRefType pRefType,
                                final ThemisXAnalysisSolverPackage pPackage) {
        /* Create result map */
        final ThemisXAnalysisSolverRefPackage myReferences
                = new ThemisXAnalysisSolverRefPackage(pPackage.getPackageName(), pRefType);

        /* Look for references */
        findReferences(myReferences, pPackage);

        /* If we have any references */
        if (!myReferences.getReferences().isEmpty()) {
            pMap.addReferences(myReferences);
        }
    }

    /**
     * Find references to the target package and it's children.
     *
     * @param pReferences the referenceSet
     * @param pPackage    the source package
     */
    private void findReferences(final ThemisXAnalysisSolverRefPackage pReferences,
                                final ThemisXAnalysisSolverPackage pPackage) {
        /* Loop through the files */
        for (ThemisXAnalysisSolverFile myFile : pPackage.getFiles()) {
            findReferences(pReferences, myFile);
        }

        /* Loop through the children to find further references */
        if (!ThemisXAnalysisRefType.CHILD.equals(pReferences.getReferenceType())) {
            for (ThemisXAnalysisSolverPackage myChild : pPackage.getChildren()) {
                findReferences(pReferences, myChild);
            }
        }
    }

    /**
     * Find references from the file to the target package and it's children.
     *
     * @param pReferences the referenceSet
     * @param pFile       the source file
     */
    private void findReferences(final ThemisXAnalysisSolverRefPackage pReferences,
                                final ThemisXAnalysisSolverFile pFile) {
        /* Create list and prefix */
        final List<ThemisXAnalysisSolverClass> myReferences = new ArrayList<>();
        final String myPackage = pReferences.getPackage();
        final String myPrefix = myPackage + ThemisXAnalysisChar.PERIOD;
        final boolean isParent = ThemisXAnalysisRefType.PARENT.equals(pReferences.getReferenceType());

        /* Loop through the references */
        for (ThemisXAnalysisSolverClass myReference : pFile.getReferenced()) {
            final String myName = myReference.getFullName();
            if (isParent
                    ? myName.equals(myPrefix + myReference.getName())
                    : myName.startsWith(myPrefix)) {
                myReferences.add(myReference);
            }
        }

        /* If we have references, add to map */
        if (!myReferences.isEmpty()) {
            /* Create the reference map and add to references */
            final ThemisXAnalysisSolverRefClass myClass = new ThemisXAnalysisSolverRefClass(pFile.getTopLevel(), myReferences);
            pReferences.addReferences(myClass);
        }
    }
}
