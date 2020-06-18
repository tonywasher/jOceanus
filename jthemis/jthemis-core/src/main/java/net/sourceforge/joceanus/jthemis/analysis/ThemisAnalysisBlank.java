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

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * A series of blank lines.
 */
public class ThemisAnalysisBlank
    implements ThemisAnalysisProcessed {
    /**
     * The blankLines.
     */
    private final List<ThemisAnalysisLine> theBlanks;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial blank line
     * @throws OceanusException on error
     */
    ThemisAnalysisBlank(final ThemisAnalysisParser pParser,
                        final ThemisAnalysisLine pLine) throws OceanusException {
        /* Create the list of blank lines */
        theBlanks = new ArrayList<>();
        theBlanks.add(pLine);

        /* While there are further lines */
        while (pParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) pParser.peekNextLine();

            /* It it is also blank */
            if (isBlank(myLine)) {
                /* Add the blank line and remove from input */
                theBlanks.add(myLine);
                pParser.popNextLine();

                /* else break loop */
            } else {
                break;
            }
        }
    }

    @Override
    public int getNumLines() {
        return theBlanks.size();
    }

    /**
     * Is the line blank?
     * @param pLine the line
     * @return true/false
     */
    static boolean isBlank(final ThemisAnalysisLine pLine) {
        return pLine.getLength() == 0;
    }
}
