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

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Finally construct.
 */
public class ThemisAnalysisFinally
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
     * The number of lines.
     */
    private final int theNumLines;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pOwner the owning try
     * @param pLine the initial finally line
     * @throws OceanusException on error
     */
    ThemisAnalysisFinally(final ThemisAnalysisParser pParser,
                          final ThemisAnalysisContainer pOwner,
                          final ThemisAnalysisLine pLine) throws OceanusException {
        /* Record the parent */
        theParent = pOwner;

        /* Create the arrays */
        theHeaders = ThemisAnalysisBuilder.parseHeaders(pParser, pLine);
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);
        theNumLines = theHeaders.size();

        /* Create a parser */
        theContents = new ArrayDeque<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theContents, theParent);
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
    public int getNumLines() {
        return theNumLines;
    }
}
