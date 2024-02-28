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
 * Binary operation.
 */
public class ThemisStatementBinaryOp
    implements ThemisStatementElement {
    /**
     * Left-hand side.
     */
    private final ThemisStatementElement theLeft;

    /**
     * Operator.
     */
    private final ThemisStatementOperator theOperator;

    /**
     * Right-hand side.
     */
    private final ThemisStatementElement theRight;

    /**
     * The format.
     */
    private String theFormat;

    /**
     * Constructor.
     * @param pLeft the left expression
     * @param pOperator the operator
     * @param pRight the right expression
     */
    ThemisStatementBinaryOp(final ThemisStatementElement pLeft,
                            final ThemisStatementOperator pOperator,
                            final ThemisStatementElement pRight) {
        theLeft = pLeft;
        theOperator = pOperator;
        theRight = pRight;
    }

    @Override
    public String toString() {
        /* If we have not yet built the format */
        if (theFormat == null) {
            /* Build the format */
            theFormat = String.valueOf(theLeft)
                    + ThemisAnalysisChar.BLANK
                    + theOperator
                    + ThemisAnalysisChar.BLANK
                    + theRight;
        }

        /* Return the format */
        return theFormat;
    }
}
