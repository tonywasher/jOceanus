/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.themis.lethe.analysis;

import java.util.ArrayDeque;
import java.util.Deque;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;

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
    private ThemisAnalysisContainer theParent;

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
    private ThemisAnalysisDataMap theDataMap;

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
        theContents = new ArrayDeque<>();
        final Deque<ThemisAnalysisElement> myHeaders = isAbstract
                        ? ThemisAnalysisBuilder.parseTrailers(pParser, pLine)
                        : parseHeaders(pParser, pLine);
        theNumLines = myHeaders.size() + (isAbstract ? 0 : 1);

        /* Create a parser */
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myHeaders, theContents, this);

        /* Resolve the generics */
        theProperties.resolveGeneric(myParser);
        theReference.resolveGeneric(myParser);

        /* resolve the parameters */
        theParameters = new ThemisAnalysisParameters(myParser, myHeaders);

        /* Post process the lines */
        postProcessLines();
    }

    /**
     * Constructor.
     * @param pParser the parser
     * @param pName the method name
     * @param pReference the reference
     * @param pBody the method body
     * @throws OceanusException on error
     */
    ThemisAnalysisMethod(final ThemisAnalysisParser pParser,
                         final String pName,
                         final ThemisAnalysisReference pReference,
                         final ThemisAnalysisMethodBody pBody) throws OceanusException {
        /* Store parameters */
        final ThemisAnalysisLine myLine = pBody.getHeader();
        isInitializer = pName.length() == 0;
        theName = isInitializer ? pReference.toString() : pName;
        theReference = pReference;
        theProperties = myLine.getProperties();

        /* Access details from body */
        theParent = pBody.getParent();
        theDataMap = pBody.getDataMap();

        /* Record the headers */
        final Deque<ThemisAnalysisElement> myHeaders = new ArrayDeque<>();
        myHeaders.add(myLine);
        theNumLines = 2;

        /* Access the contents */
        theContents = pBody.getContents();

        /* Create a parser */
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myHeaders, theContents, this);

        /* Resolve the generics */
        theProperties.resolveGeneric(myParser);
        theReference.resolveGeneric(myParser);

        /* resolve the parameters */
        theParameters = new ThemisAnalysisParameters(myParser, myHeaders);

        /* Post process the lines */
        postProcessLines();
    }

    /**
     * Parse Headers.
     * @param pParser the parser
     * @param pLine the line
     * @return the headers
     * @throws OceanusException on error
     */
    private Deque<ThemisAnalysisElement> parseHeaders(final ThemisAnalysisParser pParser,
                                                      final ThemisAnalysisLine pLine) throws OceanusException {
        /* Initialise details */
        final Deque<ThemisAnalysisElement> myHeaders = new ArrayDeque<>();
        ThemisAnalysisElement myElement = pLine;

        /* Add lines to header */
        while (myElement instanceof ThemisAnalysisLine) {
            myHeaders.add(myElement);
            myElement = pParser.popNextLine();
        }

        /* Must end with a method body */
        if (!(myElement instanceof ThemisAnalysisMethodBody)) {
            throw new ThemisDataException("Unexpected dataType");
        }

        /* Copy details from method body */
        final ThemisAnalysisMethodBody myBody = (ThemisAnalysisMethodBody) myElement;
        myHeaders.add(myBody.getHeader());
        theContents.addAll(myBody.getContents());
        theParent = myBody.getParent();
        theDataMap = myBody.getDataMap();

        /* return the headers */
        return myHeaders;
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

    @Override
    public String toString() {
        return isInitializer
               ? getName() + theParameters.toString()
               : theReference.toString() + " " + getName() + theParameters.toString();
    }
}
