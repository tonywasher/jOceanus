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
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisContainer.ThemisAnalysisAdoptable;

/**
 * Catch construct.
 */
public class ThemisAnalysisCatch
        implements ThemisAnalysisContainer, ThemisAnalysisAdoptable {
    /**
     * The parent.
     */
    private ThemisAnalysisContainer theParent;

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
     * The number of lines.
     */
    private final int theNumLines;

    /**
     * The dataMap.
     */
    private final ThemisAnalysisDataMap theDataMap;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pOwner the owning try
     * @param pLine the initial catch line
     * @throws OceanusException on error
     */
    ThemisAnalysisCatch(final ThemisAnalysisParser pParser,
                        final ThemisAnalysisContainer pOwner,
                        final ThemisAnalysisLine pLine) throws OceanusException {
        /* Record the parent */
        theParent = pOwner;
        theDataMap = new ThemisAnalysisDataMap(theParent.getDataMap());

        /* Create the arrays */
        theHeaders = ThemisAnalysisBuilder.parseHeaders(pParser, pLine);
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);
        theNumLines = theHeaders.size();

        /* Look for catch clauses */
        theCatch = (ThemisAnalysisCatch) pParser.processExtra(pOwner, ThemisAnalysisKeyWord.CATCH);

        /* Create a parser */
        theContents = new ArrayDeque<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theContents, this);
        myParser.processLines();
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
    public void setParent(final ThemisAnalysisContainer pParent) {
        theParent = pParent;
        theDataMap.setParent(pParent.getDataMap());
        if (theCatch != null) {
            theCatch.setParent(pParent);
        }
    }

    @Override
    public ThemisAnalysisDataMap getDataMap() {
        return theDataMap;
    }

    @Override
    public void postProcessExtras() throws OceanusException {
        /* Process the catch clause if required */
        if (theCatch != null) {
            theCatch.postProcessLines();
        }
    }

    /**
     * Obtain the additional catch clause (if any).
     * @return the catch clause
     */
    public ThemisAnalysisCatch getCatch() {
        return theCatch;
    }

    @Override
    public Iterator<ThemisAnalysisContainer> containerIterator() {
        return theCatch == null
                ? Collections.emptyIterator()
                : Collections.singleton((ThemisAnalysisContainer) theCatch).iterator();
    }

    @Override
    public int getNumLines() {
        return theNumLines;
    }

    @Override
    public String toString() {
        return ThemisAnalysisBuilder.formatLines(theHeaders);
    }
}
