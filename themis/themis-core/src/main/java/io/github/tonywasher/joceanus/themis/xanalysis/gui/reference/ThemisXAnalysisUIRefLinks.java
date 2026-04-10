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

package io.github.tonywasher.joceanus.themis.xanalysis.gui.reference;

import io.github.tonywasher.joceanus.themis.xanalysis.gui.base.ThemisXAnalysisUIDocBuilder;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverPackage;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverReference;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverReference.ThemisXAnalysisSolverRefClass;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverReference.ThemisXAnalysisSolverRefPackage;
import org.w3c.dom.Element;

/**
 * Links table builder.
 */
public class ThemisXAnalysisUIRefLinks {
    /**
     * The builder.
     */
    private final ThemisXAnalysisUIDocBuilder theBuilder;

    /**
     * Constructor.
     *
     * @param pBuilder the builder
     */
    ThemisXAnalysisUIRefLinks(final ThemisXAnalysisUIDocBuilder pBuilder) {
        theBuilder = pBuilder;
    }

    /**
     * Create document for package links.
     *
     * @param pSource the source package
     * @param pTarget the target package
     * @param pTable  the table
     */
    void formatLinks(final ThemisXAnalysisSolverPackage pSource,
                     final ThemisXAnalysisSolverPackage pTarget,
                     final Element pTable) {
        /* Access link map */
        final ThemisXAnalysisSolverReference myMap = pSource.getReferenceMap();
        final ThemisXAnalysisSolverRefPackage myLinkMap = myMap.getReferences(pTarget);
        /* If we have links */
        if (myLinkMap != null) {
            /* Loop through the references */
            for (ThemisXAnalysisSolverRefClass myClass : myLinkMap.getReferences()) {
                /* Add link element */
            }
        }
    }
}
