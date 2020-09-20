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

/**
 * Binary operation.
 */
public class ThemisStatementUnaryOp
        implements ThemisStatementElement {
    /**
     * Left-hand side.
     */
    private final ThemisStatementUnary theOperator;

    /**
     * Left/right.
     */
    private final boolean isLeft;

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
    ThemisStatementUnaryOp() {
        isLeft = false;
        theOperator = null;
        theExpression = null;
    }

    @Override
    public String toString() {
        /* If we have not yet built the format */
        if (theFormat == null) {
            /* Build the format */
            final StringBuilder myBuilder = new StringBuilder();
            if (isLeft) {
                myBuilder.append(theOperator);
            }
            myBuilder.append(theExpression);
            if (!isLeft) {
                myBuilder.append(theOperator);
            }
            theFormat = myBuilder.toString();
        }

        /* Return the format */
        return theFormat;
    }
}
