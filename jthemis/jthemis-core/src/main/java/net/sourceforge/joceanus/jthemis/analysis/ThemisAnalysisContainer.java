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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/**
 * Interface for containers that require postProcessing.
 */
public interface ThemisAnalysisContainer
    extends ThemisAnalysisProcessed {
    /**
     * Obtain the dataType Map.
     * @return the map
     */
    Map<String, ThemisAnalysisDataType> getDataTypes();

    /**
     * Obtain the contents.
     * @return the contents
     */
    Deque<ThemisAnalysisElement> getContents();

    /**
     * Obtain the parent of this container.
     * @return the parent
     */
    ThemisAnalysisContainer getParent();

    /**
     * Post process lines.
     */
    default void postProcessLines() {
        /* Create a copy of the contents list and clear original */
        final Deque<ThemisAnalysisElement> myContents = getContents();
        final Deque<ThemisAnalysisElement> myLines = new ArrayDeque<>(myContents);
        myContents.clear();

        /* Create the new input parser */
        final ThemisAnalysisParser myParser = new ThemisAnalysisParser(myLines, myContents, getParent());

        /* Loop through the lines */
        while (myParser.hasLines()) {
            /* Access next line */
            final ThemisAnalysisElement myElement = myParser.popNextLine();
            boolean processed = false;

            /* If the element is a container */
            if (myElement instanceof ThemisAnalysisContainer) {
                /* Access and process the container */
                final ThemisAnalysisContainer myContainer = (ThemisAnalysisContainer) myElement;
                myContainer.postProcessLines();
                myContents.add(myContainer);
                processed = true;
            }

            /* If the element is already fully processed */
            if (!processed
                    && myElement instanceof ThemisAnalysisProcessed) {
                myContents.add(myElement);
                processed = true;
            }

            /* Everything should now be a line. */
            if (!processed
                 && !(myElement instanceof ThemisAnalysisLine)) {
                throw new IllegalStateException("Unexpected dataType");
            }

            /* process fields and methods */
            if (!processed) {
                final ThemisAnalysisLine myLine = (ThemisAnalysisLine) myElement;
                final ThemisAnalysisElement myResult = myParser.processFieldsAndMethods(myLine);

                /* If we have a field/method */
                if (myResult != null) {
                    /* Add the element and postProcess any Containers */
                    myContents.add(myResult);
                    if (myResult instanceof ThemisAnalysisContainer) {
                        ((ThemisAnalysisContainer) myResult).postProcessLines();
                    }
                    processed = true;
                }
            }

            /* process statements */
            if (!processed) {
                /* Just add the line for the moment */
                myContents.add(myElement);
            }
        }

        /* Process extras */
        postProcessExtras();
    }

    /**
     * Post process extra lines.
     */
    default void postProcessExtras() {
        /* NoOp by default */
    }
}
