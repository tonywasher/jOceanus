/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2022 Tony Washer
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
 * A series of comment lines.
 */
public class ThemisAnalysisComment
        implements ThemisAnalysisProcessed {
    /**
     * Is this a javaDoc comment?
     */
    private final boolean isJavaDoc;

    /**
     * The commentLines.
     */
    private final List<ThemisAnalysisLine> theComments;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial comment line
     * @throws OceanusException on error
     */
    ThemisAnalysisComment(final ThemisAnalysisParser pParser,
                          final ThemisAnalysisLine pLine) throws OceanusException {
        /* Create the list of comment lines */
        theComments = new ArrayList<>();
        theComments.add(pLine);

        /* Determine whether this is a javaDoc comment */
        isJavaDoc = isJavaDocComment(pLine);

        /* If this is not a complete comment */
        ThemisAnalysisLine myLine = pLine;
        while (!isEndComment(myLine)) {
            /* Pop next line and add it */
            myLine = (ThemisAnalysisLine) pParser.popNextLine();
            theComments.add(myLine);
        }
    }

    @Override
    public int getNumLines() {
        return theComments.size();
    }

    /**
     * Is this a javaDoc comment.
     * @return true/false
     */
    public boolean isJavaDoc() {
        return isJavaDoc;
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
     * Is the line a javaDoc comment?
     * @param pLine the line
     * @return true/false
     */
    private static boolean isJavaDocComment(final ThemisAnalysisLine pLine) {
        return pLine.startsWithSequence("/**");
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
