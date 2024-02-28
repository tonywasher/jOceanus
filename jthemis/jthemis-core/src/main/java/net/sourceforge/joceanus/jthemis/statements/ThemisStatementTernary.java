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
package net.sourceforge.joceanus.jthemis.statements;

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisChar;

/**
 * Ternary.
 */
public class ThemisStatementTernary {
    /**
     * The condition.
     */
    private final ThemisStatementElement theCondition;

    /**
     * The then expression.
     */
    private final ThemisStatementElement theThen;

    /**
     * The else expression.
     */
    private final ThemisStatementElement theElse;

    /**
     * The format.
     */
    private String theFormat;

    /**
     * Constructor.
     * @param pCondition the condition
     * @param pThen the then expression
     * @param pElse the else expression
     */
    ThemisStatementTernary(final ThemisStatementElement pCondition,
                           final ThemisStatementElement pThen,
                           final ThemisStatementElement pElse) {
        theCondition = pCondition;
        theThen = pThen;
        theElse = pElse;
    }

    @Override
    public String toString() {
        /* If we have not yet built the format */
        if (theFormat == null) {
            /* Build the format */
            theFormat = String.valueOf(theCondition)
                    + ThemisAnalysisChar.BLANK
                    + ThemisStatementLogical.THEN
                    + ThemisAnalysisChar.BLANK
                    + theThen
                    + ThemisAnalysisChar.BLANK
                    + ThemisStatementLogical.ELSE
                    + ThemisAnalysisChar.BLANK
                    + theElse;
        }

        /* Return the format */
        return theFormat;
    }
}
