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
import java.util.Map;

/**
 * Switch construct.
 */
public class ThemisAnalysisSwitch
        implements ThemisAnalysisContainer {
    /**
     * The parent.
     */
    private final ThemisAnalysisContainer theParent;

    /**
     * The headers.
     */
    private final List<ThemisAnalysisElement> theHeaders;

    /**
     * The contents.
     */
    private final List<ThemisAnalysisElement> theContents;

    /**
     * The dataTypes.
     */
    private final Map<String, ThemisAnalysisDataType> theDataTypes;

    /**
     * The number of lines.
     */
    private final int theNumLines;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial switch line
     */
    ThemisAnalysisSwitch(final ThemisAnalysisParser pParser,
                         final ThemisAnalysisLine pLine) {
        /* Access details from parser */
        theDataTypes = pParser.getDataTypes();
        theParent = pParser.getParent();

        /* Create the arrays */
        theHeaders = ThemisAnalysisBuilder.processHeaders(pParser, pLine);
        final List<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);
        final int myBaseLines = myLines.size();

        /* Create a parser */
        theContents = new ArrayList<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theContents, theParent);
        processLines(myParser);

        /* Calculate the number of lines */
        theNumLines = calculateNumLines(myBaseLines);
    }

    /**
     * process the lines.
     * @param pParser the parser
     */
    void processLines(final ThemisAnalysisParser pParser) {
        /* Loop through the lines */
        while (pParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) pParser.popNextLine();

            /* Process comments and blanks */
            boolean processed = pParser.processCommentsAndBlanks(myLine);

            /* Process default/case statements */
            if (!processed) {
                processed = pParser.processCase(myLine);
            }

            /* If we haven't processed yet */
            if (!processed) {
                /* We should never reach here */
                throw new IllegalStateException("Unexpected code in switch");
            }
        }
    }

    @Override
    public Map<String, ThemisAnalysisDataType> getDataTypes() {
        return theDataTypes;
    }

    @Override
    public List<ThemisAnalysisElement> getContents() {
        return theContents;
    }

    @Override
    public ThemisAnalysisContainer getParent() {
        return theParent;
    }

    @Override
    public int getNumLines() {
        return theNumLines;
    }

    /**
     * Calculate the number of lines for the construct.
     * @param pBaseCount the baseCount
     * @return the number of lines
     */
    public int calculateNumLines(final int pBaseCount) {
        /* Add 1+ line(s) for the switch headers  */
        final int myNumLines = pBaseCount + Math.max(theHeaders.size() - 1, 1);

        /* Add one for the clause terminator */
        return myNumLines + 1;
    }
}
