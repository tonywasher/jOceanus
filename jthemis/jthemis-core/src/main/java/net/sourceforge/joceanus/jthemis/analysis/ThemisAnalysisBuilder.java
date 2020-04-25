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

/**
 * Builder utilities.
 */
public final class ThemisAnalysisBuilder {
    /**
     * Open body.
     */
    static final char BRACE_OPEN = '{';

    /**
     * Close body.
     */
    static final char BRACE_CLOSE = '}';

    /**
     * Case terminator.
     */
    static final char CASE_COLON = ':';

    /**
     * Statement separator.
     */
    static final char STATEMENT_SEP = ',';

    /**
     * Statement terminator.
     */
    static final char STATEMENT_END = ';';

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
     */
    static Deque<ThemisAnalysisElement> parseHeaders(final ThemisAnalysisParser pParser,
                                                     final ThemisAnalysisLine pLine) {
        /* Allocate scanner */
        final ThemisAnalysisScanner myScanner = new ThemisAnalysisScanner(pParser);
        return myScanner.scanForTerminator(pLine, BRACE_OPEN);
    }

    /**
     * Process trailers.
     * @param pParser the parser
     * @param pLine the current line
     * @return the trailers
     */
    static Deque<ThemisAnalysisElement> parseTrailers(final ThemisAnalysisParser pParser,
                                                      final ThemisAnalysisLine pLine) {
        /* Allocate scanner */
        final ThemisAnalysisScanner myScanner = new ThemisAnalysisScanner(pParser);
        return myScanner.scanForTerminator(pLine, STATEMENT_END);
    }

    /**
     * Process body.
     * @param pParser the parser
     * @return the body
     */
    static Deque<ThemisAnalysisElement> processBody(final ThemisAnalysisParser pParser) {
        /* Allocate queue */
        final Deque<ThemisAnalysisElement> myBody = new ArrayDeque<>();

        /* Loop through the lines */
        int myNest = 1;
        while (myNest > 0 && pParser.hasLines()) {
            /* Access as line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) pParser.popNextLine();

            /* If we have a closing brace */
            if (myLine.startsWithChar(BRACE_CLOSE)) {
                /* Decrement nesting */
                myNest--;

                /* If we have finished the class */
                if (myNest == 0) {
                    /* Strip start sequence from line */
                    myLine.stripStartChar(BRACE_CLOSE);

                    /* Return a non-blank line to the stack and break loop */
                    if (!ThemisAnalysisBlank.isBlank(myLine)) {
                        pParser.pushLine(myLine);
                    }
                    break;
                }
            }

            /* Handle start of nested sequence */
            if (myLine.endsWithChar(BRACE_OPEN)) {
                myNest++;
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
     * @return the body
     */
    static Deque<ThemisAnalysisElement> processMethodBody(final ThemisAnalysisParser pParser) {
        /* Allocate queue */
        final Deque<ThemisAnalysisElement> myBody = new ArrayDeque<>();

        /* Loop through the lines */
        boolean keepLooking = true;
        while (keepLooking && pParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisElement myElement = pParser.popNextLine();

            /* Skip already processed items */
            if (myElement instanceof ThemisAnalysisProcessed) {
                myBody.add(myElement);
                continue;
            }

            /* Access as line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) myElement;

            /* If we have a closing brace */
            if (myLine.startsWithChar(BRACE_CLOSE)) {
                /* Strip start sequence from line */
                myLine.stripStartChar(BRACE_CLOSE);
                keepLooking = false;

                /* Return a non-blank line to the stack and break loop */
                if (!ThemisAnalysisBlank.isBlank(myLine)) {
                    pParser.pushLine(myLine);
                }
            } else {
                /* Add the line */
                myBody.add(myLine);
            }
        }

        /* return the body */
        return myBody;
    }
}
