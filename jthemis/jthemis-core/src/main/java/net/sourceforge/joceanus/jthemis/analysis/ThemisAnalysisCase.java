/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2024 Tony Washer
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
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Case construct.
 */
public class ThemisAnalysisCase
    implements ThemisAnalysisContainer {
    /**
     * The parent.
     */
    private final ThemisAnalysisContainer theParent;

    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * The case values.
     */
    private final List<Object> theCases;

    /**
     * The number of lines.
     */
    private final int theNumLines;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pOwner the owning switch
     * @param pCase the case
     * @throws OceanusException on error
     */
    ThemisAnalysisCase(final ThemisAnalysisParser pParser,
                       final ThemisAnalysisContainer pOwner,
                       final Object pCase) throws OceanusException {
        /* Record the parent */
        theParent = pOwner;

        /* Initialise the case value */
        theCases = new ArrayList<>();
        theCases.add(pCase);

        /* Create a parser */
        theContents = new ArrayDeque<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(pParser, theContents);
        processLines(myParser);

        /* Calculate the number of lines */
        theNumLines = theCases.size();
    }

    /**
     * process the lines.
     * @param pParser the parser
     * @throws OceanusException on error
     */
    void processLines(final ThemisAnalysisParser pParser) throws OceanusException {
        /* we are still processing Cases */
        boolean look4Case = true;

        /* Loop through the lines */
        while (pParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) pParser.popNextLine();

            /* Process comments/blanks/languageConstructs */
            boolean processed = pParser.processCommentsAndBlanks(myLine)
                         || pParser.processLanguage(myLine)
                         || pParser.processBlocks(myLine);

            /* If we have not processed */
            if (!processed) {
                /* Look for new case */
                myLine.mark();
                final Object myCase = ThemisAnalysisParser.parseCase(myLine);

                /* If we have a new case */
                if (myCase != null) {
                    /* If we are still looking for further cases */
                    if (look4Case) {
                        /* Process additional caseValue */
                        theCases.add(myCase);
                        processed = true;

                        /* else we have finished */
                    } else {
                        /* reset and restore line and break loop */
                        myLine.reset();
                        pParser.pushLine(myLine);
                        return;
                    }
                }
            }

            /* If we have not processed */
            if (!processed) {
                /* Have finished looking for cases */
                look4Case = false;

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

    @Override
    public String toString() {
        /* Start parameters */
        final StringBuilder myBuilder = new StringBuilder();

        /* Build parameters */
        boolean bFirst = true;
        for (Object myCase : theCases) {
            /* Handle separators */
            if (!bFirst) {
                myBuilder.append(ThemisAnalysisChar.COMMA);
                myBuilder.append(ThemisAnalysisChar.BLANK);
            } else {
                bFirst = false;
            }

            /* Add case */
            myBuilder.append(myCase);
        }

        /* Return the string */
        return myBuilder.toString();
    }
}
