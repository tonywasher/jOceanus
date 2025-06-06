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

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.proj.ThemisXAnalysisModule;
import net.sourceforge.joceanus.themis.xanalysis.proj.ThemisXAnalysisPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * DSM Module.
 */
public class ThemisXAnalysisDSMModule {
    /**
     * The underlying module.
     */
    private final ThemisXAnalysisModule theModule;

    /**
     * The list of packages.
     */
    private final List<ThemisXAnalysisDSMPackage> thePackages;

    /**
     * The parser.
     */
    private final ThemisXAnalysisDSMParser theParser;

    /**
     * Constructor.
     * @param pModule the parsed module
     * @throws OceanusException on error
     */
    ThemisXAnalysisDSMModule(final ThemisXAnalysisModule pModule) throws OceanusException {
        /* Store the parameters */
        theModule = pModule;

        /* Create the list and map */
        thePackages = new ArrayList<>();
        theParser = new ThemisXAnalysisDSMParser();

        /* Initialise the packages */
        for (ThemisXAnalysisPackage myPackage : theModule.getPackages()) {
            final ThemisXAnalysisDSMPackage myDSMPackage = new ThemisXAnalysisDSMPackage(myPackage);
            thePackages.add(myDSMPackage);
            theParser.declarePackage(myPackage.getPackage(), myDSMPackage.getClassMap());
        }

        /* Process the packages */
        theParser.processPackages();
    }

    /**
     * Obtain the module.
     * @return the module
     */
    public ThemisXAnalysisModule getModule() {
        return theModule;
    }

    /**
     * Obtain the packages.
     * @return the packages
     */
    public List<ThemisXAnalysisDSMPackage> getPackages() {
        return thePackages;
    }

    @Override
    public String toString() {
        return theModule.toString();
    }
}
