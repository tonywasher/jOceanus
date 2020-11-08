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
import java.util.Deque;
import java.util.List;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile.ThemisAnalysisObject;

/**
 * Local Class.
 */
public class ThemisAnalysisLocalClass
        implements ThemisAnalysisObject {
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
     * The number of lines.
     */
    private final int theNumLines;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial class line
     * @throws OceanusException on error
     */
    ThemisAnalysisLocalClass(final ThemisAnalysisParser pParser,
                             final ThemisAnalysisLine pLine) throws OceanusException {
        /* Access parent dataMap and determine the shortName */
        final ThemisAnalysisContainer myParent = pParser.getParent();
        final ThemisAnalysisDataMap myParentDataMap = myParent.getDataMap();
        theShortName = pLine.stripNextToken();

        /* Determine the full name */
        final int myId = myParentDataMap.getLocalId(theShortName);
        theFullName = myParent.determineFullChildName(theShortName);

        /* Create local dataMap */
        theDataMap = new ThemisAnalysisDataMap(myParent.getDataMap());

        /* Parse the headers */
        final Deque<ThemisAnalysisElement> myHeaders = ThemisAnalysisBuilder.parseHeaders(pParser, pLine);
        theNumLines = myHeaders.size() + 1;

        /* Parse the body */
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);

        /* Create a parser */
        theContents = new ArrayDeque<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theContents, this);

        /* Parse the ancestor and lines */
        theAncestors = myParser.parseAncestors(myHeaders);
        initialProcessingPass(myParser);
    }

    /**
     * perform initial processing pass.
     * @param pParser the parser
     * @throws OceanusException on error
     */
    void initialProcessingPass(final ThemisAnalysisParser pParser) throws OceanusException {
        /* Loop through the lines */
        while (pParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) pParser.popNextLine();

            /* Process comments/blanks/languageConstructs */
            final boolean processed = pParser.processCommentsAndBlanks(myLine)
                    || pParser.processLanguage(myLine);

            /* If we haven't processed yet */
            if (!processed) {
                /* Just add the line to contents at present */
                theContents.add(myLine);
            }
        }
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
    public ThemisAnalysisProperties getProperties() {
        return null;
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

    @Override
    public String toString() {
        return getShortName();
    }
}
