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
 * Generic construct.
 */
public class ThemisAnalysisGeneric {
    /**
     * The contents of the generic.
     */
    private final ThemisAnalysisLine theContents;

    /**
     * Constructor.
     * @param pLine the line
     */
    ThemisAnalysisGeneric(final ThemisAnalysisLine pLine) {
        /* Find the end of the generic sequence */
        final int myEnd = pLine.findEndOfNestedSequence(0, 0,  ThemisAnalysisChar.GENERIC_CLOSE, ThemisAnalysisChar.GENERIC_OPEN);
        if (myEnd < 0) {
            throw new IllegalStateException("End character not found");
        }

        /* Obtain the contents */
        theContents = pLine.stripUpToPosition(myEnd);
        theContents.stripStartChar(ThemisAnalysisChar.GENERIC_OPEN);
        theContents.stripEndChar(ThemisAnalysisChar.GENERIC_CLOSE);
    }

    /**
     * Is the line a generic?
     * @param pLine the line
     * @return true/false
     */
    static boolean isGeneric(final ThemisAnalysisLine pLine) {
        /* If we are started with a GENERIC_OPEN */
        return pLine.startsWithChar(ThemisAnalysisChar.GENERIC_OPEN);
    }

    @Override
    public String toString() {
        return "" + ThemisAnalysisChar.GENERIC_OPEN + theContents + ThemisAnalysisChar.GENERIC_CLOSE;
    }
}
