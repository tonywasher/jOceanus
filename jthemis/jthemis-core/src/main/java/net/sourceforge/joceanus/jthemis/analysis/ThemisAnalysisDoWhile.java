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
 * DoWhile construct.
 */
public class ThemisAnalysisDoWhile
        implements ThemisAnalysisContainer {
    /**
     * The parent.
     */
    private final ThemisAnalysisContainer theParent;

    /**
     * The trailers.
     */
    private final List<ThemisAnalysisElement> theTrailers;

    /**
     * The elements.
     */
    private final List<ThemisAnalysisElement> theProcessed;

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
     */
    ThemisAnalysisDoWhile(final ThemisAnalysisParser pParser) {
        /* Access details from parser */
        theDataTypes = pParser.getDataTypes();
        theParent = pParser.getParent();

        /* Create the arrays */
        final List<ThemisAnalysisElement> myLines = ThemisAnalysisBody.processBody(pParser);
        final int myBaseLines =  myLines.size();

        /* Parse trailers */
        theTrailers = ThemisAnalysisBody.processTrailers(pParser);

        /* Create a parser */
        theProcessed = new ArrayList<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theProcessed, theParent);
        myParser.processLines();

        /* Calculate the number of lines */
        theNumLines = calculateNumLines(myBaseLines);
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
    public ThemisAnalysisContainer getParent() {
        return theParent;
    }

    /**
     * Obtain the number of lines in the block.
     * @return the number of lines
     */
    public int getNumLines() {
        return theNumLines;
    }
    /**
     * Calculate the number of lines for the construct.
     * @param pBaseCount the baseCount
     * @return the number of lines
     */
    public int calculateNumLines(final int pBaseCount) {
        /* Add 1+ line(s) for the while trailers  */
        final int myNumLines = pBaseCount + Math.max(theTrailers.size() - 1, 1);

        /* Add one for the clause terminator */
        return myNumLines + 1;
    }
}
