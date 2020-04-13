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

import java.util.ArrayList;
import java.util.List;

/**
 * Switch construct.
 */
public class ThemisAnalysisSwitch
        implements ThemisAnalysisElement {
    /**
     * The headers.
     */
    private final List<ThemisAnalysisLine> theHeaders;

    /**
     * The elements.
     */
    private final List<ThemisAnalysisElement> theProcessed;

    /**
     * The number of lines in the class.
     */
    private final int theNumLines;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial switch line
     */
    ThemisAnalysisSwitch(final ThemisAnalysisParser pParser,
                         final ThemisAnalysisLine pLine) {
        /* Create the arrays */
        theHeaders = ThemisAnalysisBody.processHeaders(pParser, pLine);
        final List<ThemisAnalysisLine> myLines = ThemisAnalysisBody.processBody(pParser);
        theNumLines = myLines.size();

        /* Create a parser */
        theProcessed = new ArrayList<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theProcessed, pParser);
        myParser.postProcessLines();
    }

    /**
     * Obtain the number of lines in the block.
     * @return the number of lines
     */
    public int getNumLines() {
        return theNumLines;
    }
}
