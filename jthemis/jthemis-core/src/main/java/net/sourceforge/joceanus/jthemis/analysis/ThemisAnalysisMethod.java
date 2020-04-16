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
    private final List<ThemisAnalysisElement> theHeaders;

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
        theHeaders = ThemisAnalysisBody.processHeaderTrailers(pParser, pLine);

        /* Determine whether this method is abstract */
        final boolean hasModifiers = theModifiers != null;
        final boolean isInterface = theParent instanceof ThemisAnalysisInterface;
        final boolean markedDefault = hasModifiers && theModifiers.contains(ThemisAnalysisModifier.DEFAULT);
        final boolean markedAbstract = hasModifiers && theModifiers.contains(ThemisAnalysisModifier.ABSTRACT);
        final boolean isAbstract = markedAbstract || (isInterface && !markedDefault);

        /* Process the body if we have one */
        theProcessed = isAbstract
                       ? new ArrayList<>()
                       : ThemisAnalysisBody.processBody(pParser);
        theNumLines = theProcessed.size();

        /* Post process the lines */
        postProcessLines();
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the number of lines in the class.
     * @return the number of lines
     */
    public int getNumLines() {
        return theNumLines;
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

    @Override
    public String toString() {
        return isInitializer
               ? getName() + "()"
               : theReference.toString() + " " + getName() + "()";
    }
}
