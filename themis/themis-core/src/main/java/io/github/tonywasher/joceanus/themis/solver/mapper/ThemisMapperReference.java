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
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverClass;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverFile;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverModule;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverPackage;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverProject;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverReference;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverReference.ThemisRefType;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverReference.ThemisSolverRefClass;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverReference.ThemisSolverRefPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to analyse references.
 */
public class ThemisMapperReference {
    /**
     * Process references for a project.
     *
     * @param pProject the project
     */
    public void processReferences(final ThemisSolverProject pProject) {
        /* Process references for all modules */
        for (ThemisSolverModule myModule : pProject.getModules()) {
            /* Process references for root */
            processReferences(myModule.getRoot());
        }
    }

    /**
     * Process references for a package and it's children.
     *
     * @param pPackage the package
     */
    private void processReferences(final ThemisSolverPackage pPackage) {
        /* Process references for all children */
        for (ThemisSolverPackage myChild : pPackage.getChildren()) {
            /* Process references for all siblings */
            for (ThemisSolverPackage mySibling : pPackage.getChildren()) {
                /* Avoid self references */
                if (!myChild.equals(mySibling)) {
                    findReferences(myChild, ThemisRefType.SIBLING, mySibling);
                }
            }

            /* Process sibling references */
            determineLocalReferences(myChild);
            pPackage.processLocalReferences();

            /* find references from the child to the parent */
            findReferences(myChild, ThemisRefType.PARENT, pPackage);

            /* find references from the parent to the child */
            findReferences(pPackage, ThemisRefType.CHILD, myChild);

            /* Process child */
            processReferences(myChild);
        }

        /* Process references for all children */
        for (ThemisSolverPackage myChild : pPackage.getChildren()) {
            /* Process sibling references */
            pPackage.processLocalReferences();

            /* Check for incest */
            check4Incest(pPackage, myChild);
        }
    }

    /**
     * Find references from the source package plus children to the target package and it's children.
     *
     * @param pSource  the sourcePackage
     * @param pRefType the referenceType
     * @param pTarget  the targetPackage
     */
    private void findReferences(final ThemisSolverPackage pSource,
                                final ThemisRefType pRefType,
                                final ThemisSolverPackage pTarget) {
        /* Obtain the map for the package */
        final ThemisSolverReference myMap = pSource.getReferenceMap();

        /* Create result map */
        final ThemisSolverRefPackage myReferences = new ThemisSolverRefPackage(pTarget, pRefType);

        /* Look for references */
        findReferences(myReferences, pSource);

        /* If we have any references */
        if (!myReferences.getReferences().isEmpty()) {
            myMap.addReferences(myReferences);
        }
    }

    /**
     * Find references to the target package and it's children.
     *
     * @param pReferences the referenceSet
     * @param pSource     the source package
     */
    private void findReferences(final ThemisSolverRefPackage pReferences,
                                final ThemisSolverPackage pSource) {
        /* Loop through the files */
        for (ThemisSolverFile myFile : pSource.getFiles()) {
            findReferences(pReferences, myFile);
        }

        /* Loop through the children to find further references */
        if (!ThemisRefType.CHILD.equals(pReferences.getReferenceType())) {
            for (ThemisSolverPackage myChild : pSource.getChildren()) {
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
    private void findReferences(final ThemisSolverRefPackage pReferences,
                                final ThemisSolverFile pFile) {
        /* Create list and prefix */
        final List<ThemisSolverClass> myReferences = new ArrayList<>();
        final String myPackage = pReferences.getPackage().getPackageName();
        final String myPrefix = myPackage + ThemisChar.PERIOD;
        final boolean isParent = ThemisRefType.PARENT.equals(pReferences.getReferenceType());

        /* Loop through the references */
        for (ThemisSolverClass myReference : pFile.getReferenced()) {
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
            final ThemisSolverRefClass myClass = new ThemisSolverRefClass(pFile.getTopLevel(), myReferences);
            pReferences.addReferences(myClass);
        }
    }

    /**
     * Determine local references.
     *
     * @param pPackage the package
     */
    private void determineLocalReferences(final ThemisSolverPackage pPackage) {
        /* Create list */
        final List<ThemisSolverPackage> myReferences = new ArrayList<>();
        final ThemisSolverReference myReferenceMap = pPackage.getReferenceMap();

        /* Add all Sibling references to the list */
        for (ThemisSolverRefPackage myPackage : myReferenceMap.getReferences(ThemisRefType.SIBLING)) {
            myReferences.add(myPackage.getPackage());
        }

        /* Register the references */
        pPackage.setReferenced(myReferences);
    }

    /**
     * Check for incest.
     *
     * @param pParent the parent
     * @param pChild  the child
     */
    private void check4Incest(final ThemisSolverPackage pParent,
                              final ThemisSolverPackage pChild) {
        /* Obtain the map for the package */
        final ThemisSolverReference myParentMap = pParent.getReferenceMap();
        final ThemisSolverReference myChildMap = pChild.getReferenceMap();

        /* If we have two-way links parent to/from child */
        final ThemisSolverRefPackage myParentRefs = myChildMap.getReferredPackage(pParent);
        final ThemisSolverRefPackage myChildRefs = myParentMap.getReferredPackage(pChild);
        if (myParentRefs != null && myChildRefs != null) {
            /* Mark child as incestuous */
            pChild.markIncestuous();
        }
    }
}
