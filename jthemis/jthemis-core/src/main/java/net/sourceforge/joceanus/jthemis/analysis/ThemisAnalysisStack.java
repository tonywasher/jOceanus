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
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisScanner.ThemisAnalysisSource;

/**
 * Stack of Analysis Elements.
 */
public class ThemisAnalysisStack
    implements ThemisAnalysisSource {
    /**
     * The stack of lines.
     */
    private final Deque<ThemisAnalysisElement> theStack;

    /**
     * Constructor.
     * @param pStack the stack
     */
    ThemisAnalysisStack(final Deque<ThemisAnalysisElement> pStack) {
        theStack = pStack;
    }

    /**
     * Constructor.
     * @param pElement the element
     */
    ThemisAnalysisStack(final ThemisAnalysisElement pElement) {
        theStack = new ArrayDeque<>();
        theStack.add(pElement);
    }

    @Override
    public boolean hasLines() {
        return !theStack.isEmpty();
    }

    @Override
    public ThemisAnalysisElement popNextLine() throws OceanusException {
        /* Check that there is a line to pop */
        if (theStack.isEmpty()) {
            throw new ThemisDataException("No more lines");
        }

        /* Access the first line and remove from the list */
        return theStack.removeFirst();
    }

    @Override
    public void pushLine(final ThemisAnalysisElement pLine) {
        /* Insert the line at the front of the stack */
        theStack.offerFirst(pLine);
    }

    /**
     * Is the stack empty?
     * @return true/false
     */
    public boolean isEmpty() {
        return theStack.isEmpty()
                || (theStack.size() == 1 && theStack.peekFirst().toString().length() == 0);
    }

    /**
     * Obtain the size of the stack.
     * @return the size
     */
    public int size() {
        return theStack.size();
    }

    /**
     * Strip parentheses.
     * @return a new stack with stripped parentheses
     * @throws OceanusException on error
     */
    public ThemisAnalysisStack extractParentheses() throws OceanusException {
        /* Extract the parenthesised block */
        final ThemisAnalysisLine myLine = (ThemisAnalysisLine) popNextLine();
        final ThemisAnalysisScanner myScanner = new ThemisAnalysisScanner(this);
        final Deque<ThemisAnalysisElement> myParenthesised = myScanner.scanForParenthesis(myLine);
        final ThemisAnalysisStack myResult = new ThemisAnalysisStack(myParenthesised);

        /* Strip the parentheses */
        myResult.stripStartChar(ThemisAnalysisChar.PARENTHESIS_OPEN);
        myResult.stripEndChar(ThemisAnalysisChar.PARENTHESIS_CLOSE);

        /* Return the result */
        return myResult;
    }

    /**
     * Strip parentheses.
     * @return a new stack with stripped parentheses
     * @throws OceanusException on error
     */
    public ThemisAnalysisStack extractArray() throws OceanusException {
        /* Extract the parenthesised block */
        final ThemisAnalysisLine myLine = (ThemisAnalysisLine) popNextLine();
        final ThemisAnalysisScanner myScanner = new ThemisAnalysisScanner(this);
        final Deque<ThemisAnalysisElement> myArray = myScanner.scanForArray(myLine);
        final ThemisAnalysisStack myResult = new ThemisAnalysisStack(myArray);

        /* Strip the parentheses */
        myResult.stripStartChar(ThemisAnalysisChar.ARRAY_OPEN);
        myResult.stripEndChar(ThemisAnalysisChar.ARRAY_CLOSE);

        /* Return the result */
        return myResult;
    }

    /**
     * Does the stack start with this character?
     * @param pChar the character
     * @return true/false
     */
    public boolean startsWithChar(final char pChar) {
        return !theStack.isEmpty()
                && ((ThemisAnalysisLine) theStack.peekFirst()).startsWithChar(pChar);
    }

    /**
     * Strip the starting character.
     * @param pChar the character
     */
    public void stripStartChar(final char pChar) {
        if (!theStack.isEmpty()) {
            ((ThemisAnalysisLine) theStack.peekFirst()).stripStartChar(pChar);
        }
    }

    /**
     * Strip the ending character.
     * @param pChar the character
     */
    public void stripEndChar(final char pChar) {
        if (!theStack.isEmpty()) {
            ((ThemisAnalysisLine) theStack.peekLast()).stripEndChar(pChar);
        }
    }

    /**
     * Rebuild the stack.
     * @param pSource the source to rebuild from
     */
    public void rebuild(final Deque<ThemisAnalysisElement> pSource) {
        /* Clear the existing stack */
        theStack.clear();

        /* Loop through the source */
        for (ThemisAnalysisElement myElement : pSource) {
            theStack.offerLast(myElement);
        }
    }

    @Override
    public String toString() {
        return ThemisAnalysisBuilder.formatLines(theStack);
    }
}
