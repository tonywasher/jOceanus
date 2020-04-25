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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/**
 * Else construct.
 */
public class ThemisAnalysisElse
        implements ThemisAnalysisContainer {
    /**
     * The parent.
     */
    private final ThemisAnalysisContainer theParent;

    /**
     * The headers.
     */
    private final Deque<ThemisAnalysisElement> theHeaders;

    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * The else clause(s).
     */
    private final ThemisAnalysisElse theElse;

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
     * @param pLine the initial else line
     */
    ThemisAnalysisElse(final ThemisAnalysisParser pParser,
                       final ThemisAnalysisLine pLine) {
        /* Access details from parser */
        theDataTypes = pParser.getDataTypes();
        theParent = pParser.getParent();

        /* Create the arrays */
        theHeaders = ThemisAnalysisBuilder.parseHeaders(pParser, pLine);
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);
        final int myBaseLines = myLines.size();

        /* Look for else clauses */
        theElse = (ThemisAnalysisElse) pParser.processExtra(ThemisAnalysisKeyWord.ELSE);

        /* Create a parser */
        theContents = new ArrayDeque<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theContents, theParent);
        myParser.processLines();

        /* Calculate the number of lines */
        theNumLines = calculateNumLines(myBaseLines);
    }

    @Override
    public Map<String, ThemisAnalysisDataType> getDataTypes() {
        return theDataTypes;
    }

    @Override
    public Deque<ThemisAnalysisElement> getContents() {
        return theContents;
    }

    @Override
    public ThemisAnalysisContainer getParent() {
        return theParent;
    }

    @Override
    public void postProcessExtras() {
        /* Process the else clause if required */
        if (theElse != null) {
            theElse.postProcessLines();
        }
    }

    /**
     * Obtain the additional else clause (if any).
     * @return the else clause
     */
    public ThemisAnalysisElse getElse() {
        return theElse;
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
        /* Add 1+ line(s) for the else headers  */
        int myNumLines = pBaseCount + Math.max(theHeaders.size() - 1, 1);

        /* Add lines for additional else clauses */
        if (theElse != null) {
            myNumLines += theElse.getNumLines();
        }

        /* Return the lines */
        return myNumLines;
    }
}
