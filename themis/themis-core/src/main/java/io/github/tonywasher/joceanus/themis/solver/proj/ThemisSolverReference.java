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

package io.github.tonywasher.joceanus.themis.solver.proj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class holding references to other packages.
 */
public class ThemisSolverReference {
    /**
     * Map of references to other packages.
     */
    private final Map<ThemisSolverPackage, ThemisSolverRefPackage> theMap;

    /**
     * Constructor.
     */
    public ThemisSolverReference() {
        theMap = new HashMap<>();
    }

    /**
     * Obtain the references for a package.
     *
     * @param pPackage the package
     * @return the references
     */
    public ThemisSolverRefPackage getReferences(final ThemisSolverPackage pPackage) {
        return theMap.get(pPackage);
    }

    /**
     * Obtain the references for a package.
     *
     * @param pPackage the package
     * @return the reference
     */
    public ThemisSolverRefPackage getReferredPackage(final ThemisSolverPackage pPackage) {
        return theMap.get(pPackage);
    }

    /**
     * Add references to map.
     *
     * @param pReferences the references
     */
    public void addReferences(final ThemisSolverRefPackage pReferences) {
        final ThemisSolverPackage myPackage = pReferences.getPackage();
        theMap.put(myPackage, pReferences);
    }

    /**
     * Obtain references of type.
     *
     * @param pRefType the refType
     * @return the references
     */
    public List<ThemisSolverRefPackage> getReferences(final ThemisRefType pRefType) {
        return theMap.values().stream().filter(p -> p.getReferenceType() == pRefType).toList();
    }

    /**
     * Class representing links from a class to classes in a particular package.
     */
    public static class ThemisSolverRefPackage {
        /**
         * The package that is referred to.
         */
        private final ThemisSolverPackage thePackage;

        /**
         * The referenceType.
         */
        private final ThemisRefType theRefType;

        /**
         * The list of classes that refer to the package.
         */
        private final List<ThemisSolverRefClass> theReferences;

        /**
         * Constructor.
         *
         * @param pPackage the package
         * @param pRefType the reference type
         */
        public ThemisSolverRefPackage(final ThemisSolverPackage pPackage,
                                      final ThemisRefType pRefType) {
            thePackage = pPackage;
            theRefType = pRefType;
            theReferences = new ArrayList<>();
        }

        /**
         * Obtain the class.
         *
         * @return the class
         */
        public ThemisSolverPackage getPackage() {
            return thePackage;
        }

        /**
         * Obtain the referenceType.
         *
         * @return the refType
         */
        public ThemisRefType getReferenceType() {
            return theRefType;
        }

        /**
         * Obtain the references.
         *
         * @return the references
         */
        public List<ThemisSolverRefClass> getReferences() {
            return theReferences;
        }

        /**
         * Add class that has references.
         *
         * @param pReferences the references
         */
        public void addReferences(final ThemisSolverRefClass pReferences) {
            theReferences.add(pReferences);
        }

        @Override
        public String toString() {
            return thePackage.toString();
        }
    }

    /**
     * Class representing links from a class to classes in a particular package.
     */
    public static class ThemisSolverRefClass {
        /**
         * The class that holds the references.
         */
        private final ThemisSolverClass theClass;

        /**
         * The references.
         */
        private final List<ThemisSolverClass> theReferences;

        /**
         * Constructor.
         *
         * @param pClass      the class
         * @param pReferences the references
         */
        public ThemisSolverRefClass(final ThemisSolverClass pClass,
                                    final List<ThemisSolverClass> pReferences) {
            theClass = pClass;
            theReferences = pReferences;
        }

        /**
         * Obtain the class.
         *
         * @return the class
         */
        public ThemisSolverClass getSubject() {
            return theClass;
        }

        /**
         * Obtain the references.
         *
         * @return the references
         */
        public List<ThemisSolverClass> getReferences() {
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
    public enum ThemisRefType {
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
