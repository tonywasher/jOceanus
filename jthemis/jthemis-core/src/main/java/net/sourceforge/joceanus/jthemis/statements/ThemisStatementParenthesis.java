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
package net.sourceforge.joceanus.jthemis.statements;

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisChar;

/**
 * Parenthesis.
 */
public class ThemisStatementParenthesis
        implements ThemisStatementElement {
    /**
     * Right-hand side.
     */
    private final ThemisStatementElement theExpression;

    /**
     * The format.
     */
    private String theFormat;

    /**
     * Constructor.
     */
    ThemisStatementParenthesis() {
        theExpression = null;
    }

    @Override
    public String toString() {
        /* If we have not yet built the format */
        if (theFormat == null) {
            /* Build the format */
            theFormat = String.valueOf(ThemisAnalysisChar.PARENTHESIS_OPEN)
                    + theExpression
                    + ThemisAnalysisChar.PARENTHESIS_CLOSE;
        }

        /* Return the format */
        return theFormat;
    }
}
