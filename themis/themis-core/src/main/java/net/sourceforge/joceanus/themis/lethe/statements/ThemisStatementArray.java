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
package net.sourceforge.joceanus.themis.lethe.statements;

import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisChar;

/**
 * Array.
 */
public class ThemisStatementArray
        implements ThemisStatementElement {
    /**
     * Owner.
     */
    private final ThemisStatementElement theOwner;

    /**
     * Contents.
     */
    private final ThemisStatementElement theContents;

    /**
     * The format.
     */
    private String theFormat;

    /**
     * Constructor.
     * @param pOwner the owner
     * @param pContents the contents
     */
    ThemisStatementArray(final ThemisStatementElement pOwner,
                         final ThemisStatementElement pContents) {
        theOwner = pOwner;
        theContents = pContents;
    }

    @Override
    public String toString() {
        /* If we have not yet built the format */
        if (theFormat == null) {
            /* Build the format */
            theFormat = String.valueOf(theOwner)
                    + ThemisAnalysisChar.ARRAY_OPEN
                    + theContents
                    + ThemisAnalysisChar.ARRAY_CLOSE;
        }

        /* Return the format */
        return theFormat;
    }
}
