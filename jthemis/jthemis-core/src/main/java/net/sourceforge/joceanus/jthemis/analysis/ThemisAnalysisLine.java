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
import java.util.Arrays;
import java.util.List;

/**
 * Line buffer.
 */
public class ThemisAnalysisLine
    implements ThemisAnalysisElement {
    /**
     * The line comment character.
     */
    private static final char COMMENT = '/';

    /**
     * Open parenthesis.
     */
    static final char PARENTHESIS_OPEN = '(';

    /**
     * Close parenthesis.
     */
    static final char PARENTHESIS_CLOSE = ')';

    /**
     * The line buffer.
     */
    private List<ThemisAnalysisPrefix> theModifiers;

    /**
     * The line buffer.
     */
    private final char[] theBuffer;

    /**
     * The starting offset within the line buffer.
     */
    private int theOffset;

    /**
     * The length of valid data in the buffer.
     */
    private int theLength;

    /**
     * Constructor.
     * @param pBuffer the buffer
     * @param pOffset the offset to copy from
     * @param pLen the length
     */
    ThemisAnalysisLine(final char[] pBuffer,
                       final int pOffset,
                       final int pLen) {
        /* Store values */
        theLength = pLen;
        theBuffer = new char[theLength];
        theOffset = 0;

        /* copy the data */
        System.arraycopy(pBuffer, pOffset, theBuffer, theOffset, theLength);

        /* Strip any trailing comments */
        stripTrailingComments();

        /* Strip any leading/trailing whiteSpace */
        stripLeadingWhiteSpace();
        stripTrailingWhiteSpace();

        /* Strip Modifiers */
        stripModifiers();
    }

    /**
     * Obtain the length.
     * @return the length
     */
    public int getLength() {
        return theLength;
    }

    /**
     * Obtain the modifiers.
     * @return the modifiers
     */
    public List<ThemisAnalysisPrefix> getModifiers() {
        return theModifiers;
    }

    /**
     * Strip trailing comments.
     */
    private void stripTrailingComments() {
        /* Loop through the characters */
        for (int i = 0; i < theLength - 1; i++) {
            /* If we have a line comment */
            if (theBuffer[i + theOffset] == COMMENT
                && theBuffer[i + theOffset + 1] == COMMENT) {
                /* Reset the length */
                theLength = i;
            }
        }
    }

    /**
     * Trim leading whiteSpace.
     */
    void stripLeadingWhiteSpace() {
        /* Loop through the characters */
        int myWhiteSpace = 0;
        for (int i = 0; i < theLength; i++) {
            /* Break loop if not whiteSpace */
            if (!Character.isWhitespace(theBuffer[i + theOffset])) {
                break;
            }

            /* Increment count */
            myWhiteSpace++;
        }

        /* Adjust counts */
        theOffset += myWhiteSpace;
        theLength -= myWhiteSpace;
    }

    /**
     * Trim trailing whiteSpace.
     */
    private void stripTrailingWhiteSpace() {
        /* Loop through the characters */
        int myWhiteSpace = 0;
        for (int i = theLength - 1; i >= 0; i--) {
            /* Break loop if not whiteSpace */
            if (!Character.isWhitespace(theBuffer[i + theOffset])) {
                break;
            }

            /* Increment count */
            myWhiteSpace++;
        }

        /* Adjust counts */
        theLength -= myWhiteSpace;
    }

    /**
     * Strip Modifiers.
     */
    private void stripModifiers() {
        /* Loop through the modifiers */
        for (ThemisAnalysisModifier myModifier : ThemisAnalysisModifier.values()) {
            if (isStartedBy(myModifier.getModifier())) {
                /* Allocate array if needed and add modifier */
                if (theModifiers == null) {
                    theModifiers = new ArrayList<>();
                }
                theModifiers.add(myModifier);

                /* Try for more modifiers */
                stripModifiers();
            }
        }
    }

    /**
     * Does line start with identifier?
     * @param pIdentifier the identifier
     * @return true/false
     */
    boolean isStartedBy(final String pIdentifier) {
        /* If the line is too short, just return */
        final int myLen = pIdentifier.length();
        if (myLen > theLength) {
            return false;
        }

        /* Loop through the modifier */
        for (int i = 0; i < myLen; i++) {
            if (theBuffer[i + theOffset] != pIdentifier.charAt(i)) {
                return false;
            }
        }

        /* Catch any solo modifiers */
        if (myLen == theLength) {
            throw new IllegalStateException("Modifier found without object");
        }

        /* The next character must be whitespace */
        if (Character.isWhitespace(theBuffer[myLen + theOffset])) {
            theLength -= myLen + 1;
            theOffset += myLen + 1;
            stripLeadingWhiteSpace();
            return true;
        }

        /* False alarm */
        return false;
    }

    /**
     * Strip NextToken.
     * @return the next token
     */
    String stripNextToken() {
        /* Access the next token */
        final String myToken = peekNextToken();
        stripStartSequence(myToken);
        return myToken;
    }

    /**
     * Peek NextToken.
     * @return the next token
     */
    String peekNextToken() {
        /* Loop through the buffer */
        for (int i = 0; i < theLength; i++) {
            /* if we have hit whiteSpace or GENERIC/PARENTHESIS OPEN */
            final char myChar = theBuffer[i + theOffset];
            if (Character.isWhitespace(myChar)
                    || myChar == ThemisAnalysisGeneric.GENERIC_OPEN
                    || myChar == PARENTHESIS_OPEN) {
                /* Strip out the characters */
                final char[] myChars = Arrays.copyOfRange(theBuffer, theOffset, i + theOffset);
                return new String(myChars);
            }
        }

        /* Whole buffer is the token */
        return toString();
    }

    /**
     * Does line start with the sequence?
     * @param pSequence the sequence
     * @return true/false
     */
    boolean startsWithSequence(final CharSequence pSequence) {
        /* If the line is too short, just return */
        final int myLen = pSequence.length();
        if (myLen > theLength) {
            return false;
        }

        /* Loop through the sequence */
        for (int i = 0; i < myLen; i++) {
            if (theBuffer[i + theOffset] != pSequence.charAt(i)) {
                return false;
            }
        }

        /* True */
        return true;
    }

    /**
     * Does line end with the sequence?
     * @param pSequence the sequence
     * @return true/false
     */
    boolean endsWithSequence(final CharSequence pSequence) {
        /* If the line is too short, just return */
        final int myLen = pSequence.length();
        if (myLen > theLength) {
            return false;
        }

        /* Loop through the buffer */
        for (int i = 0; i <= theLength - myLen; i++) {
            /* Loop through the sequence */
            boolean found = true;
            for (int j = 0; j < myLen; j++) {
                if (theBuffer[i + j + theOffset] != pSequence.charAt(j)) {
                    /* Not found */
                    found = false;
                    break;
                }
            }

            /* If we found the sequence */
            if (found) {
                /* Check that it is at the end */
                if (i != theLength - myLen) {
                    throw new IllegalStateException("EndSequence found midLine");
                }

                /* found it */
                return true;
            }
        }

        /* Not found */
        return false;
    }

    /**
     * Strip the ending sequence.
     * @param pSequence the sequence
     */
    void stripStartSequence(final CharSequence pSequence) {
        /* If the line starts with the sequence */
        if (startsWithSequence(pSequence)) {
            /* adjust the length */
            theLength -= pSequence.length();
            theOffset += pSequence.length();
            stripLeadingWhiteSpace();
        }
    }

    /**
     * Strip the ending sequence.
     * @param pSequence the sequence
     */
    void stripEndSequence(final CharSequence pSequence) {
        /* If the line ends with the sequence */
        if (endsWithSequence(pSequence)) {
            /* adjust the length */
            theLength -= pSequence.length();
            stripTrailingWhiteSpace();
        }
    }

    @Override
    public String toString() {
        final char[] myChars = Arrays.copyOfRange(theBuffer, theOffset, theOffset + theLength);
        return new String(myChars);
    }
}
