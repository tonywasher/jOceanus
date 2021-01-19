/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2021 Tony Washer
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
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisContainer.ThemisAnalysisAdoptable;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisIf.ThemisIteratorChain;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisStatement.ThemisAnalysisStatementHolder;

/**
 * Try construct.
 */
public class ThemisAnalysisTry
        implements ThemisAnalysisContainer, ThemisAnalysisAdoptable, ThemisAnalysisStatementHolder {
    /**
     * The contents.
     */
    private final Deque<ThemisAnalysisElement> theContents;

    /**
     * The catch clause(s).
     */
    private final ThemisAnalysisCatch theCatch;

    /**
     * The finally clause.
     */
    private final ThemisAnalysisFinally theFinally;

    /**
     * The headers.
     */
    private final ThemisAnalysisStack theHeaders;

    /**
     * The fields.
     */
    private final List<ThemisAnalysisField> theFields;

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
     * @param pLine the initial try line
     * @throws OceanusException on error
     */
    ThemisAnalysisTry(final ThemisAnalysisParser pParser,
                      final ThemisAnalysisLine pLine) throws OceanusException {
        /* Access details from parser */
        theParent = pParser.getParent();
        theDataMap = new ThemisAnalysisDataMap(theParent.getDataMap());

        /* Create the arrays */
        final Deque<ThemisAnalysisElement> myHeaders = ThemisAnalysisBuilder.parseHeaders(pParser, pLine);
        final Deque<ThemisAnalysisElement> myLines = ThemisAnalysisBuilder.processBody(pParser);
        theNumLines = myHeaders.size() + 1;

        /* Look for catch clauses */
        theCatch = (ThemisAnalysisCatch) pParser.processExtra(this, ThemisAnalysisKeyWord.CATCH);

        /* Look for finally clauses */
        theFinally = (ThemisAnalysisFinally) pParser.processExtra(this, ThemisAnalysisKeyWord.FINALLY);

        /* Create a parser */
        theContents = new ArrayDeque<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, theContents, this);
        myParser.processLines();

        /* Parse the headers */
        theFields = new ArrayList<>();
        theHeaders = new ThemisAnalysisStack(myHeaders);
    }

    @Override
    public Deque<ThemisAnalysisElement> getContents() {
        return theContents;
    }

    @Override
    public void postProcessExtras() throws OceanusException {
        /* Process the catch clause if required */
        if (theCatch != null) {
            theCatch.postProcessLines();
        }

        /* Process the finally clause if required */
        if (theFinally != null) {
            theFinally.postProcessLines();
        }

        /* If the headers are non empty */
        if (!theHeaders.isEmpty()) {
            /* Strip out parentheses and create scanner */
            final ThemisAnalysisStack myResources = theHeaders.extractParentheses();
            final ThemisAnalysisScanner myScanner = new ThemisAnalysisScanner(myResources);

            /* Create field for each resource */
            while (myResources.hasLines()) {
                final Deque<ThemisAnalysisElement> myResource = myScanner.scanForSeparator(ThemisAnalysisChar.SEMICOLON);
                theFields.add(new ThemisAnalysisField(getDataMap(), new ThemisAnalysisStack(myResource)));
            }
        }
    }

    @Override
    public ThemisAnalysisContainer getParent() {
        return theParent;
    }

    @Override
    public void setParent(final ThemisAnalysisContainer pParent) {
        theParent = pParent;
        theDataMap.setParent(pParent.getDataMap());
        if (theCatch != null) {
            theCatch.setParent(pParent);
        }
        if (theFinally != null) {
            theFinally.setParent(pParent);
        }
    }

    @Override
    public ThemisAnalysisDataMap getDataMap() {
        return theDataMap;
    }

    /**
     * Obtain the additional catch clause (if any).
     * @return the catch clause
     */
    public ThemisAnalysisCatch getCatch() {
        return theCatch;
    }

    /**
     * Obtain the additional finally clause (if any).
     * @return the finally clause
     */
    public ThemisAnalysisFinally getFinally() {
        return theFinally;
    }

    @Override
    public int getNumLines() {
        return theNumLines;
    }

    @Override
    public Iterator<ThemisAnalysisStatement> statementIterator() {
        return ThemisAnalysisField.statementIteratorForFields(theFields);
    }

    @Override
    public Iterator<ThemisAnalysisContainer> containerIterator() {
        final Iterator<ThemisAnalysisContainer> myCatch = theCatch == null
                ? Collections.emptyIterator()
                : Collections.singleton((ThemisAnalysisContainer) theCatch).iterator();
        final Iterator<ThemisAnalysisContainer> myFinally = theFinally == null
                ? Collections.emptyIterator()
                : Collections.singleton((ThemisAnalysisContainer) theFinally).iterator();
        return new ThemisIteratorChain<>(myCatch, myFinally);
    }

    @Override
    public String toString() {
        /* Start parameters */
        final StringBuilder myBuilder = new StringBuilder();

        /* Build parameters */
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

        /* Return the string */
        return myBuilder.toString();
    }
}
