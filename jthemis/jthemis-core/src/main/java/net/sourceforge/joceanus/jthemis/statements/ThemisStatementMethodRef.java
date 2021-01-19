/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2021 Tony Washer
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
 * Method Reference.
 */
public class ThemisStatementMethodRef
        implements ThemisStatementElement {
    /**
     * Seperator.
     */
    private static final String SEP = "::";

    /**
     * Context.
     */
    private final ThemisStatementEntity theContext;

    /**
     * Method.
     */
    private final ThemisStatementEntity theMethod;

    /**
     * The format.
     */
    private String theFormat;

    /**
     * Constructor.
     * @param pContext the context
     * @param pMethod the method
     */
    ThemisStatementMethodRef(final ThemisStatementEntity pContext,
                             final ThemisStatementEntity pMethod) {
        theContext = pContext;
        theMethod = pMethod;
    }

    @Override
    public String toString() {
        /* If we have not yet built the format */
        if (theFormat == null) {
            /* Build the format */
            theFormat = String.valueOf(theContext)
                    + SEP
                    + theMethod;
        }

        /* Return the format */
        return theFormat;
    }
}
