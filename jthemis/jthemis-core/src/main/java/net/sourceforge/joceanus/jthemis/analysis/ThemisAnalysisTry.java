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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Try construct.
 */
public class ThemisAnalysisTry
        implements ThemisAnalysisContainer {
    /**
     * The headers.
     */
    private final List<ThemisAnalysisElement> theHeaders;

    /**
     * The elements.
     */
    private final List<ThemisAnalysisElement> theProcessed;

    /**
     * The catch clause(s).
     */
    private final ThemisAnalysisCatch theCatch;

    /**
     * The finally clause.
     */
    private final ThemisAnalysisFinally theFinally;

    /**
     * The dataTypes.
     */
    private final Map<String, ThemisAnalysisDataType> theDataTypes;

    /**
     * The number of lines in the class.
     */
    private final int theNumLines;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial try line
     */
    ThemisAnalysisTry(final ThemisAnalysisParser pParser,
                      final ThemisAnalysisLine pLine) {
        /* Store dataTypes */
        theDataTypes = pParser.getDataTypes();

        /* Create the arrays */
        theHeaders = ThemisAnalysisBody.processHeaders(pParser, pLine);
        final List<ThemisAnalysisElement> myLines = ThemisAnalysisBody.processBody(pParser);
        theNumLines = myLines.size();

        /* Look for catch clauses */
        theCatch = (ThemisAnalysisCatch) pParser.processExtra(ThemisAnalysisKeyWord.CATCH);

        /* Look for finally clauses */
        theFinally = (ThemisAnalysisFinally) pParser.processExtra(ThemisAnalysisKeyWord.FINALLY);

        /* Create a parser */
        theProcessed = new ArrayList<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theProcessed, theDataTypes);
        myParser.postProcessLines();
    }

    @Override
    public Map<String, ThemisAnalysisDataType> getDataTypes() {
        return theDataTypes;
    }

    @Override
    public List<ThemisAnalysisElement> getProcessed() {
        return theProcessed;
    }

    @Override
    public void postProcessExtras() {
        /* Process the catch clause if required */
        if (theCatch != null) {
            theCatch.postProcessLines();
        }

        /* Process the finally clause if required */
        if (theFinally != null) {
            theFinally.postProcessLines();
        }
    }

    /**
     * Obtain the number of lines in the block.
     * @return the number of lines
     */
    public int getNumLines() {
        return theNumLines;
    }
}
