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
package net.sourceforge.joceanus.themis.xanalysis.dsm;

import net.sourceforge.joceanus.themis.xanalysis.proj.ThemisXAnalysisFile;
import net.sourceforge.joceanus.themis.xanalysis.proj.ThemisXAnalysisPackage;

import java.util.HashMap;
import java.util.Map;

/**
 * DSM Package.
 */
public class ThemisXAnalysisDSMPackage {
    /**
     * The underlying package.
     */
    private final ThemisXAnalysisPackage thePackage;

    /**
     * The class map.
     */
    private final Map<String, ThemisXAnalysisDSMClass> theClasses;

    /**
     * Constructor.
     * @param pPackage the parsed package
     */
    public ThemisXAnalysisDSMPackage(final ThemisXAnalysisPackage pPackage) {
        /* Store the package */
        thePackage = pPackage;

        /* Create the Map */
        theClasses = new HashMap<>();

        /* Populate the classMap */
        for (ThemisXAnalysisFile myFile : thePackage.getFiles()) {
            theClasses.put(myFile.getName(), new ThemisXAnalysisDSMClass(myFile));
        }
    }


    /**
     * Obtain the package.
     * @return the package
     */
    public ThemisXAnalysisPackage getPackage() {
        return thePackage;
    }

    /**
     * Obtain the class map.
     * @return the class map
     */
    Map<String, ThemisXAnalysisDSMClass> getClassMap() {
        return theClasses;
    }
}
