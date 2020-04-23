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

/**
 * Array construct.
 */
public class ThemisAnalysisArray {
    /**
     * Start array.
     */
    static final char ARRAY_OPEN = '[';

    /**
     * End array.
     */
    static final char ARRAY_CLOSE = ']';

    /**
     * The array notation.
     */
    private final String theNotation;

    /**
     * Constructor.
     * @param pLine the line
     */
    ThemisAnalysisArray(final ThemisAnalysisLine pLine) {
        /* Loop while we have an array start */
        int myDepth = 0;
        while (pLine.startsWithChar(ARRAY_OPEN)) {
            pLine.stripStartChar(ARRAY_OPEN);
            if (!pLine.startsWithChar(ARRAY_CLOSE)) {
                throw new IllegalStateException("Bad array notation");
            }
            pLine.stripStartChar(ARRAY_CLOSE);
            myDepth++;
        }
        pLine.stripLeadingWhiteSpace();

        /* Build the array notation */
        final StringBuilder myBuilder = new StringBuilder();
        for (int i = 0; i < myDepth; i++) {
            myBuilder.append(ARRAY_OPEN).append(ARRAY_CLOSE);
        }
        theNotation = myBuilder.toString();
    }

    /**
     * Is the line a generic?
     * @param pLine the line
     * @return true/false
     */
    static boolean isArray(final ThemisAnalysisLine pLine) {
        /* If we are started with an ARRAY_OPEN */
        return pLine.startsWithChar(ARRAY_OPEN);
    }

    @Override
    public String toString() {
        return theNotation;
    }
}
