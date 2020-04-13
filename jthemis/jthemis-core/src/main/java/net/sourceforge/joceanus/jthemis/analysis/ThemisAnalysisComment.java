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

/**
 * A series of comment lines.
 */
public class ThemisAnalysisComment
        implements ThemisAnalysisElement {
    /**
     * The commentLines.
     */
    private final List<ThemisAnalysisLine> theComments;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial comment line
     */
    ThemisAnalysisComment(final ThemisAnalysisParser pParser,
                          final ThemisAnalysisLine pLine) {
        /* Create the list of comment lines */
        theComments = new ArrayList<>();
        theComments.add(pLine);

        /* If this is not a complete comment */
        ThemisAnalysisLine myLine = pLine;
        while (!isEndComment(myLine)) {
            /* Pop next line and add it */
            myLine = pParser.popNextLine();
            theComments.add(myLine);
        }
    }

    /**
     * Obtain the number of comment lines.
     * @return the number of comments
     */
    public int getNumComments() {
        return theComments.size();
    }

    /**
     * Is the line a starting comment?
     * @param pLine the line
     * @return true/false
     */
    static boolean isStartComment(final ThemisAnalysisLine pLine) {
        return pLine.startsWithSequence("/*");
    }

    /**
     * Is the line an ending comment?
     * @param pLine the line
     * @return true/false
     */
    private static boolean isEndComment(final ThemisAnalysisLine pLine) {
        return pLine.endsWithSequence("*/");
    }
}
