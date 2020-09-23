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
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisStatement.ThemisAnalysisStatementHolder;

/**
 * If construct.
 */
public class ThemisAnalysisIf
        implements ThemisAnalysisContainer, ThemisAnalysisAdoptable, ThemisAnalysisStatementHolder {
    /**
     * The condition.
     */
    private final ThemisAnalysisStatement theCondition;

    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * The else clause.
     */
    private final ThemisAnalysisElse theElse;

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
     * @param pLine the initial if line
     * @throws OceanusException on error
     */
    ThemisAnalysisIf(final ThemisAnalysisParser pParser,
                     final ThemisAnalysisLine pLine) throws OceanusException {
        /* Access details from parser */
        theParent = pParser.getParent();

        /* Parse the condition */
        final Deque<ThemisAnalysisElement> myHeaders = ThemisAnalysisBuilder.parseHeaders(pParser, pLine);
        theCondition = new ThemisAnalysisStatement(myHeaders);

        /* Parse the body */
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);
        final int myBaseLines = myLines.size();

        /* Look for else clauses */
        theElse = (ThemisAnalysisElse) pParser.processExtra(this, ThemisAnalysisKeyWord.ELSE);

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
    public Iterator<ThemisAnalysisStatement> statementIterator() {
        final Iterator<ThemisAnalysisStatement> myLocal = Collections.singleton(theCondition).iterator();
        return theElse == null ? myLocal : new ThemisIteratorChain<>(myLocal, theElse.statementIterator());
    }

    @Override
    public ThemisAnalysisContainer getParent() {
        return theParent;
    }

    @Override
    public void setParent(final ThemisAnalysisContainer pParent) {
        theParent = pParent;
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
    public Iterator<ThemisAnalysisContainer> containerIterator() {
        return theElse == null
                ? Collections.emptyIterator()
                : theElse.containerIterator();
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
        int myNumLines = pBaseCount + Math.max(theCondition.getNumLines() - 1, 1);

        /* Add lines for additional else clauses */
        if (theElse != null) {
            myNumLines += theElse.getNumLines();
        }

        /* Add one for the clause terminator */
        return myNumLines + 1;
    }

    @Override
    public String toString() {
        return theCondition.toString();
    }

    /**
     * Chained Iterator.
     * @param <T> the item class
     */
    static class ThemisIteratorChain<T> implements Iterator<T> {
        /**
         * Local Iterator.
         */
        private final Iterator<T> theLocal;

        /**
         * Chained Iterator.
         */
        private final Iterator<T> theChained;

        /**
         * Constructor.
         * @param pLocal the local iterator
         * @param pChained the chained iterator
         */
        ThemisIteratorChain(final Iterator<T> pLocal,
                            final Iterator<T> pChained) {
            theLocal = pLocal;
            theChained = pChained;
        }

        @Override
        public boolean hasNext() {
            return theLocal.hasNext() || theChained.hasNext();
        }

        @Override
        public T next() {
            return theLocal.hasNext() ? theLocal.next() : theChained.next();
        }
    }
}
