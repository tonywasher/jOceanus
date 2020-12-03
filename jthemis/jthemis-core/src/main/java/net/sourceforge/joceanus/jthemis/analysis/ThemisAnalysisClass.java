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
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisGeneric.ThemisAnalysisGenericBase;

/**
 * Class representation.
 */
public class ThemisAnalysisClass
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
     * The properties.
     */
    private ThemisAnalysisProperties theProperties;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial class line
     * @throws OceanusException on error
     */
    ThemisAnalysisClass(final ThemisAnalysisParser pParser,
                        final ThemisAnalysisLine pLine) throws OceanusException {
        /* Store parameters */
        theShortName = pLine.stripNextToken();
        theProperties = pLine.getProperties();
        final ThemisAnalysisContainer myParent = pParser.getParent();
        final ThemisAnalysisDataMap myParentDataMap = myParent.getDataMap();
        theDataMap = new ThemisAnalysisDataMap(myParentDataMap);

        /* If this is a local class */
        if (!(myParent instanceof ThemisAnalysisObject)
            &&  (!(myParent instanceof ThemisAnalysisFile))) {
            final int myId = myParentDataMap.getLocalId(theShortName);
            theFullName = myParent.determineFullChildName(myId + theShortName);

            /* else handle standard name */
        } else {
            theFullName = myParent.determineFullChildName(theShortName);
        }

        /* Handle generic variables */
        ThemisAnalysisLine myLine = pLine;
        if (ThemisAnalysisGeneric.isGeneric(pLine)) {
            /* Declare them to the properties */
            theProperties = theProperties.setGenericVariables(new ThemisAnalysisGenericBase(pParser, myLine));
            myLine = (ThemisAnalysisLine) pParser.popNextLine();
        }

        /* declare the class */
        theDataMap.declareObject(this);

        /* Parse the headers */
        final Deque<ThemisAnalysisElement> myHeaders = ThemisAnalysisBuilder.parseHeaders(pParser, myLine);
        theNumLines = myHeaders.size() + 1;

        /* Parse the body */
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);

        /* Create a parser */
        theContents = new ArrayDeque<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theContents, this);

        /* Resolve the generics */
        theProperties.resolveGeneric(myParser);

        /* Parse the ancestors and lines */
        theAncestors = myParser.parseAncestors(myHeaders);
        myParser.processLines();
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
        return theProperties;
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
