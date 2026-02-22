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

package io.github.tonywasher.joceanus.themis.xanalysis.solver.proj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class holding references to other packages.
 */
public class ThemisXAnalysisSolverReference {
    /**
     * Map of references to other packages.
     */
    private final Map<String, ThemisXAnalysisSolverRefPackage> theMap;

    /**
     * Constructor.
     */
    public ThemisXAnalysisSolverReference() {
        theMap = new HashMap<>();
    }

    /**
     * Obtain the references for a package.
     *
     * @param pPackage the package name
     * @return the references
     */
    public ThemisXAnalysisSolverRefPackage getReferences(final String pPackage) {
        return theMap.get(pPackage);
    }

    /**
     * Add references to map.
     *
     * @param pReferences the references
     */
    public void addReferences(final ThemisXAnalysisSolverRefPackage pReferences) {
        final String myPackage = pReferences.getPackage();
        theMap.put(myPackage, pReferences);
    }

    /**
     * Class representing links from a class to classes in a particular package.
     */
    public static class ThemisXAnalysisSolverRefPackage {
        /**
         * The package that is referred to.
         */
        private final String thePackage;

        /**
         * The referenceType.
         */
        private final ThemisXAnalysisRefType theRefType;

        /**
         * The list of classes that refer to the package.
         */
        private final List<ThemisXAnalysisSolverRefClass> theReferences;

        /**
         * Constructor.
         *
         * @param pPackage the package
         * @param pRefType the reference type
         */
        public ThemisXAnalysisSolverRefPackage(final String pPackage,
                                               final ThemisXAnalysisRefType pRefType) {
            thePackage = pPackage;
            theRefType = pRefType;
            theReferences = new ArrayList<>();
        }

        /**
         * Obtain the class.
         *
         * @return the class
         */
        public String getPackage() {
            return thePackage;
        }

        /**
         * Obtain the referenceType.
         *
         * @return the refType
         */
        public ThemisXAnalysisRefType getReferenceType() {
            return theRefType;
        }

        /**
         * Obtain the references.
         *
         * @return the references
         */
        public List<ThemisXAnalysisSolverRefClass> getReferences() {
            return theReferences;
        }

        /**
         * Add class that has references.
         *
         * @param pReferences the references
         */
        public void addReferences(final ThemisXAnalysisSolverRefClass pReferences) {
            theReferences.add(pReferences);
        }

        @Override
        public String toString() {
            return thePackage;
        }
    }

    /**
     * Class representing links from a class to classes in a particular package.
     */
    public static class ThemisXAnalysisSolverRefClass {
        /**
         * The class that holds the references.
         */
        private final ThemisXAnalysisSolverClass theClass;

        /**
         * The references.
         */
        private final List<ThemisXAnalysisSolverClass> theReferences;

        /**
         * Constructor.
         *
         * @param pClass      the class
         * @param pReferences the references
         */
        public ThemisXAnalysisSolverRefClass(final ThemisXAnalysisSolverClass pClass,
                                             final List<ThemisXAnalysisSolverClass> pReferences) {
            theClass = pClass;
            theReferences = pReferences;
        }

        /**
         * Obtain the class.
         *
         * @return the class
         */
        public ThemisXAnalysisSolverClass getSubject() {
            return theClass;
        }

        /**
         * Obtain the references.
         *
         * @return the references
         */
        public List<ThemisXAnalysisSolverClass> getReferences() {
            return theReferences;
        }

        @Override
        public String toString() {
            return theClass.getFullName();
        }
    }


    /**
     * Map types.
     */
    public enum ThemisXAnalysisRefType {
        /**
         * Sibling.
         */
        SIBLING,

        /**
         * Child.
         */
        CHILD,

        /**
         * Parent.
         */
        PARENT;
    }
}
