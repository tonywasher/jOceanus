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

import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeCompilationUnit;
import net.sourceforge.joceanus.themis.xanalysis.proj.ThemisXAnalysisFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DSM Class.
 */
public class ThemisXAnalysisDSMClass {
    /**
     * The fully qualified name of the class.
     */
    private final String theFullName;

    /**
     * The package containing the class.
     */
    private final String thePackage;

    /**
     * The underlying file.
     */
    private final ThemisXAnalysisFile theFile;

    /**
     * The contained class.
     */
    private final ThemisXAnalysisClassInstance theClass;

    /**
     * The class state.
     */
    private final ThemisXAnalysisDSMClassState theState;

    /**
     * Constructor.
     * @param pPackage the package
     * @param pFile the parsed file
     */
    ThemisXAnalysisDSMClass(final String pPackage,
                            final ThemisXAnalysisFile pFile) {
        /* Store the parameters */
        thePackage = pPackage;
        theFile = pFile;

        /* Create the state */
        theState = new ThemisXAnalysisDSMClassState(this);

        /* Access the class definition */
        final ThemisXAnalysisNodeCompilationUnit myUnit = pFile.getContents();
        theClass = myUnit.getContents();
        theFullName = theClass.getFullName();
    }

    /**
     * Obtain the fullName.
     * @return the fullName
     */
    public String getFullName() {
        return theFullName;
    }

    /**
     * Obtain the package.
     * @return the package
     */
    public String getPackage() {
        return thePackage;
    }

    /**
     * Obtain the file.
     * @return the file
     */
    public ThemisXAnalysisFile getFile() {
        return theFile;
    }

    /**
     * Obtain the parsed class.
     * @return the class
     */
    public ThemisXAnalysisClassInstance getParsedClass() {
        return (ThemisXAnalysisClassInstance) theClass;
    }

    /**
     * Obtain the state.
     * @return the state
     */
    public ThemisXAnalysisDSMClassState getState() {
        return theState;
    }

    /**
     * Obtain the list of classes that reference the named package.
     * @param pPackage the package
     * @return the list of referencing classes
     */
    public List<ThemisXAnalysisDSMClass> getReferencesTo(final String pPackage) {
        /* Loop through the classes */
        final List<ThemisXAnalysisDSMClass> myReferences = new ArrayList<>();
        for (ThemisXAnalysisDSMClass myReference : theState.getReferencedClasses()) {
            if (pPackage.equals(myReference.getPackage())) {
                myReferences.add(myReference);
            }
        }
        return myReferences;
    }

    /**
     * Obtain the list of local classes that are referenced.
     * @return the list of referencing classes
     */
    private List<ThemisXAnalysisDSMClass> getLocalReferences() {
        return getReferencesTo(thePackage);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a DSMClass */
        if (!(pThat instanceof ThemisXAnalysisDSMClass myThat)) {
            return false;
        }

        /* Check full name */
        return theFullName.equals(myThat.getFullName());
    }

    @Override
    public int hashCode() {
        return theFullName.hashCode();
    }

    @Override
    public String toString() {
        return theFullName;
    }

    /**
     * Class state.
     */
    public static final class ThemisXAnalysisDSMClassState {
        /**
         * The class.
         */
        private final ThemisXAnalysisDSMClass theClass;

        /**
         * The known classes.
         */
        private final Map<String, ThemisXAnalysisDSMClass> theKnownClasses;

        /**
         * The fully referenced local classes.
         */
        private final List<ThemisXAnalysisDSMClass> theLocalReferences;

        /**
         * The referenced classes.
         */
        private final List<ThemisXAnalysisDSMClass> theReferencedClasses;

        /**
         * Constructor.
         * @param pClass the class
         */
        private ThemisXAnalysisDSMClassState(final ThemisXAnalysisDSMClass pClass) {
            theClass = pClass;
            theKnownClasses = new LinkedHashMap<>();
            theLocalReferences = new ArrayList<>();
            theReferencedClasses = new ArrayList<>();
        }

        /**
         * Obtain the referenced classes.
         * @return the referenced classes
         */
        public List<ThemisXAnalysisDSMClass> getReferencedClasses() {
            return theReferencedClasses;
        }

        /**
         * Declare class that is same package.
         * @param pName the name of the class.
         * @param pClass the class
         */
        void declarePackageClass(final String pName,
                                 final ThemisXAnalysisDSMClass pClass) {
            if (!pClass.equals(theClass)) {
                theKnownClasses.put(pName, pClass);
            }
        }

        /**
         * process possible reference.
         * @param pReference the possible reference.
         * @return the resolved class (if found)
         */
        ThemisXAnalysisDSMClass processPossibleReference(final String pReference) {
            /* If the reference is interesting */
            final ThemisXAnalysisDSMClass myReference = theKnownClasses.get(pReference);
            if (myReference != null) {
                declareReferencedClass(myReference);
            }
            return myReference;
        }

        /**
         * Declare imported class.
         * @param pName the name of the class.
         * @param pClass the class
         */
        void declareImportedClass(final String pName,
                                  final ThemisXAnalysisDSMClass pClass) {
            if (!pClass.equals(theClass)) {
                theKnownClasses.put(pName, pClass);
                declareReferencedClass(pClass);
            }
        }

        /**
         * Declare referenced class.
         * @param pClass the class
         */
        void declareReferencedClass(final ThemisXAnalysisDSMClass pClass) {
            /* If this is the first instance of the reference */
            if (!theReferencedClasses.contains(pClass)) {
                /* Add to the list of referenced classes */
                theReferencedClasses.add(pClass);
            }
        }

        /**
         * process local references.
         */
        void processLocalReferences() {
            /* Loop through the referenced local classes */
            for (ThemisXAnalysisDSMClass myClass : theClass.getLocalReferences()) {
                /* Process the class */
                processLocalReferences(myClass);
            }
        }

        /**
         * process local references.
         * @param pClass the class to process local references for
         */
        private void processLocalReferences(final ThemisXAnalysisDSMClass pClass) {
            /* If this is not already in the local reference list */
            if (!theLocalReferences.contains(pClass)) {
                /* Add the class */
                theLocalReferences.add(pClass);

                /* Only process further if we have not found circularity */
                if (!pClass.equals(theClass)) {
                    /* Loop through the local references */
                    for (ThemisXAnalysisDSMClass myClass : pClass.getLocalReferences()) {
                        /* Process the local references */
                        processLocalReferences(myClass);
                    }
                }
            }
        }

        /**
         * is this class circularly dependent?
         * @return true/false
         */
        public boolean isCircular() {
            /* Do we end up referencing ourselves? */
            return theLocalReferences.contains(theClass);
        }
    }
}
