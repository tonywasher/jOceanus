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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.themis.analysis.ThemisAnalysisContainer.ThemisAnalysisAdoptable;
import net.sourceforge.joceanus.themis.analysis.ThemisAnalysisIf.ThemisIteratorChain;
import net.sourceforge.joceanus.themis.analysis.ThemisAnalysisStatement.ThemisAnalysisStatementHolder;

/**
 * For construct.
 */
public class ThemisAnalysisFor
        implements ThemisAnalysisContainer, ThemisAnalysisAdoptable, ThemisAnalysisStatementHolder {
    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * The headers.
     */
    private final ThemisAnalysisStack theHeaders;

    /**
     * The fields.
     */
    private final List<ThemisAnalysisField> theFields;

    /**
     * The statements.
     */
    private final List<ThemisAnalysisStatement> theStatements;

    /**
     * The number of lines.
     */
    private final int theNumLines;

    /**
     * The dataMap.
     */
    private final ThemisAnalysisDataMap theDataMap;

    /**
     * The parent.
     */
    private ThemisAnalysisContainer theParent;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial for line
     * @throws OceanusException on error
     */
    ThemisAnalysisFor(final ThemisAnalysisParser pParser,
                      final ThemisAnalysisLine pLine) throws OceanusException {
        /* Access details from parser */
        theParent = pParser.getParent();
        theDataMap = new ThemisAnalysisDataMap(theParent.getDataMap());

        /* Create the arrays */
        final Deque<ThemisAnalysisElement> myHeaders = ThemisAnalysisBuilder.parseHeaders(pParser, pLine);
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);
        theNumLines = myHeaders.size() + 1;

        /* Create a parser */
        theContents = new ArrayDeque<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theContents, this);
        myParser.processLines();

        /* Parse the headers */
        theFields = new ArrayList<>();
        theStatements = new ArrayList<>();
        theHeaders = new ThemisAnalysisStack(myHeaders);
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
    public void setParent(final ThemisAnalysisContainer pParent) {
        theParent = pParent;
        theDataMap.setParent(pParent.getDataMap());
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
    public void postProcessExtras() throws OceanusException {
        parseHeaders();
    }

    @Override
    public Iterator<ThemisAnalysisStatement> statementIterator() {
        final Iterator<ThemisAnalysisStatement> myField = ThemisAnalysisField.statementIteratorForFields(theFields);
        final Iterator<ThemisAnalysisStatement> myStatement = theStatements.iterator();
        return new ThemisIteratorChain<>(myField, myStatement);
    }

    /**
     * Parse headers.
     * @throws OceanusException on error
     */
    private void parseHeaders() throws OceanusException {
        /* Strip parentheses and create scanner */
        final ThemisAnalysisStack myParts = theHeaders.extractParentheses();
        final ThemisAnalysisScanner myScanner = new ThemisAnalysisScanner(myParts);

        /* Determine separator */
        char mySep = ThemisAnalysisChar.COLON;
        if (!myScanner.checkForSeparator(mySep)) {
            mySep = ThemisAnalysisChar.SEMICOLON;
        }

        /* Loop through the items */
        boolean isField = true;
        while (myParts.hasLines()) {
            /* Access next part */
            final Deque<ThemisAnalysisElement> myPart = myScanner.scanForSeparator(mySep);
            final ThemisAnalysisStack myStack = new ThemisAnalysisStack(myPart);
            if (myStack.isEmpty()) {
                continue;
            }

            /* Process as field/statement */
            if (isField) {
                parseField(myStack);
            } else {
                parseStatement(myStack);
            }
            isField = false;
        }
    }

    /**
     * Parse field.
     * @param pField the field
     * @throws OceanusException on error
     */
    private void parseField(final ThemisAnalysisStack pField) throws OceanusException {
        /* Create field for each resource */
        final ThemisAnalysisScanner myScanner = new ThemisAnalysisScanner(pField);
        myScanner.skipGenerics();
        ThemisAnalysisField myLast = null;
        while (pField.hasLines()) {
            final Deque<ThemisAnalysisElement> myResource = myScanner.scanForSeparator(ThemisAnalysisChar.COMMA);
            myLast = myLast == null
                    ? new ThemisAnalysisField(getDataMap(), new ThemisAnalysisStack(myResource))
                    : new ThemisAnalysisField(myLast, new ThemisAnalysisStack(myResource));
            theFields.add(myLast);
        }
    }

    /**
     * Parse statements.
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    private void parseStatement(final ThemisAnalysisStack pStatement) throws OceanusException {
        /* Create field for each resource */
        final ThemisAnalysisScanner myScanner = new ThemisAnalysisScanner(pStatement);
        while (pStatement.hasLines()) {
            final Deque<ThemisAnalysisElement> myResource = myScanner.scanForSeparator(ThemisAnalysisChar.COMMA);
            theStatements.add(new ThemisAnalysisStatement(myResource));
        }
    }

    @Override
    public String toString() {
        /* Start parameters */
        final StringBuilder myBuilder = new StringBuilder();

        /* Build fields */
        boolean bFirst = true;
        for (ThemisAnalysisField myField : theFields) {
            /* Handle separators */
            if (!bFirst) {
                myBuilder.append(ThemisAnalysisChar.LF);
            } else {
                bFirst = false;
            }

            /* Add parameter */
            myBuilder.append(myField);
        }

        /* Build fields */
        for (ThemisAnalysisStatement myStatement : theStatements) {
            /* Handle separators */
            if (!bFirst) {
                myBuilder.append(ThemisAnalysisChar.LF);
            } else {
                bFirst = false;
            }

            /* Add parameter */
            myBuilder.append(myStatement);
        }

        /* Return the string */
        return myBuilder.toString();
    }
}
