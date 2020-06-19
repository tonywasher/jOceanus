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

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Method Representation.
 */
public class ThemisAnalysisMethod
        implements ThemisAnalysisContainer {
    /**
     * The name of the class.
     */
    private final String theName;

    /**
     * The reference of the class.
     */
    private final ThemisAnalysisReference theReference;

    /**
     * The parent.
     */
    private final ThemisAnalysisContainer theParent;

    /**
     * The properties.
     */
    private final ThemisAnalysisProperties theProperties;

    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * The dataMap.
     */
    private final ThemisAnalysisDataMap theDataMap;

    /**
     * The parameters.
     */
    private final ThemisAnalysisParameters theParameters;

    /**
     * The number of lines.
     */
    private final int theNumLines;

    /**
     * Is this an initialiser?
     */
    private final boolean isInitializer;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pName the method name
     * @param pReference the reference
     * @param pLine the initial method line
     * @throws OceanusException on error
     */
    ThemisAnalysisMethod(final ThemisAnalysisParser pParser,
                         final String pName,
                         final ThemisAnalysisReference pReference,
                         final ThemisAnalysisLine pLine) throws OceanusException {
        /* Store parameters */
        isInitializer = pName.length() == 0;
        theName = isInitializer ? pReference.toString() : pName;
        theReference = pReference;
        theProperties = pLine.getProperties();

        /* Access details from parser */
        theParent = pParser.getParent();
        theDataMap = new ThemisAnalysisDataMap(theParent.getDataMap());

        /* Determine whether this method is abstract */
        final boolean isInterface = theParent instanceof ThemisAnalysisInterface;
        final boolean markedDefault = theProperties.hasModifier(ThemisAnalysisModifier.DEFAULT);
        final boolean markedStatic = theProperties.hasModifier(ThemisAnalysisModifier.STATIC);
        final boolean markedAbstract = theProperties.hasModifier(ThemisAnalysisModifier.ABSTRACT);
        final boolean isAbstract = markedAbstract || (isInterface && !markedDefault && !markedStatic);

        /* Parse the headers */
        final Deque<ThemisAnalysisElement> myHeaders = isAbstract
                        ? ThemisAnalysisBuilder.parseTrailers(pParser, pLine)
                        : ThemisAnalysisBuilder.parseHeaders(pParser, pLine);

        /* Process the body if we have one */
        theContents = isAbstract
                       ? new ArrayDeque<>()
                       : ThemisAnalysisBuilder.processMethodBody(pParser, this);
        final int myBaseLines = theContents.size();

        /* Create a parser */
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myHeaders, theContents, this);

        /* Resolve the generics */
        theProperties.resolveGeneric(myParser);
        theReference.resolveGeneric(myParser);

        /* resolve the parameters */
        theParameters = new ThemisAnalysisParameters(myParser, myHeaders);

        /* Post process the lines */
        postProcessLines();

        /* Calculate the number of lines */
        theNumLines = calculateNumLines(myBaseLines, myHeaders.size());
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
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
    public ThemisAnalysisDataMap getDataMap() {
        return theDataMap;
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
        /* Add 1+ line(s) for the class headers  */
        int myNumLines = pBaseCount + Math.max(pHdrCount - 1, 1);

        /* Loop through the contents */
        for (ThemisAnalysisElement myElement : theContents) {
            if (myElement instanceof ThemisAnalysisProcessed) {
                myNumLines += ((ThemisAnalysisProcessed) myElement).getNumLines() - 1;
            }
        }

        /* Add one for the clause terminator */
        return myNumLines + 1;
    }

    @Override
    public String toString() {
        return isInitializer
               ? getName() + theParameters.toString()
               : theReference.toString() + " " + getName() + theParameters.toString();
    }
}
