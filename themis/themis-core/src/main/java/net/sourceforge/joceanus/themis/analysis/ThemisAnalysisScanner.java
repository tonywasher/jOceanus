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
package net.sourceforge.joceanus.themis.analysis;

import java.util.ArrayDeque;
import java.util.Deque;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;

/**
 * Scanner for headers and trailers.
 */
public class ThemisAnalysisScanner {
    /**
     * Terminator processor.
     */
    @FunctionalInterface
    private interface ThemisScannerTerminator {
        /**
         * Handle Terminator.
         * @param pTerminator the terminator
         * @throws OceanusException on error
         */
        void handle(char pTerminator) throws OceanusException;
    }

    /**
     * The source.
     */
    private final ThemisAnalysisSource theSource;

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
     * Are we investigating a potential comment?
     */
    private boolean maybeComment;

    /**
     * Should we skip generics?
     */
    private boolean skipGenerics;

    /**
     * Constructor.
     *
     * @param pSource the source
     */
    ThemisAnalysisScanner(final ThemisAnalysisSource pSource) {
        theSource = pSource;
        theResults = new ArrayDeque<>();
    }

    /**
     * Set skip generics flag.
     */
    void skipGenerics() {
        skipGenerics = true;
    }

    /**
     * Scan For Terminator.
     *
     * @param pLine       the current line
     * @param pTerminator the terminator
     * @return the results
     * @throws OceanusException on error
     */
    Deque<ThemisAnalysisElement> scanForTerminator(final ThemisAnalysisLine pLine,
                                                   final char pTerminator) throws OceanusException {
        /* Initialise scan */
        initialiseScan(pLine);

        /* Loop through the line */
        for (;;) {
            /* If we have finished the line */
            if (theCurPos == theLength) {
                /* Shift to the next line */
                shiftToNextLine(true);
                maybeComment = false;
            }

            /* Access current character */
            final char myChar = theCurLine.charAt(theCurPos);

            /* Check for trailing line comments */
            if (checkComments(myChar)) {
                continue;
            }

            /* Break loop if this is a terminator */
            if (checkForTerminator(myChar, pTerminator, this::handleEOLTerminator)) {
                break;
            }
        }

        /* return the results */
        return theResults;
    }

    /**
     * Scan For Separator.
     *
     * @param pSeparator the separator
     * @return the results
     * @throws OceanusException on error
     */
    Deque<ThemisAnalysisElement> scanForSeparator(final char pSeparator) throws OceanusException {
        /* Initialise scan */
        initialiseScan((ThemisAnalysisLine) theSource.popNextLine());

        /* Loop through the line */
        for (;;) {
            /* If we have finished the line */
            if (theCurPos == theLength) {
                /* Shift to the next line */
                if (shiftToNextLine(false)) {
                    break;
                }
                maybeComment = false;
            }

            /* Access current character */
            final char myChar = theCurLine.charAt(theCurPos);

            /* Check for trailing line comments */
            if (checkComments(myChar)) {
                continue;
            }

            /* Break loop if this is a terminator */
            if (checkForTerminator(myChar, pSeparator, this::handleSeparator)) {
                break;
            }
        }

        /* return the results */
        return theResults;
    }

    /**
     * Check For Separator.
     *
     * @param pSeparator the separator
     * @return the results
     * @throws OceanusException on error
     */
    boolean checkForSeparator(final char pSeparator) throws OceanusException {
        /* Initialise scan */
        initialiseScan((ThemisAnalysisLine) theSource.popNextLine());

        /* Loop through the line */
        for (;;) {
            /* If we have finished the line */
            if (theCurPos == theLength) {
                /* Shift to the next line */
                if (shiftToNextLine(false)) {
                    restoreStack(pSeparator);
                    return false;
                }
                maybeComment = false;
            }

            /* Access current character */
            final char myChar = theCurLine.charAt(theCurPos);

            /* Check for trailing line comments */
            if (checkComments(myChar)) {
                continue;
            }

            /* Break loop if this is a terminator */
            if (checkForTerminator(myChar, pSeparator, this::restoreStack)) {
                return true;
            }
        }
    }

    /**
     * Initialise scan.
     *
     * @param pLine       the current line
     */
    private void initialiseScan(final ThemisAnalysisLine pLine) {
        /* Clear the results array */
        theResults.clear();

        /* Access line details */
        theCurLine = pLine;
        theLength = theCurLine.getLength();

        /* reset flags */
        theCurPos = 0;
        maybeComment = false;
    }

    /**
     * Check for comment.
     * @param pChar the current character
     * @return reloop true/false
     * @throws OceanusException on error
     */
    private boolean checkComments(final char pChar) throws OceanusException {
        /* If this is the comment character */
        if (pChar == ThemisAnalysisChar.COMMENT) {
            /* Flip flag */
            maybeComment = !maybeComment;

            /* If we have double comment */
            if (!maybeComment) {
                /* Strip trailing comments and re-loop */
                theCurLine.stripTrailingComments();
                theCurPos = theLength;
                return true;
            }
        } else {
            maybeComment = false;
        }

        /* Continue */
        return false;
    }

    /**
     * Scan For Terminator.
     *
     * @param pChar the current character
     * @param pTerminator the terminator
     * @param pHandler the handler
     * @return terminator found true/false
     * @throws OceanusException on error
     */
    private boolean checkForTerminator(final char pChar,
                                       final char pTerminator,
                                       final ThemisScannerTerminator pHandler) throws OceanusException {
        /* If this is a single/double quote */
        if (pChar == ThemisAnalysisChar.SINGLEQUOTE
                || pChar == ThemisAnalysisChar.DOUBLEQUOTE) {
            /* Find the end of the sequence and skip the quotes */
            final int myEnd = theCurLine.findEndOfQuotedSequence(theCurPos);
            theCurPos = myEnd + 1;

            /* If we have found the terminator */
        } else if (pChar == pTerminator) {
            /* Handle the terminator */
            pHandler.handle(pTerminator);
            return true;

            /* If this is a parenthesisOpen character */
        } else if (pChar == ThemisAnalysisChar.PARENTHESIS_OPEN) {
            /* Handle the nested sequence */
            handleNestedSequence(ThemisAnalysisChar.PARENTHESIS_CLOSE);

            /* If this is a braceOpen character */
        } else if (pChar == ThemisAnalysisChar.BRACE_OPEN) {
            /* Handle the nested sequence */
            handleNestedSequence(ThemisAnalysisChar.BRACE_CLOSE);

            /* If we should skip Generics and this is a genericOpen character */
        } else if (skipGenerics && pChar == ThemisAnalysisChar.GENERIC_OPEN) {
            /* Handle the nested sequence */
            handleNestedSequence(ThemisAnalysisChar.GENERIC_CLOSE);

            /* else move to next character */
        } else {
            /* Increment position */
            theCurPos++;
        }

        /* not found */
        return false;
    }

    /**
     * Handle Terminator at End of Line.
     * @param pTerminator the terminator
     * @throws OceanusException on error
     */
    private void handleEOLTerminator(final char pTerminator) throws OceanusException {
        /* Must be at the end of the line */
        if (theCurPos != theLength - 1) {
            throw new ThemisDataException("Not at end of line");
        }

        /* Strip end character, add to results and break loop */
        theCurLine.stripEndChar(pTerminator);
        theResults.add(theCurLine);
    }

    /**
     * Handle Separator (possible mid-line).
     * @param pTerminator the terminator
     */
    private void handleSeparator(final char pTerminator) {
        /* If separator is not at end of line */
        if (theCurPos != theLength - 1) {
            /* Strip to end character and add to results */
            final ThemisAnalysisLine myLine = theCurLine.stripUpToPosition(theCurPos - 1);
            theResults.add(myLine);

            /* Return a non-blank line to the stack and break loop */
            theCurLine.stripStartChar(pTerminator);
            if (!ThemisAnalysisBlank.isBlank(theCurLine)) {
                theSource.pushLine(theCurLine);
            }

            /* else terminator is at end of line */
        } else {
            /* Strip end character, add to results and break loop */
            theCurLine.stripEndChar(pTerminator);
            theResults.add(theCurLine);
        }
    }

    /**
     * Restore the stack.
     * @param pTerminator the terminator
     */
    private void restoreStack(final char pTerminator) {
        /* Restore current line */
        if (theCurLine != null) {
            theSource.pushLine(theCurLine);
        }

        /* Loop through the results */
        while (!theResults.isEmpty()) {
            theSource.pushLine(theResults.removeLast());
        }
    }

    /**
     * Scan For Generic Terminator.
     *
     * @param pLine the current line
     * @return the results
     * @throws OceanusException on error
     */
    Deque<ThemisAnalysisElement> scanForGeneric(final ThemisAnalysisLine pLine) throws OceanusException {
        return scanForContents(pLine, ThemisAnalysisChar.GENERIC_CLOSE);
    }

    /**
     * Scan For Parenthesis Terminator.
     *
     * @param pLine the current line
     * @return the results
     * @throws OceanusException on error
     */
    Deque<ThemisAnalysisElement> scanForParenthesis(final ThemisAnalysisLine pLine) throws OceanusException {
        return scanForContents(pLine, ThemisAnalysisChar.PARENTHESIS_CLOSE);
    }

    /**
     * Scan For Array Terminator.
     *
     * @param pLine the current line
     * @return the results
     * @throws OceanusException on error
     */
    Deque<ThemisAnalysisElement> scanForArray(final ThemisAnalysisLine pLine) throws OceanusException {
        return scanForContents(pLine, ThemisAnalysisChar.ARRAY_CLOSE);
    }

    /**
     * Scan For Terminator across multiple lines.
     *
     * @param pLine       the current line
     * @param pTerminator the terminator
     * @return the results
     * @throws OceanusException on error
     */
    private Deque<ThemisAnalysisElement> scanForContents(final ThemisAnalysisLine pLine,
                                                         final char pTerminator) throws OceanusException {
        /* Allocate array */
        theResults = new ArrayDeque<>();

        /* Access line details */
        theCurLine = pLine;
        theLength = theCurLine.getLength();
        theCurPos = 0;

        /* Locate the end of the sequence */
        handleNestedSequence(pTerminator);

        /* Strip to end character and add to results */
        final ThemisAnalysisLine myLine = theCurLine.stripUpToPosition(theCurPos - 1);
        theResults.add(myLine);

        /* Return a non-blank line to the stack and break loop */
        if (!ThemisAnalysisBlank.isBlank(theCurLine)) {
            theSource.pushLine(theCurLine);
        }

        /* Return the results */
        return theResults;
    }

    /**
     * Shift to next line.
     * @param pErrorOnEmpty throw exception on empty
     * @return empty true/false
     * @throws OceanusException on error
     */
    private boolean shiftToNextLine(final boolean pErrorOnEmpty) throws OceanusException {
        /* Add current line to the results */
        theResults.add(theCurLine);
        theCurLine = null;

        /* Check for additional lines */
        if (!theSource.hasLines()) {
            /* Throw exception if required, else return empty */
            if (pErrorOnEmpty) {
                throw new ThemisDataException("Did not find terminator");
            }
            return true;
        }

        /* Access the next line */
        theCurLine = (ThemisAnalysisLine) theSource.popNextLine();
        theLength = theCurLine.getLength();
        theCurPos = 0;

        /* Handle empty lines */
        return theLength == 0 && shiftToNextLine(pErrorOnEmpty);
    }

    /**
     * Handle nested sequence.
     *
     * @param pNestEnd the nest end character
     * @throws OceanusException on error
     */
    private void handleNestedSequence(final char pNestEnd) throws OceanusException {
        /* Access the nest start character */
        final char myNestStart = theCurLine.charAt(theCurPos);

        /* Look for end of nest in this line */
        int myNested = theCurLine.findEndOfNestedSequence(theCurPos, 0, pNestEnd, myNestStart);

        /* While the end of the nest is not found */
        while (myNested < 0) {
            /* Shift to the next line */
            shiftToNextLine(true);

            /* Repeat test for end of nest */
            myNested = theCurLine.findEndOfNestedSequence(0, myNested, pNestEnd, myNestStart);
        }

        /* Adjust position */
        theCurPos = myNested + 1;
    }

    /**
     * Scanner Source.
     */
    public interface ThemisAnalysisSource {
        /**
         * Are there more lines to process?
         * @return true/false
         */
        boolean hasLines();

        /**
         * Pop next line from list.
         * @return the next line
         * @throws OceanusException on error
         */
        ThemisAnalysisElement popNextLine() throws OceanusException;

        /**
         * Push line back onto stack.
         * @param pLine to line to push onto stack
         */
        void pushLine(ThemisAnalysisElement pLine);
    }
}
