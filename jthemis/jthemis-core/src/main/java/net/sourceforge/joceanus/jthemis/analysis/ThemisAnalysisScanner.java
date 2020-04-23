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
 * Scanner for headers and trailers.
 */
public class ThemisAnalysisScanner {
    /**
     * The parser.
     */
    private final ThemisAnalysisParser theParser;

    /**
     * The list of result lines.
     */
    private Deque<ThemisAnalysisElement> theResults;

    /**
     * The current line.
     */
    private ThemisAnalysisLine theCurLine;

    /**
     * The length of the current line.
     */
    private int theLength;

    /**
     * The current position in the line.
     */
    private int theCurPos;

    /**
     * Constructor.
     * @param pParser the parser
     */
    ThemisAnalysisScanner(final ThemisAnalysisParser pParser) {
        theParser = pParser;
    }

    /**
     * Scan For Terminator.
     * @param pLine the current line
     * @param pTerminator the terminator
     * @return the results
     */
    Deque<ThemisAnalysisElement> scanForTerminator(final ThemisAnalysisLine pLine,
                                                   final char pTerminator) {
        /* Allocate array */
        theResults = new ArrayDeque<>();

        /* Access line details */
        theCurLine = pLine;
        theLength = theCurLine.getLength();

        /* Loop through the line */
        theCurPos = 0;
        for (;;) {
            /* If we have finished the line */
            if (theCurPos == theLength) {
                /* Shift to the next line */
                shiftToNextLine();
            }

            /* Access current character */
            final char myChar = theCurLine.charAt(theCurPos);

            /* If this is a single/double quote */
            if (myChar == ThemisAnalysisLine.SINGLEQUOTE
                    || myChar == ThemisAnalysisLine.DOUBLEQUOTE) {
                /* Find the end of the sequence and skip the quotes */
                final int myEnd = theCurLine.findEndOfQuotedSequence(theCurPos);
                theCurPos = myEnd + 1;

                /* If we have found the terminator */
            } else if (myChar == pTerminator) {
                /* Must be at the end of the line */
                if (theCurPos != theLength - 1) {
                    throw new IllegalStateException("Not at end of line");
                }

                /* Strip end character, add to results and break loop */
                theCurLine.stripEndChar(pTerminator);
                theResults.add(theCurLine);
                break;

                /* If this is a parenthesisOpen character */
            } else if (myChar == ThemisAnalysisParenthesis.PARENTHESIS_OPEN) {
                /* Handle the nested sequence */
                handleNestedSequence(ThemisAnalysisParenthesis.PARENTHESIS_CLOSE);

                /* If this is a braceOpen character */
            } else if (myChar == ThemisAnalysisBuilder.BRACE_OPEN) {
                /* Handle the nested sequence */
                handleNestedSequence(ThemisAnalysisBuilder.BRACE_CLOSE);

                /* else move to next character */
            } else {
                /* Increment position */
                theCurPos++;
            }
        }

        /* return the results */
        return theResults;
    }

    /**
     * Shift to next line.
     */
    private void shiftToNextLine() {
         /* Add current line to the results */
         theResults.add(theCurLine);

         /* Check for additional lines */
         if (!theParser.hasLines()) {
             throw new IllegalStateException("Did not find terminator");
         }

         /* Access the next line */
         theCurLine = (ThemisAnalysisLine) theParser.popNextLine();
         theLength = theCurLine.getLength();
         theCurPos = 0;
    }

    /**
     * Handle nested sequence.
     * @param pNestEnd the nest end character
     */
    private void handleNestedSequence(final char pNestEnd) {
        /* Access the nest start character */
        final char myNestStart = theCurLine.charAt(theCurPos);

        /* Look for end of nest in this line */
        int myNested = theCurLine.findEndOfNestedSequence(theCurPos, 0, pNestEnd, myNestStart);

        /* While the end of the nest is not found */
        while (myNested < 0) {
            /* Shift to the next line */
            shiftToNextLine();

            /* Repeat test for end of nest */
            myNested = theCurLine.findEndOfNestedSequence(0, myNested, pNestEnd, myNestStart);
        }

        /* Adjust position */
        theCurPos = myNested + 1;
    }
}
