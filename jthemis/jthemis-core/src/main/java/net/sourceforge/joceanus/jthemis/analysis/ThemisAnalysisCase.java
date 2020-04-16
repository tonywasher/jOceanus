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
 * Case construct.
 */
public class ThemisAnalysisCase
    implements ThemisAnalysisContainer {
    /**
     * The parent.
     */
    private final ThemisAnalysisContainer theParent;

    /**
     * The elements.
     */
    private final List<ThemisAnalysisElement> theProcessed;

    /**
     * The dataTypes.
     */
    private final Map<String, ThemisAnalysisDataType> theDataTypes;

    /**
     * The case values.
     */
    private final List<Object> theCases;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pCase the case
     */
    ThemisAnalysisCase(final ThemisAnalysisParser pParser,
                       final Object pCase) {
        /* Access details from parser */
        theDataTypes = pParser.getDataTypes();
        theParent = pParser.getParent();

        /* Initialise the case value */
        theCases = new ArrayList<>();
        theCases.add(pCase);

        /* Create a parser */
        theProcessed = new ArrayList<>();
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(pParser, theProcessed);
        processLines(myParser);
    }

    /**
     * process the lines.
     * @param pParser the parser
     */
    void processLines(final ThemisAnalysisParser pParser) {
        /* we are still processing Cases */
        boolean look4Case = true;

        /* Loop through the lines */
        while (pParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) pParser.popNextLine();

            /* Process comments and blanks */
            boolean processed = pParser.processCommentsAndBlanks(myLine);

            /* If we have not processed */
            if (!processed) {
                /* Look for new case */
                myLine.mark();
                final Object myCase = ThemisAnalysisParser.parseCase(myLine);

                /* If we have a new case */
                if (myCase != null) {
                    /* If we are still looking for further cases */
                    if (look4Case) {
                        /* Process additional caseValue */
                        theCases.add(myCase);
                        processed = true;

                        /* else we have finished */
                    } else {
                        /* reset and restore line and break loop */
                        myLine.reset();
                        pParser.pushLine(myLine);
                        break;
                    }
                }
            }

            /* If we have not processed */
            if (!processed) {
                /* Have finished looking for cases */
                look4Case = false;

                /* Just add the line to processed at present */
                theProcessed.add(myLine);
            }
        }
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
}
