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

import java.util.Deque;

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisChar;

/**
 * Assignment operation.
 */
public class ThemisStatementMethodCall
        implements ThemisStatementElement {
    /**
     * The assigned entity.
     */
    private final ThemisStatementElement theMethod;

    /**
     * The parameters.
     */
    private final Deque<ThemisStatementElement> theParameters;

    /**
     * The format.
     */
    private String theFormat;

    /**
     * Constructor.
     * @param pMethod the method
     * @param pParams the parameters
     */
    ThemisStatementMethodCall(final ThemisStatementEntity pMethod,
                              final Deque<ThemisStatementElement> pParams) {
        theMethod = pMethod;
        theParameters = pParams;
    }

    @Override
    public String toString() {
        /* If we have not yet built the format */
        if (theFormat == null) {
            /* Build the format */
            final StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(theMethod);
            myBuilder.append(ThemisAnalysisChar.PARENTHESIS_OPEN);
            boolean notFirst = false;
            for (ThemisStatementElement myParam : theParameters) {
                if (notFirst) {
                    myBuilder.append(ThemisAnalysisChar.COMMA);
                }
                notFirst = true;
                myBuilder.append(myParam);
            }
            myBuilder.append(ThemisAnalysisChar.PARENTHESIS_CLOSE);
            theFormat = myBuilder.toString();
        }

        /* Return the format */
        return theFormat;
    }
}
