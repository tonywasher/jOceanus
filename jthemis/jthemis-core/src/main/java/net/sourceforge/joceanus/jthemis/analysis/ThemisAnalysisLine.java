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

import java.nio.CharBuffer;
import java.util.ArrayList;
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
     * The null character.
     */
    static final char CHAR_NULL = (char) 0;

    /**
     * The token terminators.
     */
    private static final char[] TERMINATORS = {
        ThemisAnalysisParenthesis.PARENTHESIS_OPEN,
        ThemisAnalysisParenthesis.PARENTHESIS_CLOSE,
        ThemisAnalysisGeneric.GENERIC_OPEN,
        ThemisAnalysisBuilder.STATEMENT_COMMA,
        ThemisAnalysisBuilder.STATEMENT_SEMI,
        ThemisAnalysisBuilder.CASE_COLON,
        ThemisAnalysisArray.ARRAY_OPEN
    };

    /**
     * The line buffer.
     */
    private final List<ThemisAnalysisPrefix> theModifiers;

    /**
     * The line buffer.
     */
    private final CharBuffer theBuffer;

    /**
     * Constructor.
     * @param pBuffer the buffer
     * @param pOffset the offset to copy from
     * @param pLen the length
     */
    ThemisAnalysisLine(final char[] pBuffer,
                       final int pOffset,
                       final int pLen) {
        /* create the buffer */
        final char[] myBuffer = new char[pLen];
        System.arraycopy(pBuffer, pOffset, myBuffer, 0, pLen);
        theBuffer = CharBuffer.wrap(myBuffer);

        /* Strip any trailing comments */
        stripTrailingComments();

        /* Strip any leading/trailing whiteSpace */
        stripLeadingWhiteSpace();
        stripTrailingWhiteSpace();

        /* Strip Modifiers */
        theModifiers = new ArrayList<>();
        stripModifiers();
    }

    /**
     * Constructor.
     * @param pBuffer the buffer
     */
    private ThemisAnalysisLine(final CharBuffer pBuffer) {
        /* Store values */
        theBuffer = pBuffer;
        theModifiers = new ArrayList<>();
    }

    /**
     * Obtain the length.
     * @return the length
     */
    public int getLength() {
        return theBuffer.remaining();
    }

    /**
     * Set the new length.
     * @param pLen the length
     */
    private void setLength(final int pLen) {
        theBuffer.limit(theBuffer.position() + pLen);
    }

    /**
     * Adjust the position.
     * @param pAdjust the adjustment
     */
    private void adjustPosition(final int pAdjust) {
        theBuffer.position(theBuffer.position() + pAdjust);
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
        final int myLength = getLength();
        for (int i = 0; i < myLength - 1; i++) {
            /* If we have a line comment */
            if (theBuffer.charAt(i) == COMMENT
                && theBuffer.charAt(i + 1) == COMMENT) {
                /* Reset the length */
                setLength(i);
                break;
            }
        }
    }

    /**
     * Trim leading whiteSpace.
     */
    void stripLeadingWhiteSpace() {
        /* Loop through the characters */
        int myWhiteSpace = 0;
        final int myLength = getLength();
        for (int i = 0; i < myLength; i++) {
            /* Break loop if not whiteSpace */
            if (!Character.isWhitespace(theBuffer.charAt(i))) {
                break;
            }

            /* Increment count */
            myWhiteSpace++;
        }

        /* Adjust position */
        adjustPosition(myWhiteSpace);
    }

    /**
     * Trim trailing whiteSpace.
     */
    private void stripTrailingWhiteSpace() {
        /* Loop through the characters */
        int myWhiteSpace = 0;
        final int myLength = getLength();
        for (int i = myLength - 1; i >= 0; i--) {
            /* Break loop if not whiteSpace */
            if (!Character.isWhitespace(theBuffer.charAt(i))) {
                break;
            }

            /* Increment count */
            myWhiteSpace++;
        }

        /* Adjust length */
        setLength(myLength - myWhiteSpace);
    }

    /**
     * Mark the line.
     */
    void mark() {
        theBuffer.mark();
    }

    /**
     * Reset the line.
     */
    void reset() {
        theBuffer.reset();
    }

    /**
     * Strip Modifiers.
     */
    private void stripModifiers() {
        /* Loop through the modifiers */
        for (ThemisAnalysisModifier myModifier : ThemisAnalysisModifier.values()) {
            if (isStartedBy(myModifier.getModifier())) {
                /* Add modifier */
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
        final int myIdLen = pIdentifier.length();
        final int myLength = getLength();
        if (myIdLen > myLength) {
            return false;
        }

        /* Loop through the identifier */
        for (int i = 0; i < myIdLen; i++) {
            if (theBuffer.charAt(i) != pIdentifier.charAt(i)) {
                return false;
            }
        }

        /* Catch any solo modifiers */
        if (myIdLen == myLength) {
            throw new IllegalStateException("Modifier found without object");
        }

        /* The next character must be whitespace */
        if (Character.isWhitespace(theBuffer.charAt(myIdLen))) {
            adjustPosition(myIdLen + 1);
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
        final int myLength = getLength();
        for (int i = 0; i < myLength; i++) {
            /* if we have hit whiteSpace or a terminator */
            final char myChar = theBuffer.charAt(i);
            if (isTerminator(myChar)) {
                /* Strip out the characters */
                final CharBuffer myToken = theBuffer.subSequence(0, i);
                return myToken.toString();
            }
        }

        /* Whole buffer is the token */
        return toString();
    }

    /**
     * Is the character a token terminator?
     * @param pChar the character
     * @return true/false
     */
    private static boolean isTerminator(final char pChar) {
        /* WhiteSpace is a terminator */
        if (Character.isWhitespace(pChar)) {
            return true;
        }

        /* Check whether char is any of the terminators */
        return isInList(pChar, TERMINATORS);
    }

    /**
     * Is the character in the list?
     * @param pChar the character
     * @param pList the list of characters
     * @return true/false
     */
    private static boolean isInList(final char pChar,
                                    final char[] pList) {
        /* Loop through the list */
        for (char myChar : pList) {
            /* if we have matched */
            if (pChar == myChar) {
                return true;
            }
        }

        /* Not a terminator */
        return false;
    }

    /**
     * Strip data up to char.
     * @param pChar the end character
     * @return the stripped token
     */
    ThemisAnalysisLine stripUpToChar(final char pChar) {
        /* Loop through the buffer */
        final int myLength = getLength();
        for (int i = 0; i < myLength; i++) {
            /* if we have hit the char */
            final char myChar = theBuffer.charAt(i);
            if (myChar == pChar) {
                /* Strip out the characters */
                final CharBuffer myChars = theBuffer.subSequence(0, i);
                adjustPosition(i + 1);
                return new ThemisAnalysisLine(myChars);
            }
        }

        /* Didn't find the end character */
        throw new IllegalStateException("end character not found");
    }

    /**
     * find next char in set.
     * @param pChars the characters to search for
     * @return the next character (or CHAR_NULL)
     */
    char findNextCharInSet(final char[] pChars) {
        /* Loop through the buffer */
        final int myLength = getLength();
        for (int i = 0; i < myLength; i++) {
            /* if we have hit the char */
            final char myChar = theBuffer.charAt(i);
            if (isInList(myChar, pChars)) {
                return myChar;
            }
        }

        /* Didn't find the end character */
        return CHAR_NULL;
    }

    /**
     * Does line start with the sequence?
     * @param pSequence the sequence
     * @return true/false
     */
    boolean startsWithSequence(final CharSequence pSequence) {
        /* If the line is too short, just return */
        final int mySeqLen = pSequence.length();
        final int myLength = getLength();
        if (mySeqLen > myLength) {
            return false;
        }

        /* Loop through the sequence */
        for (int i = 0; i < mySeqLen; i++) {
            if (theBuffer.charAt(i) != pSequence.charAt(i)) {
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
        final int mySeqLen = pSequence.length();
        final int myLength = getLength();
        if (mySeqLen > myLength) {
            return false;
        }

        /* Loop through the buffer */
        final int myBase = myLength - mySeqLen;
        for (int i = 0; i < mySeqLen; i++) {
            /* Loop through the sequence */
            if (theBuffer.charAt(i + myBase) != pSequence.charAt(i)) {
                /* Not found */
                return false;
            }
        }

        /* found */
        return true;
    }

    /**
     * Strip the ending sequence.
     * @param pSequence the sequence
     */
    void stripStartSequence(final CharSequence pSequence) {
        /* If the line starts with the sequence */
        if (startsWithSequence(pSequence)) {
            /* adjust the length */
            adjustPosition(pSequence.length());
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
            setLength(getLength() - pSequence.length());
            stripTrailingWhiteSpace();
        }
    }

    @Override
    public String toString() {
        return theBuffer.toString();
    }
}
