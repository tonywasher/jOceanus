/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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

import java.util.Collections;
import java.util.Iterator;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisStatement.ThemisAnalysisStatementHolder;

/**
 * Field Representation.
 */
public class ThemisAnalysisField
    implements ThemisAnalysisProcessed, ThemisAnalysisStatementHolder {
    /**
     * The name of the class.
     */
    private final String theName;

    /**
     * The dataType of the field.
     */
    private final ThemisAnalysisReference theDataType;

    /**
     * The properties.
     */
    private final ThemisAnalysisProperties theProperties;

    /**
     * The initial value.
     */
    private final ThemisAnalysisStatement theInitial;

    /**
     * The number of lines.
     */
    private final int theNumLines;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pName the method name
     * @param pDataType the dataType
     * @param pLine the initial class line
     * @throws OceanusException on error
     */
    ThemisAnalysisField(final ThemisAnalysisParser pParser,
                        final String pName,
                        final ThemisAnalysisReference pDataType,
                        final ThemisAnalysisLine pLine) throws OceanusException {
        /* Store parameters */
        theName = pName;
        theDataType = pDataType;
        theProperties = pLine.getProperties();

        /* If we have no initial value */
        if (pLine.startsWithChar(ThemisAnalysisChar.SEMICOLON)) {
            /* Set default values */
            theNumLines = 1;
            theInitial = null;

            /* else we have an initialiser */
        } else {
            /* Strip the equals sign */
            pLine.stripStartChar(ThemisAnalysisChar.EQUALS);

            /* Declare as statement */
            theInitial = new ThemisAnalysisStatement(pParser, pLine);
            theNumLines = theInitial.getNumLines();
        }
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    @Override
    public Iterator<ThemisAnalysisStatement> iterator() {
        return Collections.singleton(theInitial).iterator();
    }

    @Override
    public int getNumLines() {
        return theNumLines;
    }

    @Override
    public String toString() {
        return theDataType.toString() + " " + getName();
    }
}
