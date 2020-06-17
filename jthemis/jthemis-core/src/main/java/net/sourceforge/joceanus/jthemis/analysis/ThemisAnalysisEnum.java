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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile.ThemisAnalysisObject;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisGeneric.ThemisAnalysisGenericBase;

/**
 * Enum representation.
 */
public class ThemisAnalysisEnum
        implements ThemisAnalysisContainer, ThemisAnalysisObject {
    /**
     * The short name of the class.
     */
    private final String theShortName;

    /**
     * The full name of the class.
     */
    private final String theFullName;

    /**
     * The ancestors.
     */
    private final List<ThemisAnalysisReference> theAncestors;

    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * The dataMap.
     */
    private final ThemisAnalysisDataMap theDataMap;

    /**
     * The values.
     */
    private final List<String> theValues;

    /**
     * The number of lines.
     */
    private final int theNumLines;

    /**
     * The properties.
     */
    private ThemisAnalysisProperties theProperties;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial enum line
     */
    ThemisAnalysisEnum(final ThemisAnalysisParser pParser,
                       final ThemisAnalysisLine pLine) {
        /* Store parameters */
        theShortName = pLine.stripNextToken();
        theProperties = pLine.getProperties();
        theValues = new ArrayList<>();
        final ThemisAnalysisContainer myParent = pParser.getParent();
        theDataMap = new ThemisAnalysisDataMap(myParent.getDataMap());

        /* Handle generic variables */
        ThemisAnalysisLine myLine = pLine;
        if (ThemisAnalysisGeneric.isGeneric(pLine)) {
            /* Declare them to the properties */
            theProperties = theProperties.setGenericVariables(new ThemisAnalysisGenericBase(pParser, myLine));
            myLine = (ThemisAnalysisLine) pParser.popNextLine();
        }

        /* Determine the full name */
        theFullName = myParent.determineFullChildName(theShortName);

        /* declare the enum */
        theDataMap.declareEnum(this);

        /* Parse the headers */
        final Deque<ThemisAnalysisElement> myHeaders = ThemisAnalysisBuilder.parseHeaders(pParser, myLine);

        /* Parse the body */
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);
        final int myBaseLines = myLines.size();

        /* Create a parser */
        theContents = new ArrayDeque<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theContents, this);

        /* Resolve the generics */
        theProperties.resolveGeneric(myParser);

        /* Parse the ancestors and lines */
        theAncestors = myParser.parseAncestors(myHeaders);
        processLines(myParser);

        /* Calculate the number of lines */
        theNumLines = calculateNumLines(myBaseLines, myHeaders.size());
    }

    /**
     * process the lines.
     * @param pParser the parser
     */
    void processLines(final ThemisAnalysisParser pParser) {
        /* we are still processing Enums */
        boolean look4Enum = true;

        /* Loop through the lines */
        while (pParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) pParser.popNextLine();

            /* Process comments and blanks */
            boolean processed = pParser.processCommentsAndBlanks(myLine);

            /* Process enumValue */
            if (!processed && look4Enum) {
                look4Enum = processEnumValue(myLine);
                processed = true;
            }

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
     * process the enumValue.
     * @param pLine the line
     * @return continue to look for eNums true, false
     */
    private boolean processEnumValue(final ThemisAnalysisLine pLine) {
        /* Access the token */
        final String myToken = pLine.stripNextToken();
        if (pLine.startsWithChar(ThemisAnalysisChar.PARENTHESIS_OPEN)) {
            ThemisAnalysisParenthesis.stripParenthesisContents(pLine);
        }
        theValues.add(myToken);
        return pLine.endsWithChar(ThemisAnalysisChar.COMMA);
    }

    @Override
    public String getShortName() {
        return theShortName;
    }

    @Override
    public String getFullName() {
        return theFullName;
    }

    @Override
    public ThemisAnalysisDataMap getDataMap() {
        return theDataMap;
    }

    @Override
    public Deque<ThemisAnalysisElement> getContents() {
        return theContents;
    }

    @Override
    public ThemisAnalysisContainer getParent() {
        return this;
    }

    @Override
    public List<ThemisAnalysisReference> getAncestors() {
        return theAncestors;
    }

    @Override
    public int getNumLines() {
        return theNumLines;
    }

    /**
     * Calculate the number of lines for the construct.
     * @param pBaseCount the baseCount
     * @param pHdrCount the header line count
     * @return the number of lines
     */
    public int calculateNumLines(final int pBaseCount,
                                 final int pHdrCount) {
        /* Add 1+ line(s) for the while headers  */
        final int myNumLines = pBaseCount + Math.max(pHdrCount - 1, 1);

        /* Add one for the clause terminator */
        return myNumLines + 1;
    }

    @Override
    public String toString() {
        return getShortName();
    }
}
