/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.themis.analysis;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.tethys.OceanusException;
import net.sourceforge.joceanus.themis.analysis.ThemisAnalysisIf.ThemisIteratorChain;
import net.sourceforge.joceanus.themis.analysis.ThemisAnalysisStatement.ThemisAnalysisStatementHolder;

/**
 * Field Representation.
 */
public class ThemisAnalysisField
    implements ThemisAnalysisProcessed, ThemisAnalysisStatementHolder {
    /**
     * The name of the class.
     */
    private final String theName;

    /**
     * The dataType of the field.
     */
    private final ThemisAnalysisReference theDataType;

    /**
     * The properties.
     */
    private final ThemisAnalysisProperties theProperties;

    /**
     * The initial value.
     */
    private final ThemisAnalysisStatement theInitial;

    /**
     * The number of lines.
     */
    private final int theNumLines;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pName the method name
     * @param pDataType the dataType
     * @param pLine the initial field line
     * @throws OceanusException on error
     */
    ThemisAnalysisField(final ThemisAnalysisParser pParser,
                        final String pName,
                        final ThemisAnalysisReference pDataType,
                        final ThemisAnalysisLine pLine) throws OceanusException {
        /* Store parameters */
        theName = pName;
        theDataType = pDataType;
        theProperties = pLine.getProperties();

        /* If we have no initial value */
        if (pLine.startsWithChar(ThemisAnalysisChar.SEMICOLON)) {
            /* Set default values */
            theNumLines = 1;
            theInitial = null;

            /* else we have an initializer */
        } else {
            /* Strip the equals sign */
            pLine.stripStartChar(ThemisAnalysisChar.EQUAL);

            /* Declare as statement */
            theInitial = new ThemisAnalysisStatement(pParser, pLine);
            theNumLines = theInitial.getNumLines();
        }
    }

    /**
     * Constructor.
     * @param pParser the parser
     * @param pName the method name
     * @param pDataType the dataType
     * @param pEmbedded the embedded block
     * @throws OceanusException on error
     */
    ThemisAnalysisField(final ThemisAnalysisParser pParser,
                        final String pName,
                        final ThemisAnalysisReference pDataType,
                        final ThemisAnalysisEmbedded pEmbedded) throws OceanusException {
        /* Store parameters */
        theName = pName;
        theDataType = pDataType;

        /* Access header line */
        final ThemisAnalysisLine myLine = pEmbedded.getHeader();
        theProperties = myLine.getProperties();

        /* Strip the equals sign */
        myLine.stripStartChar(ThemisAnalysisChar.EQUAL);

        /* Declare as statement */
        theInitial = new ThemisAnalysisStatement(pEmbedded);
        theNumLines = theInitial.getNumLines();
    }

    /**
     * Constructor.
     * @param pDataMap the dataMap
     * @param pStack the field stack
     * @throws OceanusException on error
     */
    ThemisAnalysisField(final ThemisAnalysisDataMap pDataMap,
                        final ThemisAnalysisStack pStack) throws OceanusException {
        /* Determine initial parameters */
        final ThemisAnalysisLine myLine = (ThemisAnalysisLine) pStack.popNextLine();
        ThemisAnalysisProperties myProps = ThemisAnalysisProperties.NULL;

        /* Access the next token */
        final String nextToken = myLine.peekNextToken();

        /* If this is a final modifier */
        if (ThemisAnalysisModifier.FINAL.getModifier().equals(nextToken)) {
            /* Record modifier and strip it */
            myProps = myProps.setModifier(ThemisAnalysisModifier.FINAL);
            myLine.stripStartSequence(nextToken);
        }

        /* Process remaining data */
        theDataType = ThemisAnalysisParser.parseDataType(pDataMap, myLine);
        theName = myLine.stripNextToken();
        myLine.stripStartChar(ThemisAnalysisChar.EQUAL);
        pStack.pushLine(myLine);
        theProperties = myProps;

        /* Declare as statement */
        theInitial = new ThemisAnalysisStatement(null, pStack);
        theNumLines = theInitial.getNumLines();
    }

    /**
     * Constructor.
     * @param pPrevious the previous field
     * @param pStack the field stack
     * @throws OceanusException on error
     */
    ThemisAnalysisField(final ThemisAnalysisField pPrevious,
                        final ThemisAnalysisStack pStack) throws OceanusException {
        /* Determine initial parameters */
        final ThemisAnalysisLine myLine = (ThemisAnalysisLine) pStack.popNextLine();
        theDataType = pPrevious.theDataType;
        theName = myLine.stripNextToken();
        myLine.stripStartChar(ThemisAnalysisChar.EQUAL);
        pStack.pushLine(myLine);
        theProperties = ThemisAnalysisProperties.NULL;

        /* Declare as statement */
        theInitial = new ThemisAnalysisStatement(null, pStack);
        theNumLines = theInitial.getNumLines();
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    @Override
    public Iterator<ThemisAnalysisStatement> statementIterator() {
        return theInitial == null
                ? Collections.emptyIterator()
                : Collections.singleton(theInitial).iterator();
    }

    @Override
    public int getNumLines() {
        return theNumLines;
    }

    @Override
    public String toString() {
        return theDataType.toString() + " " + getName();
    }

    /**
     * Obtain statement iterator for list of fields.
     * @param pList the field list
     * @return the statement iterator
     */
    public static Iterator<ThemisAnalysisStatement> statementIteratorForFields(final List<ThemisAnalysisField> pList) {
        final ListIterator<ThemisAnalysisField> myIterator = pList.listIterator(pList.size());
        Iterator<ThemisAnalysisStatement> myCurr = Collections.emptyIterator();
        while (myIterator.hasPrevious()) {
            final ThemisAnalysisField myField = myIterator.previous();
            myCurr = new ThemisIteratorChain<>(myField.statementIterator(), myCurr);
        }
        return myCurr;
    }
}
