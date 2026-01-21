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

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Temporary Method Body.
 */
public class ThemisAnalysisMethodBody
        implements ThemisAnalysisContainer, ThemisAnalysisAdoptable {
    /**
     * The ArrayInit sequence.
     */
    static final String ARRAYINIT = "= " + ThemisAnalysisChar.BRACE_OPEN;

    /**
     * The Parent.
     */
    private ThemisAnalysisContainer theParent;

    /**
     * The Header.
     */
    private final ThemisAnalysisLine theHeader;

    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @param pLine   the initial class line
     * @throws OceanusException on error
     */
    ThemisAnalysisMethodBody(final ThemisAnalysisParser pParser,
                             final ThemisAnalysisLine pLine) throws OceanusException {
        /* Store parameters */
        theHeader = pLine;

        /* Store parent */
        theParent = pParser.getParent();

        /* Parse the body */
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);

        /* Create a parser */
        theContents = new ArrayDeque<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theContents, this);

        /* Parse the lines */
        myParser.processLines();
    }

    /**
     * Obtain the header.
     *
     * @return the header
     */
    ThemisAnalysisLine getHeader() {
        return theHeader;
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
    }

    @Override
    public void postProcessLines() {
        /* Disable the processing of the lines */
    }

    @Override
    public int getNumLines() {
        return theContents.size();
    }

    /**
     * Check for embedded methodBody.
     *
     * @param pLine the line to check
     * @return true/false
     */
    static boolean checkMethodBody(final ThemisAnalysisLine pLine) {
        /* Check for braceOpen */
        return pLine.endsWithChar(ThemisAnalysisChar.BRACE_OPEN);
    }
}
