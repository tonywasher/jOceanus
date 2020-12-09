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
import java.util.Objects;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisDataException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisContainer.ThemisAnalysisAdoptable;

/**
 * Embedded Block.
 */
public class ThemisAnalysisEmbedded
        implements ThemisAnalysisContainer, ThemisAnalysisAdoptable {
    /**
     * The Parent.
     */
    private ThemisAnalysisContainer theParent;

    /**
     * The Header.
     */
    private final ThemisAnalysisLine theHeader;

    /**
     * The embedded contents.
     */
    private final Deque<ThemisAnalysisElement> theEmbedded;

    /**
     * The trailer.
     */
    private final ThemisAnalysisLine theTrailer;

    /**
     * The number of lines.
     */
    private final int theNumLines;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial class line
     * @throws OceanusException on error
     */
    ThemisAnalysisEmbedded(final ThemisAnalysisParser pParser,
                           final ThemisAnalysisLine pLine) throws OceanusException {
        /* Store parameters */
        theHeader = pLine;

        /* Take a copy of the line and strip the trailer */
        theEmbedded = new ArrayDeque<>();
        theEmbedded.add(ThemisAnalysisAnonClass.checkAnon(pLine)
                ? new ThemisAnalysisAnonClass(pParser, pLine)
                : new ThemisAnalysisLambda(pParser, pLine));

        /* Store parent */
        theParent = pParser.getParent();

        /* Parse the body */
        theNumLines = 2;

        /* Pop the trailing line */
        theTrailer = (ThemisAnalysisLine) pParser.popNextLine();

        /* Make sure that there is a trailing semicolon */
        if (!theTrailer.endsWithChar(ThemisAnalysisChar.SEMICOLON)) {
            throw new ThemisDataException("Invalid embedded item");
        }
    }

    /**
     * Obtain the header.
     * @return the header
     */
    ThemisAnalysisLine getHeader() {
        return theHeader;
    }

    @Override
    public Deque<ThemisAnalysisElement> getContents() {
        return theEmbedded;
    }

    @Override
    public ThemisAnalysisContainer getParent() {
        return theParent;
    }

    @Override
    public void setParent(final ThemisAnalysisContainer pParent) {
        theParent = pParent;
        theEmbedded.forEach(e -> ((ThemisAnalysisAdoptable) e).setParent(pParent));
    }

    @Override
    public ThemisAnalysisDataMap getDataMap() {
        return ((ThemisAnalysisContainer) Objects.requireNonNull(theEmbedded.peekFirst())).getDataMap();
    }

    @Override
    public int getNumLines() {
        return theNumLines;
    }
}
