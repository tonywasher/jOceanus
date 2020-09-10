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
 * Annotation.
 */
public class ThemisAnalysisAnnotation
        implements ThemisAnalysisProcessed {
    /**
     * The annotationLines.
     */
    private final List<ThemisAnalysisAnnotationRef> theAnnotations;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pLine the initial annotation line
     * @throws OceanusException on error
     */
    ThemisAnalysisAnnotation(final ThemisAnalysisParser pParser,
                             final ThemisAnalysisLine pLine) throws OceanusException {
        /* Create the list of annotation lines */
        theAnnotations = new ArrayList<>();
        theAnnotations.add(new ThemisAnalysisAnnotationRef(pParser, pLine));

        /* While there are further lines */
        while (pParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisLine myLine = (ThemisAnalysisLine) pParser.peekNextLine();

            /* It it is also an annotation */
            if (isAnnotation(myLine)) {
                /* Add the annotation line and remove from input */
                theAnnotations.add(new ThemisAnalysisAnnotationRef(pParser, myLine));
                pParser.popNextLine();

                /* else break loop */
            } else {
                break;
            }
        }
    }

    @Override
    public int getNumLines() {
        return theAnnotations.size();
    }

    /**
     * Is the line and annotation?
     * @param pLine the line
     * @return true/false
     */
    static boolean isAnnotation(final ThemisAnalysisLine pLine) {
        return pLine.startsWithChar(ThemisAnalysisChar.ANNOTATION)
                && !pLine.peekNextToken().equals(ThemisAnalysisKeyWord.ANNOTATION.getKeyWord());
    }

    @Override
    public String toString() {
        /* Start parameters */
        final StringBuilder myBuilder = new StringBuilder();

        /* Build parameters */
        boolean bFirst = true;
        for (ThemisAnalysisAnnotationRef myRef : theAnnotations) {
            /* Handle separators */
            if (!bFirst) {
                myBuilder.append(ThemisAnalysisChar.LF);
            } else {
                bFirst = false;
            }

            /* Add parameter */
            myBuilder.append(myRef);
        }

        return myBuilder.toString();
    }

    /**
     * AnnotationReference.
     */
    static class ThemisAnalysisAnnotationRef {
        /**
         * The annotationClass.
         */
        private final ThemisAnalysisReference theAnnotation;

        /**
         * Constructor.
         * @param pParser the parser
         * @param pLine the initial annotation line
         * @throws OceanusException on error
         */
        ThemisAnalysisAnnotationRef(final ThemisAnalysisParser pParser,
                                    final ThemisAnalysisLine pLine) throws OceanusException {
            pLine.stripStartChar(ThemisAnalysisChar.ANNOTATION);
            theAnnotation = pParser.parseDataType(pLine);
        }

        @Override
        public String toString() {
            return theAnnotation.toString();
        }
    }
}
