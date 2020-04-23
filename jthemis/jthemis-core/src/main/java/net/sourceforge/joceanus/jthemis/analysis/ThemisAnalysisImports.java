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
 * The set of imports.
 */
public class ThemisAnalysisImports
    implements ThemisAnalysisProcessed {
    /**
     * Period separator.
     */
    static final char PERIOD_SEP = '.';

    /**
     * The imports.
     */
    private final List<ThemisAnalysisImport> theImports;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial import line
     */
    ThemisAnalysisImports(final ThemisAnalysisParser pParser,
                          final ThemisAnalysisLine pLine) {
        /* Create the list of comment lines */
        theImports = new ArrayList<>();

        /* Add import to list */
        theImports.add(new ThemisAnalysisImport(pLine.toString()));

        /* While there are further lines */
        final Map<String, ThemisAnalysisDataType> myMap = pParser.getDataTypes();
        while (pParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) pParser.peekNextLine();

            /* It it is also an import */
            if (isImport(myLine)) {
                /* Add the import line and remove from input */
                final ThemisAnalysisImport myImport = new ThemisAnalysisImport(myLine.toString());
                theImports.add(myImport);
                myMap.put(myImport.getSimpleName(), myImport);
                pParser.popNextLine();

                /* else break loop */
            } else {
                break;
            }
        }
    }

    @Override
    public int getNumLines() {
        return theImports.size();
    }

    /**
     * Is the line an import?
     * @param pLine the line
     * @return true/false
     */
    static boolean isImport(final ThemisAnalysisLine pLine) {
        /* If we are ended by a semi-colon and is an import line*/
        if (pLine.endsWithChar(ThemisAnalysisBuilder.STATEMENT_END)
                && pLine.isStartedBy(ThemisAnalysisKeyWord.IMPORT.getKeyWord())) {
            /* Strip the semi-colon and return true */
            pLine.stripEndChar(ThemisAnalysisBuilder.STATEMENT_END);
            return true;
        }

        /* return false */
        return false;
    }

    /**
     * Import line.
     */
    static class ThemisAnalysisImport
            implements ThemisAnalysisElement, ThemisAnalysisDataType {
        /**
         * The full name.
         */
        private final String theFullName;

        /**
         * The simple name.
         */
        private final String theSimpleName;

        /**
         * Constructor.
         * @param pImport the import.
         */
        ThemisAnalysisImport(final String pImport) {
            /* Store the full name */
            theFullName = pImport;

            /* Strip out the simple name */
            final int myIndex = theFullName.lastIndexOf(PERIOD_SEP);
            theSimpleName = myIndex == -1 ? theFullName : theFullName.substring(myIndex + 1);
        }

        /**
         * Obtain the import.
         * @return the import
         */
        public String getFullName() {
            return theFullName;
        }

        /**
         * Obtain the simple name.
         * @return the simple name
         */
        public String getSimpleName() {
            return theSimpleName;
        }

        @Override
        public String toString() {
            return getSimpleName();
        }
    }
}
