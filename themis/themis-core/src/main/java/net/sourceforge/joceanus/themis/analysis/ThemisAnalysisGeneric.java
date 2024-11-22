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

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.themis.ThemisDataException;
import net.sourceforge.joceanus.themis.analysis.ThemisAnalysisDataMap.ThemisAnalysisDataType;

/**
 * Generic construct.
 */
public interface ThemisAnalysisGeneric {
    /**
     * Is the line a generic?
     * @param pLine the line
     * @return true/false
     */
    static boolean isGeneric(final ThemisAnalysisLine pLine) {
        /* If we are started with a GENERIC_OPEN */
        return pLine.startsWithChar(ThemisAnalysisChar.GENERIC_OPEN);
    }

    /**
     * Generic Base.
     */
    class ThemisAnalysisGenericBase
            implements ThemisAnalysisGeneric {
        /**
         * The contents of the generic.
         */
        private final ThemisAnalysisLine theContents;

        /**
         * Constructor.
         * @param pLine the line
         * @throws OceanusException on error
         */
        ThemisAnalysisGenericBase(final ThemisAnalysisLine pLine) throws OceanusException {
            /* Find the end of the generic sequence */
            final int myEnd = pLine.findEndOfNestedSequence(0, 0,  ThemisAnalysisChar.GENERIC_CLOSE, ThemisAnalysisChar.GENERIC_OPEN);
            if (myEnd < 0) {
                throw new ThemisDataException("End character not found");
            }

            /* Obtain the contents */
            theContents = pLine.stripUpToPosition(myEnd);
            theContents.stripStartChar(ThemisAnalysisChar.GENERIC_OPEN);
            theContents.stripEndChar(ThemisAnalysisChar.GENERIC_CLOSE);
        }

        /**
         * Constructor.
         * @param pParser the parser
         * @param pLine the line
         * @throws OceanusException on error
         */
        ThemisAnalysisGenericBase(final ThemisAnalysisParser pParser,
                                  final ThemisAnalysisLine pLine) throws OceanusException {
            /* Create a scanner */
            final ThemisAnalysisScanner myScanner = new ThemisAnalysisScanner(pParser);

            /* Scan for the end of the generic sequence */
            final Deque<ThemisAnalysisElement> myLines = myScanner.scanForGeneric(pLine);

            /* Obtain the contents */
            theContents = new ThemisAnalysisLine(myLines);
            theContents.stripStartChar(ThemisAnalysisChar.GENERIC_OPEN);
            theContents.stripEndChar(ThemisAnalysisChar.GENERIC_CLOSE);
        }

        /**
         * Obtain the line.
         * @return the line
         */
        ThemisAnalysisLine getLine() {
            return theContents;
        }

        @Override
        public String toString() {
            return "" + ThemisAnalysisChar.GENERIC_OPEN + theContents + ThemisAnalysisChar.GENERIC_CLOSE;
        }
    }

    /**
     * Generic Reference.
     */
    class ThemisAnalysisGenericRef
            implements ThemisAnalysisGeneric {
        /**
         * The base generic.
         */
        private final ThemisAnalysisGenericBase theBase;

        /**
         * The list of references.
         */
        private final List<ThemisAnalysisReference> theReferences;

        /**
         * Constructor.
         * @param pParser the parser
         * @param pBase the base generic line
         * @throws OceanusException on error
         */
        ThemisAnalysisGenericRef(final ThemisAnalysisParser pParser,
                                 final ThemisAnalysisGenericBase pBase) throws OceanusException {
            /* Create the list */
            theBase = pBase;
            theReferences = new ArrayList<>();
            final ThemisAnalysisDataMap myDataMap = pParser.getDataMap();

            /* Take a copy of the buffer */
            final ThemisAnalysisLine myLine = new ThemisAnalysisLine(theBase.getLine());

            /* Loop through the line */
            for (;;) {
                /* Strip leading comma */
                if (myLine.startsWithChar(ThemisAnalysisChar.COMMA)) {
                    myLine.stripStartChar(ThemisAnalysisChar.COMMA);
                }

                /* Access first token */
                final String myToken = myLine.peekNextToken();
                if (myToken.length() == 0) {
                    return;
                }

                /* Handle generic wildcard */
                if (myToken.length() == 1 && myToken.charAt(0) == ThemisAnalysisChar.QUESTION) {
                    /* Strip the wildcard */
                    myLine.stripNextToken();

                    /* Create reference list */
                    final List<ThemisAnalysisReference> myRefs = new ArrayList<>();

                    /* If we have an extension/subtype */
                    final String myNext = myLine.peekNextToken();
                    if (myNext.equals(ThemisAnalysisKeyWord.EXTENDS.getKeyWord())
                        || myNext.equals(ThemisAnalysisKeyWord.SUPER.getKeyWord())) {
                        /* Access data type */
                        myLine.stripNextToken();
                        ThemisAnalysisReference myRef = ThemisAnalysisParser.parseDataType(myDataMap, myLine);
                        myRefs.add(myRef);

                        /* Loop for additional extends */
                        while (myLine.getLength() > 0 && myLine.startsWithChar(ThemisAnalysisChar.AND)) {
                            myLine.stripStartChar(ThemisAnalysisChar.AND);
                            myRef = ThemisAnalysisParser.parseDataType(myDataMap, myLine);
                            myRefs.add(myRef);
                        }
                    }

                    final ThemisAnalysisGenericVar myVar = new ThemisAnalysisGenericVar(myToken, myRefs);
                    theReferences.add(new ThemisAnalysisReference(myVar, null, null));

                    /* else handle standard generic */
                } else {
                    final ThemisAnalysisReference myReference = ThemisAnalysisParser.parseDataType(myDataMap, myLine);
                    myReference.resolveGeneric(pParser);
                    theReferences.add(myReference);
                }
            }
        }

        /**
         * Obtain the list of references.
         * @return the list
         */
        public List<ThemisAnalysisReference> getReferences() {
            return theReferences;
        }

        @Override
        public String toString() {
            return theBase.toString();
        }
    }

    /**
     * Generic Variable List.
     */
    class ThemisAnalysisGenericVarList
            implements ThemisAnalysisGeneric {
        /**
         * The base generic.
         */
        private final ThemisAnalysisGenericBase theBase;

        /**
         * The list of variables.
         */
        private final List<ThemisAnalysisGenericVar> theVariables;

        /**
         * Constructor.
         * @param pParser the parser
         * @param pBase the base generic line
         * @throws OceanusException on error
         */
        ThemisAnalysisGenericVarList(final ThemisAnalysisParser pParser,
                                     final ThemisAnalysisGenericBase pBase) throws OceanusException {
            /* Create the list */
            theBase = pBase;
            theVariables = new ArrayList<>();
            final ThemisAnalysisDataMap myDataMap = pParser.getDataMap();

            /* Take a copy of the buffer */
            final ThemisAnalysisLine myLine = new ThemisAnalysisLine(theBase.getLine());

            /* Loop through the line */
            for (;;) {
                /* Strip leading comma */
                if (myLine.startsWithChar(ThemisAnalysisChar.COMMA)) {
                    myLine.stripStartChar(ThemisAnalysisChar.COMMA);
                }

                /* Access name of variable */
                final String myName = myLine.stripNextToken();
                if (myName.length() == 0) {
                    addToDataList(pParser);
                    return;
                }

                /* Create list */
                final List<ThemisAnalysisReference> myRefs = new ArrayList<>();

                /* If we have an extension */
                final String myNext = myLine.peekNextToken();
                if (myNext.equals(ThemisAnalysisKeyWord.EXTENDS.getKeyWord())) {
                    /* Access data type */
                    myLine.stripNextToken();
                    ThemisAnalysisReference myRef = ThemisAnalysisParser.parseDataType(myDataMap, myLine);
                    myRefs.add(myRef);

                    /* Loop for additional extends */
                    while (myLine.getLength() > 0 && myLine.startsWithChar(ThemisAnalysisChar.AND)) {
                        myLine.stripStartChar(ThemisAnalysisChar.AND);
                        myRef = ThemisAnalysisParser.parseDataType(myDataMap, myLine);
                        myRefs.add(myRef);
                    }
                }

                /* Create the variable */
                final ThemisAnalysisGenericVar myVar = new ThemisAnalysisGenericVar(myName, myRefs);
                theVariables.add(myVar);
            }
        }

        /**
         * Add to dataList.
         * @param pParser the parser
         * @throws OceanusException on error
         */
        private void addToDataList(final ThemisAnalysisParser pParser) throws OceanusException {
            /* Access the dataMap */
            final ThemisAnalysisDataMap myMap = pParser.getDataMap();

            /* Loop through the variables in reverse order */
            final ListIterator<ThemisAnalysisGenericVar> myIterator = theVariables.listIterator(theVariables.size());
            while (myIterator.hasPrevious()) {
                final ThemisAnalysisGenericVar myVar = myIterator.previous();

                /* Resolve generic details and add to dataMap */
                myMap.declareGenericVar(myVar);
                myVar.resolveGeneric(pParser);
            }
        }

        @Override
        public String toString() {
            return theBase.toString();
        }
    }

    /**
     * Generic Variable.
     */
    class ThemisAnalysisGenericVar
            implements ThemisAnalysisGeneric, ThemisAnalysisDataType {
        /**
         * The name of the variable.
         */
        private final String theName;

        /**
         * The list of references.
         */
        private final List<ThemisAnalysisReference> theReferences;

        /**
         * Constructor.
         * @param pName the name
         * @param pReferences the references
         */
        ThemisAnalysisGenericVar(final String pName,
                                 final List<ThemisAnalysisReference> pReferences) {
            /* Record parameters */
            theName = pName;
            theReferences = pReferences;
        }

        /**
         * Obtain the name.
         * @return the name
         */
        String getName() {
            return theName;
        }

        /**
         * Resolve the generic references.
         * @param pParser the parser
         * @throws OceanusException on error
         */
        public void resolveGeneric(final ThemisAnalysisParser pParser) throws OceanusException {
            /* Loop through resolving the references */
            for (ThemisAnalysisReference myRef : theReferences) {
                myRef.resolveGeneric(pParser);
            }
        }

        @Override
        public String toString() {
            return theName;
        }
    }
}
