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

import java.util.List;

/**
 * Interface representation.
 */
public class ThemisAnalysisInterface
        implements ThemisAnalysisElement {
    /**
     * The name of the class.
     */
    private final String theName;

    /**
     * The modifiers.
     */
    private final List<ThemisAnalysisPrefix> theModifiers;

    /**
     * The headers.
     */
    private final List<ThemisAnalysisLine> theHeaders;

    /**
     * The lines.
     */
    private final List<ThemisAnalysisLine> theLines;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial comment line
     */
    ThemisAnalysisInterface(final ThemisAnalysisParser pParser,
                            final ThemisAnalysisLine pLine) {
        /* Store parameters */
        theName = pLine.stripNextToken();
        theModifiers = pLine.getModifiers();

        /* Create the arrays */
        theHeaders = ThemisAnalysisBody.processHeaders(pParser, pLine);
        theLines = ThemisAnalysisBody.processBody(pParser);
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the number of lines in the class.
     * @return the number of lines
     */
    public int getNumLines() {
        return theLines.size();
    }
}
