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

import java.util.Deque;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisDataException;

/**
 * Method parameters.
 */
public class ThemisAnalysisParameters {
    /**
     * The map of parameters.
     */
    private final Map<String, ThemisAnalysisReference> theParameters;

    /**
     * The thrown exception.
     */
    private final ThemisAnalysisReference theThrown;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pHeaders the headers
     * @throws OceanusException on error
     */
    ThemisAnalysisParameters(final ThemisAnalysisParser pParser,
                             final Deque<ThemisAnalysisElement> pHeaders) throws OceanusException {
        /* Convert headers to single line */
        final ThemisAnalysisLine myHeader = new ThemisAnalysisLine(pHeaders);

        /* Find the end of the generic sequence */
        final int myEnd = myHeader.findEndOfNestedSequence(0, 0,  ThemisAnalysisChar.PARENTHESIS_CLOSE, ThemisAnalysisChar.PARENTHESIS_OPEN);
        if (myEnd < 0) {
            throw new ThemisDataException("End character not found");
        }
        final ThemisAnalysisLine myParms = myHeader.stripUpToPosition(myEnd);
        myParms.stripStartChar(ThemisAnalysisChar.PARENTHESIS_OPEN);
        myParms.stripEndChar(ThemisAnalysisChar.PARENTHESIS_CLOSE);

        /* Parse the parameters */
        theParameters = pParser.parseParameters(myParms);

        /* Handle throws clause */
        final String myToken = myHeader.peekNextToken();
        if (myToken.equals(ThemisAnalysisKeyWord.THROWS.getKeyWord())) {
            myHeader.stripNextToken();
            theThrown = ThemisAnalysisParser.parseDataType(pParser.getDataMap(), myHeader);
        } else {
            theThrown = null;
        }
    }

    @Override
    public String toString() {
        /* Start parameters */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(ThemisAnalysisChar.PARENTHESIS_OPEN);

        /* Build parameters */
        boolean bFirst = true;
        for (Entry<String, ThemisAnalysisReference> myEntry : theParameters.entrySet()) {
            /* Handle separators */
            if (!bFirst) {
                myBuilder.append(ThemisAnalysisChar.COMMA);
                myBuilder.append(ThemisAnalysisChar.BLANK);
            } else {
                bFirst = false;
            }

            /* Add parameter */
            myBuilder.append(myEntry.getValue());
            myBuilder.append(ThemisAnalysisChar.BLANK);
            myBuilder.append(myEntry.getKey());
        }

        /* End parameters */
        myBuilder.append(ThemisAnalysisChar.PARENTHESIS_CLOSE);

        /* If we have a thrown exception */
        if (theThrown != null) {
            /* Format it */
            myBuilder.append(ThemisAnalysisChar.BLANK);
            myBuilder.append(ThemisAnalysisKeyWord.THROWS.getKeyWord());
            myBuilder.append(ThemisAnalysisChar.BLANK);
            myBuilder.append(theThrown.toString());
        }

        /* return the string */
        return myBuilder.toString();
    }
}
