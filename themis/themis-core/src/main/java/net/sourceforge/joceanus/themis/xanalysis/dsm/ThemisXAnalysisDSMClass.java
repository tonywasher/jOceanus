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

import java.util.HashMap;
import java.util.Map;

/**
 * DSM Class.
 */
public class ThemisXAnalysisDSMClass {
    /**
     * The underlying file.
     */
    private final ThemisXAnalysisFile theFile;

    /**
     * The fully qualified name of the class.
     */
    private final String theFullName;

    /**
     * The contained class.
     */
    private final ThemisXAnalysisClassInstance theClass;

    /**
     * The known classes.
     */
    private final Map<String, ThemisXAnalysisDSMClass> theKnownClasses;

    /**
     * Constructor.
     * @param pFile the parsed file
     */
    public ThemisXAnalysisDSMClass(final ThemisXAnalysisFile pFile) {
        /* Store the parameters */
        theFile = pFile;

        /* Create the maps */
        theKnownClasses = new HashMap<>();

        /* Access the class definition */
        final ThemisXAnalysisNodeCompilationUnit myUnit = pFile.getContents();
        theClass = myUnit.getContents();
        theFullName = theClass.getFullName();
    }

    /**
     * Obtain the file.
     * @return the file
     */
    public ThemisXAnalysisFile getFile() {
        return theFile;
    }

    /**
     * Obtain the fullName.
     * @return the fullName
     */
    public String getFullName() {
        return theFullName;
    }

    /**
     * Declare known class.
     * @param pName the name of the class.
     * @param pClass the class
     */
    void declareKnownClass(final String pName,
                           final ThemisXAnalysisDSMClass pClass) {
        theKnownClasses.put(pName, pClass);
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
}
