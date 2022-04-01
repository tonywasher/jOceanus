/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2022 Tony Washer
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
 * Chain operation.
 */
public class ThemisStatementChain
        implements ThemisStatementElement {
    /**
     * The method call.
     */
    private final ThemisStatementMethodCall theMethodCall;

    /**
     * the Chain.
     */
    private final ThemisStatementElement theChain;

    /**
     * The format.
     */
    private String theFormat;

    /**
     * Constructor.
     * @param pMethodCall the nethod call
     * @param pChain the chain
     */
    ThemisStatementChain(final ThemisStatementMethodCall pMethodCall,
                         final ThemisStatementElement pChain) {
        theMethodCall = pMethodCall;
        theChain = pMethodCall;
    }

    @Override
    public String toString() {
        /* If we have not yet built the format */
        if (theFormat == null) {
            /* Build the format */
            theFormat = String.valueOf(theMethodCall)
                    + ThemisAnalysisChar.PERIOD
                    + theChain;
        }

        /* Return the format */
        return theFormat;
    }
}
