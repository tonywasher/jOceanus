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
import net.sourceforge.joceanus.themis.exc.ThemisDataException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisChar;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeImport;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeName;

import java.util.HashMap;
import java.util.Map;

/**
 * Package Cache.
 */
public class ThemisXAnalysisDSMParser {
    /**
     * The package map.
     */
    private final Map<String, Map<String, ThemisXAnalysisDSMClass>> thePackageMap;

    /**
     * Constructor.
     */
    ThemisXAnalysisDSMParser() {
        thePackageMap = new HashMap<>();
    }

    /**
     * Declare package.
     * @param pPackage the package
     * @param pClassMap the classMap
     */
    void declarePackage(final String pPackage,
                        final Map<String, ThemisXAnalysisDSMClass> pClassMap) {
        thePackageMap.put(pPackage, pClassMap);
    }

    /**
     * Process packages.
     * @throws OceanusException on error
     */
    void processPackages() throws OceanusException {
        for (String myPackage : thePackageMap.keySet()) {
            processPackage(myPackage);
        }
    }

    /**
     * Process package.
     * @param pPackage the package
     * @throws OceanusException on error
     */
    private void processPackage(final String pPackage) throws OceanusException {
        final Map<String, ThemisXAnalysisDSMClass> myClassMap = thePackageMap.get(pPackage);
        for (ThemisXAnalysisDSMClass myClass : myClassMap.values()) {
            determineKnownClasses(myClass, myClassMap);
        }
    }

    /**
     * determineKnownClasses.
     * @param pClass the class.
     * @param pClassMap the class map for this package
     * @throws OceanusException on error
     */
    private void determineKnownClasses(final ThemisXAnalysisDSMClass pClass,
                                       final Map<String, ThemisXAnalysisDSMClass> pClassMap) throws OceanusException {
        /* Declare all the classes in the package as known classes */
        for (Map.Entry<String, ThemisXAnalysisDSMClass> myEntry : pClassMap.entrySet()) {
            pClass.declareKnownClass(myEntry.getKey(), myEntry.getValue());
        }

        /* Process the imports */
        for (ThemisXAnalysisNodeInstance myNode : pClass.getFile().getContents().getImports()) {
            /* Parse the import definition */
            final ImportDefinition myImport = new ImportDefinition((ThemisXAnalysisNodeImport) myNode);

            /* look up the class in the packageMap */
            lookupClass(pClass, myImport);
        }
    }

    /**
     * lookUp a standard package import.
     * @param pClass the class.
     * @param pImport the import
     * @throws OceanusException on error
      */
    private void lookupClass(final ThemisXAnalysisDSMClass pClass,
                             final ImportDefinition pImport) throws OceanusException {
        /* If we know about the package */
        final Map<String, ThemisXAnalysisDSMClass> myMap = thePackageMap.get(pImport.thePackage);
        if (myMap != null) {
            /* If we know about the class */
            final ThemisXAnalysisDSMClass myClass = myMap.get(pImport.theName);
            if (myClass != null) {
                /* Declare the class and return success */
                pClass.declareKnownClass(pImport.theName, myClass);
                return;
            } else {
                /* Else we have referenced a non-existing class in the package */
                throw new ThemisDataException(pImport, "Reference to non-existent class");
            }
        }

        /* Look for child class */
        lookUpChildClass(pClass, pImport);
    }

    /**
     * lookUp a child import.
     * @param pClass the class.
     * @param pImport the import
     */
    private void lookUpChildClass(final ThemisXAnalysisDSMClass pClass,
                                  final ImportDefinition pImport) {
        /* Loop through all the packages */
        for (Map.Entry<String, Map<String, ThemisXAnalysisDSMClass>> myPackageMap : thePackageMap.entrySet()) {
            /* Skip package if it cannot be relevant to the import */
            if (!pImport.thePackage.startsWith(myPackageMap.getKey() + ThemisXAnalysisChar.PERIOD)) {
                continue;
            }

            /* Loop through the classes in the package */
            for (Map.Entry<String, ThemisXAnalysisDSMClass> myMap : myPackageMap.getValue().entrySet()) {
                final String myTestName  = myPackageMap.getKey() + ThemisXAnalysisChar.PERIOD + myMap.getKey();
                /* If the import is for a top-level child of this class */
                if (myTestName.equals(pImport.thePackage)) {
                    /* Declare the class and return success */
                    pClass.declareKnownClass(pImport.theName, myMap.getValue());
                    return;

                    /* else if the import is for a grandchild or further */
                } else if (pImport.thePackage.startsWith(myTestName + ThemisXAnalysisChar.PERIOD)) {
                    /* Declare the class and return success */
                    pClass.declareKnownClass(pImport.theName, myMap.getValue());
                    return;
                }
            }
        }
    }

    /**
     * Import definition class.
     */
    private static class ImportDefinition {
        /**
         * The name.
         */
        private final String theName;

        /**
         * The package .
         */
        private final String thePackage;

        /**
         * Constructor.
         * @param pImport the import definition
         */
        ImportDefinition(final ThemisXAnalysisNodeImport pImport) {
            final ThemisXAnalysisNodeName myName = (ThemisXAnalysisNodeName) pImport.getImport();
            theName = myName.getName();
            final ThemisXAnalysisNodeName myQualifier = ((ThemisXAnalysisNodeName) myName.getQualifier());
            thePackage = myQualifier.getNode().asString();
        }
    }
}
