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

import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.themis.ThemisDataException;

/**
 * Array construct.
 */
public class ThemisAnalysisArray {
    /**
     * The varArgs notation.
     */
    static final String VARARGS = "...";

    /**
     * The array notation.
     */
    private final String theNotation;

    /**
     * Constructor.
     * @param pLine the line
     * @throws OceanusException on error
     */
    ThemisAnalysisArray(final ThemisAnalysisLine pLine) throws OceanusException {
        /* If the token is VARARGS */
        if (VARARGS.equals(pLine.peekNextToken())) {
            pLine.stripNextToken();
            theNotation = VARARGS;
            return;
        }

        /* Loop while we have an array start */
        int myDepth = 0;
        while (pLine.startsWithChar(ThemisAnalysisChar.ARRAY_OPEN)) {
            pLine.stripStartChar(ThemisAnalysisChar.ARRAY_OPEN);
            if (!pLine.startsWithChar(ThemisAnalysisChar.ARRAY_CLOSE)) {
                throw new ThemisDataException("Bad array notation");
            }
            pLine.stripStartChar(ThemisAnalysisChar.ARRAY_CLOSE);
            myDepth++;
        }
        pLine.stripLeadingWhiteSpace();

        /* Build the array notation */
        final StringBuilder myBuilder = new StringBuilder();
        for (int i = 0; i < myDepth; i++) {
            myBuilder.append(ThemisAnalysisChar.ARRAY_OPEN).append(ThemisAnalysisChar.ARRAY_CLOSE);
        }
        theNotation = myBuilder.toString();
    }

    /**
     * Is the line a generic?
     * @param pLine the line
     * @return true/false
     */
    static boolean isArray(final ThemisAnalysisLine pLine) {
        /* If we are started with an ARRAY_OPEN or we have args */
        return pLine.startsWithChar(ThemisAnalysisChar.ARRAY_OPEN)
                || VARARGS.equals(pLine.peekNextToken());
    }

    @Override
    public String toString() {
        return theNotation;
    }
}
