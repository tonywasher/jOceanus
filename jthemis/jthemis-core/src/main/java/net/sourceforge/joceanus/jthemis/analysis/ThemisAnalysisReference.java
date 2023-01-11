/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jthemis.analysis;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisDataMap.ThemisAnalysisDataType;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisGeneric.ThemisAnalysisGenericBase;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisGeneric.ThemisAnalysisGenericRef;

/**
 * Reference structure.
 */
public class ThemisAnalysisReference {
    /**
     * The generic.
     */
    private ThemisAnalysisGeneric theGeneric;

    /**
     * The array.
     */
    private final ThemisAnalysisArray theArray;

    /**
     * The dataType.
     */
    private ThemisAnalysisDataType theDataType;

    /**
     * Constructor.
     * @param pDataType the dataType
     * @param pGeneric the generic
     * @param pArray the array
     */
    ThemisAnalysisReference(final ThemisAnalysisDataType pDataType,
                            final ThemisAnalysisGeneric pGeneric,
                            final ThemisAnalysisArray pArray) {
        /* Store parameters */
        theDataType = pDataType;
        theGeneric = pGeneric;
        theArray = pArray;
    }

    /**
     * Obtain the dataType.
     * @return the dataType
     */
    ThemisAnalysisDataType getDataType() {
        return theDataType;
    }

    /**
     * Resolve the generic reference.
     * @param pParser the parser
     * @throws OceanusException on error
     */
    void resolveGeneric(final ThemisAnalysisParser pParser) throws OceanusException  {
        /* Resolve any generic base instance */
        if (theGeneric instanceof ThemisAnalysisGenericBase) {
            theGeneric = new ThemisAnalysisGenericRef(pParser, (ThemisAnalysisGenericBase) theGeneric);
        }
    }

    /**
     * Update the dataType.
     * @param pType the updated type
     */
    void updateDataType(final ThemisAnalysisDataType pType) {
        theDataType = pType;
    }

    @Override
    public String toString() {
        String myName = theDataType.toString();
        if (theGeneric != null) {
            myName += theGeneric.toString();
        }
        if (theArray != null) {
            myName += theArray.toString();
        }
        return myName;
    }
}
