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
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisContainer.ThemisAnalysisAdoptable;

/**
 * Block construct.
 */
public class ThemisAnalysisBlock
        implements ThemisAnalysisContainer, ThemisAnalysisAdoptable {
    /**
     * The Header.
     */
    private final ThemisAnalysisLine theHeader;

    /**
     * The properties.
     */
    private final ThemisAnalysisProperties theProperties;

    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

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
     * @param pLine the initial class line
     * @throws OceanusException on error
     */
    ThemisAnalysisBlock(final ThemisAnalysisParser pParser,
                        final ThemisAnalysisLine pLine) throws OceanusException {
        /* Store parameters */
        theHeader = pLine;
        theNumLines = 2;

        /* Store parameters */
        theProperties = pLine.getProperties();
        theParent = pParser.getParent();

        /* Create the arrays */
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
    public ThemisAnalysisContainer getParent() {
        return theParent;
    }

    @Override
    public void setParent(final ThemisAnalysisContainer pParent) {
        theParent = pParent;
    }

    @Override
    public int getNumLines() {
        return theNumLines;
    }

    /**
     * Is the block synchronized.
     * @return true/false
     */
    public boolean isSynchronized() {
        return theProperties.hasModifier(ThemisAnalysisModifier.SYNCHRONIZED);
    }

    @Override
    public String toString() {
        /* Create builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Handle flags */
        if (theHeader.getProperties().hasModifier(ThemisAnalysisModifier.STATIC)) {
            myBuilder.append(ThemisAnalysisModifier.STATIC);
            myBuilder.append(ThemisAnalysisChar.BLANK);
        }
        if (theHeader.getProperties().hasModifier(ThemisAnalysisModifier.SYNCHRONIZED)) {
            myBuilder.append(ThemisAnalysisModifier.SYNCHRONIZED);
            myBuilder.append(ThemisAnalysisChar.BLANK);
        }

        /* Add header */
        myBuilder.append(theHeader);
        return myBuilder.toString();
    }

    /**
     * Check for block sequence.
     * @param pLine the line to check
     * @return true/false
     */
    static boolean checkBlock(final ThemisAnalysisLine pLine) {
        /* Only interested in possible blocks */
        if (!pLine.endsWithChar(ThemisAnalysisChar.BRACE_OPEN)) {
            return false;
        }

        /* Check for synchronised block */
        if (pLine.getProperties().hasModifier(ThemisAnalysisModifier.SYNCHRONIZED)
                && pLine.startsWithChar(ThemisAnalysisChar.PARENTHESIS_OPEN)) {
            return true;
        }

        /* Check for standard block */
        return pLine.getLength() == 1;
    }
}
