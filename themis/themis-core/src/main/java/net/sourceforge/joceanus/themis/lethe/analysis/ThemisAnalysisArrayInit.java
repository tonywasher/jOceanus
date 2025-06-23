/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2025 Tony Washer
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

import java.util.Deque;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisContainer.ThemisAnalysisAdoptable;

/**
 * Array Initialisation.
 */
public class ThemisAnalysisArrayInit
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
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial class line
     * @throws OceanusException on error
     */
    ThemisAnalysisArrayInit(final ThemisAnalysisParser pParser,
                            final ThemisAnalysisLine pLine) throws OceanusException {
        /* Store parent */
        theParent = pParser.getParent();

        /* Parse the body */
        theContents = ThemisAnalysisBuilder.processBody(pParser);
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
     * Check for embedded arrayInit.
     * @param pLine the line to check
     * @return true/false
     */
    static boolean checkArrayInit(final ThemisAnalysisLine pLine) {
        /* Check for arrayInit */
        return pLine.endsWithSequence(ARRAYINIT);
    }
}
