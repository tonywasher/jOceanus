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
 * Parenthesis utilities.
 */
public final class ThemisAnalysisParenthesis {
    /**
     * Private constructor.
     */
    private ThemisAnalysisParenthesis() {
    }

    /**
     * Strip parenthesis contents from line.
     * @param pLine the initial enum line
     * @return the content
     */
    static ThemisAnalysisLine stripParenthesisContents(final ThemisAnalysisLine pLine) {
        /* Find the end of the generic sequence */
        final int myEnd = pLine.findEndOfNestedSequence(0, 0,  ThemisAnalysisChar.PARENTHESIS_CLOSE, ThemisAnalysisChar.PARENTHESIS_OPEN);
        if (myEnd < 0) {
            throw new IllegalStateException("End character not found");
        }

        /* Obtain the contents */
        final ThemisAnalysisLine myContents = pLine.stripUpToPosition(myEnd);
        myContents.stripStartChar(ThemisAnalysisChar.PARENTHESIS_OPEN);
        myContents.stripEndChar(ThemisAnalysisChar.PARENTHESIS_CLOSE);
        return myContents;
    }
}
