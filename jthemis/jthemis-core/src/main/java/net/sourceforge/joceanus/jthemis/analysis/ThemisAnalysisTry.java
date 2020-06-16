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

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisContainer.ThemisAnalysisAdoptable;

/**
 * Try construct.
 */
public class ThemisAnalysisTry
        implements ThemisAnalysisContainer, ThemisAnalysisAdoptable {
    /**
     * The headers.
     */
    private final Deque<ThemisAnalysisElement> theHeaders;

    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * The catch clause(s).
     */
    private final ThemisAnalysisCatch theCatch;

    /**
     * The finally clause.
     */
    private final ThemisAnalysisFinally theFinally;

    /**
     * The number of lines.
     */
    private final int theNumLines;

    /**
     * The parent.
     */
    private ThemisAnalysisContainer theParent;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial try line
     */
    ThemisAnalysisTry(final ThemisAnalysisParser pParser,
                      final ThemisAnalysisLine pLine) {
        /* Access details from parser */
        theParent = pParser.getParent();

        /* Create the arrays */
        theHeaders = ThemisAnalysisBuilder.parseHeaders(pParser, pLine);
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);
        final int myBaseLines = myLines.size();

        /* Look for catch clauses */
        theCatch = (ThemisAnalysisCatch) pParser.processExtra(this, ThemisAnalysisKeyWord.CATCH);

        /* Look for finally clauses */
        theFinally = (ThemisAnalysisFinally) pParser.processExtra(this, ThemisAnalysisKeyWord.FINALLY);

        /* Create a parser */
        theContents = new ArrayDeque<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theContents, theParent);
        myParser.processLines();

        /* Calculate the number of lines */
        theNumLines = calculateNumLines(myBaseLines);
    }

    @Override
    public Deque<ThemisAnalysisElement> getContents() {
        return theContents;
    }

    @Override
    public void postProcessExtras() {
        /* Process the catch clause if required */
        if (theCatch != null) {
            theCatch.postProcessLines();
        }

        /* Process the finally clause if required */
        if (theFinally != null) {
            theFinally.postProcessLines();
        }
    }

    @Override
    public ThemisAnalysisContainer getParent() {
        return theParent;
    }

    @Override
    public void setParent(final ThemisAnalysisContainer pParent) {
        theParent = pParent;
    }

    /**
     * Obtain the additional catch clause (if any).
     * @return the catch clause
     */
    public ThemisAnalysisCatch getCatch() {
        return theCatch;
    }

    /**
     * Obtain the additional finally clause (if any).
     * @return the finally clause
     */
    public ThemisAnalysisFinally getFinally() {
        return theFinally;
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
        /* Add 1+ line(s) for the if headers  */
        int myNumLines = pBaseCount + Math.max(theHeaders.size() - 1, 1);

        /* Add lines for additional catch clauses */
        if (theCatch != null) {
            myNumLines += theCatch.getNumLines();
        }

        /* Add lines for additional finally clauses */
        if (theFinally != null) {
            myNumLines += theFinally.getNumLines();
        }

        /* Add one for the clause terminator */
        return myNumLines + 1;
    }
}
