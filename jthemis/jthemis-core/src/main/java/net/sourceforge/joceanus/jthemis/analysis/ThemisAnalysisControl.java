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

import java.util.Deque;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Control Statements.
 */
public class ThemisAnalysisControl
    implements ThemisAnalysisProcessed {
    /**
     * The control.
     */
    private final ThemisAnalysisKeyWord theControl;

    /**
     * The line.
     */
    private final Deque<ThemisAnalysisElement> theParameters;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the line
     * @throws OceanusException on error
     */
    ThemisAnalysisControl(final ThemisAnalysisParser pParser,
                          final ThemisAnalysisLine pLine) throws OceanusException {
        this(pParser, null, pLine);
    }

    /**
     * Constructor.
     * @param pParser the parser
     * @param pControl the control
     * @param pLine the line
     * @throws OceanusException on error
     */
    ThemisAnalysisControl(final ThemisAnalysisParser pParser,
                          final ThemisAnalysisKeyWord pControl,
                          final ThemisAnalysisLine pLine) throws OceanusException {
        /* Store parameters */
        theControl = pControl;
        theParameters = ThemisAnalysisBuilder.parseTrailers(pParser, pLine);
    }

    @Override
    public int getNumLines() {
        return theParameters.size();
    }

    @Override
    public String toString() {
        final String myParms = ThemisAnalysisBuilder.formatLines(theParameters);
        return theControl == null ? myParms : theControl + " " + myParms;
    }
}
