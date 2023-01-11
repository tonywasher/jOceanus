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

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisContainer.ThemisAnalysisAdoptable;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisIf.ThemisIteratorChain;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisStatement.ThemisAnalysisStatementHolder;

/**
 * Else construct.
 */
public class ThemisAnalysisElse
        implements ThemisAnalysisContainer, ThemisAnalysisAdoptable, ThemisAnalysisStatementHolder {
    /**
     * The parent.
     */
    private ThemisAnalysisContainer theParent;

    /**
     * The condition.
     */
    private final ThemisAnalysisStatement theCondition;

    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * The else clause(s).
     */
    private final ThemisAnalysisElse theElse;

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
     * @param pOwner the owning if
     * @param pLine the initial else line
     * @throws OceanusException on error
     */
    ThemisAnalysisElse(final ThemisAnalysisParser pParser,
                       final ThemisAnalysisContainer pOwner,
                       final ThemisAnalysisLine pLine) throws OceanusException {
        /* Record the parent */
        theParent = pOwner;
        theDataMap = new ThemisAnalysisDataMap(theParent.getDataMap());

        /* Strip any if token */
        pLine.stripStartSequence(ThemisAnalysisKeyWord.IF.getKeyWord());

        /* Parse the condition */
        final Deque<ThemisAnalysisElement> myHeaders = ThemisAnalysisBuilder.parseHeaders(pParser, pLine);
        theNumLines = myHeaders.size() + 1;
        theCondition = new ThemisAnalysisStatement(myHeaders);

        /* Parse the body */
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);

        /* Look for else clauses */
        theElse = (ThemisAnalysisElse) pParser.processExtra(pOwner, ThemisAnalysisKeyWord.ELSE);

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
    public Iterator<ThemisAnalysisStatement> statementIterator() {
        final Iterator<ThemisAnalysisStatement> myLocal = theCondition.nullParameters()
                ? Collections.emptyIterator()
                : Collections.singleton(theCondition).iterator();
        return theElse == null ? myLocal : new ThemisIteratorChain<>(myLocal, theElse.statementIterator());
    }

    @Override
    public Iterator<ThemisAnalysisContainer> containerIterator() {
        return theElse == null
                ? Collections.emptyIterator()
                : Collections.singleton((ThemisAnalysisContainer) theElse).iterator();
    }

    @Override
    public ThemisAnalysisContainer getParent() {
        return theParent;
    }

    @Override
    public void setParent(final ThemisAnalysisContainer pParent) {
        theParent = pParent;
        theDataMap.setParent(pParent.getDataMap());
        if (theElse != null) {
            theElse.setParent(pParent);
        }
    }

    @Override
    public ThemisAnalysisDataMap getDataMap() {
        return theDataMap;
    }

    @Override
    public void postProcessExtras() throws OceanusException {
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

    @Override
    public String toString() {
        return theCondition.toString();
    }
}
