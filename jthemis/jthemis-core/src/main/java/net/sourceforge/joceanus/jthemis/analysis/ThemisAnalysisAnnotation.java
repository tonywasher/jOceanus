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
 * Annotation.
 */
public class ThemisAnalysisAnnotation
        implements ThemisAnalysisElement {
    /**
     * The blankLines.
     */
    private final List<ThemisAnalysisLine> theAnnotations;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial annotation line
     */
    ThemisAnalysisAnnotation(final ThemisAnalysisParser pParser,
                             final ThemisAnalysisLine pLine) {
        /* Create the list of annotation lines */
        theAnnotations = new ArrayList<>();
        theAnnotations.add(pLine);

        /* While there are further lines */
        while (pParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = pParser.peekNextLine();

            /* It it is also an annotation */
            if (isAnnotation(myLine)) {
                /* Add the annotation line and remove from input */
                theAnnotations.add(myLine);
                pParser.popNextLine();

                /* else break loop */
            } else {
                break;
            }
        }
    }

    /**
     * Obtain the number of annotations.
     * @return the number of annotations
     */
    public int getNumAnnotations() {
        return theAnnotations.size();
    }

    /**
     * Is the line and annotation?
     * @param pLine the line
     * @return true/false
     */
    static boolean isAnnotation(final ThemisAnalysisLine pLine) {
        return pLine.startsWithSequence("@");
    }
}
