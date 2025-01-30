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
package net.sourceforge.joceanus.themis.statements;

import net.sourceforge.joceanus.themis.analysis.ThemisAnalysisChar;

/**
 * Assignment operation.
 */
public class ThemisStatementAssign
    implements ThemisStatementElement {
    /**
     * The Assignment Modifier interface.
     */
    public interface ThemisStatementAssignModifier {
    }

    /**
     * The assigned entity.
     */
    private final ThemisStatementEntity theAssignee;

    /**
     * The assignment modifier.
     */
    private final ThemisStatementAssignModifier theModifier;

    /**
     * The expression.
     */
    private final ThemisStatementElement theExpression;

    /**
     * The format.
     */
    private String theFormat;

    /**
     * Constructor.
     * @param pAssignee the assignee
     * @param pModifier the modifier,
     * @param pExpression the expression
     */
    ThemisStatementAssign(final ThemisStatementEntity pAssignee,
                          final ThemisStatementAssignModifier pModifier,
                          final ThemisStatementElement pExpression) {
        theAssignee = pAssignee;
        theModifier = pModifier;
        theExpression = pExpression;
    }

    @Override
    public String toString() {
        /* If we have not yet built the format */
        if (theFormat == null) {
            /* Build the format */
            final StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(theAssignee);
            myBuilder.append(ThemisAnalysisChar.BLANK);
            if (theModifier != null) {
                myBuilder.append(theModifier);
            }
            myBuilder.append(ThemisAnalysisChar.EQUAL);
            myBuilder.append(ThemisAnalysisChar.BLANK);
            myBuilder.append(theExpression);
            theFormat = myBuilder.toString();
        }

        /* Return the format */
        return theFormat;
    }
}
