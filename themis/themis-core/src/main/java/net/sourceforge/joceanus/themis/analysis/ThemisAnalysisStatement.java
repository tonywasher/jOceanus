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
package net.sourceforge.joceanus.themis.analysis;

import java.util.Deque;
import java.util.Iterator;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.themis.ThemisDataException;

/**
 * Statements.
 */
public class ThemisAnalysisStatement
    implements ThemisAnalysisProcessed {
    /**
     * StatementHolder interface.
     */
    public interface ThemisAnalysisStatementHolder {
        /**
         * Obtain statement iterator.
         * @return the iterator
         */
        Iterator<ThemisAnalysisStatement> statementIterator();
    }

    /**
     * The control.
     */
    private final ThemisAnalysisKeyWord theControl;

    /**
     * The lines.
     */
    private final ThemisAnalysisStack theLines;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the line
     * @throws OceanusException on error
     */
    ThemisAnalysisStatement(final ThemisAnalysisParser pParser,
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
    ThemisAnalysisStatement(final ThemisAnalysisParser pParser,
                            final ThemisAnalysisKeyWord pControl,
                            final ThemisAnalysisLine pLine) throws OceanusException {
        this(pControl, ThemisAnalysisBuilder.parseTrailers(pParser, pLine));
        checkSeparator();
    }

    /**
     * Constructor.
     * @param pEmbedded the embedded block
     * @throws OceanusException on error
     */
    ThemisAnalysisStatement(final ThemisAnalysisEmbedded pEmbedded) throws OceanusException {
        this(null, pEmbedded);
    }

    /**
     * Constructor.
     * @param pControl the control keyword
     * @param pEmbedded the embedded block
     * @throws OceanusException on error
     */
    ThemisAnalysisStatement(final ThemisAnalysisKeyWord pControl,
                            final ThemisAnalysisEmbedded pEmbedded) throws OceanusException {
        this(pControl, new ThemisAnalysisStack(pEmbedded));
        pEmbedded.postProcessLines();
    }

    /**
     * Constructor.
     * @param pParams the parameters
     */
    ThemisAnalysisStatement(final Deque<ThemisAnalysisElement> pParams) {
        this(null, pParams);
    }

    /**
     * Constructor.
     * @param pStack the stack
     */
    ThemisAnalysisStatement(final ThemisAnalysisStack pStack) {
        this(null, pStack);
    }

    /**
     * Constructor.
     * @param pControl the control
     * @param pParams the parameters
     */
    ThemisAnalysisStatement(final ThemisAnalysisKeyWord pControl,
                            final Deque<ThemisAnalysisElement> pParams) {
        theControl = pControl;
        theLines = new ThemisAnalysisStack(pParams);
    }

    /**
     * Constructor.
     * @param pControl the control keyword
     * @param pStack the stack
     */
    ThemisAnalysisStatement(final ThemisAnalysisKeyWord pControl,
                            final ThemisAnalysisStack pStack) {
        theControl = pControl;
        theLines = pStack;
    }

    /**
     * Test for separator.
     * @throws OceanusException on error
     */
    void checkSeparator() throws OceanusException {
        final ThemisAnalysisScanner myScanner = new ThemisAnalysisScanner(theLines);
        final boolean isSep = myScanner.checkForSeparator(ThemisAnalysisChar.COMMA);
        if (isSep) {
            throw new ThemisDataException("Multi-statement");
        }
    }

    @Override
    public int getNumLines() {
        return theLines.size();
    }

    /**
     * Are there null parameters?
     * @return true/false
     */
    public boolean nullParameters() {
        return theLines.isEmpty();
    }

    /**
     * Obtain the embedded statement (if any).
     * @return the embedded statement (or null)
     */
    public ThemisAnalysisElement getEmbedded() {
        final ThemisAnalysisElement myLast = theLines.peekLastLine();
        return myLast instanceof ThemisAnalysisEmbedded
                ? ((ThemisAnalysisEmbedded) myLast).getContents().peekFirst()
                : null;
    }

    @Override
    public String toString() {
        final String myParms = theLines.toString();
        return theControl == null ? myParms : theControl + " " + myParms;
    }
}
