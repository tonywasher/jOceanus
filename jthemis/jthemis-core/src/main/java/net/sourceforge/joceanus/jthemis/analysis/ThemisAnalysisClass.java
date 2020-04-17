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
 * Class representation.
 */
public class ThemisAnalysisClass
    implements ThemisAnalysisContainer, ThemisAnalysisDataType {
    /**
     * The name of the class.
     */
    private final String theName;

    /**
     * The modifiers.
     */
    private final List<ThemisAnalysisPrefix> theModifiers;

    /**
     * The headers.
     */
    private final List<ThemisAnalysisElement> theHeaders;

    /**
     * The contents.
     */
    private final List<ThemisAnalysisElement> theContents;

    /**
     * The dataTypes.
     */
    private final Map<String, ThemisAnalysisDataType> theDataTypes;

    /**
     * The number of lines.
     */
    private final int theNumLines;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial class line
     */
    ThemisAnalysisClass(final ThemisAnalysisParser pParser,
                        final ThemisAnalysisLine pLine) {
        /* Store parameters */
        theName = pLine.stripNextToken();
        theModifiers = pLine.getModifiers();
        theDataTypes = pParser.getDataTypes();

        /* Create the arrays */
        theHeaders = ThemisAnalysisBuilder.processHeaders(pParser, pLine);
        final List<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);
        final int myBaseLines = myLines.size();

        /* add/replace the class in the map */
        final Map<String, ThemisAnalysisDataType> myMap = pParser.getDataTypes();
        myMap.put(theName, this);

        /* Create a parser */
        theContents = new ArrayList<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theContents, this);
        processLines(myParser);

        /* Calculate the number of lines */
        theNumLines = calculateNumLines(myBaseLines);
    }

    /**
     * process the lines.
     * @param pParser the parser
     */
    void processLines(final ThemisAnalysisParser pParser) {
       /* Loop through the lines */
        while (pParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) pParser.popNextLine();

            /* Process comments and blanks */
            boolean processed = pParser.processCommentsAndBlanks(myLine);

            /* Process embedded classes */
            if (!processed) {
                processed = pParser.processClass(myLine);
            }

            /* Process language constructs */
            if (!processed) {
                processed = pParser.processLanguage(myLine);
            }

            /* If we haven't processed yet */
            if (!processed) {
                /* Just add the line to contents at present */
                theContents.add(myLine);
            }
        }
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    @Override
    public Map<String, ThemisAnalysisDataType> getDataTypes() {
        return theDataTypes;
    }

    @Override
    public List<ThemisAnalysisElement> getContents() {
        return theContents;
    }

    @Override
    public ThemisAnalysisContainer getParent() {
        return this;
    }

    @Override
    public int getNumLines() {
        return theNumLines;
    }

    /**
     * Calculate the number of lines for the construct.
     * @param pBaseCount the baseCount
     * @return the number of lines
     */
    public int calculateNumLines(final int pBaseCount) {
        /* Add 1+ line(s) for the class headers  */
        final int myNumLines = pBaseCount + Math.max(theHeaders.size() - 1, 1);

        /* Add one for the clause terminator */
        return myNumLines + 1;
    }

    @Override
    public String toString() {
        return getName();
    }
}
