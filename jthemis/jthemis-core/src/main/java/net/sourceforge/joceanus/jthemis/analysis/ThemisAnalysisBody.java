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

import java.util.ArrayList;
import java.util.List;

/**
 * Block structure.
 */
public final class ThemisAnalysisBody {
    /**
     * Open body.
     */
    static final String BRACE_OPEN = "\u007B";

    /**
     * Close body.
     */
    static final String BRACE_CLOSE = "}";

    /**
     * Statement terminator.
     */
    static final String STATEMENT_END = ";";

    /**
     * Constructor.
     */
    private ThemisAnalysisBody() {
    }

    /**
     * Process headers.
     * @param pParser the parser
     * @param pLine the current line
     * @return the headers
     */
    static List<ThemisAnalysisLine> processHeaders(final ThemisAnalysisParser pParser,
                                                   final ThemisAnalysisLine pLine) {
        /* Allocate array */
        final List<ThemisAnalysisLine> myHeaders = new ArrayList<>();

        /* Read headers */
        ThemisAnalysisLine myLine = pLine;
        while (!myLine.endsWithSequence(BRACE_OPEN)) {
            myHeaders.add(myLine);
            if (!pParser.hasLines()) {
                break;
            }
            myLine = pParser.popNextLine();
        }

        /* If we never found a start sequence. shout */
        if (!myLine.endsWithSequence(BRACE_OPEN)) {
            throw new IllegalStateException("Never found start Block");
        }

        /* Add the current line if it is non blank */
        myLine.stripEndSequence(BRACE_OPEN);
        if (!ThemisAnalysisBlank.isBlank(myLine)) {
            myHeaders.add(myLine);
        }

        /* return the headers */
        return myHeaders;
    }

    /**
     * Process trailers.
     * @param pParser the parser
     * @return the trailers
     */
    static List<ThemisAnalysisLine> processTrailers(final ThemisAnalysisParser pParser) {
        /* Just return if there are no more lines */
        if (!pParser.hasLines()) {
            return null;
        }

        /* Allocate array */
        final List<ThemisAnalysisLine> myTrailers = new ArrayList<>();

        /* Access keyWord */
        ThemisAnalysisLine myLine = pParser.popNextLine();

        /* Read Trailers */
        while (!myLine.endsWithSequence(STATEMENT_END)) {
            myTrailers.add(myLine);
            if (!pParser.hasLines()) {
                break;
            }
            myLine = pParser.popNextLine();
        }

        /* If we never found an end sequence. shout */
        if (!myLine.endsWithSequence(STATEMENT_END)) {
            throw new IllegalStateException("Never found end Block");
        }

        /* Add the current line */
        myLine.stripEndSequence(STATEMENT_END);
        myTrailers.add(myLine);

        /* return the trailers */
        return myTrailers;
    }

    /**
     * Process body.
     * @param pParser the parser
     * @return the body
     */
    static List<ThemisAnalysisLine> processBody(final ThemisAnalysisParser pParser) {
        /* Allocate array */
        final List<ThemisAnalysisLine> myBody = new ArrayList<>();

        /* Loop through the lines */
        int myNest = 1;
        while (pParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = pParser.popNextLine();

            /* If we have a closing brace */
            if (myLine.startsWithSequence(BRACE_CLOSE)) {
                /* Decrement nesting */
                myNest--;

                /* If we have finished the class */
                if (myNest == 0) {
                    /* Strip start sequence from line */
                    myLine.stripStartSequence(BRACE_CLOSE);

                    /* Return a non-blank line to the stack and break loop */
                    if (!ThemisAnalysisBlank.isBlank(myLine)) {
                        pParser.pushLine(myLine);
                    }
                    break;
                }
            }

            /* Handle start of nested sequence */
            if (myLine.endsWithSequence(BRACE_OPEN)) {
                myNest++;
            }

            /* Add the line */
            myBody.add(myLine);
        }

        /* return the body */
        return myBody;
    }
}
