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
import java.util.Map;

/**
 * Method Representation.
 */
public class ThemisAnalysisMethod
        implements ThemisAnalysisContainer, ThemisAnalysisDataType {
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
     * The modifiers.
     */
    private final List<ThemisAnalysisPrefix> theModifiers;

    /**
     * The headers.
     */
    private final Deque<ThemisAnalysisElement> theHeaders;

    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * The dataTypes.
     */
    private final Map<String, ThemisAnalysisDataType> theDataTypes;

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
     */
    ThemisAnalysisMethod(final ThemisAnalysisParser pParser,
                         final String pName,
                         final ThemisAnalysisReference pReference,
                         final ThemisAnalysisLine pLine) {
        /* Store parameters */
        isInitializer = pName.length() == 0;
        theName = isInitializer ? pReference.toString() : pName;
        theReference = pReference;
        theModifiers = pLine.getModifiers();

        /* Access details from parser */
        theDataTypes = pParser.getDataTypes();
        theParent = pParser.getParent();

        /* Create the arrays */
        theHeaders = ThemisAnalysisBuilder.processHeaderTrailers(pParser, pLine);

        /* Determine whether this method is abstract */
        final boolean isInterface = theParent instanceof ThemisAnalysisInterface;
        final boolean markedDefault = theModifiers.contains(ThemisAnalysisModifier.DEFAULT);
        final boolean markedAbstract = theModifiers.contains(ThemisAnalysisModifier.ABSTRACT);
        final boolean isAbstract = markedAbstract || (isInterface && !markedDefault);

        /* Process the body if we have one */
        theContents = isAbstract
                       ? new ArrayDeque<>()
                       : ThemisAnalysisBuilder.processMethodBody(pParser);
        final int myBaseLines = theContents.size();

        /* Post process the lines */
        postProcessLines();

        /* Calculate the number of lines */
        theNumLines = calculateNumLines(myBaseLines);
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
    public Deque<ThemisAnalysisElement> getContents() {
        return theContents;
    }

    @Override
    public ThemisAnalysisContainer getParent() {
        return theParent;
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
        int myNumLines = pBaseCount + Math.max(theHeaders.size() - 1, 1);

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
               ? getName() + "()"
               : theReference.toString() + " " + getName() + "()";
    }
}
