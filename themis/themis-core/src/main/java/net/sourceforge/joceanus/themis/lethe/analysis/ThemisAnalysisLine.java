/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.themis.lethe.analysis;

import java.nio.CharBuffer;
import java.util.Deque;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;
import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisGeneric.ThemisAnalysisGenericBase;

/**
 * Line buffer.
 */
public class ThemisAnalysisLine
    implements ThemisAnalysisElement {
    /**
     * The token terminators.
     */
    private static final char[] TERMINATORS = {
        ThemisAnalysisChar.PARENTHESIS_OPEN,
        ThemisAnalysisChar.PARENTHESIS_CLOSE,
        ThemisAnalysisChar.GENERIC_OPEN,
        ThemisAnalysisChar.COMMA,
        ThemisAnalysisChar.SEMICOLON,
        ThemisAnalysisChar.COLON,
        ThemisAnalysisChar.ARRAY_OPEN
    };

    /**
     * The line buffer.
     */
    private final CharBuffer theBuffer;

    /**
     * The properties.
     */
    private ThemisAnalysisProperties theProperties;

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

        /* Strip any leading/trailing whiteSpace */
        stripLeadingWhiteSpace();
        stripTrailingWhiteSpace();

        /* Set null properties */
        theProperties = ThemisAnalysisProperties.NULL;
    }

    /**
     * Constructor.
     * @param pBuffer the buffer
     */
    ThemisAnalysisLine(final CharBuffer pBuffer) {
        /* Store values */
        theBuffer = pBuffer;

        /* Set null properties */
        theProperties = ThemisAnalysisProperties.NULL;
    }

    /**
     * Constructor.
     * @param pLine the line
     */
    ThemisAnalysisLine(final ThemisAnalysisLine pLine) {
        /* Store values */
        final CharBuffer myBuffer = pLine.theBuffer;
        theBuffer = myBuffer.subSequence(0, myBuffer.length());

        /* Set null properties */
        theProperties = ThemisAnalysisProperties.NULL;
    }

    /**
     * Constructor.
     * @param pLines the lines
     */
    ThemisAnalysisLine(final Deque<ThemisAnalysisElement> pLines) {
        /* Create a stringBuilder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Loop through the lines */
        for (ThemisAnalysisElement myLine : pLines) {
            myBuilder.append(((ThemisAnalysisLine) myLine).theBuffer);
            myBuilder.append(ThemisAnalysisChar.BLANK);
        }

        /* Create the buffer */
        theBuffer = CharBuffer.wrap(myBuilder.toString());
        stripLeadingWhiteSpace();
        stripTrailingWhiteSpace();

        /* Set null properties */
        theProperties = ThemisAnalysisProperties.NULL;
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
     * Obtain the character at the given position.
     * @param pIndex the position of the character
     * @return the character
     */
    char charAt(final int pIndex) {
        return theBuffer.charAt(pIndex);
    }

    /**
     * Obtain the properties.
     * @return the properties
     */
    public ThemisAnalysisProperties getProperties() {
        return theProperties;
    }

    /**
     * Strip trailing comments.
     * @throws OceanusException on error
     */
    void stripTrailingComments() throws OceanusException {
        /* Loop through the characters */
        final int myLength = getLength();
        int mySkipped = 0;
        for (int i = 0; i < myLength - mySkipped - 1; i++) {
            /* Access position and current character */
            final int myPos = i + mySkipped;
            final char myChar = theBuffer.charAt(myPos);

            /* If this is a single/double quote */
            if (myChar == ThemisAnalysisChar.SINGLEQUOTE
                    || myChar == ThemisAnalysisChar.DOUBLEQUOTE) {
                final int myEnd = findEndOfQuotedSequence(myPos);
                mySkipped += myEnd - myPos;

            /* If we have a line comment */
            } else if (myChar == ThemisAnalysisChar.COMMENT
                && theBuffer.charAt(myPos + 1) == ThemisAnalysisChar.COMMENT) {
                /* Reset the length */
                setLength(myPos);
                stripTrailingWhiteSpace();
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
     * @throws OceanusException on error
     */
    void stripModifiers() throws OceanusException {
        /* Loop while we find a modifier */
        boolean bContinue = true;
        while (bContinue) {
            /* If we have a generic variable list */
            if (ThemisAnalysisGeneric.isGeneric(this)) {
                /* Declare them to the properties */
                theProperties = theProperties.setGenericVariables(new ThemisAnalysisGenericBase(this));
            }

            /* Access the next token */
            final String nextToken = peekNextToken();
            bContinue = false;

            /* If this is a modifier */
            final ThemisAnalysisModifier myModifier = ThemisAnalysisModifier.findModifier(nextToken);
            if (myModifier != null) {
                /* Set modifier */
                theProperties = theProperties.setModifier(myModifier);
                stripStartSequence(nextToken);
                bContinue = true;
            }
        }
    }

    /**
     * Does line start with identifier?
     * @param pIdentifier the identifier
     * @return true/false
     * @throws OceanusException on error
     */
    boolean isStartedBy(final String pIdentifier) throws OceanusException {
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
            throw new ThemisDataException("Modifier found without object");
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
     * Strip NextToken.
     * @return the next token
     */
    String stripLastToken() {
        /* Access the next token */
        final String myToken = peekLastToken();
        stripEndSequence(myToken);
        return myToken;
    }

    /**
     * Peek lastToken.
     * @return the last token
     */
    String peekLastToken() {
        /* Loop through the buffer */
        final int myLength = getLength();
        for (int i = myLength - 1; i >= 0; i--) {
            /* if we have hit whiteSpace or a terminator */
            final char myChar = theBuffer.charAt(i);
            if (isTerminator(myChar)) {
                /* Strip out the characters */
                final CharBuffer myToken = theBuffer.subSequence(i + 1, myLength);
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
     * Strip data up to position.
     * @param pPosition the position to strip to (inclusive)
     * @return the stripped line
     */
    ThemisAnalysisLine stripUpToPosition(final int pPosition) {
        /* Obtain the new buffer */
        final CharBuffer myChars = theBuffer.subSequence(0, pPosition + 1);
        adjustPosition(pPosition + 1);
        stripLeadingWhiteSpace();
        return new ThemisAnalysisLine(myChars);
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
     * Does line start with the character?
     * @param pChar the character
     * @return true/false
     */
    boolean startsWithChar(final char pChar) {
        /* If the line is too short, just return false */
        final int myLength = getLength();
        if (myLength == 0) {
            return false;
        }

        /* Test the character */
        return theBuffer.charAt(0) == pChar;
    }

    /**
     * Strip the starting sequence.
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
     * Strip the starting character.
     * @param pChar the character
     */
    void stripStartChar(final char pChar) {
        /* If the line starts with the character */
        if (startsWithChar(pChar)) {
            /* adjust the length */
            adjustPosition(1);
            stripLeadingWhiteSpace();
        }
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
     * Does line end with the character?
     * @param pChar the character
     * @return true/false
     */
    boolean endsWithChar(final char pChar) {
        /* If the line is too short, just return false */
        final int myLength = getLength();
        if (myLength == 0) {
            return false;
        }

        /* Test the character */
        return theBuffer.charAt(myLength - 1) == pChar;
    }

    /**
     * Strip the starting sequence.
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

    /**
     * Strip the ending character.
     * @param pChar the character
     */
    void stripEndChar(final char pChar) {
        /* If the line ends with the sequence */
        if (endsWithChar(pChar)) {
            /* adjust the length */
            setLength(getLength() - 1);
            stripTrailingWhiteSpace();
        }
    }

    /**
     * Find end of nested sequence, allowing for escaped quotes.
     * <p>
     *     To enable distinction between finding the end of the sequence from still being nested, the nestLevel
     *     is negative. Hence a result that is negative indicates that the sequence is continuing.
     * </p>
     * @param pStart the start position
     * @param pLevel the current nestLevel (negative value)
     * @param pTerm the end nest character
     * @param pNest the start nest character
     * @return the position of the end of the nest if (non-negative), or nestLevel (negative) if not terminated.
     * @throws OceanusException on error
     */
    int findEndOfNestedSequence(final int pStart,
                                final int pLevel,
                                final char pTerm,
                                final char pNest) throws OceanusException {
        /* Access details of quote */
        final int myLength = getLength();

        /* Loop through the line */
        boolean maybeComment = false;
        int myNested = pLevel;
        int mySkipped = 0;
        for (int i = pStart; i < myLength - mySkipped; i++) {
            /* Access position and current character */
            final int myPos = i + mySkipped;
            final char myChar = theBuffer.charAt(myPos);

            /* If this is the comment character */
            if (myChar == ThemisAnalysisChar.COMMENT) {
                /* Flip flag */
                maybeComment = !maybeComment;

                /* If we have double comment, break loop */
                if (!maybeComment) {
                    break;
                }
            } else {
                maybeComment = false;
            }

            /* If this is a single/double quote */
            if (myChar == ThemisAnalysisChar.SINGLEQUOTE
                    || myChar == ThemisAnalysisChar.DOUBLEQUOTE) {
                /* Find the end of the sequence and skip the quotes */
                final int myEnd = findEndOfQuotedSequence(myPos);
                mySkipped += myEnd - myPos;

                /* If we should be increasing the nest level */
            } else if (myChar == pNest) {
                myNested--;

                /* If we should be decreasing the nest level */
            } else if (myChar == pTerm) {
                myNested++;

                /* Return current position if we have finished */
                if (myNested == 0) {
                    return myPos;
                }
            }
        }

        /* Return the new nest level */
        return myNested;
    }

    /**
     * Find end of single/double quoted sequence, allowing for escaped quote.
     * <p>
     *     Note that a quoted sequence cannot span lines.
     * </p>
     * @param pStart the start position of the quote
     * @return the end position of the sequence.
     * @throws OceanusException on error
     */
    int findEndOfQuotedSequence(final int pStart) throws OceanusException {
        /* Access details of single/double quote */
        final int myLength = getLength();
        final char myQuote = theBuffer.charAt(pStart);

        /* Loop through the characters */
        int mySkipped = 0;
        for (int i = pStart + 1; i < myLength - mySkipped; i++) {
            /* Access position and current character */
            final int myPos = i + mySkipped;
            final char myChar = theBuffer.charAt(myPos);

            /* Skip escaped character */
            if (myChar == ThemisAnalysisChar.ESCAPE) {
                mySkipped++;

                /* Return current position if we have finished */
            } else if (myChar == myQuote) {
                return myPos;
            }
        }

        /* We should always be terminated */
        throw new ThemisDataException("Unable to find end of quote in line");
    }

    @Override
    public String toString() {
        return theBuffer.toString();
    }
}
