/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2021 Tony Washer
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
 * Builder utilities.
 */
public final class ThemisAnalysisBuilder {
    /**
     * Constructor.
     */
    private ThemisAnalysisBuilder() {
    }

    /**
     * Parse headers.
     * @param pParser the parser
     * @param pLine the current line
     * @return the headers
     * @throws OceanusException on error
     */
    static Deque<ThemisAnalysisElement> parseHeaders(final ThemisAnalysisParser pParser,
                                                     final ThemisAnalysisLine pLine) throws OceanusException {
        /* Allocate scanner */
        final ThemisAnalysisScanner myScanner = new ThemisAnalysisScanner(pParser);
        return myScanner.scanForTerminator(pLine, ThemisAnalysisChar.BRACE_OPEN);
    }

    /**
     * Process trailers.
     * @param pParser the parser
     * @param pLine the current line
     * @return the trailers
     * @throws OceanusException on error
     */
    static Deque<ThemisAnalysisElement> parseTrailers(final ThemisAnalysisParser pParser,
                                                      final ThemisAnalysisLine pLine) throws OceanusException {
        /* Allocate scanner */
        final ThemisAnalysisScanner myScanner = new ThemisAnalysisScanner(pParser);
        return myScanner.scanForTerminator(pLine, ThemisAnalysisChar.SEMICOLON);
    }

    /**
     * Process body.
     * @param pParser the parser
     * @return the body
     * @throws OceanusException on error
     */
    static Deque<ThemisAnalysisElement> processBody(final ThemisAnalysisParser pParser) throws OceanusException {
        /* Allocate queue */
        final Deque<ThemisAnalysisElement> myBody = new ArrayDeque<>();

        /* Loop through the lines */
        int myNest = 1;
        while (myNest > 0 && pParser.hasLines()) {
            /* Access as line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) pParser.popNextLine();

            /* If we have a closing brace */
            if (myLine.startsWithChar(ThemisAnalysisChar.BRACE_CLOSE)) {
                /* Decrement nesting */
                myNest--;

                /* If we have finished the class */
                if (myNest == 0) {
                    /* Strip start sequence from line */
                    myLine.stripStartChar(ThemisAnalysisChar.BRACE_CLOSE);

                    /* Return a non-blank line to the stack and break loop */
                    if (!ThemisAnalysisBlank.isBlank(myLine)) {
                        pParser.pushLine(myLine);
                    }
                    break;
                }
            }

            /* Handle start of nested sequence */
            if (myLine.endsWithChar(ThemisAnalysisChar.BRACE_OPEN)) {
                /* Strip trailing comments */
                myLine.stripTrailingComments();
                if (myLine.endsWithChar(ThemisAnalysisChar.BRACE_OPEN)) {
                    myNest++;
                }
            }

            /* Add the line */
            myBody.add(myLine);
        }

        /* return the body */
        return myBody;
    }

    /**
     * Process method body.
     * @param pParser the parser
     * @param pOwner the owning method
     * @return the body
     * @throws OceanusException on error
     */
    static Deque<ThemisAnalysisElement> processMethodBody(final ThemisAnalysisParser pParser,
                                                          final ThemisAnalysisMethod pOwner) throws OceanusException {
        /* Allocate queue */
        final Deque<ThemisAnalysisElement> myBody = new ArrayDeque<>();

        /* Loop through the lines */
        int myNest = 1;
        while (myNest > 0 && pParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisElement myElement = pParser.popNextLine();

            /* Skip already processed items */
            if (myElement instanceof ThemisAnalysisProcessed) {
                /* Adopt the element if required */
                if (myElement instanceof ThemisAnalysisAdoptable) {
                    ((ThemisAnalysisAdoptable) myElement).setParent(pOwner);
                }

                /* Add to body */
                myBody.add(myElement);
                continue;
            }

            /* Access as line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) myElement;

            /* If we have a closing brace */
            if (myLine.startsWithChar(ThemisAnalysisChar.BRACE_CLOSE)) {
                /* Decrement nesting */
                myNest--;

                /* If we have finished the class */
                if (myNest == 0) {
                    /* Strip start sequence from line */
                    myLine.stripStartChar(ThemisAnalysisChar.BRACE_CLOSE);

                    /* Return a non-blank line to the stack and break loop */
                    if (!ThemisAnalysisBlank.isBlank(myLine)) {
                        pParser.pushLine(myLine);
                    }
                    break;
                }
            }

            /* Handle start of nested sequence */
            if (myLine.endsWithChar(ThemisAnalysisChar.BRACE_OPEN)) {
                /* Strip trailing comments */
                myLine.stripTrailingComments();
                if (myLine.endsWithChar(ThemisAnalysisChar.BRACE_OPEN)) {
                    myNest++;
                }
            }

            /* Add the line */
            myBody.add(myLine);
        }

        /* return the body */
        return myBody;
    }

    /**
     * format lines.
     * @param pLines the lines to format
     * @return the formatted lines
     */
    public static String formatLines(final Deque<ThemisAnalysisElement> pLines) {
        /* Start parameters */
        final StringBuilder myBuilder = new StringBuilder();

        /* Build parameters */
        boolean bFirst = true;
        for (ThemisAnalysisElement myLine : pLines) {
            /* Handle separators */
            if (!bFirst) {
                myBuilder.append(ThemisAnalysisChar.LF);
            } else {
                bFirst = false;
            }

            /* Add parameter */
            myBuilder.append(myLine);
        }

        /* Return the string */
        return myBuilder.toString();
    }
}
