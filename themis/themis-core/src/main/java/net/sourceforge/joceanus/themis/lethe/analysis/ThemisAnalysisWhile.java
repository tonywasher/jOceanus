/*
 * Themis: Java Project Framework
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.joceanus.themis.lethe.analysis;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisContainer.ThemisAnalysisAdoptable;
import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisStatement.ThemisAnalysisStatementHolder;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;

/**
 * While construct.
 */
public class ThemisAnalysisWhile
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
     *
     * @param pParser the parser
     * @param pLine   the initial while line
     * @throws OceanusException on error
     */
    ThemisAnalysisWhile(final ThemisAnalysisParser pParser,
                        final ThemisAnalysisLine pLine) throws OceanusException {
        /* Access details from parser */
        theParent = pParser.getParent();
        theDataMap = new ThemisAnalysisDataMap(theParent.getDataMap());

        /* Parse the condition */
        final Deque<ThemisAnalysisElement> myHeaders = ThemisAnalysisBuilder.parseHeaders(pParser, pLine);
        theCondition = new ThemisAnalysisStatement(myHeaders);
        theNumLines = myHeaders.size() + 1;

        /* Parse the body */
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);

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
        return Collections.singleton(theCondition).iterator();
    }

    @Override
    public ThemisAnalysisContainer getParent() {
        return theParent;
    }

    @Override
    public void setParent(final ThemisAnalysisContainer pParent) {
        theParent = pParent;
        theDataMap.setParent(pParent.getDataMap());
    }

    @Override
    public ThemisAnalysisDataMap getDataMap() {
        return theDataMap;
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
