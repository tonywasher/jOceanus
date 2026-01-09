/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.themis.lethe.analysis;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisContainer.ThemisAnalysisAdoptable;
import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisStatement.ThemisAnalysisStatementHolder;

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
     * The dataMap.
     */
    private final ThemisAnalysisDataMap theDataMap;

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
        theDataMap = new ThemisAnalysisDataMap(theParent.getDataMap());

        /* Parse the condition */
        final Deque<ThemisAnalysisElement> myHeaders = ThemisAnalysisBuilder.parseHeaders(pParser, pLine);
        theNumLines = myHeaders.size() + 1;
        theCondition = new ThemisAnalysisStatement(myHeaders);

        /* Parse the body */
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);

        /* Look for else clauses */
        theElse = (ThemisAnalysisElse) pParser.processExtra(this, ThemisAnalysisKeyWord.ELSE);

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
    public Iterator<ThemisAnalysisContainer> containerIterator() {
        return theElse == null
                ? Collections.emptyIterator()
                : theElse.containerIterator();
    }

    @Override
    public int getNumLines() {
        return theNumLines;
    }

    @Override
    public String toString() {
        return theCondition.toString();
    }

    /**
     * Chained Iterator.
     * @param <T> the item class
     */
    public static class ThemisIteratorChain<T> implements Iterator<T> {
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
        public ThemisIteratorChain(final Iterator<T> pLocal,
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

    /**
     * Reduced Iterator.
     * @param <T> the item class
     */
    public static class ThemisReducedIterator<T> implements Iterator<T> {
        /**
         * Local Iterator.
         */
        private final Iterator<? extends T> theBase;

         /**
         * Constructor.
         * @param pBase the base iterator
         */
        public ThemisReducedIterator(final Iterator<? extends T> pBase) {
            theBase = pBase;
        }

        @Override
        public boolean hasNext() {
            return theBase.hasNext();
        }

        @Override
        public T next() {
            return theBase.next();
        }
    }
}
