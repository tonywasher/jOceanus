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
import net.sourceforge.joceanus.jthemis.ThemisDataException;

/**
 * Embedded Block.
 */
public class ThemisAnalysisEmbedded
        implements ThemisAnalysisContainer {
    /**
     * The lambda sequence.
     */
    static final String LAMBDA = "-> " + ThemisAnalysisChar.BRACE_OPEN;

    /**
     * The anon sequence.
     */
    static final String ANON = "() " + ThemisAnalysisChar.BRACE_OPEN;

    /**
     * The diamond sequence.
     */
    static final String DIAMOND = "<>";

    /**
     * The Parent.
     */
    private final ThemisAnalysisContainer theParent;

    /**
     * The Header.
     */
    private final ThemisAnalysisLine theHeader;

    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

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

        /* Store parent */
        theParent = pParser.getParent();

        /* Parse the body */
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);
        theNumLines = myLines.size() + 2;

        /* Create a parser */
        theContents = new ArrayDeque<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theContents, this);

        /* Parse the lines */
        initialProcessingPass(myParser);

        /* Pop the trailing line */
        theTrailer = (ThemisAnalysisLine) pParser.popNextLine();

        /* Make sure that there is a trailing semicolon */
        if (!theTrailer.endsWithChar(ThemisAnalysisChar.SEMICOLON)) {
            throw new ThemisDataException("Invalid embedded item");
        }
    }

    /**
     * perform initial processing pass.
     * @param pParser the parser
     * @throws OceanusException on error
     */
    void initialProcessingPass(final ThemisAnalysisParser pParser) throws OceanusException {
        /* Loop through the lines */
        while (pParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) pParser.popNextLine();

            /* Process comments/blanks/languageConstructs */
            final boolean processed = pParser.processCommentsAndBlanks(myLine)
                    || pParser.processLanguage(myLine);

            /* If we haven't processed yet */
            if (!processed) {
                /* Just add the line to contents at present */
                theContents.add(myLine);
            }
        }
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

    /**
     * Check for embedded sequence.
     * @param pLine the line to check
     * @return true/false
     */
    static boolean checkEmbedded(final ThemisAnalysisLine pLine) {
        /* Check for lambda */
        if (pLine.endsWithSequence(LAMBDA)) {
            return true;
        }

        /* Check for possible anonymous class */
        if (!pLine.endsWithSequence(ANON)) {
            return false;
        }

        /* Take a copy of the line and strip the trailer */
        final ThemisAnalysisLine myLine = new ThemisAnalysisLine(pLine);
        myLine.stripEndSequence(ANON);

        /* Check for diamonds */
        if (myLine.endsWithSequence(DIAMOND)) {
            myLine.stripEndSequence(DIAMOND);
        }

        /* Check that this is an anonymous class */
        final String baseClass = myLine.stripLastToken();
        final String myKey = myLine.peekLastToken();
        return ThemisAnalysisKeyWord.NEW.getKeyWord().equals(myKey);
    }
}
